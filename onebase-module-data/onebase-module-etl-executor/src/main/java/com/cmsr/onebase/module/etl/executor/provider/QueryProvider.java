package com.cmsr.onebase.module.etl.executor.provider;

import com.cmsr.onebase.module.etl.executor.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.executor.graph.conf.JdbcConnectionProperties;
import com.cmsr.onebase.module.etl.executor.util.GsonUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.util.Map;

public class QueryProvider {

    private final DSLContext context = DSL.using(SQLDialect.DEFAULT);

    private final QueryRunner runner;

    public QueryProvider(DataSource dataSource) {
        this.runner = new QueryRunner(dataSource);
    }

    private final ResultSetHandler<WorkflowGraph> workflowHandler = resultSet -> {
        if (resultSet.next()) {
            String config = resultSet.getString("config");
            return GsonUtil.GSON.fromJson(config, WorkflowGraph.class);
        }
        return null;
    };

    private final ResultSetHandler<JdbcConnectionProperties> datasourceHandler = resultSet -> {
        if (resultSet.next()) {
            String config = resultSet.getString("config");
            return GsonUtil.GSON.fromJson(config, JdbcConnectionProperties.class);
        }
        return null;
    };

    private final MapHandler tableHandler = new MapHandler();


    public WorkflowGraph findWorkflowConfig(Long workflowId) throws Exception {
        var workflowQuery = context.select(DSL.field("config", String.class))
                .from(DSL.table("etl_workflow"))
                .where(DSL.field("id").eq(workflowId));
        WorkflowGraph workflowGraph = runner.query(workflowQuery.getSQL(ParamType.INDEXED), workflowHandler, workflowQuery.getBindValues().toArray());
        if (workflowGraph == null) {
            throw new IllegalArgumentException(workflowId + " not exists");
        }
        return workflowGraph;
    }

    public Map<String, Object> findTableById(Long tableId) throws Exception {
        var tableInfoQuery = context.select(
                        DSL.field("datasource_id", Long.class),
                        DSL.field("table_name", String.class),
                        DSL.field("meta_info", String.class)
                )
                .from(DSL.table("etl_table"))
                .where(
                        DSL.field("id", Long.class).eq(tableId)
                );
        return runner.query(tableInfoQuery.getSQL(ParamType.INDEXED),
                tableHandler,
                tableInfoQuery.getBindValues().toArray());
    }

    public JdbcConnectionProperties findConnectPropertiesById(Long datasourceId) throws Exception {
        var datasourceInfoQuery = context.select(
                        DSL.field("config", String.class)
                )
                .from(DSL.table("etl_datasource"))
                .where(
                        DSL.field("id").eq(datasourceId)
                );
        return runner.query(datasourceInfoQuery.getSQL(ParamType.INDEXED),
                datasourceHandler,
                datasourceInfoQuery.getBindValues().toArray());
    }
}
