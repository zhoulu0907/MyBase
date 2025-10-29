package com.cmsr.onebase.module.bpm.runtime.service;

import com.cmsr.onebase.module.bpm.runtime.vo.BpmSubmitReqVO;
import com.cmsr.onebase.module.bpm.runtime.vo.BpmSubmitRespVO;
import com.cmsr.onebase.module.bpm.runtime.vo.ExecTaskReqVO;
import com.cmsr.onebase.module.bpm.runtime.vo.ListActButtonRespVO;

/**
 * 流程执行服务接口
 *
 * @author liyang
 * @date 2025-10-27
 */
public interface BpmInstanceService {
    /**
     * 获取流程实例的操作按钮
     *
     * @param taskId      任务ID
     * @param businessId  业务ID
     */
     ListActButtonRespVO getActButtons(String taskId, String businessId);

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
}
