package com.cmsr.onebase.module.etl.common.graph.conf;

import lombok.Data;

/**
 * 来自数据库补充
 */
@Data
public class JdbcConfig {

    private String databaseType;
    private String driver;
    private String jdbcUrl;
    private String tableName;
    private String username;
    private String password;

}
