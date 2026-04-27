package com.cmsr.onebase.module.app.core.enums.app;

import java.util.Arrays;

/**
 * @Author：huangjie
 * @Date：2025/7/22 15:39
 */
public enum DevelopStatusEnum {

    DEVELOPING(0, "开发中"),

    PUBLISHED(1, "已发布"),

    UPDATED(2, "有更新"),
    ;

    private final Integer value;

    private final String text;


    DevelopStatusEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    public static boolean isPublished(Integer developStatus) {
        return PUBLISHED.value.equals(developStatus);
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
                .map(DevelopStatusEnum::getText)
                .orElseThrow(() -> new IllegalArgumentException("未知开发状态[" + value + "]"));
    }
}