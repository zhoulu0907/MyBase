package com.cmsr.onebase.module.etl.executor;

import com.cmsr.onebase.module.etl.executor.action.CreateTableAction;
import com.cmsr.onebase.module.etl.executor.action.ExecuteSqlAction;
import com.cmsr.onebase.module.etl.executor.graph.Node;
import com.cmsr.onebase.module.etl.executor.graph.WorFlowGraph;
import com.cmsr.onebase.module.etl.executor.provider.WorkflowProvider;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;

/**
 * @Author：huangjie
 * @Date：2025/11/6 9:44
 */
public class WorkFlowExecutor {

    private InputArgs inputArgs;

    private WorFlowGraph worFlowGraph;

    private TableEnvironment tableEnv;

    public WorkFlowExecutor(InputArgs inputArgs) {
        this.inputArgs = inputArgs;
        BeanManager beanManager = new BeanManager(inputArgs);
        WorkflowProvider workflowProvider = beanManager.getWorkflowDao();
        try {
            worFlowGraph = workflowProvider.getWorkflowGraph(inputArgs.getWorkflowId());
        } finally {
            beanManager.close();
        }
        EnvironmentSettings settings =
                EnvironmentSettings.newInstance().inBatchMode().build();
        tableEnv = TableEnvironment.create(settings);
    }

    public void execute() {
        for (Node node : worFlowGraph.getNodes()) {
            if (node instanceof CreateTableAction action) {
                action.createTable(tableEnv);
            }
            if (node instanceof ExecuteSqlAction action) {
                action.executeSql(tableEnv);
            }
        }
    }

}
