package com.cmsr.onebase.module.etl.build.service.etl;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.build.service.etl.vo.*;
import com.cmsr.onebase.module.etl.core.dal.database.ETLExecutionLogRepository;
import com.cmsr.onebase.module.etl.core.dal.database.ETLScheduleJobRepository;
import com.cmsr.onebase.module.etl.core.dal.database.ETLWorkflowRepository;
import com.cmsr.onebase.module.etl.core.dal.database.ETLWorkflowTableRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLWorkflowDO;
import com.cmsr.onebase.module.etl.core.enums.ETLErrorCodeConstants;
import com.cmsr.onebase.module.etl.core.enums.ScheduleType;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ETLWorkflowServiceImpl implements ETLWorkflowService {

    @Resource
    private ETLWorkflowRepository workflowRepository;

    @Resource
    private ETLWorkflowTableRepository workflowTableRepository;

    @Resource
    private ETLScheduleJobRepository scheduleJobRepository;

    @Resource
    private ETLExecutionLogRepository executionLogRepository;

    @Override
    public PageResult<ETLWorkflowBriefVO> getWorkflowPage(ETLPageReqVO pageReqVO) {
        return null;
    }

    @Override
    public ETLWorkflowDetailVO getWorkflowDetail() {
        return null;
    }

    @Override
    public Long createWorkflow(ETLWorkflowCreateVO createVO) {
        ETLWorkflowDO workflowDO = new ETLWorkflowDO();
        workflowDO.setApplicationId(createVO.getApplicationId());
        workflowDO.setWorkflowName(createVO.getName());
        workflowDO.setConfig(createVO.getConfig());
        workflowDO.setIsEnabled(0);
        workflowDO.setScheduleStrategy(ScheduleType.MANUALLY.getValue());

        ETLWorkflowDO result = workflowRepository.insert(workflowDO);
        return result.getId();
    }

    @Override
    public void updateWorkflow(ETLWorkflowUpdateVO updateVO) {
        Long workflowId = updateVO.getWorkflowId();
        ETLWorkflowDO oldWorkflow = workflowRepository.findById(workflowId);
        if (oldWorkflow == null) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.WORKFLOW_NOT_EXIST);
        }
        if (oldWorkflow.getIsEnabled() == 1) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.WORKFLOW_ENABLED);
        }
        if (!Objects.equals(oldWorkflow.getApplicationId(), updateVO.getApplicationId())) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.DATA_CONFLICT);
        }
        oldWorkflow.setWorkflowName(updateVO.getName());
        oldWorkflow.setConfig(updateVO.getConfig());

        workflowRepository.update(oldWorkflow);
    }

    @Override
    public void deleteWorkflow(Long etlId) {

    }

    @Override
    public void startWorkflowManually(Long etlId) {

    }

    @Override
    public void configScheduleStrategy(ETLScheduleConfigVO scheduleVO) {

    }

    @Override
    public void getWorkflowExecutionLogs(Long etlId) {

    }

    @Override
    public PageResult<Object> previewOutputData(Long id) {
        return null;
    }
}
