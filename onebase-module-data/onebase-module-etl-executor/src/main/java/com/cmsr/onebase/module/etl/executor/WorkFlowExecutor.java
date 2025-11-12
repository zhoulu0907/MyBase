package com.cmsr.onebase.module.etl.executor;

import com.cmsr.onebase.module.etl.common.graph.Node;
import com.cmsr.onebase.module.etl.common.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.common.graph.conf.Field;
import com.cmsr.onebase.module.etl.executor.action.CreateTableAction;
import com.cmsr.onebase.module.etl.executor.action.ExecuteSqlAction;
import com.cmsr.onebase.module.etl.executor.action.SqlQueryAction;
import com.cmsr.onebase.module.etl.executor.provider.WorkflowProvider;
import com.cmsr.onebase.module.etl.executor.util.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.table.api.*;
import org.apache.flink.table.catalog.Column;
import org.apache.flink.types.Row;
import org.apache.flink.util.CloseableIterator;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/11/6 9:44
 */
@Slf4j
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
                WorkflowGraph graph = JacksonUtil.readValue(inputArgs.getPreviewWorkflow(), WorkflowGraph.class);
                WorkflowGraph subgraph = graph.subgraph(inputArgs.getPreviewNodeId());
                workflowGraph = workflowProvider.getWorkflowGraph(subgraph);
            }
        }
        EnvironmentSettings settings =
                EnvironmentSettings.newInstance().inBatchMode().build();
        this.tableEnv = TableEnvironment.create(settings);
    }

    public void execute() throws Exception {
        for (Node node : workflowGraph.getNodes()) {
            doAction(node);
        }
        log.info("execute workflow end");
    }

    private void doAction(Node node) throws Exception {
        if (node instanceof CreateTableAction action) {
            action.createTable(tableEnv, workflowGraph);
        }
        if (node instanceof SqlQueryAction action) {
            Table table = action.sqlQuery(tableEnv, workflowGraph);
            log.info("sqlQuery table: {}", table.toString());
        }
        if (node instanceof ExecuteSqlAction action) {
            TableResult tableResult = action.executeSql(tableEnv, workflowGraph);
            tableResult.await();
            ResultKind resultKind = tableResult.getResultKind();
            log.info("resultKind: {}", resultKind);
        }
    }


    public DataPreview preview() throws Exception {
        for (Node node : workflowGraph.getNodes()) {
            doAction(node);
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
