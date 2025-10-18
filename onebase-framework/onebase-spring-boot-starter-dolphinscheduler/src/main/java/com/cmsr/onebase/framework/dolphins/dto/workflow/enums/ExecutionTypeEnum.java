package com.cmsr.onebase.framework.dolphins.dto.workflow.enums;

/**
 * 工作流执行类型枚举
 *
 * @author matianyu
 * @date 2025-01-17
 */
public enum ExecutionTypeEnum {

    /**
     * 并行执行
     */
    PARALLEL,

    /**
     * 串行等待
     */
    SERIAL_WAIT,

    /**
     * 串行丢弃
     */
    SERIAL_DISCARD,

    /**
     * 串行优先
     */
    SERIAL_PRIORITY
}
