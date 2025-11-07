package com.cmsr.onebase.module.etl.executor.provider;

import com.cmsr.onebase.module.etl.executor.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.executor.util.GsonUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
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

    public Object findWorkflowConfig(Long workflowId) {
        ResultSetHandler<WorkflowGraph> handler = new ResultSetHandler<>() {
            @Override
            public WorkflowGraph handle(ResultSet resultSet) throws SQLException {
                resultSet.next();
                String resultJson = resultSet.getString(1);

                return GsonUtil.GSON.fromJson(resultJson, WorkflowGraph.class);
            }
        };
        String workflowQuery = context.select(DSL.field("config"))
                .from("etl_workflow")
                .where(DSL.field("id").eq(workflowId))
                .getSQL(ParamType.INLINED);

        try {
            var query = runner.query(workflowQuery, handler);

            System.out.println(query);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
