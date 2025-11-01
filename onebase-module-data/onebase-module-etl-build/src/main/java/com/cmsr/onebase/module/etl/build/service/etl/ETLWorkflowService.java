package com.cmsr.onebase.module.etl.build.service.etl;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.build.service.etl.vo.*;

public interface ETLWorkflowService {

    PageResult<ETLWorkflowBriefVO> getWorkflowPage(ETLPageReqVO pageReqVO);

    ETLWorkflowDetailVO getWorkflowDetail();

    Long createWorkflow(ETLWorkflowCreateVO createVO);

    void updateWorkflow(ETLWorkflowUpdateVO updateVO);

    void deleteWorkflow(Long etlId);

    void startWorkflowManually(Long etlId);

    void configScheduleStrategy(ETLScheduleConfigVO scheduleVO);

    void getWorkflowExecutionLogs(Long etlId);

    PageResult<Object> previewOutputData(Long id);
}
