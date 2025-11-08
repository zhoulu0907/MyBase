package com.cmsr.onebase.module.etl.executor.provider;

import com.cmsr.onebase.module.etl.executor.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.executor.graph.conf.JdbcConfig;
import com.cmsr.onebase.module.etl.executor.provider.dao.EtlTable;
import com.cmsr.onebase.module.etl.executor.util.GsonUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryProvider {

    private final DSLContext context = DSL.using(SQLDialect.DEFAULT);

    private final QueryRunner runner;

    public QueryProvider(DataSource dataSource) {
        this.runner = new QueryRunner(dataSource);
    }

    public WorkflowGraph findWorkflowConfig(Long workflowId) throws Exception {
        var workflowQuery = context.select(DSL.field("config", String.class))
                .from(DSL.table("etl_workflow"))
                .where(DSL.field("id").eq(workflowId));
        WorkflowGraph workflowGraph = runner.query(workflowQuery.getSQL(ParamType.INDEXED), new ResultSetHandler<WorkflowGraph>() {
            @Override
            public WorkflowGraph handle(ResultSet resultSet) throws SQLException {
                if (resultSet.next()) {
                    String config = resultSet.getString("config");
                    return GsonUtil.GSON.fromJson(config, WorkflowGraph.class);
                }
                return null;
            }
        }, workflowQuery.getBindValues().toArray());
        if (workflowGraph == null) {
            throw new IllegalArgumentException(workflowId + " not exists");
        }
        return workflowGraph;
    }

    public EtlTable findTableById(Long tableId) throws Exception {
        var tableInfoQuery = context.select(
                        DSL.field("datasource_id", Long.class),
                        DSL.field("table_name", String.class),
                        DSL.field("meta_info", String.class)
                )
                .from(DSL.table("etl_table"))
                .where(
                        DSL.field("id", Long.class).eq(tableId).and(DSL.field("deleted", Long.class).eq(0L))
                );
        return runner.query(tableInfoQuery.getSQL(ParamType.INDEXED),
                new ResultSetHandler<EtlTable>() {
                    @Override
                    public EtlTable handle(java.sql.ResultSet rs) throws java.sql.SQLException {
                        if (rs.next()) {
                            EtlTable etlTable = new EtlTable();
                            etlTable.setDatasourceId(rs.getLong("datasource_id"));
                            etlTable.setTableName(rs.getString("table_name"));
                            etlTable.setMetaInfo(rs.getString("meta_info"));
                            return etlTable;
                        }
                        return null;
                    }
                },
                tableInfoQuery.getBindValues().toArray());
    }

    public JdbcConfig findConnectPropertiesById(Long datasourceId) throws Exception {
        var datasourceInfoQuery = context.select(
                        DSL.field("config", String.class)
                )
                .from(DSL.table("etl_datasource"))
                .where(
                        DSL.field("id").eq(datasourceId).and(DSL.field("deleted", Long.class).eq(0L))
                );
        return runner.query(datasourceInfoQuery.getSQL(ParamType.INDEXED),
                new ResultSetHandler<JdbcConfig>() {
                    @Override
                    public JdbcConfig handle(java.sql.ResultSet rs) throws java.sql.SQLException {
                        if (rs.next()) {
                            return GsonUtil.GSON.fromJson(rs.getString("config"), JdbcConfig.class);
                        }
                        return null;
                    }
                },
                datasourceInfoQuery.getBindValues().toArray());
    }
}
