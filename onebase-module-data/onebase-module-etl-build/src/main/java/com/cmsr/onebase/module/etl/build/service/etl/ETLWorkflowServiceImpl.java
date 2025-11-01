package com.cmsr.onebase.module.etl.build.service.etl;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.ds.client.DolphinSchedulerClient;
import com.cmsr.onebase.module.etl.build.service.etl.vo.*;
import com.cmsr.onebase.module.etl.core.dal.database.*;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLScheduleJobDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLWorkflowDO;
import com.cmsr.onebase.module.etl.core.enums.ETLErrorCodeConstants;
import com.cmsr.onebase.module.etl.core.enums.ScheduleType;
import com.cmsr.onebase.module.etl.core.vo.etl.ETLWorkflowPageReqVO;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class ETLWorkflowServiceImpl implements ETLWorkflowService {

    @Value("${etl-project}")
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
    public PageResult<ETLWorkflowBriefVO> getWorkflowPage(ETLWorkflowPageReqVO pageReqVO) {
        PageResult<ETLWorkflowDO> pageDOs = workflowRepository.getWorkflowPage(pageReqVO);
        List<ETLWorkflowBriefVO> pageVOs = new ArrayList<>();
        for (ETLWorkflowDO workflowDO : pageDOs.getList()) {
            ETLWorkflowBriefVO briefVO = new ETLWorkflowBriefVO();
            Long workflowId = workflowDO.getId();
            briefVO.setId(workflowId);
            briefVO.setName(workflowDO.getWorkflowName());
            briefVO.setEnabled(workflowDO.isEnabled());
            briefVO.setScheduleStrategy(workflowDO.getScheduleStrategy());
            ETLScheduleJobDO scheduleJobDO = scheduleJobRepository.findByWorkflowId(workflowId);
            briefVO.setStatus(scheduleJobDO.getJobStatus());
            briefVO.setLastSuccessTime(scheduleJobDO.getLastSuccessTime());
            Set<Long> relatedSourceTableIds = workflowTableRepository.findSourceTableIdsByWorkflowId(workflowId);
            if (CollectionUtils.isNotEmpty(relatedSourceTableIds)) {
                List<String> sourceTableNames = tableRepository.getNameByIds(relatedSourceTableIds);
                briefVO.setSourceTables(sourceTableNames);
            }
            Long relatedTargetTableId = workflowTableRepository.findTargetTableIdByWorkflowId(workflowId);
            if (relatedTargetTableId != null) {
                String targetTableName = tableRepository.getNameById(relatedTargetTableId);
                briefVO.setTargetTable(targetTableName);
            }
            pageVOs.add(briefVO);
        }
        return new PageResult<>(pageVOs, pageDOs.getTotal());
    }

    @Override
    public ETLWorkflowDetailVO getWorkflowDetail(Long workflowId) {
        ETLWorkflowDO workflowDO = workflowRepository.findById(workflowId);
        if (workflowDO == null) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.WORKFLOW_NOT_EXIST);
        }
        ETLWorkflowDetailVO workflowDetailVO = new ETLWorkflowDetailVO();
        workflowDetailVO.setId(workflowDO.getId());
        workflowDetailVO.setWorkflowName(workflowDO.getWorkflowName());
        workflowDetailVO.setConfig(workflowDO.getConfig());

        return workflowDetailVO;
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
        ETLWorkflowDO oldWorkflow = getOperableWorkflow(workflowId);
        if (!Objects.equals(oldWorkflow.getApplicationId(), updateVO.getApplicationId())) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.DATA_CONFLICT);
        }
        oldWorkflow.setWorkflowName(updateVO.getName());
        oldWorkflow.setConfig(updateVO.getConfig());

        workflowRepository.update(oldWorkflow);
    }

    @Override
    public void deleteWorkflow(Long workflowId) {
        getOperableWorkflow(workflowId);
        executionLogRepository.deleteByWorkflowId(workflowId);
        workflowTableRepository.deleteByWorkflowId(workflowId);
        scheduleJobRepository.deleteByWorkflowId(workflowId);
        workflowRepository.deleteById(workflowId);
    }

    @Override
    public void startWorkflowManually(Long workflowId) {

    }

    @Override
    public void configScheduleStrategy(ETLScheduleConfigVO scheduleVO) {

    }

    @Override
    public void getWorkflowExecutionLogs(Long workflowId) {

    }

    @Override
    public PageResult<Object> previewOutputData(Long workflowId) {
        return null;
    }

    private ETLWorkflowDO getOperableWorkflow(Long workflowId) {
        ETLWorkflowDO workflowDO = workflowRepository.findById(workflowId);
        if (workflowDO == null) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.WORKFLOW_NOT_EXIST);
        }
        if (workflowDO.isEnabled()) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.WORKFLOW_ENABLED);
        }
        return workflowDO;
    }
}
