package com.cmsr.onebase.module.etl.executor.provider;

import com.cmsr.onebase.module.etl.executor.graph.Field;
import com.cmsr.onebase.module.etl.executor.graph.Node;
import com.cmsr.onebase.module.etl.executor.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.executor.graph.conf.JdbcConnectionProperties;
import com.cmsr.onebase.module.etl.executor.graph.conf.JdbcInputConfig;
import com.cmsr.onebase.module.etl.executor.graph.node.JdbcInputNode;
import com.cmsr.onebase.module.etl.executor.util.GsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Setter;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        return null;
    }

    public WorkflowGraph getWorkflowGraph(String previewWorkflow) {

        return null;
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
        inputConfig.setConnectionProperties(new JdbcConnectionProperties());
        Long tableId = inputConfig.getTableId();
        Map<String, Object> tableInfo = queryProvider.findTableById(tableId);
        Long datasourceId = (Long) tableInfo.get("datasource_id");
        String tableName = (String) tableInfo.get("table_name");
        inputConfig.getConnectionProperties().setTableName(tableName);
        JsonObject tableMeta = GsonUtil.GSON.fromJson((String) tableInfo.get("meta_info"), JsonObject.class);
        List<String> filteredColumnIds = inputConfig.getFields().stream().map(Field::getFieldId).toList();
        List<Field> fields = complementTableColumns(tableMeta, filteredColumnIds);
        inputConfig.setFields(fields);

        JdbcConnectionProperties connectionProperties = queryProvider.findConnectPropertiesById(datasourceId);
        inputConfig.getConnectionProperties().setDriver(connectionProperties.getDriver());
        inputConfig.getConnectionProperties().setJdbcUrl(connectionProperties.getJdbcUrl());
        inputConfig.getConnectionProperties().setUsername(connectionProperties.getUsername());
        inputConfig.getConnectionProperties().setPassword(connectionProperties.getPassword());
    }

    private List<Field> complementTableColumns(JsonObject tableMetaInfo, List<String> filteredColumns) {
        List<Field> fields = new ArrayList<>();
        JsonArray columns = tableMetaInfo.getAsJsonArray("columns");
        columns.forEach(column -> {
            Field field = new Field();
            JsonObject columnJson = column.getAsJsonObject();
            String columnId = columnJson.get("id").getAsString();
            if (!filteredColumns.contains(columnId)) {
                return;
            }
            field.setFieldId(columnId);
            field.setFieldName(columnJson.get("name").getAsString());
            field.setFieldType(columnJson.get("flinkType").getAsString());
            int ignoreLength = columnJson.get("ignoreLength").getAsInt();
            if (ignoreLength == 0) {
                field.setLength(columnJson.get("length").getAsInt());
            }
            int ignorePrecision = columnJson.get("ignorePrecision").getAsInt();
            if (ignorePrecision == 0) {
                field.setPrecision(columnJson.get("precision").getAsInt());
            }
            int ignoreScale = columnJson.get("ignoreScale").getAsInt();
            if (ignoreScale == 0) {
                field.setScale(columnJson.get("scale").getAsInt());
            }

            fields.add(field);
        });

        return fields;
    }
}
