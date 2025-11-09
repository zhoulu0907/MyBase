package com.cmsr.onebase.module.etl.executor.provider;

import com.cmsr.onebase.module.etl.executor.graph.Field;
import com.cmsr.onebase.module.etl.executor.graph.Node;
import com.cmsr.onebase.module.etl.executor.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.executor.graph.conf.JdbcConfig;
import com.cmsr.onebase.module.etl.executor.graph.conf.JdbcInputConfig;
import com.cmsr.onebase.module.etl.executor.graph.conf.JdbcOutputConfig;
import com.cmsr.onebase.module.etl.executor.graph.conf.OutputField;
import com.cmsr.onebase.module.etl.executor.graph.node.JdbcInputNode;
import com.cmsr.onebase.module.etl.executor.graph.node.JdbcOutputNode;
import com.cmsr.onebase.module.etl.executor.provider.dao.EtlColumn;
import com.cmsr.onebase.module.etl.executor.provider.dao.EtlTableColumn;
import com.cmsr.onebase.module.etl.executor.util.GsonUtil;
import lombok.Setter;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public WorkflowGraph getWorkflowGraph(Long workflowId) throws Exception {
        String workflowGraphJson = queryProvider.findWorkflowConfig(workflowId);
        WorkflowGraph workflowGraph = GsonUtil.GSON.fromJson(workflowGraphJson, WorkflowGraph.class);
        complementGraphInfomation(workflowGraph);
        return workflowGraph;
    }

    public WorkflowGraph getWorkflowGraph(String previewWorkflow) throws Exception {
        WorkflowGraph workflowGraph = GsonUtil.GSON.fromJson(previewWorkflow, WorkflowGraph.class);
        complementGraphInfomation(workflowGraph);
        return workflowGraph;
    }

    private void complementGraphInfomation(WorkflowGraph workflowGraph) throws Exception {
        for (Node node : workflowGraph.getNodes()) {
            if (node instanceof JdbcInputNode jdbcInputNode) {
                complementJdbcInputInformation(jdbcInputNode);
            } else if (node instanceof JdbcOutputNode jdbcOutputNode) {
                complementJdbcOutputInformation(jdbcOutputNode);
            }
        }
    }

    private void complementJdbcOutputInformation(JdbcOutputNode node) throws Exception {
        JdbcOutputConfig outputConfig = node.getConfig();
        Long datasourceId = outputConfig.getDatasourceId();
        String jdbcConfigJson = queryProvider.findConnectPropertiesById(datasourceId);
        JdbcConfig jdbcConfig = GsonUtil.GSON.fromJson(jdbcConfigJson, JdbcConfig.class);
        Long tableId = outputConfig.getTableId();
        List<String> tableQuery = queryProvider.findTableById(datasourceId, tableId);
        String tableName = tableQuery.get(0);
        jdbcConfig.setTableName(tableName);
        outputConfig.setJdbcConfig(jdbcConfig);

        EtlTableColumn etlTableColumn = GsonUtil.GSON.fromJson(tableQuery.get(1), EtlTableColumn.class);
        Map<String, EtlColumn> columnMap = etlTableColumn.getColumns()
                .stream().collect(Collectors.toMap(EtlColumn::getId, col -> col));
        List<Field> targetFieldList = new ArrayList<>();
        for (OutputField field : outputConfig.getFields()) {
            Field targetField = new Field();
            String fieldId = field.getTargetFieldId();
            targetField.setFieldId(fieldId);
            if (!columnMap.containsKey(fieldId)) {
                throw new IllegalArgumentException(fieldId + " not exists");
            }
            EtlColumn columnRelated = columnMap.get(fieldId);
            targetField.complementColumn(columnRelated);
            targetFieldList.add(targetField);
        }
        outputConfig.setTargetFields(targetFieldList);
    }

    private void complementJdbcInputInformation(JdbcInputNode node) throws Exception {
        JdbcInputConfig inputConfig = node.getConfig();
        Long datasourceId = inputConfig.getDatasourceId();
        String jdbcConfigJson = queryProvider.findConnectPropertiesById(datasourceId);
        JdbcConfig jdbcConfig = GsonUtil.GSON.fromJson(jdbcConfigJson, JdbcConfig.class);
        Long tableId = inputConfig.getTableId();
        List<String> tableQuery = queryProvider.findTableById(datasourceId, tableId);
        String tableName = tableQuery.get(0);
        jdbcConfig.setTableName(tableName);
        inputConfig.setJdbcConfig(jdbcConfig);

        EtlTableColumn etlTableColumn = GsonUtil.GSON.fromJson(tableQuery.get(1), EtlTableColumn.class);
        Map<String, EtlColumn> columnMap = etlTableColumn.getColumns()
                .stream().collect(Collectors.toMap(EtlColumn::getId, col -> col));
        for (Field field : inputConfig.getFields()) {
            String fieldId = field.getFieldId();
            if (!columnMap.containsKey(fieldId)) {
                throw new IllegalArgumentException(fieldId + " not exists");
            }
            EtlColumn columnRelated = columnMap.get(fieldId);
            field.complementColumn(columnRelated);
        }
    }
}
