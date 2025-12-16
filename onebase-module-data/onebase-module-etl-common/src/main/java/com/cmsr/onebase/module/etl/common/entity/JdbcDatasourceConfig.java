package com.cmsr.onebase.module.etl.common.entity;

import lombok.Data;

@Data
public class JdbcDatasourceConfig {
    private String connectMode;
    private String host;
    private String port;
    private String database;
    private String username;
    private String password;

    private String driver;
    private String jdbcUrl;

}
