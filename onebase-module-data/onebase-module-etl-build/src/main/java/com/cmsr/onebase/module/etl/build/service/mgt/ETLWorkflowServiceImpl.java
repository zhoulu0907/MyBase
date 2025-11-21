package com.cmsr.onebase.module.etl.build.service.mgt;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.ds.client.DolphinSchedulerClient;
import com.cmsr.onebase.framework.ds.model.schedule.sub.Schedule;
import com.cmsr.onebase.framework.ds.model.task.def.HttpTask;
import com.cmsr.onebase.jose.JoseGenerator;
import com.cmsr.onebase.module.etl.build.service.mgt.vo.*;
import com.cmsr.onebase.module.etl.build.util.Cron;
import com.cmsr.onebase.module.etl.common.excute.ExecuteRequest;
import com.cmsr.onebase.module.etl.common.graph.Node;
import com.cmsr.onebase.module.etl.common.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.common.graph.conf.JdbcInputConfig;
import com.cmsr.onebase.module.etl.common.graph.conf.JdbcOutputConfig;
import com.cmsr.onebase.module.etl.common.preview.ColumnDefine;
import com.cmsr.onebase.module.etl.common.preview.DataPreview;
import com.cmsr.onebase.module.etl.core.dal.database.*;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLExecutionLogDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLScheduleJobDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLWorkflowDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLWorkflowTableDO;
import com.cmsr.onebase.module.etl.core.enums.ETLConstants;
import com.cmsr.onebase.module.etl.core.enums.ETLErrorCodeConstants;
import com.cmsr.onebase.module.etl.core.enums.ScheduleJobStatus;
import com.cmsr.onebase.module.etl.core.enums.ScheduleType;
import com.cmsr.onebase.module.etl.core.vo.WorkflowPageReqVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import jakarta.annotation.Resource;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class ETLWorkflowServiceImpl implements ETLWorkflowService {

    @Value("${onebase.scheduler.etl-project}")
    private Long etlProjectCode;

    @Value("${onebase.flink.address}")
    private String flinkServerUrl;

    @Resource
    private DolphinSchedulerClient dolphinSchedulerClient;

    @Resource
    private ETLWorkflowRepository workflowRepository;

    @Resource
    private ETLWorkflowTableRepository workflowTableRepository;

    @Resource
    private ETLScheduleJobRepository scheduleJobRepository;

    @Resource
    private ETLExecutionLogRepository executionLogRepository;

    @Resource
    private ETLTableRepository tableRepository;

    @Override
    public PageResult<WorkflowBriefVO> getWorkflowPage(WorkflowPageReqVO pageReqVO) {
        PageResult<ETLWorkflowDO> pageDOs = workflowRepository.getWorkflowPage(pageReqVO);
        List<WorkflowBriefVO> pageVOs = new ArrayList<>();
        for (ETLWorkflowDO workflowDO : pageDOs.getList()) {
            WorkflowBriefVO briefVO = new WorkflowBriefVO();
            Long workflowId = workflowDO.getId();
            Long applicationId = workflowDO.getApplicationId();
            briefVO.setId(workflowId);
            briefVO.setApplicationId(applicationId);
            briefVO.setFlowName(workflowDO.getWorkflowName());
            briefVO.setEnableStatus(workflowDO.getIsEnabled());
            briefVO.setScheduleStrategy(workflowDO.getScheduleStrategy());
            ETLScheduleJobDO scheduleJobDO = scheduleJobRepository.findByApplicationIdAndWorkflowId(applicationId, workflowId);
            if (scheduleJobDO != null) {
                briefVO.setIsSyncDone(scheduleJobDO.getJobStatus());
                briefVO.setLastSuccessTime(scheduleJobDO.getLastSuccessTime());
            }
            Set<Long> relatedSourceTableIds = workflowTableRepository.findSourceTableIdsByWorkflowId(workflowId);
            if (CollectionUtils.isNotEmpty(relatedSourceTableIds)) {
                List<String> sourceTableNames = tableRepository.getNameByIds(relatedSourceTableIds);
                briefVO.setSourceTables(sourceTableNames);
            } else {
                briefVO.setSourceTables(null);
            }
            ETLWorkflowTableDO relatedTargetTable = workflowTableRepository.findTargetTableIdByWorkflowId(workflowId);
            if (relatedTargetTable != null) {
                String targetTableName = tableRepository.getNameById(relatedTargetTable.getTableId());
                briefVO.setTargetTable(targetTableName);
            }
            pageVOs.add(briefVO);
        }
        return new PageResult<>(pageVOs, pageDOs.getTotal());
    }

    @Override
    public WorkflowDetailVO getWorkflowDetail(Long workflowId) {
        ETLWorkflowDO workflowDO = getWorkflowById(workflowId);
        WorkflowDetailVO workflowDetailVO = new WorkflowDetailVO();
        workflowDetailVO.setId(workflowDO.getId());
        workflowDetailVO.setFlowName(workflowDO.getWorkflowName());
        workflowDetailVO.setConfig(workflowDO.getConfig());

        return workflowDetailVO;
    }

    @Override
    public Long createWorkflow(WorkflowCreateVO createVO) {
        ETLWorkflowDO workflowDO = new ETLWorkflowDO();
        Long applicationId = createVO.getApplicationId();
        workflowDO.setApplicationId(applicationId);
        workflowDO.setWorkflowName(createVO.getFlowName());
        workflowDO.setDeclaration(createVO.getDeclaration());
        workflowDO.setConfig(createVO.getConfig());
        workflowDO.setIsEnabled(0);
        workflowDO.setScheduleStrategy(ScheduleType.MANUALLY.getValue());
        // 创建workflow
        workflowRepository.save(workflowDO);
        Long workflowId = workflowDO.getId();
        // 创建scheduleJob
        ETLScheduleJobDO scheduleJobDO = new ETLScheduleJobDO();
        scheduleJobDO.setApplicationId(applicationId);
        scheduleJobDO.setWorkflowId(workflowId);
        scheduleJobDO.setJobStatus(ScheduleJobStatus.INITIALIZED.getValue());
        scheduleJobRepository.save(scheduleJobDO);
        // 解析workflow相关的表信息
        updateWorkflowTableRelations(workflowDO);
        return workflowId;
    }

    @Override
    public void updateWorkflow(WorkflowUpdateVO updateVO) {
        Long workflowId = updateVO.getId();
        ETLWorkflowDO oldWorkflow = getOperableWorkflow(workflowId);
        if (!Objects.equals(oldWorkflow.getApplicationId(), updateVO.getApplicationId())) {
            throw new IllegalArgumentException("应用ID不一致");
        }
        oldWorkflow.setWorkflowName(updateVO.getFlowName());
        oldWorkflow.setDeclaration(updateVO.getDeclaration());
        oldWorkflow.setConfig(updateVO.getConfig());
        updateWorkflowTableRelations(oldWorkflow);
        workflowRepository.updateById(oldWorkflow);
    }

    private void updateWorkflowTableRelations(ETLWorkflowDO workflowDO) {
        try {
            Long applicationId = workflowDO.getApplicationId();
            Long workflowId = workflowDO.getId();
            workflowTableRepository.deleteByWorkflowId(workflowId);
            List<ETLWorkflowTableDO> workflowTableDOList = new ArrayList<>();
            if (StringUtils.isBlank(workflowDO.getConfig())) {
                return;
            }
            WorkflowGraph workflowGraph = JsonUtils.parseObject(workflowDO.getConfig(), WorkflowGraph.class);
            List<Node> nodes = workflowGraph.getNodes();
            if (CollectionUtils.isEmpty(nodes)) {
                return;
            }
            List<Node> startNodes = workflowGraph.findStartNodes();
            List<Node> endNodes = workflowGraph.findEndNodes();
            for (Node startNode : startNodes) {
                try {
                    JdbcInputConfig inputConfig = (JdbcInputConfig) startNode.getConfig();
                    ETLWorkflowTableDO workflowTableRel = new ETLWorkflowTableDO();
                    workflowTableRel.setWorkflowId(workflowId);
                    workflowTableRel.setApplicationId(applicationId);
                    workflowTableRel.setRelation(ETLConstants.WORKFLOW_TABLE_RELATION_SOURCE);
                    workflowTableRel.setDatasourceId(inputConfig.getDatasourceId());
                    workflowTableRel.setTableId(inputConfig.getTableId());
                    workflowTableDOList.add(workflowTableRel);
                } catch (Exception ignored) {
                }
            }
            for (Node endNode : endNodes) {
                try {
                    JdbcOutputConfig outputConfig = (JdbcOutputConfig) endNode.getConfig();
                    ETLWorkflowTableDO workflowTableRel = new ETLWorkflowTableDO();
                    workflowTableRel.setWorkflowId(workflowId);
                    workflowTableRel.setApplicationId(applicationId);
                    workflowTableRel.setRelation(ETLConstants.WORKFLOW_TABLE_RELATION_TARGET);
                    workflowTableRel.setDatasourceId(outputConfig.getDatasourceId());
                    workflowTableRel.setTableId(outputConfig.getTableId());
                    workflowTableDOList.add(workflowTableRel);
                } catch (Exception ignored) {
                }
            }
            workflowTableRepository.insertBatch(workflowTableDOList);
        } catch (Exception ignored) {
            log.warn("保存表关系结构异常, {}", workflowDO.getConfig());
        }
    }

    @Override
    public void deleteWorkflow(Long workflowId) {
        getOperableWorkflow(workflowId);
        deleteAllRelated(workflowId);
    }

    @Transactional(rollbackFor = Exception.class)
    private void deleteAllRelated(Long workflowId) {
        executionLogRepository.deleteByWorkflowId(workflowId);
        workflowTableRepository.deleteByWorkflowId(workflowId);
        scheduleJobRepository.deleteByWorkflowId(workflowId);
        workflowRepository.removeById(workflowId);
    }

    @Override
    public void enableWorkflow(Long workflowId) {
        ETLWorkflowDO workflowDO = getOperableWorkflow(workflowId);
        syncEnableStatus(workflowDO);
    }

    private void syncEnableStatus(ETLWorkflowDO workflowDO) {
        Long workflowId = workflowDO.getId();
        ScheduleType scheduleType = ScheduleType.of(workflowDO.getScheduleStrategy());
        ETLScheduleJobDO scheduleJobDO = scheduleJobRepository.findByApplicationIdAndWorkflowId(workflowDO.getApplicationId(), workflowId);
        if (scheduleJobDO == null) {
            throw new IllegalStateException("调度信息不存在");
        }
        Long jobId;
        String jobIdStr = scheduleJobDO.getJobId();
        if (StringUtils.isBlank(jobIdStr)) {
            // create
            HttpTask httpTask = HttpTask.ofUrl(flinkServerUrl + "/flink/execute")
                    .method(HttpTask.HttpMethod.POST)
                    .body(JsonUtils.toJsonString(Map.of("workflowId", workflowId)));
            jobId = dolphinSchedulerClient.createSingletonHttpWorkflow(etlProjectCode,
                    String.valueOf(workflowId),
                    httpTask,
                    workflowDO.getWorkflowName() + "：" + workflowDO.getDeclaration());
            scheduleJobDO.setJobId(String.valueOf(jobId));
            scheduleJobRepository.updateById(scheduleJobDO);
        } else {
            jobId = Long.parseLong(jobIdStr);
        }
        // online
        switch (scheduleType) {
            case MANUALLY -> dolphinSchedulerClient.onlineWorkflow(etlProjectCode, jobId);
            case FIXED -> {
                Schedule schedule = new Schedule();
                FixedDurationSchedule fixedSchedule = JsonUtils.parseObject(workflowDO.getScheduleConfig(), FixedDurationSchedule.class);
                String repeatType = fixedSchedule.getRepeatType();
                String crontab = null;
                Cron cron = new Cron();
                switch (repeatType) {
                    case "none":
                        cron.setDateTime(fixedSchedule.getTriggerTime());
                        break;
                    case "cron":
                        crontab = fixedSchedule.getTriggerTime();
                        break;
                    case "day": {
                        cron.setHourAndMinute(fixedSchedule.getTriggerTime());
                        break;
                    }
                    case "week": {
                        cron.setWeeks(fixedSchedule.getRepeatWeek());
                        cron.setHourAndMinute(fixedSchedule.getTriggerTime());
                        break;
                    }
                    case "month": {
                        cron.setDays(fixedSchedule.getRepeatDay());
                        cron.setHourAndMinute(fixedSchedule.getTriggerTime());
                        break;
                    }
                    case "year": {
                        cron.setMonthAndDay(fixedSchedule.getTriggerDate());
                        cron.setHourAndMinute(fixedSchedule.getTriggerTime());
                        break;
                    }
                    default:
                        throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.ILLEGAL_SCHEDULE_TYPE);
                }
                if (crontab == null) {
                    crontab = cron.toCron();
                }

                schedule.setCrontab(crontab);

                dolphinSchedulerClient.onlineWorkflowWithSchedule(etlProjectCode, jobId, schedule);
            }
            default -> throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.ILLEGAL_SCHEDULE_TYPE);
        }
        workflowDO.setIsEnabled(1);
        workflowRepository.updateById(workflowDO);
    }

    @Override
    public void disableWorkflow(Long workflowId) {
        ETLWorkflowDO workflowDO = getWorkflowById(workflowId);
        if (!workflowDO.isEnabled()) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.WORKFLOW_DISABLED);
        }
        ETLScheduleJobDO scheduleJobDO = scheduleJobRepository.findByApplicationIdAndWorkflowId(workflowDO.getApplicationId(), workflowId);
        if (scheduleJobDO == null) {
            throw new IllegalStateException("调度信息不存在");
        }
        Long jobId = Long.parseLong(scheduleJobDO.getJobId());
        dolphinSchedulerClient.purgeWorkflow(etlProjectCode, jobId);
        workflowDO.setIsEnabled(0);
        workflowRepository.updateById(workflowDO);

        scheduleJobRepository.removeJobId(workflowId);
    }

    @Override
    public void configScheduleStrategy(ScheduleConfigVO scheduleVO) {
        ETLWorkflowDO workflowDO = getWorkflowById(scheduleVO.getWorkflowId());
        workflowDO.setWorkflowName(scheduleVO.getFlowName());
        workflowDO.setScheduleStrategy(scheduleVO.getScheduleStrategy().getValue());
        workflowDO.setScheduleConfig(JsonUtils.toJsonString(scheduleVO.getConfig()));
        workflowDO.setIsEnabled(scheduleVO.getEnableStatus());
        workflowRepository.updateById(workflowDO);
        if (workflowDO.isEnabled()) {
            syncEnableStatus(workflowDO);
        }
    }

    @Override
    public void startWorkflowManually(Long workflowId) {
        // 必须是已启用的ETL
        ETLWorkflowDO workflowDO = getWorkflowById(workflowId);
        if (workflowDO.getIsEnabled() == 0) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.WORKFLOW_DISABLED);
        }
        Long applicationId = workflowDO.getApplicationId();
        ETLScheduleJobDO scheduleJobDO = scheduleJobRepository.findByApplicationIdAndWorkflowId(applicationId, workflowId);
        if (scheduleJobDO == null) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.WORKFLOW_ALREADY_OFFLINE);
        }
        Long jobId = Long.parseLong(scheduleJobDO.getJobId());
        dolphinSchedulerClient.runWorkflowManually(etlProjectCode, jobId, null, null);
    }

    @Override
    public PageResult<ExecutionLogVO> getWorkflowExecutionLogs(Long applicationId, Long workflowId, Integer pageNo, Integer pageSize) {
        PageResult<ETLExecutionLogDO> executionLogResult = executionLogRepository.queryPage(applicationId, workflowId, pageNo, pageSize);
        List<ExecutionLogVO> logVOList = new ArrayList<>();
        for (ETLExecutionLogDO logDO : executionLogResult.getList()) {
            ExecutionLogVO executionLogVO = new ExecutionLogVO();
            executionLogVO.setApplicationId(logDO.getApplicationId());
            executionLogVO.setWorkflowId(logDO.getWorkflowId());
            executionLogVO.setBusinessDate(logDO.getBussinessDate());
            executionLogVO.setStartTime(logDO.getStartTime());
            executionLogVO.setEndTime(logDO.getEndTime());
            executionLogVO.setDuration(logDO.getDurationTime());
            executionLogVO.setTriggerType(logDO.getTriggerType());
            executionLogVO.setTriggerUser(String.valueOf(logDO.getTriggerUser()));
            executionLogVO.setTaskStatus(logDO.getTaskStatus());

            logVOList.add(executionLogVO);
        }
        return new PageResult<>(logVOList, executionLogResult.getTotal());
    }

    @Override
    public DataPreview previewWorkflow(PreviewReqVO previewReqVO) {
        ExecuteRequest executeRequest = new ExecuteRequest();
        JsonNode workflow = previewReqVO.getWorkflow();
        if (workflow instanceof NullNode) {
            throw new IllegalArgumentException("流程参数为空");
        }
        executeRequest.setPreviewWorkflow(workflow.toString());
        executeRequest.setPreviewNodeId(previewReqVO.getNodeId());
        String token = JoseGenerator.generateToken(30);
        HttpResponse<String> response = Unirest.post(flinkServerUrl + "/flink/preview")
                .header("Content-Type", "application/json")
                .header("X-Exec-Token", token)
                .body(executeRequest)
                .asString();
        if (response.getStatus() != 200) {
            log.error("Flink Server 预览数据响应错误: {}", response.getBody());
            throw new IllegalStateException("请求响应异常，" + response.getBody());
        }
        return JsonUtils.parseObject(response.getBody(), DataPreview.class);
    }

    @Override
    public List<ColumnDefine> nodeColumns(PreviewReqVO previewReqVO) {
        ExecuteRequest executeRequest = new ExecuteRequest();
        JsonNode workflow = previewReqVO.getWorkflow();
        if (workflow instanceof NullNode) {
            throw new IllegalArgumentException("流程参数为空");
        }
        executeRequest.setPreviewWorkflow(workflow.toString());
        executeRequest.setPreviewNodeId(previewReqVO.getNodeId());
        String token = JoseGenerator.generateToken(30);
        HttpResponse<String> response = Unirest.post(flinkServerUrl + "/flink/columns")
                .header("Content-Type", "application/json")
                .header("X-Exec-Token", token)
                .body(executeRequest)
                .asString();
        if (response.getStatus() != 200) {
            log.error("Flink Server 列分析响应错误: {}", response.getBody());
            throw new IllegalStateException("请求响应异常，" + response.getBody());
        }
        return JsonUtils.parseArray(response.getBody(), ColumnDefine.class);
    }

    @Override
    public ScheduleRespVO getWorkflowSchedule(Long workflowId) {
        ETLWorkflowDO workflowDO = getWorkflowById(workflowId);

        ScheduleRespVO scheduleRespVO = new ScheduleRespVO();
        scheduleRespVO.setApplicationId(workflowDO.getApplicationId());
        scheduleRespVO.setWorkflowId(workflowId);
        scheduleRespVO.setFlowName(workflowDO.getWorkflowName());
        scheduleRespVO.setEnableStatus(workflowDO.getIsEnabled());
        scheduleRespVO.setScheduleStrategy(workflowDO.getScheduleStrategy());
        scheduleRespVO.setConfig(workflowDO.getScheduleConfig());

        return scheduleRespVO;
    }

    /**
     * 获得一个可以操作(offline)的Workflow对象
     *
     * @param workflowId Worflow ID
     * @return Workflow对象
     */
    private ETLWorkflowDO getOperableWorkflow(Long workflowId) {
        ETLWorkflowDO workflowDO = getWorkflowById(workflowId);
        if (workflowDO.isEnabled()) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.WORKFLOW_ENABLED);
        }
        return workflowDO;
    }

    private ETLWorkflowDO getWorkflowById(Long workflowId) {
        ETLWorkflowDO workflowDO = workflowRepository.getById(workflowId);
        if (workflowDO == null) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.WORKFLOW_NOT_EXIST);
        }
        return workflowDO;
    }
}
