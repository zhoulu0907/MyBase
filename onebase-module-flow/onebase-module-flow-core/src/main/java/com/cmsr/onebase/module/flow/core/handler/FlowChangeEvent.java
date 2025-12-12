package com.cmsr.onebase.module.flow.core.handler;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/11/4 9:20
 */
@Data
public class FlowChangeEvent {

    public static final String UPDATE_EVENT = "update";
    public static final String DELETE_EVENT = "delete";

    private String eventType;

    private Long applicationId;

    private Long version;

}
