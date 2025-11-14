package com.cmsr.onebase.module.etl.executor.provider.dao;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/11/14 14:31
 */
@Data
public class EtlWorkflow {

    private Long applicationId;

    private Long workflowId;

    private String config;

}
