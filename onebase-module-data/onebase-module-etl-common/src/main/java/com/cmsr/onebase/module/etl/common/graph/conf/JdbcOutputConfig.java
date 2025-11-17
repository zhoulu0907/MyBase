package com.cmsr.onebase.module.etl.common.graph.conf;

import com.cmsr.onebase.module.etl.common.graph.NodeConfig;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
public class JdbcOutputConfig extends NodeConfig {

    private Long datasourceId;

    private Long tableId;

    /**
     * 界面配置，部分从数据库补充
     */
    private List<JdbcOutputMapper> fields;


    /**
     * 从数据库补充
     */
    private JdbcConfig jdbcConfig;
}
