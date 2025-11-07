package com.cmsr.onebase.module.etl.executor.provider;

import com.cmsr.onebase.module.etl.executor.graph.Node;
import com.cmsr.onebase.module.etl.executor.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.executor.util.GsonUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
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

    public Object findWorkflowConfig(Long workflowId) {
        ResultSetHandler<String> handler = resultSet -> {
            if (resultSet.next()) {
                return resultSet.getString("config");
            }
            return null;
        };
        String workflowQuery = context.select(DSL.field("config", String.class))
                .from("etl_workflow")
                .where(DSL.field("id").eq(workflowId))
                .getSQL(ParamType.INLINED);

        try {
            var query = runner.query(workflowQuery, handler);
            WorkflowGraph workflowGraph = GsonUtil.GSON.fromJson(query, WorkflowGraph.class);
            System.out.println(query);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
