package com.cmsr.onebase.module.etl.executor.graph.conf;

import com.cmsr.onebase.module.etl.executor.graph.Field;
import lombok.Data;

import java.util.List;

@Data
public class JdbcInputConfig {

    /**
     * 来自界面定义
     */
    private Long tableId;
    private List<Field> fields;

    /**
     * 来自数据库补充
     */
    private String driver;
    private String jdbcUrl;
    private String tableName;
    private String username;
    private String password;

    /**
     * 可选参数
     */
    private String schema;
    private String catalog;
}
