package com.cmsr.onebase.module.etl.executor.provider;

import com.cmsr.onebase.module.etl.executor.provider.dao.*;
import com.github.f4b6a3.tsid.TsidCreator;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class QueryProvider {

    private final QueryRunner runner;

    public QueryProvider(DataSource dataSource) {
        this.runner = new QueryRunner(dataSource);
    }

    public EtlWorkflow findWorkflowConfig(Long workflowId) throws Exception {
        String sql = """
                select id, application_id, config from etl_workflow where id = ? and deleted = ?
                """;
        return runner.query(sql, resultSet -> {
            if (resultSet.next()) {
                EtlWorkflow etlWorkflow = new EtlWorkflow();
                etlWorkflow.setWorkflowId(resultSet.getLong("id"));
                etlWorkflow.setApplicationId(resultSet.getLong("application_id"));
                etlWorkflow.setConfig(resultSet.getString("config"));
                return etlWorkflow;
            }
            return null;
        }, workflowId, 0);
    }

    public EtlTable findTableById(Long tableId) throws Exception {
        String sql = """
                select datasource_id, table_name, meta_info from etl_table where id = ?  and deleted = ?
                """;
        return runner.query(sql, resultSet -> {
                    EtlTable etlTable = new EtlTable();
                    if (resultSet.next()) {
                        etlTable.setDatasourceId(resultSet.getLong("datasource_id"));
                        etlTable.setTableName(resultSet.getString("table_name"));
                        etlTable.setMetaInfo(resultSet.getString("meta_info"));
                        return etlTable;
                    }
                    return null;
                },
                tableId, 0);
    }

    public EtlDataSource findConnectPropertiesById(Long datasourceId) throws Exception {
        String sql = """
                select datasource_type, config from etl_datasource where id = ? and deleted = ?
                """;
        return runner.query(sql, resultSet -> {
                    if (resultSet.next()) {
                        EtlDataSource etlDataSource = new EtlDataSource();
                        etlDataSource.setDatasourceType(resultSet.getString("datasource_type"));
                        etlDataSource.setConfig(resultSet.getString("config"));
                        return etlDataSource;
                    }
                    return null;
                },
                datasourceId, 0);
    }

    public List<EtlFlinkMapping> findFlinkMapping(String datasourceType) throws Exception {
        String sql = """
                select origin_type, flink_type from etl_flink_mapping where datasource_type = ? and deleted = ?
                """;
        return runner.query(sql, resultSet -> {
                    List<EtlFlinkMapping> etlFlinkMappings = new ArrayList<>();
                    while (resultSet.next()) {
                        EtlFlinkMapping etlFlinkMapping = new EtlFlinkMapping();
                        etlFlinkMapping.setOriginType(resultSet.getString("origin_type"));
                        etlFlinkMapping.setFlinkType(resultSet.getString("flink_type"));
                        etlFlinkMappings.add(etlFlinkMapping);
                    }
                    return etlFlinkMappings;
                },
                datasourceType, 0);
    }

    public void insertEtlExecutionLog(EtlExecutionLog etlExecutionLog) throws Exception {
        String sql = """
                insert into etl_execution_log(id, application_id, workflow_id, start_time, end_time, duration_time, trigger_type, trigger_user, task_status, error_message)
                values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        etlExecutionLog.setId(TsidCreator.getTsid().toLong());
        runner.execute(sql, etlExecutionLog.getId(),
                etlExecutionLog.getApplicationId(),
                etlExecutionLog.getWorkflowId(),
                etlExecutionLog.getStartTime(),
                etlExecutionLog.getEndTime(),
                etlExecutionLog.getDurationTime(),
                etlExecutionLog.getTriggerType(),
                etlExecutionLog.getTriggerUser(),
                etlExecutionLog.getTaskStatus(),
                etlExecutionLog.getErrorMessage());
    }

}
