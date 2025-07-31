package com.cmsr.onebase.module.app.enums.app;

/**
 * @Author：huangjie
 * @Date：2025/7/30 16:32
 */
public enum ApplicationMenuVisible {

    YES(1, "是"),
    NO(0, "否");

    private final Integer value;

    private final String text;

    ApplicationMenuVisible(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    public Integer getValue() {
        return value;
    }
}
