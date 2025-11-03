package com.cmsr.onebase.module.etl.build.service.etl;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.build.service.etl.vo.*;
import com.cmsr.onebase.module.etl.core.vo.etl.ETLWorkflowPageReqVO;

public interface ETLWorkflowService {

    PageResult<ETLWorkflowBriefVO> getWorkflowPage(ETLWorkflowPageReqVO pageReqVO);

    ETLWorkflowDetailVO getWorkflowDetail(Long workflowId);

    Long createWorkflow(ETLWorkflowCreateVO createVO);

    void updateWorkflow(ETLWorkflowUpdateVO updateVO);

    void deleteWorkflow(Long workflowId);

    void startWorkflowManually(Long workflowId);

    void configScheduleStrategy(ETLScheduleConfigVO scheduleVO);

    void getWorkflowExecutionLogs(Long workflowId);

    void enableWorkflow(Long workflowId);

    void disableWorkflow(Long workflowId);
}
