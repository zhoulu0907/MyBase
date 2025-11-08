package com.cmsr.onebase.module.etl.executor.graph.conf;

import com.cmsr.onebase.module.etl.executor.graph.Field;
import com.cmsr.onebase.module.etl.executor.graph.NodeConfig;
import lombok.Data;

import java.util.List;

@Data
public class JdbcOutputConfig extends NodeConfig {

    private Long datasourceId;

    private Long tableId;


    private List<OutputField> fields;

    /**
     * 从数据库补充
     */
    private List<Field> targetFields;

    /**
     * 从数据库补充
     */
    private JdbcConfig jdbcConfig;
}
