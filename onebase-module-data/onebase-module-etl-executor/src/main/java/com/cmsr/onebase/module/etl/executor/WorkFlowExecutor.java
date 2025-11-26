package com.cmsr.onebase.module.etl.executor;

import com.cmsr.onebase.module.etl.common.excute.ExecuteRequest;
import com.cmsr.onebase.module.etl.common.graph.Node;
import com.cmsr.onebase.module.etl.common.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.common.preview.ColumnDefine;
import com.cmsr.onebase.module.etl.common.preview.DataPreview;
import com.cmsr.onebase.module.etl.common.preview.PreviewColumn;
import com.cmsr.onebase.module.etl.executor.action.CreateTableAction;
import com.cmsr.onebase.module.etl.executor.action.ExecuteSqlAction;
import com.cmsr.onebase.module.etl.executor.action.SqlQueryAction;
import com.cmsr.onebase.module.etl.executor.provider.QueryProvider;
import com.cmsr.onebase.module.etl.executor.provider.WorkflowProvider;
import com.cmsr.onebase.module.etl.executor.provider.dao.EtlExecutionLog;
import com.cmsr.onebase.module.etl.executor.provider.dao.EtlWorkflow;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.flink.table.api.*;
import org.apache.flink.table.catalog.Column;
import org.apache.flink.types.Row;
import org.apache.flink.util.CloseableIterator;

import javax.sql.DataSource;
import java.io.Closeable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/11/6 9:44
 */
@Slf4j
public class WorkFlowExecutor implements Closeable {

    private ExecuteRequest executeRequest;

    private BeanManager beanManager;

    private EtlWorkflow etlWorkflow;

    private WorkflowGraph workflowGraph;

    private TableEnvironment tableEnv;

    private LocalDateTime execStartTime;

    public WorkFlowExecutor(ExecuteRequest executeRequest) throws Exception {
        this(executeRequest, null);
    }

    public WorkFlowExecutor(ExecuteRequest executeRequest, DataSource dataSource) throws Exception {
        this.execStartTime = LocalDateTime.now();
        this.executeRequest = executeRequest;
        initializeWorkflowGraph(dataSource);
        EnvironmentSettings settings = EnvironmentSettings.newInstance().inBatchMode().build();
        this.tableEnv = TableEnvironment.create(settings);
    }

    private void initializeWorkflowGraph(DataSource dataSource) throws Exception {
        beanManager = dataSource == null ? new BeanManager(executeRequest) : new BeanManager(executeRequest, dataSource);
        WorkflowProvider workflowProvider = beanManager.getWorkflowDao();
        QueryProvider queryProvider = beanManager.getQueryProvider();
        if (executeRequest.getWorkflowId() != null) {
            etlWorkflow = queryProvider.findWorkflowConfig(executeRequest.getWorkflowId());
            checkWorkflow(etlWorkflow);
            workflowGraph = workflowProvider.createWorkflowGraph(etlWorkflow.getConfig());
        } else {
            workflowGraph = workflowProvider.createSubWorkflowGraph(executeRequest.getPreviewWorkflow(), executeRequest.getPreviewNodeId());
        }
    }

    private void checkWorkflow(EtlWorkflow etlWorkflow) throws Exception {
        if (etlWorkflow.getWorkflowId() == null) {
            throw new Exception("未找到工作流");
        }
        if (etlWorkflow.getConfig() == null) {
            throw new Exception("未找到工作流配置");
        }
    }

    public void execute() throws Exception {
        EtlExecutionLog executionLog = new EtlExecutionLog();
        executionLog.setApplicationId(etlWorkflow.getApplicationId());
        executionLog.setWorkflowId(executeRequest.getWorkflowId());
        executionLog.setStartTime(execStartTime);
        try {
            for (Node node : workflowGraph.iterateNodes()) {
                doAction(node);
            }
            executionLog.setTaskStatus("success");
            log.info("execute workflow end");
        } catch (Exception e) {
            executionLog.setTaskStatus("failed");
            executionLog.setErrorMessage(ExceptionUtils.getRootCauseMessage(e));
            log.error("execute workflow error", e);
            throw e;
        } finally {
            executionLog.setEndTime(LocalDateTime.now());
            executionLog.calcDurationTime();
            beanManager.getQueryProvider().insertEtlExecutionLog(executionLog);
        }
    }

    private void doAction(Node node) throws Exception {
        if (node instanceof CreateTableAction action) {
            action.createTable(tableEnv, workflowGraph);
        }
        if (node instanceof SqlQueryAction action) {
            Table table = action.sqlQuery(tableEnv, workflowGraph);
            tableEnv.createTemporaryView(node.getId(), table);
            log.info("sqlQuery table: {}", table.toString());
        }
        if (node instanceof ExecuteSqlAction action) {
            TableResult tableResult = action.executeSql(tableEnv, workflowGraph);
            tableResult.await();
            ResultKind resultKind = tableResult.getResultKind();
            log.info("resultKind: {}", resultKind);
        }
    }


    public DataPreview nodePreview() throws Exception {
        for (Node node : workflowGraph.iterateNodes()) {
            doAction(node);
            if (node.getId().equals(executeRequest.getPreviewNodeId())) {
                String sql = "select * from " + node.getId() + " limit 20";
                TableResult tableResult = tableEnv.executeSql(sql);
                return tableResultToDataPreview(node.getId(), tableResult);
            }
        }
        throw new Exception("未找到预览节点");
    }

    public List<ColumnDefine> nodeColumns() throws Exception {
        for (Node node : workflowGraph.iterateNodes()) {
            doAction(node);
            if (node.getId().equals(executeRequest.getPreviewNodeId())) {
                Table table = tableEnv.from(node.getId());
                return tableToColumns(table);
            }
        }
        throw new Exception("未找到预览节点");
    }

    private DataPreview tableResultToDataPreview(String nodeId, TableResult tableResult) {
        DataPreview dataPreview = new DataPreview();
        for (Column column : tableResult.getResolvedSchema().getColumns()) {
            PreviewColumn previewColumn = new PreviewColumn();
            previewColumn.setTitle(column.getName());
            previewColumn.setDataIndex("_" + column.getName());
            previewColumn.setFieldType(column.getDataType().getLogicalType().getTypeRoot().name());
            dataPreview.getColumns().add(previewColumn);
        }
        try (CloseableIterator<Row> collected = tableResult.collect()) {
            int rowIndex = 1;
            while (collected.hasNext()) {
                Row row = collected.next();
                Map<String, Object> rowMap = rowToList(tableResult.getResolvedSchema().getColumns(), row);
                rowMap.put("key", rowIndex);
                dataPreview.getData().add(rowMap);
                rowIndex++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dataPreview;
    }

    private Map<String, Object> rowToList(List<Column> columns, Row row) {
        Map<String, Object> dataRow = new HashMap<>();
        for (Column column : columns) {
            String rowIndex = column.getName();
            Object value = row.getField(rowIndex);
            dataRow.put("_" + rowIndex, value);
        }
        return dataRow;
    }


    private List<ColumnDefine> tableToColumns(Table table) {
        List<ColumnDefine> columns = new ArrayList<>();
        for (Column column : table.getResolvedSchema().getColumns()) {
            ColumnDefine field = new ColumnDefine();
            field.setFieldName(column.getName());
            field.setDisplayName(column.getName());
            field.setFieldType(column.getDataType().getLogicalType().getTypeRoot().name());
            columns.add(field);
        }
        return columns;
    }


    @Override
    public void close() {
        beanManager.close();
    }
}
