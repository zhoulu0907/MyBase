package com.cmsr.onebase.module.flow.core.enums;

/**
 * @Author：huangjie
 * @Date：2025/12/18 9:54
 */
public enum FlowNodeConfigTypeEnum {

    CODE("code", "代码"),
    FORM("form", "动态表单");

    private String code;
    private String name;

    FlowNodeConfigTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

}
