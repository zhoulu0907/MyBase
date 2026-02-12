package com.cmsr.onebase.module.bpm.core.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author：gaoqi
 * @Date：2026/2/3 11:00
 */
public enum FieldScopeEnum {

    ENTITY("entity", "表单字段 "),
    INSTANCE("instance", "流程实例属性"),
    PRE_NODE("pre_node", "上个审批节点属性");

    private final String code;
    private final String desc;

    FieldScopeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
    public static FieldScopeEnum getByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        for (FieldScopeEnum value : FieldScopeEnum.values()) {
            if (value.code.equalsIgnoreCase(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid FieldScopeEnum code: " + code);
    }
}
