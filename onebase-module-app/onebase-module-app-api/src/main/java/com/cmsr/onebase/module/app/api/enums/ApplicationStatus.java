package com.cmsr.onebase.module.app.api.enums;

import java.util.Arrays;

/**
 * @Author：huangjie
 * @Date：2025/7/22 15:39
 */
public enum ApplicationStatus {

    /**
     * 编辑中
     */
    EDITING(0, "开发中"),

    /**
     * 已发布
     */
    PUBLISHED(1, "已发布"),
    ;

    private final Integer value;

    private final String name;


    ApplicationStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static String getStatusName(Integer value) {
        return Arrays.stream(values())
                .filter(item -> item.value.equals(value))
                .findFirst()
                .map(ApplicationStatus::getName)
                .orElseThrow(() -> new IllegalArgumentException("未知用状态[" + value + "]"));
    }
}
