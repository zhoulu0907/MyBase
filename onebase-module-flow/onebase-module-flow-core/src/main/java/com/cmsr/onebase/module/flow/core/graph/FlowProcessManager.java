package com.cmsr.onebase.module.flow.core.graph;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.flow.context.graph.JsonGraph;
import com.cmsr.onebase.module.flow.context.graph.nodes.start.StartDateFieldNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.start.StartTimeNodeData;
import com.cmsr.onebase.module.flow.core.config.FlowEnableCondition;
import com.cmsr.onebase.module.flow.core.config.FlowProperties;
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
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    private FlowProcessCache flowProcessCache = FlowProcessCache.getInstance();

    public void initAllProcess() {
        List<FlowProcessDO> flowProcessDOS = TenantManager.withoutTenantCondition(() ->
                flowProcessRepository.findAllByEnableStatusAndVersionTag(
                        FlowEnableStatusEnum.ENABLE.getStatus(),
                        flowProperties.getVersionTag()
                ));
        // 检查是否为需要详细跟踪的流程（用于调试）
        Long traceProcessId = flowProperties.getTraceProcessId();

        for (FlowProcessDO flowProcessDO : flowProcessDOS) {

            // 提取流程ID，用于日志跟踪和缓存键
            Long processId = flowProcessDO.getId();
            boolean isTrace = Objects.equals(processId, traceProcessId);

            try {
                onProcessUpdate(flowProcessDO, false);
                if (isTrace){
                    log.info("[TRACE-{}] 加载flowProcess流程成功", processId);
                }
            } catch (Exception e) {
                log.error("初始化flowProcessDO异常：processId={}", processId, e);
            }
        }
        executor.execute(() -> {
            Map<Long, List<FlowProcessDO>> applicationProcessMap = flowProcessDOS.stream().collect(Collectors.groupingBy(FlowProcessDO::getApplicationId));
            for (Map.Entry<Long, List<FlowProcessDO>> entry : applicationProcessMap.entrySet()) {
                Long applicationId = entry.getKey();
                List<FlowProcessDO> dos = entry.getValue();
                cleanApplicationJob(applicationId, dos);
            }
        });
    }

    public void onApplicationChange(Long applicationId, boolean sync) {
        List<FlowProcessDO> flowProcessDOS = TenantManager.withoutTenantCondition(() ->
                flowProcessRepository.findByApplicationIdAndEnableStatus(
                        applicationId,
                        FlowEnableStatusEnum.ENABLE.getStatus(),
                        flowProperties.getVersionTag()
                ));
        Set<Long> oldProcessIds = flowProcessCache.findProcessByApplicationId(applicationId);
        for (FlowProcessDO flowProcessDO : flowProcessDOS) {
            oldProcessIds.remove(flowProcessDO.getId());
        }
        for (Long processId : oldProcessIds) {
            onProcessDelete(applicationId, processId);
        }
        for (FlowProcessDO flowProcessDO : flowProcessDOS) {
            onProcessUpdate(flowProcessDO, sync);
        }
        cleanApplicationJob(applicationId, flowProcessDOS);
        log.info("处理应用更新: applicationId={}, 删除：{} ，添加：{}", applicationId, oldProcessIds, flowProcessDOS.stream().map(FlowProcessDO::getId).toList());
    }


    @SneakyThrows
    public void onApplicationDelete(Long applicationId) {
        Set<Long> ids = flowProcessCache.findProcessByApplicationId(applicationId);
        ids.forEach(id -> {
            flowProcessCache.deleteByProcessId(id);
        });
        stopApplicationJob(applicationId);
        log.info("处理应用删除：applicationId={}, 删除: {}", applicationId, ids);
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
        Map<Long, List<FlowProcessDO>> applicationProcessMap = flowProcessDOS.stream().collect(Collectors.groupingBy(FlowProcessDO::getApplicationId));
        for (Map.Entry<Long, List<FlowProcessDO>> entry : applicationProcessMap.entrySet()) {
            Long applicationId = entry.getKey();
            List<FlowProcessDO> dos = entry.getValue();
            cleanApplicationJob(applicationId, dos);
        }
    }

    @SneakyThrows
    private void stopApplicationJob(Long applicationId) {
        lockExecute(applicationId, () -> {
            jobSchedulerClient.deleteJob(applicationId);
        });
    }

    private void cleanApplicationJob(Long applicationId, List<FlowProcessDO> flowProcessDOS) {
        lockExecute(applicationId, () -> {
            Set<Long> newIds = flowProcessDOS.stream().map(FlowProcessDO::getId).collect(Collectors.toSet());
            Set<Long> oldIds = jobSchedulerClient.queryProcessIds(applicationId);
            oldIds.removeAll(newIds);
            for (Long oldId : oldIds) {
                log.info("删除遗留的job: {}-{}", applicationId, oldId);
                jobSchedulerClient.deleteJob(applicationId, oldId);
                TenantManager.withoutTenantCondition(() -> {
                    flowProcessTimeRepository.deleteByProcessId(oldId);
                    flowProcessDateFieldRepository.deleteByProcessId(oldId);
                });
            }
        });
    }

    /**
     * 处理流程更新 - Flow 流程核心方法
     *
     * <p>当流程配置发生变更时调用，负责完成以下工作：</p>
     * <ol>
     *   <li>验证流程定义的有效性</li>
     *   <li>解析 JSON 流程定义，构建内存中的流程图结构</li>
     *   <li>将构建好的流程图缓存到内存中，供流程执行时使用</li>
     *   <li>根据触发类型（定时/日期字段），启动相应的调度任务</li>
     * </ol>
     *
     * <p><b>核心概念：</b></p>
     * <ul>
     *   <li><b>FlowProcessDO</b>: 流程的数据库持久化对象，包含流程定义 JSON</li>
     *   <li><b>JsonGraph</b>: 流程图在内存中的表示，包含节点、边、连接关系</li>
     *   <li><b>FlowProcessCache</b>: 流程图内存缓存，避免每次执行都重新解析</li>
     *   <li><b>调度任务</b>: 对于定时触发或日期字段触发的流程，需要创建定时任务</li>
     * </ul>
     *
     * <p><b>执行流程：</b></p>
     * <pre>
     * FlowProcessDO (数据库)
     *       ↓
     * processDefinition (JSON字符串)
     *       ↓
     * FlowGraphBuilder.build() 解析构建
     *       ↓
     * JsonGraph (内存流程图)
     *       ↓
     * FlowProcessCache (缓存)
     *       ↓
     * startSchedulingJob() (启动调度)
     * </pre>
     *
     * @param processDO 流程数据对象，包含流程定义、触发类型、应用ID等信息
     * @param sync 是否同步启动调度任务
     *             <ul>
     *               <li>true: 同步执行，适用于单条流程更新场景</li>
     *               <li>false: 异步执行，适用于批量初始化场景，避免阻塞</li>
     *             </ul>
     */
    private void onProcessUpdate(FlowProcessDO processDO, boolean sync) {
        // 提取流程ID，用于日志跟踪和缓存键
        Long processId = processDO.getId();

        // 检查是否为需要详细跟踪的流程（用于调试）
        Long traceProcessId = flowProperties.getTraceProcessId();
        boolean isTrace = Objects.equals(processId, traceProcessId);

        // 记录流程更新开始日志
        if (isTrace) {
            log.info("[TRACE-{}] ========== onProcessUpdate开始 ==========", processId);
            log.info("[TRACE-{}] 处理流程更新: applicationId={}, processName={}, triggerType={}, enableStatus={}, publishStatus={}",
                    processId, processDO.getApplicationId(), processDO.getProcessName(),
                    processDO.getTriggerType(), processDO.getEnableStatus(), processDO.getPublishStatus());
        }

        // ========== 第一步：验证流程定义 ==========
        // 流程定义是一个JSON字符串，描述了节点、边、连接关系
        if (StringUtils.isBlank(processDO.getProcessDefinition())) {
            log.error("流程定义错误, 未包含内容：{}", processDO);
            return;
        }

        if (isTrace) {
            log.info("[TRACE-{}] 开始构建流程图, processDefinitionLength={}",
                    processId, processDO.getProcessDefinition().length());
        }

        // ========== 第二步：构建流程图 ==========
        // 将 JSON 字符串解析为内存中的 JsonGraph 对象
        // 此过程会：
        // 1. 解析 JSON 结构
        // 2. 验证节点类型和数据
        // 3. 补全字段类型信息（如 StartForm 节点的 tableName）
        // 4. 建立节点间的连接关系
        JsonGraph jsonGraph = flowGraphBuilder.build(processDO.getApplicationId(),
                processDO.getProcessDefinition(), processId);

        if (jsonGraph == null) {
            log.error("流程定义错误：{}", processDO);
            return;
        }

        // ========== 第三步：更新缓存 ==========
        // 将构建好的流程图存入内存缓存，后续执行时直接从缓存获取
        // 缓存 key: processId, value: (FlowProcessDO, JsonGraph)
        flowProcessCache.update(processDO, jsonGraph);
        if (isTrace) {
            log.info("[TRACE-{}] 流程图构建成功: nodeCount={}, hasStartNode={}",
                    processId,
                    jsonGraph.getNodes() != null ? jsonGraph.getNodes().size() : 0,
                    jsonGraph.getStartNode() != null);
        }
        // ========== 第四步：启动调度任务 ==========
        // 根据触发类型决定是否需要启动定时任务：
        // - TIME: 定时触发（如 cron 表达式）
        // - DATE_FIELD: 日期字段触发（如某个日期字段到达时）
        // - FORM: 表单触发（无需定时任务，由前端调用触发）
        if (sync) {
            // 同步执行：立即启动调度任务
            if (isTrace) {
                log.info("[TRACE-{}] 同步启动调度任务", processId);
            }
            startSchedulingJob(processDO);
        } else {
            // 异步执行：放入线程池，避免阻塞当前线程
            // 批量初始化时使用，提高启动速度
            if (isTrace) {
                log.info("[TRACE-{}] 异步启动调度任务", processId);
            }
            executor.execute(() -> startSchedulingJob(processDO));
        }

        if (isTrace) {
            log.info("[TRACE-{}] ========== onProcessUpdate完成 ==========", processId);
        }
    }

    private void onProcessDelete(Long applicationId, Long processId) {
        log.info("处理流程删除：{}-{}", applicationId, processId);
        //
        flowProcessCache.deleteByProcessId(processId);
        //
        stopSchedulingJob(applicationId, processId);
        flowProcessTimeRepository.deleteByProcessId(processId);
        flowProcessDateFieldRepository.deleteByProcessId(processId);
    }

    @SneakyThrows
    private void stopSchedulingJob(Long applicationId, Long processId) {
        lockExecute(applicationId, () -> {
            log.info("流程流程任务：{}-{}", applicationId, processId);
            jobSchedulerClient.deleteJob(applicationId, processId);
        });
    }

    @SneakyThrows
    private void startSchedulingJob(FlowProcessDO flowProcessDO) {
        lockExecute(flowProcessDO.getApplicationId(), () -> {
            if (FlowTriggerTypeEnum.isTime(flowProcessDO.getTriggerType())) {
                startTimeJob(flowProcessDO);
            } else if (FlowTriggerTypeEnum.isDateField(flowProcessDO.getTriggerType())) {
                startDateFieldJob(flowProcessDO);
            }
        });
    }

    private void startTimeJob(FlowProcessDO flowProcessDO) {
        FlowProcessTimeDO flowProcessTimeDO = TenantManager.withoutTenantCondition(() -> ApplicationManager.withoutApplicationCondition(() ->
                flowProcessTimeRepository.findByProcessId(flowProcessDO.getId())
        ));
        if (flowProcessTimeDO != null
                && flowProcessTimeDO.getJobId() != null
                && FlowJobStatusEnum.isDeployed(flowProcessTimeDO.getJobStatus())) {
            log.info("流程Time任务已存在：{}-{}", flowProcessTimeDO.getApplicationId(), flowProcessTimeDO.getProcessId());
            return;
        }
        StartTimeNodeData startTimeNodeData = flowProcessCache.findStartTimeNodeDataByProcessId(flowProcessDO.getId());
        JobCreateRequest jobCreateRequest = consumerSettingParams(startTimeNodeData);
        FlowRemoteCallRequest flowRemoteCallRequest = new FlowRemoteCallRequest();
        flowRemoteCallRequest.setJobType(FlowRemoteCallRequest.JOB_TYPE_TIME);
        flowRemoteCallRequest.setApplicationId(flowProcessDO.getApplicationId());
        flowRemoteCallRequest.setProcessId(flowProcessDO.getId());
        flowRemoteCallRequest.setProcessName(flowProcessDO.getProcessName());
        jobCreateRequest.setFlowRemoteCallRequest(flowRemoteCallRequest);
        String jobId = jobSchedulerClient.startJob(jobCreateRequest);
        log.info("启动流程Time任务成功：{}-{}", flowProcessDO.getApplicationId(), flowProcessDO.getId());
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
            log.info("流程DateField任务已存在：{}-{}", flowProcessDateFieldDO.getApplicationId(), flowProcessDateFieldDO.getProcessId());
            return;
        }
        StartDateFieldNodeData startDateFieldNodeData = flowProcessCache.findStartDateFieldNodeDataByProcessId(flowProcessDO.getId());
        JobCreateRequest jobCreateRequest = consumerSettingParams(startDateFieldNodeData);
        FlowRemoteCallRequest flowRemoteCallRequest = new FlowRemoteCallRequest();
        flowRemoteCallRequest.setJobType(FlowRemoteCallRequest.JOB_TYPE_FIELD);
        flowRemoteCallRequest.setApplicationId(flowProcessDO.getApplicationId());
        flowRemoteCallRequest.setProcessId(flowProcessDO.getId());
        flowRemoteCallRequest.setProcessName(flowProcessDO.getProcessName());
        jobCreateRequest.setFlowRemoteCallRequest(flowRemoteCallRequest);
        String jobId = jobSchedulerClient.startJob(jobCreateRequest);
        log.info("启动流程DateField任务成功：{}-{}", flowProcessDO.getApplicationId(), flowProcessDO.getId());
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

    private void lockExecute(Long applicationId, Runnable runnable) {
        RLock lock = redissonClient.getLock(FlowUtils.toRedisFlowLockKey(applicationId));
        try {
            if (lock.tryLock(120, TimeUnit.SECONDS)) {
                runnable.run();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

}
