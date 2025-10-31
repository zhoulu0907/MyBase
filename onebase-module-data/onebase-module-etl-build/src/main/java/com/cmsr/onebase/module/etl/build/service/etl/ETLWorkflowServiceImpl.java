package com.cmsr.onebase.module.etl.build.service.etl;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.build.service.etl.vo.ETLPageReqVO;
import com.cmsr.onebase.module.etl.build.service.etl.vo.ETLScheduleConfigVO;
import com.cmsr.onebase.module.etl.build.service.etl.vo.ETLWorkflowBriefVO;
import com.cmsr.onebase.module.etl.build.service.etl.vo.ETLWorkflowDetailVO;
import com.cmsr.onebase.module.etl.core.dal.database.ETLExecutionLogRepository;
import com.cmsr.onebase.module.etl.core.dal.database.ETLScheduleJobRepository;
import com.cmsr.onebase.module.etl.core.dal.database.ETLWorkflowRepository;
import com.cmsr.onebase.module.etl.core.dal.database.ETLWorkflowTableRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

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
    public Long createWorkflow(ETLWorkflowDetailVO createVO) {
        return 0L;
    }

    @Override
    public void updateWorkflow(ETLWorkflowDetailVO updateVO) {

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
