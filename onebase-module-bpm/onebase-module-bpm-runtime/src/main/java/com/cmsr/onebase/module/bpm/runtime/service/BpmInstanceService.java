package com.cmsr.onebase.module.bpm.runtime.service;

import com.cmsr.onebase.module.bpm.runtime.vo.*;

import java.util.List;

/**
 * 流程执行服务接口
 *
 * @author liyang
 * @date 2025-10-27
 */
public interface BpmInstanceService {
    /**
     * 发起流程实例
     *
     * @param reqVO 开启流程实例请求VO
     */
    BpmSubmitRespVO submit(BpmSubmitReqVO reqVO);

    /**
     * 执行流程任务
     *
     * @param reqVO 执行操作按钮请求VO
     */
    void execTask(ExecTaskReqVO reqVO);

    /**
     * 获取流程实例的操作记录
     *
     * @param instanceId 流程实例ID
     */
    List<BpmOperatorRecordRespVO.OperatorRecord> getOperatorRecord(Long instanceId);

    /**
     * 获取流程实例的表单详情
     *
     * @param instanceId 流程实例ID
     */
    BpmFlowTaskDetailVO getFormDetail(Long instanceId);

    /**
     * 流程预测
     *
     * @param reqVO 流程预测请求VO
     * @return 流程图
     */
    List<BpmPredictRespVO.NodeInfo> flowPredict(BpmPredictReqVO reqVO);
}
