package com.cmsr.onebase.module.bpm.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字段权限类型
 *
 * @author liyang
 * @date 2025-10-21
 */
@Getter
@AllArgsConstructor
public enum FieldPermTypeEnum {

    /**
     * 编辑
     */
    WRITE("write", "编辑"),

    /**
     * 只读
     */
    READ("read", "只读"),

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
    public static FieldPermTypeEnum getByCode(String code) {
        for (FieldPermTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 转换为前端组件用的显示模式枚举
     *
     * @return 前端组件用的显示模式枚举
     */
    public FieldUiShowModeEnum toFieldUiShowModeEnum() {
        return switch (this) {
            case WRITE -> FieldUiShowModeEnum.WRITE;
            case READ -> FieldUiShowModeEnum.READ;
            case HIDDEN -> FieldUiShowModeEnum.HIDDEN;
            default -> null;
        };
    }
}
