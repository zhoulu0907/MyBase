package com.cmsr.onebase.framework.dolphins.dto.workflow.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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
    PARALLEL("PARALLEL"),

    /**
     * 串行等待
     */
    SERIAL_WAIT("SERIAL_WAIT"),

    /**
     * 串行丢弃
     */
    SERIAL_DISCARD("SERIAL_DISCARD"),

    /**
     * 串行优先
     */
    SERIAL_PRIORITY("SERIAL_PRIORITY");

    private final String value;

    ExecutionTypeEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ExecutionTypeEnum fromValue(String value) {
        for (ExecutionTypeEnum e : ExecutionTypeEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown ExecutionTypeEnum value: " + value);
    }
}
