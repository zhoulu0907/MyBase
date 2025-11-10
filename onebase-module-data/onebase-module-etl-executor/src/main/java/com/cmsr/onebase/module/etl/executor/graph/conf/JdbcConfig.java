package com.cmsr.onebase.module.etl.executor.graph.conf;

import lombok.Data;

@Data
public class JdbcConfig {
    /**
     * 来自数据库补充
     */
    private String driver;
    private String jdbcUrl;
    private String tableName;
    private String username;
    private String password;
}
