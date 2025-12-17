package com.cmsr.onebase.module.app.core.enums.menu;

/**
 * @Author：huangjie
 * @Date：2025/7/30 16:32
 */
public enum MenuVisibleEnum {

    YES(1, "是"),
    NO(0, "否");

    private final Integer value;

    private final String text;


    MenuVisibleEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

}
