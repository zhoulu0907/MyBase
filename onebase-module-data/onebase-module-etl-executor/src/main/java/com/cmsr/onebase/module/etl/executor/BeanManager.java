package com.cmsr.onebase.module.etl.executor;

import com.cmsr.onebase.module.etl.executor.provider.WorkflowProvider;
import com.cmsr.onebase.module.etl.executor.util.DataSourceUtil;
import com.zaxxer.hikari.HikariDataSource;

import java.io.Closeable;

/**
 * @Author：huangjie
 * @Date：2025/11/6 11:58
 */
public class BeanManager implements Closeable {

    private InputArgs inputArgs;

    private WorkflowProvider workflowProvider;

    private HikariDataSource dataSource;

    public BeanManager(InputArgs inputArgs) {
        this.inputArgs = inputArgs;
        this.dataSource = DataSourceUtil.createDataSource(inputArgs);
        this.workflowProvider = new WorkflowProvider(dataSource);
    }

    public WorkflowProvider getWorkflowDao() {
        return workflowProvider;
    }

    @Override
    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
