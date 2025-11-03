package com.cmsr.onebase.module.etl.build.service.etl;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.ds.client.DolphinSchedulerClient;
import com.cmsr.onebase.module.etl.build.service.etl.vo.*;
import com.cmsr.onebase.module.etl.core.dal.database.*;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLScheduleJobDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLWorkflowDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLWorkflowTableDO;
import com.cmsr.onebase.module.etl.core.enums.ETLErrorCodeConstants;
import com.cmsr.onebase.module.etl.core.enums.ScheduleJobStatus;
import com.cmsr.onebase.module.etl.core.enums.ScheduleType;
import com.cmsr.onebase.module.etl.core.vo.etl.WorkflowPageReqVO;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class ETLWorkflowServiceImpl implements ETLWorkflowService {

    @Value("${onebase.scheduler.etl-project}")
    private Long etlProjectCode;

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
        validateWorkflowNameUnique(createVO.getFlowName(), null);
        ETLWorkflowDO workflowDO = new ETLWorkflowDO();
        Long applicationId = createVO.getApplicationId();
        workflowDO.setApplicationId(applicationId);
        workflowDO.setWorkflowName(createVO.getFlowName());
        workflowDO.setConfig(createVO.getConfig());
        workflowDO.setIsEnabled(0);
        workflowDO.setScheduleStrategy(ScheduleType.MANUALLY.getValue());
        // 创建workflow
        ETLWorkflowDO result = workflowRepository.insert(workflowDO);
        Long workflowId = result.getId();
        // 创建scheduleJob
        ETLScheduleJobDO scheduleJobDO = new ETLScheduleJobDO();
        scheduleJobDO.setApplicationId(applicationId);
        scheduleJobDO.setWorkflowId(workflowId);
        scheduleJobDO.setJobStatus(ScheduleJobStatus.INITIALIZED.getValue());

        return workflowId;
    }

    @Override
    public void updateWorkflow(WorkflowUpdateVO updateVO) {
        Long workflowId = updateVO.getId();
        validateWorkflowNameUnique(updateVO.getFlowName(), workflowId);
        ETLWorkflowDO oldWorkflow = getOperableWorkflow(workflowId);
        if (!Objects.equals(oldWorkflow.getApplicationId(), updateVO.getApplicationId())) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.DATA_CONFLICT);
        }
        oldWorkflow.setWorkflowName(updateVO.getFlowName());
        oldWorkflow.setConfig(updateVO.getConfig());

        workflowRepository.update(oldWorkflow);
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
        workflowRepository.deleteById(workflowId);
    }

    @Override
    public void startWorkflowManually(Long workflowId) {
        // 必须是已启用的ETL
        ETLWorkflowDO workflowDO = getOperableWorkflow(workflowId);
        Long applicationId = workflowDO.getApplicationId();
        ETLScheduleJobDO scheduleJobDO = scheduleJobRepository.findByApplicationIdAndWorkflowId(applicationId, workflowId);
        if (scheduleJobDO == null) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.WORKFLOW_IS_OFFLINE);
        }
        Long jobId = Long.parseLong(scheduleJobDO.getJobId());
        // TODO: 添加记录到数据库？
        //      etl_execution_log <applicationId, workflowId, businessDate, startTime, triggerType, triggerUser, taskStatus>
        dolphinSchedulerClient.runWorkflowManually(etlProjectCode, jobId, null, null);
    }

    @Override
    public void configScheduleStrategy(ScheduleConfigVO scheduleVO) {
        Long workflowId = scheduleVO.getWorkflowId();
        ETLWorkflowDO workflowDO = getWorkflowById(scheduleVO.getWorkflowId());
        // TODO:
    }

    @Override
    public void getWorkflowExecutionLogs(Long workflowId) {
        // TODO:
    }

    @Override
    public void enableWorkflow(Long workflowId) {
        // TODO:
    }

    @Override
    public void disableWorkflow(Long workflowId) {
        ETLWorkflowDO workflowDO = getOperableWorkflow(workflowId);
    }

    private void validateWorkflowNameUnique(String flowName, Long filterId) {
        ETLWorkflowDO workflowDO = workflowRepository.findOneByNameFilterById(flowName, filterId);
        if (workflowDO != null) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.WORKFLOW_NAME_DUPLICATE);
        }
    }

    private ETLWorkflowDO getOperableWorkflow(Long workflowId) {
        ETLWorkflowDO workflowDO = getWorkflowById(workflowId);
        if (workflowDO.isEnabled()) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.WORKFLOW_ENABLED);
        }
        return workflowDO;
    }

    private ETLWorkflowDO getWorkflowById(Long workflowId) {
        ETLWorkflowDO workflowDO = workflowRepository.findById(workflowId);
        if (workflowDO == null) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.WORKFLOW_NOT_EXIST);
        }
        return workflowDO;
    }
}
