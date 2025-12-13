package com.cmsr.onebase.module.app.core.enums.app;

import java.util.Arrays;

/**
 * @Author：huangjie
 * @Date：2025/7/22 15:39
 */
public enum AppStatusEnum {

    OFFLINE(0, "未上线"),

    ONLINE(1, "已上线"),
    ;

    private final Integer value;

    private final String text;


    AppStatusEnum(Integer value, String text) {
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
                .map(AppStatusEnum::getText)
                .orElseThrow(() -> new IllegalArgumentException("未知用状态[" + value + "]"));
    }
}
