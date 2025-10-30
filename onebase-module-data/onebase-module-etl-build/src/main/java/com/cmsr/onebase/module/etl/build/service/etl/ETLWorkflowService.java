package com.cmsr.onebase.module.etl.build.service.etl;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.etl.build.service.etl.vo.ETLPageReqVO;
import com.cmsr.onebase.module.etl.build.service.etl.vo.ETLScheduleVO;
import com.cmsr.onebase.module.etl.build.service.etl.vo.ETLWorkflowBriefVO;
import com.cmsr.onebase.module.etl.build.service.etl.vo.ETLWorkflowDetailVO;

public interface ETLWorkflowService {

    PageResult<ETLWorkflowBriefVO> getWorkflowPage(ETLPageReqVO pageReqVO);

    ETLWorkflowDetailVO getWorkflowDetail();

    Long createWorkflow(ETLWorkflowDetailVO createVO);

    void updateWorkflow(ETLWorkflowDetailVO updateVO);

    void deleteWorkflow(Long etlId);

    void startWorkflowManually(Long etlId);

    void configScheduleStrategy(ETLScheduleVO scheduleVO);

    void getWorkflowExecutionLogs(Long etlId);

    PageResult<Object> previewOutputData(Long id);
}
