package com.cmsr.onebase.module.etl.executor.provider;

import com.cmsr.onebase.module.etl.executor.graph.Field;
import com.cmsr.onebase.module.etl.executor.graph.Node;
import com.cmsr.onebase.module.etl.executor.graph.NodeConfig;
import com.cmsr.onebase.module.etl.executor.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.executor.graph.conf.JdbcConfig;
import com.cmsr.onebase.module.etl.executor.graph.conf.JdbcInputConfig;
import com.cmsr.onebase.module.etl.executor.graph.conf.JdbcOutputConfig;
import com.cmsr.onebase.module.etl.executor.provider.dao.EtlColumn;
import com.cmsr.onebase.module.etl.executor.provider.dao.EtlTable;
import com.cmsr.onebase.module.etl.executor.provider.dao.EtlTableColumn;
import com.cmsr.onebase.module.etl.executor.util.JacksonUtil;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/11/6 10:54
 */
@Setter
public class WorkflowProvider {

    private QueryProvider queryProvider;

    /**
     * 从数据库里面获得配置，并且补充完整信息，然后转换为工作流图
     *
     * @param workflowId
     * @return
     */
    public WorkflowGraph getWorkflowGraph(Long workflowId) throws Exception {
        String workflowGraphJson = queryProvider.findWorkflowConfig(workflowId);
        WorkflowGraph workflowGraph = JacksonUtil.fromJson(workflowGraphJson, WorkflowGraph.class);
        complementGraphInformation(workflowGraph);
        return workflowGraph;
    }

    public WorkflowGraph getWorkflowGraph(WorkflowGraph workflowGraph) throws Exception {
        complementGraphInformation(workflowGraph);
        return workflowGraph;
    }

    private void complementGraphInformation(WorkflowGraph workflowGraph) throws Exception {
        for (Node node : workflowGraph.getNodes()) {
            NodeConfig config = node.getConfig();
            if (config instanceof JdbcInputConfig jdbcInputConfig) {
                complementJdbcInputInformation(jdbcInputConfig);
            } else if (config instanceof JdbcOutputConfig jdbcOutputConfig) {
                complementJdbcOutputInformation(jdbcOutputConfig);
            }
        }
    }

    private void complementJdbcOutputInformation(JdbcOutputConfig jdbcOutputConfig) throws Exception {
        Long datasourceId = jdbcOutputConfig.getDatasourceId();
        String jdbcConfigJson = queryProvider.findConnectPropertiesById(datasourceId);
        JdbcConfig jdbcConfig = JacksonUtil.fromJson(jdbcConfigJson, JdbcConfig.class);
        Long tableId = jdbcOutputConfig.getTableId();
        EtlTable etlTable = queryProvider.findTableById(datasourceId, tableId);
        jdbcConfig.setTableName(etlTable.getTableName());
        jdbcOutputConfig.setJdbcConfig(jdbcConfig);

        EtlTableColumn etlTableColumn = JacksonUtil.fromJson(etlTable.getMetaInfo(), EtlTableColumn.class);
        List<Field> targetFieldList = etlTableColumn.getColumns().stream().map(col -> Field.of(col)).toList();
        jdbcOutputConfig.setTargetFields(targetFieldList);

    }

    private void complementJdbcInputInformation(JdbcInputConfig jdbcInputConfig) throws Exception {
        Long datasourceId = jdbcInputConfig.getDatasourceId();
        String jdbcConfigJson = queryProvider.findConnectPropertiesById(datasourceId);
        JdbcConfig jdbcConfig = JacksonUtil.fromJson(jdbcConfigJson, JdbcConfig.class);
        Long tableId = jdbcInputConfig.getTableId();
        EtlTable etlTable = queryProvider.findTableById(datasourceId, tableId);
        jdbcConfig.setTableName(etlTable.getTableName());
        jdbcInputConfig.setJdbcConfig(jdbcConfig);

        EtlTableColumn etlTableColumn = JacksonUtil.fromJson(etlTable.getMetaInfo(), EtlTableColumn.class);
        Map<String, EtlColumn> columnMap = etlTableColumn.getColumns()
                .stream().collect(Collectors.toMap(EtlColumn::getId, col -> col));
        for (Field field : jdbcInputConfig.getFields()) {
            String fieldId = field.getFieldId();
            if (!columnMap.containsKey(fieldId)) {
                throw new IllegalArgumentException(fieldId + " not exists");
            }
            EtlColumn columnRelated = columnMap.get(fieldId);
            Field.of(field, columnRelated);
        }
    }
}
