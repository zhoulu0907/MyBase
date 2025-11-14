package com.cmsr.onebase.module.etl.executor;

import com.cmsr.onebase.module.etl.executor.provider.QueryProvider;
import com.cmsr.onebase.module.etl.executor.provider.WorkflowProvider;
import com.cmsr.onebase.module.etl.executor.util.DataSourceUtil;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;

/**
 * @Author：huangjie
 * @Date：2025/11/6 11:58
 */
@Slf4j
public class BeanManager implements Closeable {

    private InputArgs inputArgs;

    private WorkflowProvider workflowProvider;

    private DataSource dataSource;

    private QueryProvider queryProvider;

    public BeanManager(InputArgs inputArgs, DataSource dataSource) {
        this.inputArgs = inputArgs;
        this.dataSource = dataSource;
        this.queryProvider = new QueryProvider(dataSource);
        this.workflowProvider = new WorkflowProvider();
        this.workflowProvider.setQueryProvider(queryProvider);
    }


    public BeanManager(InputArgs inputArgs) {
        this.inputArgs = inputArgs;
        this.dataSource = DataSourceUtil.createDataSource(inputArgs);
        this.queryProvider = new QueryProvider(dataSource);
        this.workflowProvider = new WorkflowProvider();
        this.workflowProvider.setQueryProvider(queryProvider);
    }

    public WorkflowProvider getWorkflowDao() {
        return workflowProvider;
    }

    @Override
    public void close() {
        if (dataSource != null && dataSource instanceof Closeable dsCloseable) {
            try {
                dsCloseable.close();
            } catch (IOException e) {
                log.warn("关闭数据源时发生异常: {}", e.getMessage());
            }
        }
    }
}
