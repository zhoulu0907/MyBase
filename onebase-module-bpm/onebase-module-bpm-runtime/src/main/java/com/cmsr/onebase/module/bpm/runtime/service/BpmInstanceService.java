package com.cmsr.onebase.module.bpm.runtime.service;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.bpm.core.vo.BpmFormDataPageReqVO;
import com.cmsr.onebase.module.bpm.runtime.vo.*;

import java.util.List;
import java.util.Map;

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
     * @param reqVO 流程详情请求VO
     */
    BpmTaskDetailRespVO getFormDetail(BpmTaskDetailReqVO reqVO);

    /**
     * 流程预测
     *
     * @param reqVO 流程预测请求VO
     * @return 流程图
     */
    List<BpmPredictRespVO.NodeInfo> flowPredict(BpmPredictReqVO reqVO);


    /**
     * 流程预览
     *
     * @param reqVO 流程预览请求VO
     * @return 流程图
     */
    BpmPreviewRespVO flowPreview(BpmPreviewReqVO reqVO);

    /**
     * 获取列表数据
     *
     * @param reqVO 获取列表数据请求VO
     */
    PageResult<Map<String, Object>> formDataPage(BpmFormDataPageReqVO reqVO);

    /**
     * 删除表单数据
     *
     * @param reqVO 删除表单数据请求VO
     */
    void deleteFormData(BpmDeleteFormDataReqVO reqVO);
}
