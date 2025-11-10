package com.cmsr.onebase.module.etl.common.graph.conf;

import com.cmsr.onebase.module.etl.common.graph.NodeConfig;
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

    /**
     * 来自界面定义
     */
    private Long tableId;

    /**
     * 来自界面定义，需要补充类型和精度
     */
    private List<Field> fields;

    /**
     * 数据库补充的信息
     */
    private JdbcConfig jdbcConfig;
}
