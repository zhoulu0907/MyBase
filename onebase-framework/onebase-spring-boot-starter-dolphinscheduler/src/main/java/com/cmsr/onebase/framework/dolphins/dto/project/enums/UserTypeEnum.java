package com.cmsr.onebase.framework.dolphins.dto.project.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 用户类型枚举
 *
 * @author matianyu
 * @date 2025-01-17
 */
public enum UserTypeEnum {

    /** 管理员用户 */
    ADMIN_USER("ADMIN_USER"),

    /** 普通用户 */
    GENERAL_USER("GENERAL_USER");

    private final String value;

    UserTypeEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
