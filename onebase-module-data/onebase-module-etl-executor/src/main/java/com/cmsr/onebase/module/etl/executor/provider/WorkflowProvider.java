package com.cmsr.onebase.module.etl.executor.provider;

import com.cmsr.onebase.module.etl.executor.graph.Field;
import com.cmsr.onebase.module.etl.executor.graph.Node;
import com.cmsr.onebase.module.etl.executor.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.executor.graph.conf.JdbcConfig;
import com.cmsr.onebase.module.etl.executor.graph.conf.JdbcInputConfig;
import com.cmsr.onebase.module.etl.executor.graph.node.JdbcInputNode;
import com.cmsr.onebase.module.etl.executor.provider.dao.EtlTable;
import com.cmsr.onebase.module.etl.executor.provider.dao.EtlTableColumn;
import com.cmsr.onebase.module.etl.executor.util.GsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Setter;

import javax.sql.DataSource;
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
        WorkflowGraph workflowGraph = queryProvider.findWorkflowConfig(workflowId);
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
            }
        }
    }

    private void complementJdbcInputInformation(JdbcInputNode node) throws Exception {
        JdbcInputConfig inputConfig = node.getConfig();
        Long tableId = inputConfig.getTableId();
        EtlTable etlTable = queryProvider.findTableById(tableId);

        EtlTableColumn etlTableColumn = GsonUtil.GSON.fromJson(etlTable.getMetaInfo(), EtlTableColumn.class);
        Map<String, Field> columnMap = inputConfig.getFields().stream()
                .collect(Collectors.toMap(Field::getFieldId, field -> field));
        complementTableColumns(etlTableColumn, columnMap);

        JdbcConfig connectionProperties = queryProvider.findConnectPropertiesById(etlTable.getDatasourceId());
        connectionProperties.setTableName(etlTable.getTableName());
        inputConfig.setJdbcConfig(connectionProperties);
    }

    private void complementTableColumns(EtlTableColumn etlTableColumn, Map<String, Field> columnMap) {
        etlTableColumn.getColumns().forEach(column -> {
            JsonObject columnJson = column.getAsJsonObject();
            String columnId = columnJson.get("id").getAsString();
            if (!columnMap.containsKey(columnId)) {
                return;
            }
            Field field = columnMap.get(columnId);
            field.applyJson(columnJson);
        });
    }
}
