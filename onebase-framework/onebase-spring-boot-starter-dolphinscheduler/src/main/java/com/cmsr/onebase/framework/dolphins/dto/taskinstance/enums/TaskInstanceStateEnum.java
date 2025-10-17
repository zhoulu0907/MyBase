package com.cmsr.onebase.framework.dolphins.dto.taskinstance.enums;

/**
 * 任务实例状态枚举
 *
 * 对应 swagger 中的任务实例状态
 *
 * @author matianyu
 * @date 2025-10-17
 */
public enum TaskInstanceStateEnum {
    /** 提交成功 */
    SUBMITTED_SUCCESS,
    /** 运行中 */
    RUNNING_EXECUTION,
    /** 暂停 */
    PAUSE,
    /** 失败 */
    FAILURE,
    /** 成功 */
    SUCCESS,
    /** 需要容错 */
    NEED_FAULT_TOLERANCE,
    /** 终止 */
    KILL,
    /** 延迟执行 */
    DELAY_EXECUTION,
    /** 强制成功 */
    FORCED_SUCCESS,
    /** 已分发 */
    DISPATCH
}
