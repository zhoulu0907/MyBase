package com.cmsr.onebase.module.flow.core.handler;

import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartDateFieldNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartTimeNodeData;
import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessDateFieldRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessRepository;
import com.cmsr.onebase.module.flow.core.dal.database.FlowProcessTimeRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDateFieldDO;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessTimeDO;
import com.cmsr.onebase.module.flow.core.enums.FlowEnableStatusEnum;
import com.cmsr.onebase.module.flow.core.enums.FlowJobStatusEnum;
import com.cmsr.onebase.module.flow.core.enums.FlowTriggerTypeEnum;
import com.cmsr.onebase.module.flow.core.flow.RemoteCallRequest;
import com.cmsr.onebase.module.flow.core.graph.FlowGraphBuilder;
import com.cmsr.onebase.module.flow.core.job.JobClient;
import com.cmsr.onebase.module.flow.core.job.JobCreateRequest;
import com.cmsr.onebase.module.flow.core.utils.FlowUtils;
import com.mybatisflex.core.tenant.TenantManager;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author：huangjie
 * @Date：2025/10/10 10:19
 */
@Slf4j
@Component
@Conditional(FlowRuntimeCondition.class)
public class FlowTimeJobHandler {

    @Setter
    @Autowired
    private FlowProcessRepository flowProcessRepository;

    @Setter
    @Autowired
    private FlowProcessTimeRepository flowProcessTimeRepository;

    @Setter
    @Autowired
    private FlowProcessDateFieldRepository flowProcessDateFieldRepository;

    @Setter
    @Autowired
    private JobClient jobClient;

    @Setter
    @Autowired
    private RedissonClient redissonClient;

    public void initAllJob() {
        List<FlowProcessDO> flowProcessDOS = TenantManager.withoutTenantCondition(() -> flowProcessRepository.findAllByEnableStatus(FlowEnableStatusEnum.ENABLE.getStatus()));
        for (FlowProcessDO flowProcessDO : flowProcessDOS) {
            try {
                startJob(flowProcessDO);
            } catch (Exception e) {
                log.error("初始化flowProcessDO异常：{}, {}", flowProcessDO, e.getMessage(), e);
            }
        }
    }

    public void onApplicationChange(Long applicationId) throws InterruptedException {
        List<FlowProcessDO> flowProcessDOS = TenantManager.withoutTenantCondition(() -> flowProcessRepository.findByApplicationIdAndEnableStatus(applicationId, FlowEnableStatusEnum.ENABLE.getStatus()));
        for (FlowProcessDO flowProcessDO : flowProcessDOS) {
            RLock lock = redissonClient.getLock(FlowUtils.toRedisProcessLockKey(flowProcessDO.getId()));
            if (lock.tryLock(60, TimeUnit.SECONDS)) {
                try {
                    startJob(flowProcessDO);
                } finally {
                    lock.unlock();
                }
            }
        }
    }


    public void onApplicationDelete(Long applicationId) throws InterruptedException {
        RLock lock = redissonClient.getLock(FlowUtils.toRedisProcessLockKey(applicationId));
        if (lock.tryLock(120, TimeUnit.SECONDS)) {
            try {
                jobClient.deleteJob(applicationId);
            } finally {
                lock.unlock();
            }
        }
    }


    private void startJob(FlowProcessDO flowProcessDO) throws InterruptedException {
        if (FlowTriggerTypeEnum.isTime(flowProcessDO.getTriggerType())) {
            startTimeJob(flowProcessDO);
        }
        if (FlowTriggerTypeEnum.isDateField(flowProcessDO.getTriggerType())) {
            startDateFieldJob(flowProcessDO);
        }
    }

    private void startTimeJob(FlowProcessDO flowProcessDO) {
        FlowProcessTimeDO flowProcessTimeDO = TenantManager.withoutTenantCondition(() -> flowProcessTimeRepository.findByProcessId(flowProcessDO.getId()));
        if (flowProcessTimeDO != null
                && flowProcessTimeDO.getJobId() != null
                && FlowJobStatusEnum.isDeployed(flowProcessTimeDO.getJobStatus())) {
            return;
        }
        JsonGraph jsonGraph = FlowGraphBuilder.build(flowProcessDO.getProcessDefinition());
        StartTimeNodeData startTimeNodeData = (StartTimeNodeData) jsonGraph.getStartNode().getData();
        JobCreateRequest jobCreateRequest = consumerSettingParams(startTimeNodeData);
        RemoteCallRequest remoteCallRequest = new RemoteCallRequest();
        remoteCallRequest.setJobType(RemoteCallRequest.JOB_TYPE_TIME);
        remoteCallRequest.setApplicationId(flowProcessDO.getApplicationId());
        remoteCallRequest.setProcessId(flowProcessDO.getId());
        remoteCallRequest.setProcessName(flowProcessDO.getProcessName());
        jobCreateRequest.setRemoteCallRequest(remoteCallRequest);
        String jobId = jobClient.startJob(jobCreateRequest);
        if (flowProcessTimeDO == null) {
            flowProcessTimeDO = new FlowProcessTimeDO();
            flowProcessTimeDO.setProcessId(flowProcessDO.getId());
            flowProcessTimeDO.setJobId(jobId);
            flowProcessTimeDO.setJobStatus(FlowJobStatusEnum.DEPLOYED.getStatus());
            flowProcessTimeRepository.save(flowProcessTimeDO);
        } else {
            flowProcessTimeDO.setJobId(jobId);
            flowProcessTimeDO.setJobStatus(FlowJobStatusEnum.DEPLOYED.getStatus());
            flowProcessTimeRepository.updateById(flowProcessTimeDO);
        }
        log.info("加载flowProcess流程成功：{}", flowProcessDO.getId());
    }


    private JobCreateRequest consumerSettingParams(StartTimeNodeData startTimeNodeData) {
        JobCreateRequest jobCreateRequest = new JobCreateRequest();
        jobCreateRequest.setStartTime(startTimeNodeData.getStartTime().trim());
        jobCreateRequest.setEndTime(startTimeNodeData.getEndTime().trim());
        jobCreateRequest.setCrontab(startTimeNodeData.createCronExpression().trim());
        return jobCreateRequest;
    }

    private void startDateFieldJob(FlowProcessDO flowProcessDO) {
        FlowProcessDateFieldDO flowProcessDateFieldDO = TenantManager.withoutTenantCondition(() -> flowProcessDateFieldRepository.findByProcessId(flowProcessDO.getId()));
        if (flowProcessDateFieldDO != null
                && flowProcessDateFieldDO.getJobId() != null
                && FlowJobStatusEnum.isDeployed(flowProcessDateFieldDO.getJobStatus())) {
            return;
        }
        JsonGraph jsonGraph = FlowGraphBuilder.build(flowProcessDO.getProcessDefinition());
        StartDateFieldNodeData startDateFieldNodeData = (StartDateFieldNodeData) jsonGraph.getStartNode().getData();
        JobCreateRequest jobCreateRequest = consumerSettingParams(startDateFieldNodeData);
        RemoteCallRequest remoteCallRequest = new RemoteCallRequest();
        remoteCallRequest.setJobType(RemoteCallRequest.JOB_TYPE_FIELD);
        remoteCallRequest.setApplicationId(flowProcessDO.getApplicationId());
        remoteCallRequest.setProcessId(flowProcessDO.getId());
        remoteCallRequest.setProcessName(flowProcessDO.getProcessName());
        jobCreateRequest.setRemoteCallRequest(remoteCallRequest);
        String jobId = jobClient.startJob(jobCreateRequest);
        if (flowProcessDateFieldDO == null) {
            flowProcessDateFieldDO = new FlowProcessDateFieldDO();
            flowProcessDateFieldDO.setProcessId(flowProcessDO.getId());
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
        jobCreateRequest.setStartTime(LocalDateTime.now().format(JobClient.DATETIME_FORMATTER));
        jobCreateRequest.setEndTime("2050-12-30 23:59:59");
        jobCreateRequest.setCrontab(startDateFieldNodeData.createCronExpression());
        return jobCreateRequest;
    }


}
