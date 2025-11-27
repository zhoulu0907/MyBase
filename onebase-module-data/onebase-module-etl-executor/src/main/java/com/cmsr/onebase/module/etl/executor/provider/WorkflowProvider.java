package com.cmsr.onebase.module.etl.executor.provider;

import com.cmsr.onebase.module.etl.common.entity.ColumnData;
import com.cmsr.onebase.module.etl.common.entity.TableData;
import com.cmsr.onebase.module.etl.common.graph.Node;
import com.cmsr.onebase.module.etl.common.graph.NodeConfig;
import com.cmsr.onebase.module.etl.common.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.common.graph.conf.*;
import com.cmsr.onebase.module.etl.executor.provider.dao.EtlDataSource;
import com.cmsr.onebase.module.etl.executor.provider.dao.EtlFlinkMapping;
import com.cmsr.onebase.module.etl.executor.provider.dao.EtlTable;
import com.cmsr.onebase.module.etl.executor.util.JacksonUtil;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

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


    public WorkflowGraph createSubWorkflowGraph(String graphJson, String nodeId) throws Exception {
        WorkflowGraph graph = JacksonUtil.readValue(graphJson, WorkflowGraph.class);
        graph.init();
        WorkflowGraph subgraph = graph.subgraph(nodeId);
        complementGraphInformation(subgraph);
        return subgraph;
    }

    public WorkflowGraph createWorkflowGraph(String graphJson) throws Exception {
        WorkflowGraph graph = JacksonUtil.fromJson(graphJson, WorkflowGraph.class);
        graph.init();
        complementGraphInformation(graph);
        return graph;
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

    private void complementJdbcInputInformation(JdbcInputConfig jdbcInputConfig) throws Exception {
        String datasourceUuid = jdbcInputConfig.getDatasourceUuid();
        String tableUuid = jdbcInputConfig.getTableUuid();

        EtlTable etlTable = queryProvider.findTableByUuid(tableUuid);
        EtlDataSource etlDataSource = queryProvider.findConnectPropertiesByUuid(datasourceUuid);

        JdbcConfig jdbcConfig = JacksonUtil.fromJson(etlDataSource.getConfig(), JdbcConfig.class);
        jdbcConfig.setDatabaseType(etlDataSource.getDatasourceType());
        jdbcConfig.setTableName(etlTable.getTableName());
        jdbcInputConfig.setJdbcConfig(jdbcConfig);

        List<EtlFlinkMapping> flinkMappings = queryProvider.findFlinkMapping(jdbcConfig.getDatabaseType());
        List<ColumnData> etlColumns = JacksonUtil.fromJson(etlTable.getMetaInfo(), TableData.class).getColumns();
        List<Field> fields = jdbcInputConfig.getFields();

        // 调用优化后的方法
        complementFields(fields, etlColumns, flinkMappings);
    }

    private void complementFields(List<Field> fields, List<ColumnData> etlColumns, List<EtlFlinkMapping> flinkMappings) {
        // 预构建查找表，提高查找效率
        Map<String, ColumnData> columnMap = etlColumns.stream()
                .collect(Collectors.toMap(ColumnData::getName, column -> column));

        // 按类型名转小写构建映射，支持忽略大小写查找
        Map<String, EtlFlinkMapping> flinkMappingMap = flinkMappings.stream()
                .collect(Collectors.toMap(mapping -> mapping.getOriginType().toLowerCase(), mapping -> mapping));

        for (Field field : fields) {
            String fieldName = field.getFieldName();
            ColumnData etlColumn = columnMap.get(fieldName);
            if (etlColumn == null) {
                throw new IllegalArgumentException(String.format("字段ID %s 不存在", fieldName));
            }

            String originType = etlColumn.getType();
            EtlFlinkMapping etlFlinkMapping = flinkMappingMap.get(originType.toLowerCase());
            if (etlFlinkMapping == null) {
                throw new IllegalArgumentException(String.format("字段类型 %s 不存在映射", originType));
            }

            field.setFieldType(etlFlinkMapping.getFlinkType());
            field.setLength(etlColumn.getLength());
            field.setPrecision(etlColumn.getPrecision());
            field.setScale(etlColumn.getScale());
        }
    }


    private void complementJdbcOutputInformation(JdbcOutputConfig jdbcOutputConfig) throws Exception {
        String datasourceUuid = jdbcOutputConfig.getDatasourceUuid();
        String tableUuid = jdbcOutputConfig.getTableUuid();

        EtlDataSource etlDataSource = queryProvider.findConnectPropertiesByUuid(datasourceUuid);
        EtlTable etlTable = queryProvider.findTableByUuid(tableUuid);

        JdbcConfig jdbcConfig = JacksonUtil.fromJson(etlDataSource.getConfig(), JdbcConfig.class);
        jdbcConfig.setDatabaseType(etlDataSource.getDatasourceType());
        jdbcConfig.setTableName(etlTable.getTableName());
        jdbcOutputConfig.setJdbcConfig(jdbcConfig);

        List<EtlFlinkMapping> flinkMappings = queryProvider.findFlinkMapping(jdbcConfig.getDatabaseType());
        List<ColumnData> etlColumns = JacksonUtil.fromJson(etlTable.getMetaInfo(), TableData.class).getColumns();
        List<JdbcOutputMapper> jdbcOutputMappers = jdbcOutputConfig.getFields();
        complementJdbcOutputMappers(jdbcOutputMappers, etlColumns, flinkMappings);
    }

    private void complementJdbcOutputMappers(List<JdbcOutputMapper> jdbcOutputMappers, List<ColumnData> etlColumns, List<EtlFlinkMapping> flinkMappings) {
        // 预构建查找表，提高查找效率
        Map<String, ColumnData> columnMap = etlColumns.stream()
                .collect(Collectors.toMap(ColumnData::getName, column -> column));

        // 按类型名转小写构建映射，支持忽略大小写查找
        Map<String, EtlFlinkMapping> flinkMappingMap = flinkMappings.stream()
                .collect(Collectors.toMap(mapping -> mapping.getOriginType().toLowerCase(), mapping -> mapping));

        for (JdbcOutputMapper jdbcOutputMapper : jdbcOutputMappers) {
            String fieldName = StringUtils.substringAfterLast(jdbcOutputMapper.getTargetFieldName(), ".");
            ColumnData etlColumn = columnMap.get(fieldName);
            if (etlColumn == null) {
                throw new IllegalArgumentException(String.format("目标字段ID %s 不存在", jdbcOutputMapper));
            }
            String originType = etlColumn.getType();
            EtlFlinkMapping etlFlinkMapping = flinkMappingMap.get(originType.toLowerCase());
            if (etlFlinkMapping == null) {
                throw new IllegalArgumentException(String.format("字段类型 %s 不存在映射", originType));
            }
            jdbcOutputMapper.setTargetFieldName(etlColumn.getName());
            jdbcOutputMapper.setTargetFieldType(etlFlinkMapping.getFlinkType());
            jdbcOutputMapper.setTargetFieldLength(etlColumn.getLength());
            jdbcOutputMapper.setTargetFieldPrecision(etlColumn.getPrecision());
            jdbcOutputMapper.setTargetFieldScale(etlColumn.getScale());
        }
    }


}
