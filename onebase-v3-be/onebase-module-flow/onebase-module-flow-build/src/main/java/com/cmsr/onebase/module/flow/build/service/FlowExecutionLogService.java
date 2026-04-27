package com.cmsr.onebase.module.flow.build.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.flow.build.vo.ExecutionLogVO;
import com.cmsr.onebase.module.flow.core.vo.PageExecutionLogReqVO;

import java.util.Map;

/**
 * 流程管理服务接口
 */
public interface FlowExecutionLogService {

    PageResult<ExecutionLogVO> pageList(PageExecutionLogReqVO reqVO);

    ExecutionLogVO getDetail(Long id);

    Map<String, Object> statisticTody(Long applicationId);
}
