package com.cmsr.onebase.framework.dolphins.dto.workflow.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 工作流定义发布状态枚举
 *
 * @author matianyu
 * @date 2025-01-17
 */
public enum ReleaseStateEnum {

    /**
     * 离线状态
     */
    OFFLINE("OFFLINE"),

    /**
     * 在线状态
     */
    ONLINE("ONLINE");

    private final String value;

    ReleaseStateEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ReleaseStateEnum fromValue(String value) {
        for (ReleaseStateEnum e : ReleaseStateEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown ReleaseStateEnum value: " + value);
    }
}
