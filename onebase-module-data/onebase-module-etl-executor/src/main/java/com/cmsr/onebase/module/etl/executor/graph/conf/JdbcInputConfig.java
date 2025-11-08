package com.cmsr.onebase.module.etl.executor.graph.conf;

import com.cmsr.onebase.module.etl.executor.graph.Field;
import lombok.Data;

import java.util.List;

@Data
public class JdbcInputConfig {

    /**
     * 来自界面定义
     */
    private Long datasourceId;

    private Long tableId;

    private List<Field> fields;

    private JdbcConfig jdbcConfig;
}
