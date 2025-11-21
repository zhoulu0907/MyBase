package com.cmsr.onebase.module.bpm.runtime.service.predict;

import com.cmsr.onebase.module.bpm.runtime.vo.*;

import java.util.List;

/**
 * 流程预测服务
 *
 * @author liyang
 * @date 2025-11-21
 */
public interface BpmPredictService {
    /**
     * 流程预测
     *
     * @param reqVO 流程预测请求VO
     * @return 流程图
     */
    List<BpmPredictRespVO.NodeInfo> flowPredict(BpmPredictReqVO reqVO);
}
