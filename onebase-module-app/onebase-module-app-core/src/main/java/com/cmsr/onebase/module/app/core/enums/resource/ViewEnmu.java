package com.cmsr.onebase.module.app.core.enums.resource;

/**
 * 视图模式枚举
 *
 * @author zhoumingji
 * @date 2025/10/10 19:34
 */
public enum ViewEnmu {

    /**
     * 编辑视图
     */
    EDIT("edit", "编辑视图"),

    /**
     * 详情视图
     */
    DETAIL("detail", "详情视图"),

    /**
     * 混合视图
     */
    MIX("mix", "混合视图");

    /**
     * 枚举值
     */
    private final String value;

    /**
     * 显示文本
     */
    private final String text;

    ViewEnmu(String value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 获取枚举值
     *
     * @return 枚举值
     */
    public String getValue() {
        return value;
    }

    /**
     * 获取显示文本
     *
     * @return 显示文本
     */
    public String getText() {
        return text;
    }
}
