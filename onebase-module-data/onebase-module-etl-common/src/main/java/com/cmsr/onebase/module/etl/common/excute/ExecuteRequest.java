package com.cmsr.onebase.module.etl.common.excute;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/11/6 10:11
 */
@Data
public class ExecuteRequest {

    private String jdbcDriverClass;

    private String jdbcUrl;

    private String jdbcUserName;

    private String jdbcPassword;

    private Long applicationId;

    private Long workflowId;

    private String previewWorkflow;

    private String previewNodeId;

}
