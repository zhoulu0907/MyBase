package com.cmsr.onebase.module.etl.executor.util;

import com.cmsr.onebase.module.etl.common.excute.ExecuteRequest;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @Author：huangjie
 * @Date：2025/11/6 11:29
 */
public class DataSourceUtil {

    public static HikariDataSource createDataSource(ExecuteRequest executeRequest) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(executeRequest.getJdbcDriverClass());
        config.setJdbcUrl(executeRequest.getJdbcUrl());
        config.setUsername(executeRequest.getJdbcUserName());
        config.setPassword(executeRequest.getJdbcPassword());
        config.setMaximumPoolSize(1);
        config.setMinimumIdle(1);
        HikariDataSource dataSource = new HikariDataSource(config);
        return dataSource;
    }


}
