package com.cmsr.onebase.module.bpm.runtime.service.instance.exec.strategy;

import com.cmsr.onebase.module.bpm.core.dto.node.base.BaseNodeExtDTO;
import com.cmsr.onebase.module.bpm.runtime.vo.ExecTaskReqVO;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.entity.User;

/**
 * execTask 策略接口
 *
 * 不同节点类型实现对应处理逻辑
 *
 * @author liyang
 * @date 2025-11-03
 */
public interface ExecTaskStrategy<T extends BaseNodeExtDTO> {

    /**
     * 当前策略是否支持该业务节点类型
     *
     * @param bizNodeType 节点业务类型编码
     * @return 是否支持
     */
    boolean supports(String bizNodeType);


    /**
     * 执行
     *
     * @param task 任务
     * @param extDTO 节点扩展信息
     * @param reqVO 请求参数
     */
    void execute(User matchedUser, Task task, T extDTO, ExecTaskReqVO reqVO);
}


