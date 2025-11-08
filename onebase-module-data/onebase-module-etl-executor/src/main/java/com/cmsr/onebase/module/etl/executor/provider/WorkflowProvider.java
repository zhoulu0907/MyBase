package com.cmsr.onebase.module.etl.executor.provider;

import com.cmsr.onebase.module.etl.executor.graph.Field;
import com.cmsr.onebase.module.etl.executor.graph.Node;
import com.cmsr.onebase.module.etl.executor.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.executor.graph.conf.JdbcConfig;
import com.cmsr.onebase.module.etl.executor.graph.conf.JdbcInputConfig;
import com.cmsr.onebase.module.etl.executor.graph.node.JdbcInputNode;
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
        Long tableId = inputConfig.getTableId();
        Map<String, Object> tableInfo = queryProvider.findTableById(tableId);
        Long datasourceId = (Long) tableInfo.get("datasource_id");
        String tableName = (String) tableInfo.get("table_name");

        JsonObject tableMeta = GsonUtil.GSON.fromJson((String) tableInfo.get("meta_info"), JsonObject.class);
        Map<String, Field> columnMap = inputConfig.getFields().stream()
                .collect(Collectors.toMap(Field::getFieldId, field -> field));
        complementTableColumns(tableMeta, columnMap);

        JdbcConfig connectionProperties = queryProvider.findConnectPropertiesById(datasourceId);
        connectionProperties.setTableName(tableName);
        inputConfig.setJdbcConfig(connectionProperties);
    }

    private void complementTableColumns(JsonObject tableMetaInfo, Map<String, Field> columnMap) {
        JsonArray columns = tableMetaInfo.getAsJsonArray("columns");
        columns.forEach(column -> {
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
