package com.cmsr.onebase.module.bpm.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 处理人类型枚举
 *
 * @author liyang
 * @date 2025-10-21
 */
@Getter
@AllArgsConstructor
public enum HandlerTypeEnum {

    /**
     * 指定成员
     * 可以指定某位成员来进行处理
     */
    USER("user", "指定成员"),

    /**
     * 指定角色
     * 指定某个角色例如人事、行政、财务等进行处理
     */
    ROLE("role", "指定角色");

    /**
     * 类型编码
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 枚举
     */
    public static HandlerTypeEnum getByCode(String code) {
        for (HandlerTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
