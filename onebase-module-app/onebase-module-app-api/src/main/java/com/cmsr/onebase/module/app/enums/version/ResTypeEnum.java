package com.cmsr.onebase.module.app.enums.version;


/**
 * @Author：huangjie
 * @Date：2025/8/12 11:20
 */
public enum ResTypeEnum {

    MENU("menu", "菜单"),
    PAGE_SET("pageSet", "页面集"),
    PAGE_SET_LABEL("pageSetLabel", "页面集标签"),
    PAGE_SET_PAGE("pageSetPage", "页面集页面"),
    PAGE("page", "页面");


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
