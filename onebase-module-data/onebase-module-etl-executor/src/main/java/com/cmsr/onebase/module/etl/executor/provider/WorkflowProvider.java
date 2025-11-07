package com.cmsr.onebase.module.etl.executor.provider;

import com.cmsr.onebase.module.etl.executor.graph.WorFlowGraph;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;

/**
 * @Author：huangjie
 * @Date：2025/11/6 10:54
 */
public class WorkflowProvider {

    private DataSource dataSource;

    public WorkflowProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 从数据库里面或得配置，并且补充完整信息，然后转换为工作流图
     *
     * @param workflowId
     * @return
     */
    public WorFlowGraph getWorkflowGraph(Long workflowId) {
        QueryRunner run = new QueryRunner(dataSource);
        return null;
    }
}
