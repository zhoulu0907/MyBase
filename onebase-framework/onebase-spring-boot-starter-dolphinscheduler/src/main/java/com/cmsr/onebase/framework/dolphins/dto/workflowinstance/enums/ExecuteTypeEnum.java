package com.cmsr.onebase.framework.dolphins.dto.workflowinstance.enums;

/**
 * 工作流实例执行类型枚举
 *
 * @author matianyu
 * @date 2025-10-17
 */
public enum ExecuteTypeEnum {

    /**
     * 无操作
     */
    NONE,

    /**
     * 重跑
     */
    REPEAT_RUNNING,

    /**
     * 恢复暂停的流程
     */
    RECOVER_SUSPENDED_PROCESS,

    /**
     * 从失败任务开始执行
     */
    START_FAILURE_TASK_PROCESS,

    /**
     * 停止
     */
    STOP,

    /**
     * 暂停
     */
    PAUSE,

    /**
     * 执行任务
     */
    EXECUTE_TASK
}
