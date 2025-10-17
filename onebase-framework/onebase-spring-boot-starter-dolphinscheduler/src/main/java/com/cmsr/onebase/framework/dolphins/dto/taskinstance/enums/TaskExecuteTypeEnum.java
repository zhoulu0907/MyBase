package com.cmsr.onebase.framework.dolphins.dto.taskinstance.enums;

/**
 * 任务执行类型枚举
 *
 * 对应 swagger 枚举: BATCH/STREAM
 *
 * @author matianyu
 * @date 2025-10-17
 */
public enum TaskExecuteTypeEnum {

    /**
     * 批处理
     */
    BATCH,

    /**
     * 流处理
     */
    STREAM
}
