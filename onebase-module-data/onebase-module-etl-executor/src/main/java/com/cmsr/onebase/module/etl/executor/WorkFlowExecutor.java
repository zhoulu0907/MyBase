package com.cmsr.onebase.module.etl.executor;

import com.cmsr.onebase.module.etl.executor.action.CreateTableAction;
import com.cmsr.onebase.module.etl.executor.action.ExecuteSqlAction;
import com.cmsr.onebase.module.etl.executor.graph.Node;
import com.cmsr.onebase.module.etl.executor.graph.WorFlowGraph;
import com.cmsr.onebase.module.etl.executor.provider.WorkflowProvider;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;

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
        try (BeanManager beanManager = new BeanManager(inputArgs)) {
            WorkflowProvider workflowProvider = beanManager.getWorkflowDao();
            if (inputArgs.getWorkflowId() != null) {
                worFlowGraph = workflowProvider.getWorkflowGraph(inputArgs.getWorkflowId());
            } else {
                worFlowGraph = null; //TODO 定义新方法 workflowProvider.getWorkflowGraph(inputArgs.getPreviewWorkflow());
            }
        }
        EnvironmentSettings settings = EnvironmentSettings.newInstance().inBatchMode().build();
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

    public String preview() {
        for (Node node : worFlowGraph.getNodes()) {
            if (node instanceof CreateTableAction action) {
                action.createTable(tableEnv);
            }
            if (node instanceof ExecuteSqlAction action) {
                action.executeSql(tableEnv);
            }
            if (node.getId().equals(inputArgs.getPreviewNodeId())) {
                Table table = tableEnv.from(node.getId());
                TableResult tableResult = table.execute();
                //TODO 返回预览对象
                return null;
            }
        }
        throw new RuntimeException("未找到预览节点");
    }

}
