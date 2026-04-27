package com.cmsr.onebase.module.app.core.enums.app;

import java.util.Arrays;

/**
 * @Author：huangjie
 * @Date：2025/7/22 15:39
 */
public enum AppPublishEnum {

    NEVER_PUBLISHED(0, "从未发布"),

    ONCE_PUBLISHED(1, "发布过"),
    ;

    private final Integer value;

    private final String text;


    AppPublishEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    public static boolean isNotPublished(Integer appStatus) {
        return appStatus == null || appStatus.equals(NEVER_PUBLISHED.getValue());
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }


}
