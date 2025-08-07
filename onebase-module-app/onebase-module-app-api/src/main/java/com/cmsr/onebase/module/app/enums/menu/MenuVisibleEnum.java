package com.cmsr.onebase.module.app.enums.menu;

/**
 * @Author：huangjie
 * @Date：2025/7/30 16:32
 */
public enum MenuVisibleEnum {

    YES(Boolean.TRUE, "是"),
    NO(Boolean.FALSE, "否");

    private final Boolean value;

    private final String text;


    MenuVisibleEnum(Boolean value, String text) {
        this.value = value;
        this.text = text;
    }

    public Boolean getValue() {
        return value;
    }
}
