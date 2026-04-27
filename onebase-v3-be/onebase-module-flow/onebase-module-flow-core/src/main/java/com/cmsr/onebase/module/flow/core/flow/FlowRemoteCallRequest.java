package com.cmsr.onebase.module.flow.core.flow;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/10/15 18:16
 */
@Data
public class FlowRemoteCallRequest {

    public static final String JOB_TYPE_TIME = "time";

    public static final String JOB_TYPE_FIELD = "fld";

    private Long applicationId;

    private Long processId;

    private String processName;

    private String jobType;

}
