package com.cmsr.onebase.module.etl.common.graph;

import lombok.Data;

/**
 * @Author：mty
 * @Date：2026.3.17
 */
@Data
public class PreviewWorkflowGraph {
    private String        nodeId;
    private WorkflowGraph workflow;
}
