package com.cmsr.onebase.module.etl.executor.graph.conf;

import com.cmsr.onebase.module.etl.executor.graph.Field;
import com.cmsr.onebase.module.etl.executor.graph.NodeConfig;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
public class JdbcInputConfig extends NodeConfig {

    /**
     * 来自界面定义
     */
    private Long datasourceId;

    private Long tableId;

    private List<Field> fields;

    private JdbcConfig jdbcConfig;
}
