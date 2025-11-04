package com.cmsr.onebase.module.flow.core.flow;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/10/15 18:16
 */
@Data
public class ExecutorRequest {

    private Long processId;

    private String jobType;

    private String time;

    private String uuid;

}
