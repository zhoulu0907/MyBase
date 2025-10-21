package com.cmsr.onebase.framework.dolphins.dto.workflowinstance.enums;

/**
 * 命令类型枚举
 *
 * 对应 swagger 中的 commandType
 *
 * @author matianyu
 * @date 2025-10-18
 */
public enum CommandTypeEnum {

    /**
     * 启动流程
     */
    START_PROCESS,

    /**
     * 从当前任务开始启动流程
     */
    START_CURRENT_TASK_PROCESS,

    /**
     * 容错恢复流程
     */
    RECOVER_TOLERANCE_FAULT_PROCESS,

    /**
     * 恢复暂停流程
     */
    RECOVER_SUSPENDED_PROCESS,

    /**
     * 从失败任务开始启动流程
     */
    START_FAILURE_TASK_PROCESS,

    /**
     * 补数
     */
    COMPLEMENT_DATA,

    /**
     * 调度器
     */
    SCHEDULER,

    /**
     * 重跑
     */
    REPEAT_RUNNING,

    /**
     * 暂停
     */
    PAUSE,

    /**
     * 停止
     */
    STOP,

    /**
     * 恢复串行等待
     */
    RECOVER_SERIAL_WAIT,

    /**
     * 执行任务
     */
    EXECUTE_TASK,

    /**
     * 动态生成
     */
    DYNAMIC_GENERATION
}
