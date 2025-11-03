package com.cmsr.onebase.module.etl.build.service.etl;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.build.service.etl.vo.*;
import com.cmsr.onebase.module.etl.core.vo.etl.WorkflowPageReqVO;

public interface ETLWorkflowService {

    PageResult<WorkflowBriefVO> getWorkflowPage(WorkflowPageReqVO pageReqVO);

    WorkflowDetailVO getWorkflowDetail(Long workflowId);

    Long createWorkflow(WorkflowCreateVO createVO);

    void updateWorkflow(WorkflowUpdateVO updateVO);

    void deleteWorkflow(Long workflowId);

    void startWorkflowManually(Long workflowId);

    void configScheduleStrategy(ScheduleConfigVO scheduleVO);

    void getWorkflowExecutionLogs(Long workflowId);

    void enableWorkflow(Long workflowId);

    void disableWorkflow(Long workflowId);
}
