package com.cmsr.onebase.module.bpm.runtime.service;

import com.cmsr.onebase.module.bpm.runtime.vo.BpmStartReqVO;
import com.cmsr.onebase.module.bpm.runtime.vo.ExecActButtonReqVO;
import com.cmsr.onebase.module.bpm.runtime.vo.ListActButtonRespVO;

/**
 * 流程执行服务接口
 *
 * @author liyang
 * @date 2025-10-27
 */
public interface BpmExecService {
    /**
     * 获取流程实例的操作按钮
     *
     * @param taskId      任务ID
     * @param businessId  业务ID
     */
     ListActButtonRespVO getActButtons(String taskId, String businessId);

    /**
     * 流程执行
     *
     * @param reqVO 执行操作按钮请求VO
     */
    String start(BpmStartReqVO reqVO);

    /**
     * 流程执行
     *
     * @param reqVO 执行操作按钮请求VO
     */
    String execActButton(ExecActButtonReqVO reqVO);
}
