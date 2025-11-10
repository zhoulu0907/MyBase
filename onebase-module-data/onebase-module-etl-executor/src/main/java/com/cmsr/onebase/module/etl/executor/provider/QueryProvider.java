package com.cmsr.onebase.module.etl.executor.provider;

import org.apache.commons.dbutils.QueryRunner;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class QueryProvider {

    private final DSLContext context = DSL.using(SQLDialect.DEFAULT);

    private final QueryRunner runner;

    public QueryProvider(DataSource dataSource) {
        this.runner = new QueryRunner(dataSource);
    }

    public String findWorkflowConfig(Long workflowId) throws Exception {
        var workflowQuery = context.select(DSL.field("config", String.class))
                .from(DSL.table("etl_workflow"))
                .where(DSL.and(
                        DSL.field("id").eq(workflowId),
                        DSL.field("deleted").eq(0)
                ));
        String workflowGraph = runner.query(workflowQuery.getSQL(ParamType.INDEXED), resultSet -> {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            return null;
        }, workflowQuery.getBindValues().toArray());
        if (workflowGraph == null) {
            throw new IllegalArgumentException(workflowId + " not exists");
        }
        return workflowGraph;
    }

    public List<String> findTableById(Long datasourceId, Long tableId) throws Exception {
        var tableInfoQuery = context.select(
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

        List<String> result = runner.query(tableInfoQuery.getSQL(ParamType.INDEXED), resultSet -> {
                    List<String> resultList = new ArrayList<>();
                    if (resultSet.next()) {
                        resultList.add(resultSet.getString("table_name"));
                        resultList.add(resultSet.getString("meta_info"));
                        return resultList;
                    }
                    return null;
                },
                tableInfoQuery.getBindValues().toArray());
        if (result == null) {
            throw new IllegalArgumentException(tableId + " not exists");
        }
        return result;
    }

    public String findConnectPropertiesById(Long datasourceId) throws Exception {
        var datasourceInfoQuery = context.select(
                        DSL.field("config", String.class)
                )
                .from(DSL.table("etl_datasource"))
                .where(
                        DSL.and(
                                DSL.field("id").eq(datasourceId),
                                DSL.field("deleted", Long.class).eq(0L)
                        )
                );
        String result = runner.query(datasourceInfoQuery.getSQL(ParamType.INDEXED), resultSet -> {
                    if (resultSet.next()) {
                        return resultSet.getString(1);
                    }
                    return null;
                },
                datasourceInfoQuery.getBindValues().toArray());
        if (result == null) {
            throw new IllegalArgumentException(datasourceId + " not exists");
        }
        return result;
    }
}
