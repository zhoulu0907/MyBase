package com.cmsr.onebase.module.etl.build.service.mgt;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.common.util.string.UuidUtils;
import com.cmsr.onebase.framework.ds.client.DolphinSchedulerClient;
import com.cmsr.onebase.framework.ds.model.schedule.sub.Schedule;
import com.cmsr.onebase.framework.ds.model.task.def.HttpTask;
import com.cmsr.onebase.jose.JoseGenerator;
import com.cmsr.onebase.module.etl.build.util.Cron;
import com.cmsr.onebase.module.etl.build.vo.mgt.*;
import com.cmsr.onebase.module.etl.common.excute.ExecuteRequest;
import com.cmsr.onebase.module.etl.common.graph.Node;
import com.cmsr.onebase.module.etl.common.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.common.graph.conf.JdbcInputConfig;
import com.cmsr.onebase.module.etl.common.graph.conf.JdbcOutputConfig;
import com.cmsr.onebase.module.etl.common.preview.ColumnDefine;
import com.cmsr.onebase.module.etl.common.preview.DataPreview;
import com.cmsr.onebase.module.etl.core.dal.database.*;
import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlExecutionLogDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlScheduleJobDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlWorkflowDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlWorkflowTableDO;
import com.cmsr.onebase.module.etl.core.enums.EtlConstants;
import com.cmsr.onebase.module.etl.core.enums.EtlErrorCodeConstants;
import com.cmsr.onebase.module.etl.core.enums.ScheduleJobStatus;
import com.cmsr.onebase.module.etl.core.enums.ScheduleType;
import com.cmsr.onebase.module.etl.core.vo.WorkflowBriefVO;
import com.cmsr.onebase.module.etl.core.vo.WorkflowPageReqVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.github.f4b6a3.uuid.UuidCreator;
import com.mybatisflex.core.row.Db;
import jakarta.annotation.Resource;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class EtlWorkflowServiceImpl implements EtlWorkflowService {

    @Value("${onebase.scheduler.etl-project}")
    private Long etlProjectCode;

    @Value("${onebase.flink.address}")
    private String flinkServerUrl;

    @Resource
    private DolphinSchedulerClient dolphinSchedulerClient;

    @Resource
    private EtlWorkflowRepository workflowRepository;

    @Resource
    private EtlWorkflowTableRepository workflowTableRepository;

    @Resource
    private EtlScheduleJobRepository scheduleJobRepository;

    @Resource
    private EtlExecutionLogRepository executionLogRepository;

    @Resource
    private EtlTableRepository tableRepository;

    @Override
    public PageResult<WorkflowBriefVO> getWorkflowPage(WorkflowPageReqVO pageReqVO) {
        PageResult<WorkflowBriefVO> pageDOs = workflowRepository.getWorkflowPage(pageReqVO);
        for (WorkflowBriefVO workflowVO : pageDOs.getList()) {
            String workflowUuid = workflowVO.getFlowUuid();
            Set<String> relatedSourceTableIds = workflowTableRepository.findSourceTablesByWorkflow(workflowUuid);
            if (CollectionUtils.isNotEmpty(relatedSourceTableIds)) {
                List<String> sourceTableNames = tableRepository.getNameByIds(relatedSourceTableIds);
                workflowVO.setSourceTables(sourceTableNames);
            } else {
                workflowVO.setSourceTables(null);
            }
            String targetTable = workflowTableRepository.findTargetTableByWorkflow(workflowUuid);
            if (StringUtils.isNotBlank(targetTable)) {
                String targetTableName = tableRepository.getNameByUuid(targetTable);
                workflowVO.setTargetTable(targetTableName);
            }
        }
        return new PageResult<>(pageDOs.getList(), pageDOs.getTotal());
    }

    @Override
    public WorkflowDetailVO getWorkflowDetail(Long workflowId) {
        EtlWorkflowDO workflowDO = getWorkflowById(workflowId);
        WorkflowDetailVO workflowDetailVO = new WorkflowDetailVO();
        workflowDetailVO.setId(workflowDO.getId());
        workflowDetailVO.setFlowUuid(workflowDO.getWorkflowUuid());
        workflowDetailVO.setFlowName(workflowDO.getWorkflowName());
        workflowDetailVO.setConfig(workflowDO.getConfig());

        return workflowDetailVO;
    }

    @Override
    public Long createWorkflow(WorkflowCreateVO createVO) {
        EtlWorkflowDO workflowDO = new EtlWorkflowDO();
        Long applicationId = createVO.getApplicationId();
        workflowDO.setApplicationId(applicationId);
        workflowDO.setWorkflowUuid(UuidUtils.getUuid());
        workflowDO.setWorkflowName(createVO.getFlowName());
        workflowDO.setDeclaration(createVO.getDeclaration());
        workflowDO.setConfig(JsonUtils.toJsonString(createVO.getConfig()));
        workflowDO.setIsEnabled(0);
        workflowDO.setScheduleStrategy(ScheduleType.MANUALLY.getValue());
        // 创建workflow
        workflowRepository.save(workflowDO);
        Long workflowId = workflowDO.getId();
        String workflowUuid = workflowDO.getWorkflowUuid();
        // 创建scheduleJob
        EtlScheduleJobDO scheduleJobDO = new EtlScheduleJobDO();
        scheduleJobDO.setApplicationId(applicationId);
        scheduleJobDO.setWorkflowUuid(workflowUuid);
        scheduleJobDO.setJobStatus(ScheduleJobStatus.INITIALIZED.getValue());
        scheduleJobRepository.save(scheduleJobDO);
        // 解析workflow相关的表信息
        updateWorkflowTableRelations(workflowDO);
        return workflowId;
    }

    @Override
    public void updateWorkflow(WorkflowUpdateVO updateVO) {
        Long workflowId = updateVO.getId();
        EtlWorkflowDO oldWorkflow = getOperableWorkflow(workflowId);
        if (!Objects.equals(oldWorkflow.getApplicationId(), updateVO.getApplicationId())) {
            throw new IllegalArgumentException("应用ID不一致");
        }
        oldWorkflow.setWorkflowName(updateVO.getFlowName());
        oldWorkflow.setDeclaration(updateVO.getDeclaration());
        oldWorkflow.setConfig(JsonUtils.toJsonString(updateVO.getConfig()));
        updateWorkflowTableRelations(oldWorkflow);
        workflowRepository.updateById(oldWorkflow);
    }

    private void updateWorkflowTableRelations(EtlWorkflowDO workflowDO) {
        try {
            Long applicationId = workflowDO.getApplicationId();
            String workflowUuid = workflowDO.getWorkflowUuid();
            workflowTableRepository.deleteByWorkflow(workflowUuid);
            List<EtlWorkflowTableDO> workflowTableDOList = new ArrayList<>();
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
                    EtlWorkflowTableDO workflowTableRel = new EtlWorkflowTableDO();
                    workflowTableRel.setWorkflowUuid(workflowUuid);
                    workflowTableRel.setApplicationId(applicationId);
                    workflowTableRel.setRelation(EtlConstants.WORKFLOW_TABLE_RELATION_SOURCE);
                    workflowTableRel.setDatasourceUuid(inputConfig.getDatasourceUuid());
                    workflowTableRel.setTableUuid(inputConfig.getTableUuid());
                    workflowTableDOList.add(workflowTableRel);
                } catch (Exception ignored) {
                }
            }
            for (Node endNode : endNodes) {
                try {
                    JdbcOutputConfig outputConfig = (JdbcOutputConfig) endNode.getConfig();
                    EtlWorkflowTableDO workflowTableRel = new EtlWorkflowTableDO();
                    workflowTableRel.setWorkflowUuid(workflowUuid);
                    workflowTableRel.setApplicationId(applicationId);
                    workflowTableRel.setRelation(EtlConstants.WORKFLOW_TABLE_RELATION_TARGET);
                    workflowTableRel.setDatasourceUuid(outputConfig.getDatasourceUuid());
                    workflowTableRel.setTableUuid(outputConfig.getTableUuid());
                    workflowTableDOList.add(workflowTableRel);
                } catch (Exception ignored) {
                }
            }
            workflowTableRepository.saveBatch(workflowTableDOList);
        } catch (Exception ignored) {
            log.warn("保存表关系结构异常, {}", workflowDO.getConfig());
        }
    }

    @Override
    public void deleteWorkflow(Long workflowId) {
        EtlWorkflowDO operableWorkflow = getOperableWorkflow(workflowId);
        String workflowUuid = operableWorkflow.getWorkflowUuid();
        Db.tx(() -> {
            executionLogRepository.deleteByWorkflow(workflowId);
            workflowTableRepository.deleteByWorkflow(workflowUuid);
            scheduleJobRepository.deleteByWorkflow(workflowUuid);
            workflowRepository.removeById(workflowId);
            return true;
        });
    }

    @Override
    public void enableWorkflow(Long workflowId) {
        EtlWorkflowDO workflowDO = getOperableWorkflow(workflowId);
        syncEnableStatus(workflowDO);
    }

    private void syncEnableStatus(EtlWorkflowDO workflowDO) {
        Long workflowId = workflowDO.getId();
        String workflowUuid = workflowDO.getWorkflowUuid();
        ScheduleType scheduleType = ScheduleType.of(workflowDO.getScheduleStrategy());
        EtlScheduleJobDO scheduleJobDO = scheduleJobRepository.findByApplicationAndWorkflow(workflowDO.getApplicationId(), workflowUuid);
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
                        throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.ILLEGAL_SCHEDULE_TYPE);
                }
                if (crontab == null) {
                    crontab = cron.toCron();
                }

                schedule.setCrontab(crontab);

                dolphinSchedulerClient.onlineWorkflowWithSchedule(etlProjectCode, jobId, schedule);
            }
            default -> throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.ILLEGAL_SCHEDULE_TYPE);
        }
        workflowDO.setIsEnabled(1);
        workflowRepository.updateById(workflowDO);
    }

    @Override
    public void disableWorkflow(Long workflowId) {
        EtlWorkflowDO workflowDO = getWorkflowById(workflowId);
        if (!BooleanUtils.toBoolean(workflowDO.getIsEnabled())) {
            throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.WORKFLOW_DISABLED);
        }
        String workflowUuid = workflowDO.getWorkflowUuid();
        EtlScheduleJobDO scheduleJobDO = scheduleJobRepository.findByApplicationAndWorkflow(workflowDO.getApplicationId(), workflowUuid);
        if (scheduleJobDO == null) {
            throw new IllegalStateException("调度信息不存在");
        }
        Long jobId = Long.parseLong(scheduleJobDO.getJobId());
        dolphinSchedulerClient.purgeWorkflow(etlProjectCode, jobId);
        workflowDO.setIsEnabled(0);
        workflowRepository.updateById(workflowDO);

        scheduleJobRepository.removeJobId(workflowUuid);
    }

    @Override
    public void configScheduleStrategy(ScheduleConfigVO scheduleVO) {
        EtlWorkflowDO workflowDO = getWorkflowById(scheduleVO.getWorkflowId());
        workflowDO.setWorkflowName(scheduleVO.getFlowName());
        workflowDO.setScheduleStrategy(scheduleVO.getScheduleStrategy().getValue());
        workflowDO.setScheduleConfig(JsonUtils.toJsonString(scheduleVO.getConfig()));
        workflowDO.setIsEnabled(scheduleVO.getEnableStatus());
        workflowRepository.updateById(workflowDO);
        if (BooleanUtils.toBoolean(workflowDO.getIsEnabled())) {
            syncEnableStatus(workflowDO);
        }
    }

    @Override
    public void startWorkflowManually(Long workflowId) {
        // 必须是已启用的ETL
        EtlWorkflowDO workflowDO = getWorkflowById(workflowId);
        if (workflowDO.getIsEnabled() == 0) {
            throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.WORKFLOW_DISABLED);
        }
        Long applicationId = workflowDO.getApplicationId();
        String workflowUuid = workflowDO.getWorkflowUuid();
        EtlScheduleJobDO scheduleJobDO = scheduleJobRepository.findByApplicationAndWorkflow(applicationId, workflowUuid);
        if (scheduleJobDO == null) {
            throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.WORKFLOW_ALREADY_OFFLINE);
        }
        Long jobId = Long.parseLong(scheduleJobDO.getJobId());
        dolphinSchedulerClient.runWorkflowManually(etlProjectCode, jobId, null, null);
    }

    @Override
    public PageResult<ExecutionLogVO> getWorkflowExecutionLogs(Long applicationId, Long workflowId, Integer pageNo, Integer pageSize) {
        PageResult<EtlExecutionLogDO> executionLogResult = executionLogRepository.queryPage(applicationId,
                workflowId, pageNo, pageSize);
        List<ExecutionLogVO> logVOList = new ArrayList<>();
        for (EtlExecutionLogDO logDO : executionLogResult.getList()) {
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
        EtlWorkflowDO workflowDO = getWorkflowById(workflowId);

        ScheduleRespVO scheduleRespVO = new ScheduleRespVO();
        scheduleRespVO.setApplicationId(workflowDO.getApplicationId());
        scheduleRespVO.setFlowUuid(workflowDO.getWorkflowUuid());
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
    private EtlWorkflowDO getOperableWorkflow(Long workflowId) {
        EtlWorkflowDO workflowDO = getWorkflowById(workflowId);
        if (BooleanUtils.toBoolean(workflowDO.getIsEnabled())) {
            throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.WORKFLOW_ENABLED);
        }
        return workflowDO;
    }

    private EtlWorkflowDO getWorkflowById(Long workflowId) {
        EtlWorkflowDO workflowDO = workflowRepository.getById(workflowId);
        if (workflowDO == null) {
            throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.WORKFLOW_NOT_EXIST);
        }
        return workflowDO;
    }
}
