package com.cmsr.onebase.module.bpm.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字段权限类型（前端组件用）
 *
 * @author liyang
 * @date 2025-11-06
 */
@Getter
@AllArgsConstructor
public enum FieldUiShowModeEnum {

    /**
     * 编辑
     */
    WRITE("default", "编辑"),

    /**
     * 只读
     */
    READ("readonly", "只读"),

    /**
     * 隐藏
     * 字段对所有用户隐藏，不显示在表单中
     */
    HIDDEN("hidden", "隐藏");

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
    public static FieldUiShowModeEnum getByCode(String code) {
        for (FieldUiShowModeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
