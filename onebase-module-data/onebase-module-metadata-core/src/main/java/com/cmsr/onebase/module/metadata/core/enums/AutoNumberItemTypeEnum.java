package com.cmsr.onebase.module.metadata.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 自动编号规则项类型枚举
 *
 * @author bty418
 * @date 2025-09-17
 */
@Getter
@AllArgsConstructor
public enum AutoNumberItemTypeEnum {

    /**
     * 固定文本
     */
    TEXT("TEXT", "固定文本"),

    /**
     * 日期时间
     */
    DATE("DATE", "日期时间"),

    /**
     * 序号
     */
    SEQUENCE("SEQUENCE", "序号"),

    /**
     * 字段引用
     */
    FIELD_REF("FIELD_REF", "字段引用");

    /**
     * 类型编码
     */
    private final String code;

    /**
     * 类型描述
     */
    private final String description;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 枚举值
     */
    public static AutoNumberItemTypeEnum fromCode(String code) {
        for (AutoNumberItemTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown auto number item type: " + code);
    }
}
