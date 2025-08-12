package com.cmsr.onebase.module.app.enums.version;

/**
 * @Author：huangjie
 * @Date：2025/8/12 11:20
 */
public enum ResTypeEnum {

    MENU("menu", "菜单"),
    ;
    private final String value;
    private final String desc;

    ResTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }
}
