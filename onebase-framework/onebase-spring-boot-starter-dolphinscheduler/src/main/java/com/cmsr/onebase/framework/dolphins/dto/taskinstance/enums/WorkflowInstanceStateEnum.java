package com.cmsr.onebase.framework.dolphins.dto.taskinstance.enums;

/**
 * 流程实例状态枚举
 *
 * 对应 swagger 中的 WorkflowInstance state
 *
 * @author matianyu
 * @date 2025-10-17
 */
public enum WorkflowInstanceStateEnum {
    /** 提交成功 */
    SUBMITTED_SUCCESS,
    /** 运行中 */
    RUNNING_EXECUTION,
    /** 准备暂停 */
    READY_PAUSE,
    /** 暂停 */
    PAUSE,
    /** 准备停止 */
    READY_STOP,
    /** 停止 */
    STOP,
    /** 失败 */
    FAILURE,
    /** 成功 */
    SUCCESS,
    /** 串行等待 */
    SERIAL_WAIT,
    /** 容错 */
    FAILOVER
}
