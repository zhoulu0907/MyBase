package com.cmsr.onebase.module.flow.core.enums;

import java.util.Objects;

/**
 * @Author：huangjie
 * @Date：2025/8/29 16:21
 */
public enum FlowEnableStatusEnum {

    DISABLE(0, "禁用"),
    ENABLE(1, "启用");

    private final Integer status;
    private final String name;

    FlowEnableStatusEnum(Integer status, String name) {
        this.status = status;
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public static boolean isDisable(Integer status) {
        return Objects.equals(status, DISABLE.status);
    }

    public static boolean isEnable(Integer status) {
        return Objects.equals(status, ENABLE.status);
    }

    public static boolean changeToEnable(Integer oldStatus, Integer newStatus) {
        return isDisable(oldStatus) && isEnable(newStatus);
    }
}
