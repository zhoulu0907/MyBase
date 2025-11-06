package com.cmsr.onebase.module.etl.executor;

import com.cmsr.onebase.module.etl.executor.provider.WorkflowProvider;
import com.cmsr.onebase.module.etl.executor.util.DataSourceUtil;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @Author：huangjie
 * @Date：2025/11/6 11:58
 */
public class BeanManager {

    private InputArgs inputArgs;

    private WorkflowProvider workflowProvider;

    private HikariDataSource dataSource;

    public BeanManager(InputArgs inputArgs) {
        this.inputArgs = inputArgs;
        this.dataSource = DataSourceUtil.createDataSource(inputArgs);
    }

    public WorkflowProvider getWorkflowDao() {
        if (workflowProvider != null) {
            workflowProvider = new WorkflowProvider(dataSource);
        }
        return workflowProvider;
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
