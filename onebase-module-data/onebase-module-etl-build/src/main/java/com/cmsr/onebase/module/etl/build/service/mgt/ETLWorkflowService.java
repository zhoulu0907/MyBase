package com.cmsr.onebase.module.etl.build.service.mgt;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.build.service.mgt.vo.*;
import com.cmsr.onebase.module.etl.build.service.mgt.vo.ExecutionLogVO;
import com.cmsr.onebase.module.etl.common.preview.DataPreview;
import com.cmsr.onebase.module.etl.core.vo.WorkflowPageReqVO;

public interface ETLWorkflowService {

    // ETL流程相关
    PageResult<WorkflowBriefVO> getWorkflowPage(WorkflowPageReqVO pageReqVO);

    WorkflowDetailVO getWorkflowDetail(Long workflowId);

    Long createWorkflow(WorkflowCreateVO createVO);

    void updateWorkflow(WorkflowUpdateVO updateVO);

    void deleteWorkflow(Long workflowId);

    void enableWorkflow(Long workflowId);

    void disableWorkflow(Long workflowId);

    // ETL调度相关
    ScheduleRespVO getWorkflowSchedule(Long workflowId);

    void configScheduleStrategy(ScheduleConfigVO scheduleVO);

    // ETL实例相关
    void startWorkflowManually(Long workflowId);

    PageResult<ExecutionLogVO> getWorkflowExecutionLogs(Long applicationId, Long workflowId, Integer pageNo, Integer pageSize);

    DataPreview previewWorkflow(PreviewReqVO previewReqVO);

}
