package com.cmsr.onebase.module.etl.executor;

import com.cmsr.onebase.module.etl.executor.action.CreateTableAction;
import com.cmsr.onebase.module.etl.executor.action.ExecuteSqlAction;
import com.cmsr.onebase.module.etl.executor.action.SqlQueryAction;
import com.cmsr.onebase.module.etl.executor.graph.Field;
import com.cmsr.onebase.module.etl.executor.graph.Node;
import com.cmsr.onebase.module.etl.executor.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.executor.provider.WorkflowProvider;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.catalog.Column;
import org.apache.flink.types.Row;
import org.apache.flink.util.CloseableIterator;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/11/6 9:44
 */
public class WorkFlowExecutor {

    private InputArgs inputArgs;

    private WorkflowGraph workflowGraph;

    private TableEnvironment tableEnv;

    public WorkFlowExecutor(InputArgs inputArgs) throws Exception {
        this.inputArgs = inputArgs;
        try (BeanManager beanManager = new BeanManager(inputArgs)) {
            WorkflowProvider workflowProvider = beanManager.getWorkflowDao();
            if (inputArgs.getWorkflowId() != null) {
                workflowGraph = workflowProvider.getWorkflowGraph(inputArgs.getWorkflowId());
            } else {
                workflowGraph = workflowProvider.getWorkflowGraph(inputArgs.getPreviewWorkflow());
            }
        }
        EnvironmentSettings settings =
                EnvironmentSettings.newInstance().inBatchMode().build();
        tableEnv = TableEnvironment.create(settings);
    }

    public void execute() {
        for (Node node : workflowGraph.getNodes()) {
            if (node instanceof CreateTableAction action) {
                action.createTable(tableEnv);
            }
            if (node instanceof SqlQueryAction action) {
                action.sqlQuery(tableEnv);
            }
            if (node instanceof ExecuteSqlAction action) {
                action.executeSql(tableEnv);
            }
        }
    }

    public DataPreview preview() {
        for (Node node : workflowGraph.getNodes()) {
            if (node instanceof CreateTableAction action) {
                action.createTable(tableEnv);
            }
            if (node instanceof ExecuteSqlAction action) {
                action.executeSql(tableEnv);
            }
            if (node.getId().equals(inputArgs.getPreviewNodeId())) {
                Table table = tableEnv.from(node.getId());
                TableResult tableResult = table.execute();
                return tableResultToDataPreview(tableResult);
            }
        }
        throw new RuntimeException("未找到预览节点");
    }

    private DataPreview tableResultToDataPreview(TableResult tableResult) {
        DataPreview dataPreview = new DataPreview();
        for (Column column : tableResult.getResolvedSchema().getColumns()) {
            Field field = new Field();
            field.setFieldName(column.getName());
            field.setFieldType(column.getDataType().getLogicalType().toString());
            dataPreview.getColumns().add(field);
        }
        try (CloseableIterator<Row> collected = tableResult.collect()) {
            while (collected.hasNext()) {
                Row row = collected.next();
                dataPreview.getData().add(rowToList(dataPreview.getColumns(), row));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dataPreview;
    }

    private List<Object> rowToList(List<Field> columns, Row row) {
        List<Object> list = new ArrayList<>();
        for (Field column : columns) {
            Object value = row.getField(column.getFieldName());
            list.add(value);
        }
        return list;
    }

}
