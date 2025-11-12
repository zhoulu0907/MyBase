package com.cmsr.onebase.module.etl.executor;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/11/6 10:11
 */
@Data
public class InputArgs {

    private String jdbcDriverClass;

    private String jdbcUrl;

    private String jdbcUserName;

    private String jdbcPassword;

    private Long workflowId;

    private String previewWorkflow;

    private String previewNodeId;

}
