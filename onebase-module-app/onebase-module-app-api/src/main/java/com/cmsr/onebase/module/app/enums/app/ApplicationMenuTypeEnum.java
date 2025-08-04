package com.cmsr.onebase.module.app.enums.app;

/**
 * @Author：huangjie
 * @Date：2025/7/30 16:21
 */
public enum ApplicationMenuTypeEnum {

    PAGE(1, "页面"),

    GROUP(2, "目录");

    private final Integer value;

    private final String text;

    ApplicationMenuTypeEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
