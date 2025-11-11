package com.cmsr.onebase.module.etl.executor;

import com.cmsr.onebase.module.etl.common.graph.WorkflowGraph;
import com.cmsr.onebase.module.etl.executor.provider.QueryProvider;
import com.cmsr.onebase.module.etl.executor.provider.WorkflowProvider;
import com.cmsr.onebase.module.etl.executor.util.JacksonUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class QueryProviderTest {

    private QueryProvider queryProvider;

    private HikariDataSource dataSource;

    @BeforeEach
    public void init() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl("jdbc:postgresql://10.0.104.38:5432/onebase_cloud_v3");
        config.setUsername("postgres");
        config.setPassword("onebase@2025");
        config.setMaximumPoolSize(1);
        config.setMinimumIdle(1);
        this.dataSource = new HikariDataSource(config);

        this.queryProvider = new QueryProvider(this.dataSource);
    }

    @AfterEach
    public void destory() {
        if (dataSource != null) {
            dataSource.close();
        }
    }


    @Test
    public void testQuery() throws Exception {
        WorkflowProvider workflowProvider = new WorkflowProvider();
        workflowProvider.setQueryProvider(queryProvider);
        WorkflowGraph workflowGraph = workflowProvider.getWorkflowGraph(130267663914401792L);
        System.out.println(JacksonUtil.OBJECT_MAPPER.writeValueAsString(workflowGraph));
    }
}
