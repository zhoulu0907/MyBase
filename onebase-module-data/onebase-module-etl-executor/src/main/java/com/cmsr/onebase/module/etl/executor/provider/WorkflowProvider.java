package com.cmsr.onebase.module.etl.executor.provider;

import com.cmsr.onebase.module.etl.executor.graph.WorkflowGraph;
import lombok.Setter;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;

/**
 * @Author：huangjie
 * @Date：2025/11/6 10:54
 */
@Setter
public class WorkflowProvider {

    private DataSource dataSource;


    private QueryProvider queryProvider;

    public WorkflowProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 从数据库里面获得配置，并且补充完整信息，然后转换为工作流图
     *
     * @param workflowId
     * @return
     */
    public WorkflowGraph getWorkflowGraph(Long workflowId) {
        QueryRunner run = new QueryRunner(dataSource);
        return null;
    }

    public WorkflowGraph getWorkflowGraph(String previewWorkflow) {

        return null;
    }
}
