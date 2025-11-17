package com.cmsr.onebase.module.bpm.runtime.service.detail;

import com.cmsr.onebase.module.bpm.runtime.vo.BpmTaskDetailReqVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmTaskDetailRespVO;

/**
 * 流程详情服务接口
 *
 * @author liyang
 * @date 2025-10-27
 */
public interface BpmDetailService {
    /**
     * 获取流程实例的表单详情
     *
     * @param reqVO 流程详情请求VO
     * @return 流程实例的表单详情VO
     */
    BpmTaskDetailRespVO getFormDetail(BpmTaskDetailReqVO reqVO);
}
