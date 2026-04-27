package com.cmsr.onebase.module.flow.context.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author：huangjie
 * @Date：2025/9/16 21:34
 */
public enum OperatorTypeEnum {

    VALUE("value", "值"),
    VARIABLE("variables", "变量"),
    FORMULA("formula", "公式");

    private final String code;
    private final String desc;

    OperatorTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OperatorTypeEnum getByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        for (OperatorTypeEnum value : OperatorTypeEnum.values()) {
            if (value.code.equalsIgnoreCase(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid OperatorTypeEnum code: " + code);
    }
}
