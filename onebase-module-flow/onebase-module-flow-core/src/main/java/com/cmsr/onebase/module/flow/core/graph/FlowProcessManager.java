package com.cmsr.onebase.module.flow.core.graph;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.context.graph.nodes.start.StartDateFieldNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.start.StartTimeNodeData;
import com.cmsr.onebase.module.flow.core.config.FlowProperties;
import com.cmsr.onebase.module.flow.core.config.FlowEnableCondition;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessDateFieldRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessTimeRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDateFieldDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessTimeDO;
import com.cmsr.onebase.module.flow.core.enums.FlowEnableStatusEnum;
import com.cmsr.onebase.module.flow.core.enums.FlowJobStatusEnum;
import com.cmsr.onebase.module.flow.core.enums.FlowTriggerTypeEnum;
import com.cmsr.onebase.module.flow.core.flow.FlowRemoteCallRequest;
import com.cmsr.onebase.module.flow.core.job.JobCreateRequest;
import com.cmsr.onebase.module.flow.core.job.JobSchedulerClient;
import com.cmsr.onebase.module.flow.core.utils.FlowUtils;
import com.mybatisflex.core.tenant.TenantManager;
import com.yomahub.liteflow.builder.el.LiteFlowChainELBuilder;
import com.yomahub.liteflow.flow.FlowBus;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Author：huangjie
 * @Date：2025/11/1 18:27
 */
@Slf4j
@Setter
@Service
@Conditional(FlowEnableCondition.class)
public class FlowProcessManager {

    @Autowired
    private FlowProcessRepository flowProcessRepository;

    @Autowired
    private FlowProcessTimeRepository flowProcessTimeRepository;

    @Autowired
    private FlowProcessDateFieldRepository flowProcessDateFieldRepository;

    @Autowired
    private FlowGraphBuilder flowGraphBuilder;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private FlowProperties flowProperties;

    @Autowired
    private JobSchedulerClient jobSchedulerClient;

    @Autowired
    private ThreadPoolTaskScheduler executor;

    public void initAllProcess() {
        List<FlowProcessDO> flowProcessDOS = TenantManager.withoutTenantCondition(() ->
                flowProcessRepository.findAllByEnableStatusAndVersionTag(
                        FlowEnableStatusEnum.ENABLE.getStatus(),
                        flowProperties.getVersionTag()
                ));
        for (FlowProcessDO flowProcessDO : flowProcessDOS) {
            try {
                onProcessUpdate(flowProcessDO);
                log.info("加载flowProcess流程成功：{}", flowProcessDO.getId());
            } catch (Exception e) {
                log.error("初始化flowProcessDO异常：{}, {}", flowProcessDO, e.getMessage(), e);
            }
        }
    }

    public String onApplicationChange(Long applicationId) {
        List<FlowProcessDO> flowProcessDOS = TenantManager.withoutTenantCondition(() ->
                flowProcessRepository.findByApplicationIdAndEnableStatus(
                        applicationId,
                        FlowEnableStatusEnum.ENABLE.getStatus(),
                        flowProperties.getVersionTag()
                ));
        Set<Long> oldProcessIds = FlowProcessCache.findProcessByApplicationId(applicationId);
        for (FlowProcessDO flowProcessDO : flowProcessDOS) {
            oldProcessIds.remove(flowProcessDO.getId());
        }
        for (Long processId : oldProcessIds) {
            onProcessDelete(processId);
        }
        for (FlowProcessDO flowProcessDO : flowProcessDOS) {
            onProcessUpdate(flowProcessDO);
        }
        return "删除：" + oldProcessIds + "，添加：" + flowProcessDOS.stream().map(FlowProcessDO::getId).toList();
    }

    @SneakyThrows
    public String onApplicationDelete(Long applicationId) {
        Set<Long> ids = FlowProcessCache.findProcessByApplicationId(applicationId);
        ids.forEach(id -> {
            String chainId = FlowUtils.toFlowChainId(id);
            FlowBus.removeChain(chainId);
            FlowProcessCache.deleteByProcessId(id);
        });
        stopApplicationJob(applicationId);
        return "删除：" + ids;
    }

    public void checkTimeJob() {
        List<FlowProcessDO> flowProcessDOS = TenantManager.withoutTenantCondition(() ->
                flowProcessRepository.findAllByEnableStatusAndVersionTagAndTriggerType(
                        FlowEnableStatusEnum.ENABLE.getStatus(),
                        flowProperties.getVersionTag(),
                        List.of(FlowTriggerTypeEnum.TIME.getType(), FlowTriggerTypeEnum.DATE_FIELD.getType())
                )
        );
        for (FlowProcessDO flowProcessDO : flowProcessDOS) {
            startSchedulingJob(flowProcessDO);
        }
    }

    @SneakyThrows
    private void stopApplicationJob(Long applicationId) {
        RLock lock = redissonClient.getLock(FlowUtils.toRedisProcessLockKey(applicationId));
        if (lock.tryLock(120, TimeUnit.SECONDS)) {
            try {
                jobSchedulerClient.deleteJob(applicationId);
            } finally {
                lock.unlock();
            }
        }
    }

    private void onProcessUpdate(FlowProcessDO processDO) {
        log.info("处理流程更新事件：{}", processDO.getId());
        if (StringUtils.isBlank(processDO.getProcessDefinition())) {
            log.error("流程定义错误, 未包含内容：{}", processDO);
            return;
        }
        JsonGraph jsonGraph = flowGraphBuilder.build(processDO.getApplicationId(), processDO.getProcessDefinition());
        if (jsonGraph == null) {
            log.error("流程定义错误：{}", processDO);
            return;
        }
        String flowChain = FlowChainBuilder.toFlowChain(jsonGraph);
        log.debug("flowChain:{}", flowChain);
        String chainId = FlowUtils.toFlowChainId(processDO.getId());
        LiteFlowChainELBuilder.createChain().setChainId(chainId).setEL(flowChain).build();
        //
        FlowProcessCache.update(processDO, jsonGraph);
        executor.execute(() -> startSchedulingJob(processDO));
    }

    private void onProcessDelete(Long processId) {
        log.info("发布流程删除事件：{}", processId);
        String chainId = FlowUtils.toFlowChainId(processId);
        FlowBus.removeChain(chainId);
        //
        FlowProcessCache.deleteByProcessId(processId);
        //
        stopSchedulingJob(processId);
    }

    @SneakyThrows
    private void stopSchedulingJob(Long processId) {
        RLock lock = redissonClient.getLock(FlowUtils.toRedisProcessLockKey(processId));
        if (lock.tryLock(120, TimeUnit.SECONDS)) {
            try {
                jobSchedulerClient.deleteJob(processId);
            } finally {
                lock.unlock();
            }
        }
    }

    @SneakyThrows
    private void startSchedulingJob(FlowProcessDO flowProcessDO) {
        if (FlowTriggerTypeEnum.isTime(flowProcessDO.getTriggerType())) {
            RLock lock = redissonClient.getLock(FlowUtils.toRedisProcessLockKey(flowProcessDO.getId()));
            if (lock.tryLock(60, TimeUnit.SECONDS)) {
                try {
                    startTimeJob(flowProcessDO);
                } finally {
                    lock.unlock();
                }
            }
        } else if (FlowTriggerTypeEnum.isDateField(flowProcessDO.getTriggerType())) {
            RLock lock = redissonClient.getLock(FlowUtils.toRedisProcessLockKey(flowProcessDO.getId()));
            if (lock.tryLock(60, TimeUnit.SECONDS)) {
                try {
                    startDateFieldJob(flowProcessDO);
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    private void startTimeJob(FlowProcessDO flowProcessDO) {
        FlowProcessTimeDO flowProcessTimeDO = TenantManager.withoutTenantCondition(() -> ApplicationManager.withoutApplicationCondition(() ->
                flowProcessTimeRepository.findByProcessId(flowProcessDO.getId())
        ));
        if (flowProcessTimeDO != null
                && flowProcessTimeDO.getJobId() != null
                && FlowJobStatusEnum.isDeployed(flowProcessTimeDO.getJobStatus())) {
            return;
        }
        StartTimeNodeData startTimeNodeData = FlowProcessCache.findStartTimeNodeDataByProcessId(flowProcessDO.getId());
        JobCreateRequest jobCreateRequest = consumerSettingParams(startTimeNodeData);
        FlowRemoteCallRequest flowRemoteCallRequest = new FlowRemoteCallRequest();
        flowRemoteCallRequest.setJobType(FlowRemoteCallRequest.JOB_TYPE_TIME);
        flowRemoteCallRequest.setApplicationId(flowProcessDO.getApplicationId());
        flowRemoteCallRequest.setProcessId(flowProcessDO.getId());
        flowRemoteCallRequest.setProcessName(flowProcessDO.getProcessName());
        jobCreateRequest.setFlowRemoteCallRequest(flowRemoteCallRequest);
        String jobId = jobSchedulerClient.startJob(jobCreateRequest);
        if (flowProcessTimeDO == null) {
            flowProcessTimeDO = new FlowProcessTimeDO();
            flowProcessTimeDO.setProcessId(flowProcessDO.getId());
            flowProcessTimeDO.setApplicationId(flowProcessDO.getApplicationId());
            flowProcessTimeDO.setJobId(jobId);
            flowProcessTimeDO.setJobStatus(FlowJobStatusEnum.DEPLOYED.getStatus());
            flowProcessTimeRepository.save(flowProcessTimeDO);
        } else {
            flowProcessTimeDO.setJobId(jobId);
            flowProcessTimeDO.setJobStatus(FlowJobStatusEnum.DEPLOYED.getStatus());
            flowProcessTimeRepository.updateById(flowProcessTimeDO);
        }
        log.info("启动flowProcess流程成功：{}", flowProcessDO.getId());
    }


    private JobCreateRequest consumerSettingParams(StartTimeNodeData startTimeNodeData) {
        JobCreateRequest jobCreateRequest = new JobCreateRequest();
        jobCreateRequest.setStartTime(startTimeNodeData.getStartTime().trim());
        jobCreateRequest.setEndTime(startTimeNodeData.getEndTime().trim());
        jobCreateRequest.setCrontab(startTimeNodeData.createCronExpression().trim());
        return jobCreateRequest;
    }

    private void startDateFieldJob(FlowProcessDO flowProcessDO) {
        FlowProcessDateFieldDO flowProcessDateFieldDO = TenantManager.withoutTenantCondition(() -> ApplicationManager.withoutApplicationCondition(() ->
                flowProcessDateFieldRepository.findByProcessId(flowProcessDO.getId())
        ));
        if (flowProcessDateFieldDO != null
                && flowProcessDateFieldDO.getJobId() != null
                && FlowJobStatusEnum.isDeployed(flowProcessDateFieldDO.getJobStatus())) {
            return;
        }
        StartDateFieldNodeData startDateFieldNodeData = FlowProcessCache.findStartDateFieldNodeDataByProcessId(flowProcessDO.getId());
        JobCreateRequest jobCreateRequest = consumerSettingParams(startDateFieldNodeData);
        FlowRemoteCallRequest flowRemoteCallRequest = new FlowRemoteCallRequest();
        flowRemoteCallRequest.setJobType(FlowRemoteCallRequest.JOB_TYPE_FIELD);
        flowRemoteCallRequest.setApplicationId(flowProcessDO.getApplicationId());
        flowRemoteCallRequest.setProcessId(flowProcessDO.getId());
        flowRemoteCallRequest.setProcessName(flowProcessDO.getProcessName());
        jobCreateRequest.setFlowRemoteCallRequest(flowRemoteCallRequest);
        String jobId = jobSchedulerClient.startJob(jobCreateRequest);
        if (flowProcessDateFieldDO == null) {
            flowProcessDateFieldDO = new FlowProcessDateFieldDO();
            flowProcessDateFieldDO.setProcessId(flowProcessDO.getId());
            flowProcessDateFieldDO.setApplicationId(flowProcessDO.getApplicationId());
            flowProcessDateFieldDO.setJobId(jobId);
            flowProcessDateFieldDO.setJobStatus(FlowJobStatusEnum.DEPLOYED.getStatus());
            flowProcessDateFieldRepository.save(flowProcessDateFieldDO);
        } else {
            flowProcessDateFieldDO.setJobId(jobId);
            flowProcessDateFieldDO.setJobStatus(FlowJobStatusEnum.DEPLOYED.getStatus());
            flowProcessDateFieldRepository.updateById(flowProcessDateFieldDO);
        }
    }

    private JobCreateRequest consumerSettingParams(StartDateFieldNodeData startDateFieldNodeData) {
        JobCreateRequest jobCreateRequest = new JobCreateRequest();
        jobCreateRequest.setStartTime(LocalDateTime.now().format(JobSchedulerClient.DATETIME_FORMATTER));
        jobCreateRequest.setEndTime("2050-12-30 23:59:59");
        jobCreateRequest.setCrontab(startDateFieldNodeData.createCronExpression());
        return jobCreateRequest;
    }


}
