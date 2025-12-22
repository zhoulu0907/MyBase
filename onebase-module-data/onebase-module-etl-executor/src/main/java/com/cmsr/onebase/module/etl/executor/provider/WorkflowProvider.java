package com.cmsr.onebase.module.etl.executor.provider;

import com.cmsr.onebase.module.etl.common.entity.ColumnData;
import com.cmsr.onebase.module.etl.common.entity.TableData;
import com.cmsr.onebase.module.etl.common.graph.Node;
import com.cmsr.onebase.module.etl.common.graph.NodeConfig;
import com.cmsr.onebase.module.etl.common.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.common.graph.conf.*;
import com.cmsr.onebase.module.etl.executor.provider.dao.EtlDataSource;
import com.cmsr.onebase.module.etl.executor.provider.dao.EtlFlinkMappings;
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

    public WorkflowGraph createSubWorkflowGraph(Long applicationId, String graphJson, String nodeId) throws Exception {
        WorkflowGraph graph = JacksonUtil.readValue(graphJson, WorkflowGraph.class);
        graph.init();
        WorkflowGraph subgraph = graph.subgraph(nodeId);
        complementGraphInformation(applicationId, subgraph);
        return subgraph;
    }

    public WorkflowGraph createWorkflowGraph(Long applicationId, String graphJson) throws Exception {
        WorkflowGraph graph = JacksonUtil.fromJson(graphJson, WorkflowGraph.class);
        graph.init();
        complementGraphInformation(applicationId, graph);
        return graph;
    }


    private void complementGraphInformation(Long applicationId, WorkflowGraph workflowGraph) throws Exception {
        for (Node node : workflowGraph.getNodes()) {
            NodeConfig config = node.getConfig();
            if (config instanceof JdbcInputConfig jdbcInputConfig) {
                complementJdbcInputInformation(applicationId, jdbcInputConfig);
            } else if (config instanceof JdbcOutputConfig jdbcOutputConfig) {
                complementJdbcOutputInformation(applicationId, jdbcOutputConfig);
            }
        }
    }

    private void complementJdbcInputInformation(Long applicationId, JdbcInputConfig jdbcInputConfig) throws Exception {
        String datasourceUuid = jdbcInputConfig.getDatasourceUuid();
        String tableUuid = jdbcInputConfig.getTableUuid();

        EtlTable etlTable = queryProvider.findTableByUuid(applicationId, tableUuid);
        EtlDataSource etlDataSource = queryProvider.findConnectPropertiesByUuid(applicationId, datasourceUuid);

        JdbcConfig jdbcConfig = JacksonUtil.fromJson(etlDataSource.getConfig(), JdbcConfig.class);
        jdbcConfig.setDatabaseType(etlDataSource.getDatasourceType());
        jdbcConfig.setTableName(etlTable.getTableName());
        jdbcInputConfig.setJdbcConfig(jdbcConfig);

        // todo jdbcConfig.getDatabaseType()
        EtlFlinkMappings flinkMappings = queryProvider.findFlinkMapping();
        List<ColumnData> etlColumns = JacksonUtil.fromJson(etlTable.getMetaInfo(), TableData.class).getColumns();
        List<Field> fields = jdbcInputConfig.getFields();

        // 调用优化后的方法
        complementFields(etlDataSource.getDatasourceType(), fields, etlColumns, flinkMappings);
    }

    private void complementFields(String datasourceType, List<Field> fields, List<ColumnData> etlColumns, EtlFlinkMappings flinkMappings) {
        // 预构建查找表，提高查找效率
        Map<String, ColumnData> columnMap = etlColumns.stream()
                .collect(Collectors.toMap(ColumnData::getName, column -> column));

        for (Field field : fields) {
            String fieldName = field.getFieldName();
            ColumnData etlColumn = columnMap.get(fieldName);
            if (etlColumn == null) {
                throw new IllegalArgumentException(String.format("字段ID %s 不存在", fieldName));
            }

            String originType = etlColumn.getType();
            String flinkType = flinkMappings.getFlinkType(datasourceType, originType.toLowerCase());
            if (StringUtils.isBlank(flinkType)) {
                throw new IllegalArgumentException(String.format("字段类型 %s 不存在映射", originType));
            }

            field.setFieldType(flinkType);
            field.setLength(etlColumn.getLength());
            field.setPrecision(etlColumn.getPrecision());
            field.setScale(etlColumn.getScale());
        }
    }


    private void complementJdbcOutputInformation(Long applicationId, JdbcOutputConfig jdbcOutputConfig) throws Exception {
        String datasourceUuid = jdbcOutputConfig.getDatasourceUuid();
        String tableUuid = jdbcOutputConfig.getTableUuid();

        EtlDataSource etlDataSource = queryProvider.findConnectPropertiesByUuid(applicationId, datasourceUuid);
        EtlTable etlTable = queryProvider.findTableByUuid(applicationId, tableUuid);

        JdbcConfig jdbcConfig = JacksonUtil.fromJson(etlDataSource.getConfig(), JdbcConfig.class);
        jdbcConfig.setDatabaseType(etlDataSource.getDatasourceType());
        jdbcConfig.setTableName(etlTable.getTableName());
        jdbcOutputConfig.setJdbcConfig(jdbcConfig);

        // todo jdbcConfig.getDatabaseType()
        EtlFlinkMappings flinkMappings = queryProvider.findFlinkMapping();
        List<ColumnData> etlColumns = JacksonUtil.fromJson(etlTable.getMetaInfo(), TableData.class).getColumns();
        List<JdbcOutputMapper> jdbcOutputMappers = jdbcOutputConfig.getFields();
        complementJdbcOutputMappers(etlDataSource.getDatasourceType(), jdbcOutputMappers, etlColumns, flinkMappings);
    }

    private void complementJdbcOutputMappers(String datasourceType, List<JdbcOutputMapper> jdbcOutputMappers, List<ColumnData> etlColumns, EtlFlinkMappings flinkMappings) {
        // 预构建查找表，提高查找效率
        Map<String, ColumnData> columnMap = etlColumns.stream()
                .collect(Collectors.toMap(ColumnData::getName, column -> column));

        // 按类型名转小写构建映射，支持忽略大小写查找
        for (JdbcOutputMapper jdbcOutputMapper : jdbcOutputMappers) {
            String fieldName = StringUtils.substringAfterLast(jdbcOutputMapper.getTargetFieldName(), ".");
            ColumnData etlColumn = columnMap.get(fieldName);
            if (etlColumn == null) {
                throw new IllegalArgumentException(String.format("目标字段ID %s 不存在", jdbcOutputMapper));
            }
            String originType = etlColumn.getType();
            String flinkType = flinkMappings.getFlinkType(datasourceType, originType.toLowerCase());
            if (StringUtils.isBlank(flinkType)) {
                throw new IllegalArgumentException(String.format("字段类型 %s 不存在映射", originType));
            }
            jdbcOutputMapper.setTargetFieldName(etlColumn.getName());
            jdbcOutputMapper.setTargetFieldType(flinkType);
            jdbcOutputMapper.setTargetFieldLength(etlColumn.getLength());
            jdbcOutputMapper.setTargetFieldPrecision(etlColumn.getPrecision());
            jdbcOutputMapper.setTargetFieldScale(etlColumn.getScale());
        }
    }


}
