package com.cmsr.onebase.module.etl.executor.provider;

import com.cmsr.onebase.module.etl.common.entity.EtlColumn;
import com.cmsr.onebase.module.etl.executor.provider.dao.EtlDataSource;
import com.cmsr.onebase.module.etl.executor.provider.dao.EtlTable;
import com.cmsr.onebase.module.etl.common.entity.EtlTableColumn;
import com.cmsr.onebase.module.etl.common.graph.conf.Field;
import com.cmsr.onebase.module.etl.common.graph.Node;
import com.cmsr.onebase.module.etl.common.graph.NodeConfig;
import com.cmsr.onebase.module.etl.common.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.common.graph.conf.JdbcConfig;
import com.cmsr.onebase.module.etl.common.graph.conf.JdbcInputConfig;
import com.cmsr.onebase.module.etl.common.graph.conf.JdbcOutputConfig;
import com.cmsr.onebase.module.etl.common.graph.conf.JdbcOutputMapper;
import com.cmsr.onebase.module.etl.executor.provider.dao.*;
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

    private void complementJdbcInputInformation(JdbcInputConfig jdbcInputConfig) throws Exception {
        Long datasourceId = jdbcInputConfig.getDatasourceId();
        Long tableId = jdbcInputConfig.getTableId();

        EtlDataSource etlDataSource = queryProvider.findConnectPropertiesById(datasourceId);
        EtlTable etlTable = queryProvider.findTableById(datasourceId, tableId);

        JdbcConfig jdbcConfig = JacksonUtil.fromJson(etlDataSource.getConfig(), JdbcConfig.class);
        jdbcConfig.setDatabaseType(etlDataSource.getDatasourceType());
        jdbcConfig.setTableName(etlTable.getTableName());
        jdbcInputConfig.setJdbcConfig(jdbcConfig);

        List<EtlFlinkMapping> flinkMappings = queryProvider.findFlinkMapping(jdbcConfig.getDatabaseType());
        List<EtlColumn> etlColumns = JacksonUtil.fromJson(etlTable.getMetaInfo(), EtlTableColumn.class).getColumns();
        List<Field> fields = jdbcInputConfig.getFields();

        // 调用优化后的方法
        complementFields(fields, etlColumns, flinkMappings);
    }

    private void complementFields(List<Field> fields, List<EtlColumn> etlColumns, List<EtlFlinkMapping> flinkMappings) {
        // 预构建查找表，提高查找效率
        Map<String, EtlColumn> columnMap = etlColumns.stream()
                .collect(Collectors.toMap(EtlColumn::getName, column -> column));
        
        // 按类型名转小写构建映射，支持忽略大小写查找
        Map<String, EtlFlinkMapping> flinkMappingMap = flinkMappings.stream()
                .collect(Collectors.toMap(mapping -> mapping.getOriginType().toLowerCase(), mapping -> mapping));

        for (Field field : fields) {
            String fieldName = field.getFieldName();
            EtlColumn etlColumn = columnMap.get(fieldName);
            if (etlColumn == null) {
                throw new IllegalArgumentException(String.format("字段ID %s 不存在", fieldName));
            }

            String originType = etlColumn.getOriginType();
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
        Long datasourceId = jdbcOutputConfig.getDatasourceId();
        Long tableId = jdbcOutputConfig.getTableId();

        EtlDataSource etlDataSource = queryProvider.findConnectPropertiesById(datasourceId);
        EtlTable etlTable = queryProvider.findTableById(datasourceId, tableId);

        JdbcConfig jdbcConfig = JacksonUtil.fromJson(etlDataSource.getConfig(), JdbcConfig.class);
        jdbcConfig.setDatabaseType(etlDataSource.getDatasourceType());
        jdbcConfig.setTableName(etlTable.getTableName());
        jdbcOutputConfig.setJdbcConfig(jdbcConfig);

        List<EtlFlinkMapping> flinkMappings = queryProvider.findFlinkMapping(jdbcConfig.getDatabaseType());
        List<EtlColumn> etlColumns = JacksonUtil.fromJson(etlTable.getMetaInfo(), EtlTableColumn.class).getColumns();
        List<JdbcOutputMapper> fields = jdbcOutputConfig.getFields();

        List<Field> targetFieldList = toFields(fields, etlColumns, flinkMappings);
        jdbcOutputConfig.setTargetFields(targetFieldList);
    }

    private List<Field> toFields(List<JdbcOutputMapper> fields, List<EtlColumn> etlColumns, List<EtlFlinkMapping> flinkMappings) {
        // 预构建查找表，提高查找效率
        Map<String, EtlColumn> columnMap = etlColumns.stream()
                .collect(Collectors.toMap(EtlColumn::getId, column -> column));
        
        // 按类型名转小写构建映射，支持忽略大小写查找
        Map<String, EtlFlinkMapping> flinkMappingMap = flinkMappings.stream()
                .collect(Collectors.toMap(mapping -> mapping.getOriginType().toLowerCase(), mapping -> mapping));

        // 使用stream API进行转换，使代码更简洁
        return fields.stream().map(mapper -> {
            String targetFieldId = mapper.getTargetFieldId();
            EtlColumn etlColumn = columnMap.get(targetFieldId);
            if (etlColumn == null) {
                throw new IllegalArgumentException(String.format("目标字段ID %s 不存在", targetFieldId));
            }

            String originType = etlColumn.getOriginType();
            EtlFlinkMapping etlFlinkMapping = flinkMappingMap.get(originType.toLowerCase());
            if (etlFlinkMapping == null) {
                throw new IllegalArgumentException(String.format("字段类型 %s 不存在映射", originType));
            }

            Field field = new Field();
            field.setFieldName(etlColumn.getName());
            field.setFieldType(etlFlinkMapping.getFlinkType());
            field.setLength(etlColumn.getLength());
            field.setPrecision(etlColumn.getPrecision());
            field.setScale(etlColumn.getScale());
            return field;
        }).collect(Collectors.toList());
    }


}
