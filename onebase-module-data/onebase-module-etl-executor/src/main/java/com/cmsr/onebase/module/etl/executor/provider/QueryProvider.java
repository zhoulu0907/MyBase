package com.cmsr.onebase.module.etl.executor.provider;

import com.cmsr.onebase.module.etl.executor.provider.dao.EtlTable;
import org.apache.commons.dbutils.QueryRunner;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

import javax.sql.DataSource;

public class QueryProvider {

    private final DSLContext context = DSL.using(SQLDialect.DEFAULT);

    private final QueryRunner runner;

    public QueryProvider(DataSource dataSource) {
        this.runner = new QueryRunner(dataSource);
    }

    public String findWorkflowConfig(Long workflowId) throws Exception {
        var query = context.select(DSL.field("config", String.class))
                .from(DSL.table("etl_workflow"))
                .where(DSL.and(
                        DSL.field("id").eq(workflowId),
                        DSL.field("deleted").eq(0)
                ));
        return runner.query(query.getSQL(ParamType.INDEXED), resultSet -> {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            return null;
        }, query.getBindValues().toArray());
    }

    public EtlTable findTableById(Long datasourceId, Long tableId) throws Exception {
        var query = context.select(
                        DSL.field("table_name", String.class),
                        DSL.field("meta_info", String.class)
                )
                .from(DSL.table("etl_table"))
                .where(
                        DSL.and(
                                DSL.field("id", Long.class).eq(tableId),
                                DSL.field("datasource_id", Long.class).eq(datasourceId),
                                DSL.field("deleted", Long.class).eq(0L)
                        )
                );
        return runner.query(query.getSQL(ParamType.INDEXED), resultSet -> {
                    EtlTable etlTable = new EtlTable();
                    if (resultSet.next()) {
                        etlTable.setTableName(resultSet.getString("table_name"));
                        etlTable.setMetaInfo(resultSet.getString("meta_info"));
                        return etlTable;
                    }
                    return null;
                },
                query.getBindValues().toArray());
    }

    public String findConnectPropertiesById(Long datasourceId) throws Exception {
        var query = context.select(
                        DSL.field("config", String.class)
                )
                .from(DSL.table("etl_datasource"))
                .where(
                        DSL.and(
                                DSL.field("id").eq(datasourceId),
                                DSL.field("deleted", Long.class).eq(0L)
                        )
                );
        return runner.query(query.getSQL(ParamType.INDEXED), resultSet -> {
                    if (resultSet.next()) {
                        return resultSet.getString(1);
                    }
                    return null;
                },
                query.getBindValues().toArray());
    }
}
