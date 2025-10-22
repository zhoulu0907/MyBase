package com.cmsr.onebase.module.formula.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 函数类型枚举
 *
 * @author guanshipeng
 * @date 2025-10-21
 */
@Getter
@AllArgsConstructor
public enum FunctionTypeEnum {

    /**
     * 文本类型
     */
    TEXT("text", "文本"),
    /**
     * 数字类型
     */
    NUMBER("number", "数字"),
    /**
     * 逻辑类型
     */
    LOGIC("logic", "逻辑"),
    /**
     * 人员类型
     */
    PERSON("person", "人员"),
    /**
     * 日期类型
     */
    DATE("date", "日期");

    /**
     * 类型编码
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;

}