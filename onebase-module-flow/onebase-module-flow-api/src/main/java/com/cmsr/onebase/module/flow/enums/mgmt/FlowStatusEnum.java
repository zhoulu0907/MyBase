package com.cmsr.onebase.module.flow.enums.mgmt;

/**
 * @Author：huangjie
 * @Date：2025/8/29 16:21
 */
public enum FlowStatusEnum {

    DISABLE(0, "禁用"),
    ENABLE(1, "启用");

    private final Integer status;
    private final String name;

    FlowStatusEnum(Integer status, String name) {
        this.status = status;
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public static String getName(Integer status) {
        for (FlowStatusEnum value : FlowStatusEnum.values()) {
            if (value.status.equals(status)) {
                return value.name;
            }
        }
        return null;
    }
}
