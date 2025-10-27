package com.cmsr.onebase.module.bpm.runtime.service;

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
     * @param dataId      数据ID
     * @param businessId  业务ID
     */
     ListActButtonRespVO getActButtons(String dataId, String businessId);
}
