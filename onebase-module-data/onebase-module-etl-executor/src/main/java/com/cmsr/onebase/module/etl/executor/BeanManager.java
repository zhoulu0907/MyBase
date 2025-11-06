package com.cmsr.onebase.module.etl.executor;

import com.cmsr.onebase.module.etl.executor.provider.WorkflowProvider;

/**
 * @Author：huangjie
 * @Date：2025/11/6 11:58
 */
public class BeanManager {

    private InputArgs inputArgs;

    private WorkflowProvider workflowProvider;

    public BeanManager(InputArgs inputArgs) {
        this.inputArgs = inputArgs;
    }

    public WorkflowProvider getWorkflowDao() {
        workflowProvider = new WorkflowProvider();
        return workflowProvider;
    }

    public void close() {

    }
}
