package com.cmsr.onebase.module.etl.executor.provider;

import com.cmsr.onebase.module.etl.executor.graph.Node;
import com.cmsr.onebase.module.etl.executor.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.executor.graph.conf.JdbcInputConfig;
import com.cmsr.onebase.module.etl.executor.graph.node.JdbcInputNode;
import com.cmsr.onebase.module.etl.executor.util.GsonUtil;
import com.google.gson.JsonElement;
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

    private final ResultSetHandler<JdbcInputConfig> datasourceHandler = resultSet -> {
        if (resultSet.next()) {
            return resultSet.getObject("config", JdbcInputConfig.class);
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
        // TODO: complete graph with input/output..
        complementGraphInfomation(workflowGraph);
        return workflowGraph;
    }

    private void complementGraphInfomation(WorkflowGraph workflowGraph) throws Exception {
        for (Node node : workflowGraph.getNodes()) {
            if (node instanceof JdbcInputNode jdbcInputNode) {
                complementJdbcInputInformation(jdbcInputNode);
            }
        }
    }

    private void complementJdbcInputInformation(JdbcInputNode node) throws Exception {
        JdbcInputConfig inputConfig = node.getConfig();
        Long tableId = inputConfig.getTableId();
        var tableInfoQuery = context.select(
                        DSL.field("datasource_id", Long.class),
                        DSL.field("table_name", String.class),
                        DSL.field("meta_info", String.class)
                )
                .from(DSL.table("etl_table"))
                .where(
                        DSL.field("id", Long.class).eq(tableId)
                );
        Map<String, Object> tableInfo = runner.query(tableInfoQuery.getSQL(ParamType.INDEXED),
                tableHandler,
                tableInfoQuery.getBindValues().toArray());
        Long datasourceId = (Long) tableInfo.get("datasource_id");
        String tableName = (String) tableInfo.get("table_name");
        JsonElement tableMeta = GsonUtil.GSON.toJsonTree(tableInfo.get("meta_info"));

        var datasourceInfoQuery = context.select(
                        DSL.field("config", String.class)
                )
                .from(DSL.table("etl_datasource"))
                .where(
                        DSL.field("id").eq(datasourceId)
                );
        JdbcInputConfig jdbcConnectionConfig = runner.query(datasourceInfoQuery.getSQL(ParamType.INDEXED),
                datasourceHandler,
                datasourceInfoQuery.getBindValues().toArray());

        System.out.println("dnn1iovu2h1");
    }
}
