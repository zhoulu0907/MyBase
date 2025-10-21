package com.cmsr.onebase.framework.dolphins.dto.workflowinstance.enums;

/**
 * 任务依赖类型枚举
 *
 * 对应 swagger 中的 taskDependType
 *
 * @author matianyu
 * @date 2025-10-18
 */
public enum TaskDependTypeEnum {

    /**
     * 只依赖当前任务
     */
    TASK_ONLY,

    /**
     * 依赖前置任务
     */
    TASK_PRE,

    /**
     * 依赖后置任务
     */
    TASK_POST
}
