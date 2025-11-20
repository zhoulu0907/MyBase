package com.cmsr.onebase.module.etl.executor;

import com.cmsr.onebase.module.etl.common.excute.ExecuteRequest;
import com.cmsr.onebase.module.etl.common.graph.Node;
import com.cmsr.onebase.module.etl.common.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.common.preview.ColumnDefine;
import com.cmsr.onebase.module.etl.common.preview.DataPreview;
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
import java.util.List;

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


    public DataPreview preview() throws Exception {
        for (Node node : workflowGraph.iterateNodes()) {
            doAction(node);
            if (node.getId().equals(executeRequest.getPreviewNodeId())) {
                String sql = "select * from " + node.getId() + " limit 20";
                TableResult tableResult = tableEnv.executeSql(sql);
                return tableResultToDataPreview(tableResult);
            }
        }
        throw new Exception("未找到预览节点");
    }


    private DataPreview tableResultToDataPreview(TableResult tableResult) {
        DataPreview dataPreview = new DataPreview();
        for (Column column : tableResult.getResolvedSchema().getColumns()) {
            ColumnDefine field = new ColumnDefine();
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

    private List<Object> rowToList(List<ColumnDefine> columns, Row row) {
        List<Object> list = new ArrayList<>();
        for (ColumnDefine column : columns) {
            Object value = row.getField(column.getFieldName());
            list.add(value);
        }
        return list;
    }

    @Override
    public void close() {
        beanManager.close();
    }
}
