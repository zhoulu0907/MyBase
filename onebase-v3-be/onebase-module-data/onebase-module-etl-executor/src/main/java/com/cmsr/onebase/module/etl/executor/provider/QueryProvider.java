package com.cmsr.onebase.module.etl.executor.provider;

import com.cmsr.onebase.module.etl.executor.provider.dao.*;
import com.github.f4b6a3.tsid.TsidCreator;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;

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

    public EtlTable findTableByUuid(Long applicationId, String tableUuid) throws Exception {
        String sql = """
                select datasource_uuid, table_name, meta_info from etl_table where application_id = ? and table_uuid = ?  and deleted = ?
                """;
        return runner.query(sql, resultSet -> {
                    EtlTable etlTable = new EtlTable();
                    if (resultSet.next()) {
                        etlTable.setDatasourceUuid(resultSet.getString("datasource_uuid"));
                        etlTable.setTableName(resultSet.getString("table_name"));
                        etlTable.setMetaInfo(resultSet.getString("meta_info"));
                        return etlTable;
                    }
                    return null;
                },
                applicationId, tableUuid, 0);
    }

    public EtlDataSource findConnectPropertiesByUuid(Long applicationId, String datasourceUuid) throws Exception {
        String sql = """
                select datasource_type, config from etl_datasource where application_id = ? and datasource_uuid = ? and deleted = ?
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
                applicationId, datasourceUuid, 0);
    }

    public EtlFlinkMappings findFlinkMapping() throws Exception {
        String sql = """
                select datasource_type, origin_type, flink_type from etl_flink_mapping
                """;
        return runner.query(sql, resultSet -> {
            EtlFlinkMappings etlFlinkMappings = new EtlFlinkMappings();
            while (resultSet.next()) {
                String datasourceType = resultSet.getString("datasource_type");
                String originType = resultSet.getString("origin_type");
                String flinkType = resultSet.getString("flink_type");
                etlFlinkMappings.add(datasourceType, originType, flinkType);
            }
            return etlFlinkMappings;
        });
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
