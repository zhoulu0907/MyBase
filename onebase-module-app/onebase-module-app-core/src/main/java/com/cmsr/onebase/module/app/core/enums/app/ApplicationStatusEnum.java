package com.cmsr.onebase.module.app.core.enums.app;

import java.util.Arrays;

/**
 * @Author：huangjie
 * @Date：2025/7/22 15:39
 */
public enum ApplicationStatusEnum {

    /**
     * 编辑中
     */
    EDITING(0, "开发中"),

    /**
     * 已发布
     */
    PUBLISHED(1, "已发布"),

    /**
     * 迭代中
     */
    ITERATING(2, "迭代中");

    private final Integer value;

    private final String text;


    ApplicationStatusEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static String getText(Integer value) {
        return Arrays.stream(values())
                .filter(item -> item.value.equals(value))
                .findFirst()
                .map(ApplicationStatusEnum::getText)
                .orElseThrow(() -> new IllegalArgumentException("未知用状态[" + value + "]"));
    }
}
