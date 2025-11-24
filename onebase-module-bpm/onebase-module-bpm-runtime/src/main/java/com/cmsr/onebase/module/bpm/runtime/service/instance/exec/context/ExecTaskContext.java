package com.cmsr.onebase.module.bpm.runtime.service.instance.exec.context;

import com.cmsr.onebase.module.bpm.core.dal.dataobject.BpmFlowAgentInsDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.warm.flow.core.entity.User;

/**
 * 任务执行上下文DTO
 *
 * 封装任务执行时需要的用户和代理人信息
 * 用于支持一个代理人代理多个被代理人的场景
 *
 * @author liyang
 * @date 2025-11-22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecTaskContext {

    /**
     * 匹配的用户（审批人/转办人/委派人）
     */
    private User matchedUser;

    /**
     * 代理人记录（如果当前用户是代理人，则不为null）
     */
    private BpmFlowAgentInsDO agentInsDO;
}

