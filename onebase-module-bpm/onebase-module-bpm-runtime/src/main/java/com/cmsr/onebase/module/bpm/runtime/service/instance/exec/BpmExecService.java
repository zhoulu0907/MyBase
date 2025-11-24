package com.cmsr.onebase.module.bpm.runtime.service.instance.exec;

import com.cmsr.onebase.module.bpm.runtime.vo.ExecTaskReqVO;

/**
 * 流程执行服务接口
 *
 * @author liyang
 * @date 2025-11-23
 */
public interface BpmExecService {
    /**
     * 执行流程任务
     *
     * @param reqVO 执行操作按钮请求VO
     */
    void execTask(ExecTaskReqVO reqVO);
}
