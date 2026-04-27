package com.cmsr.onebase.module.flow.core.enums;

/**
 * @Author：huangjie
 * @Date：2025/9/19 15:33
 */
public enum FlowPublishStatusEnum {

    OFFLINE(0, "已下线"),
    ONLINE(1, "已上线");
    private final Integer status;
    private final String name;

    FlowPublishStatusEnum(Integer status, String name) {
        this.status = status;
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }
}
