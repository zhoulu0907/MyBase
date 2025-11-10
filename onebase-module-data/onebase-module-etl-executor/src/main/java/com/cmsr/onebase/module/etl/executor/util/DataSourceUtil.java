package com.cmsr.onebase.module.etl.executor.util;

import com.cmsr.onebase.module.etl.executor.InputArgs;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * @Author：huangjie
 * @Date：2025/11/6 11:29
 */
public class DataSourceUtil {

    public static HikariDataSource createDataSource(InputArgs inputArgs) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(inputArgs.getJdbcDriverClass());
        config.setJdbcUrl(inputArgs.getJdbcUrl());
        config.setUsername(inputArgs.getJdbcUserName());
        config.setPassword(inputArgs.getJdbcPassword());
        config.setMaximumPoolSize(1);
        config.setMinimumIdle(1);
        HikariDataSource dataSource = new HikariDataSource(config);
        return dataSource;
    }


}
