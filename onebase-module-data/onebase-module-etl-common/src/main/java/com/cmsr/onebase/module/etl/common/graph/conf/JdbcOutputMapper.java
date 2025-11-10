package com.cmsr.onebase.module.etl.common.graph.conf;

import lombok.Data;

@Data
public class JdbcOutputMapper {

    /**
     * 界面配置  TODO 有问题，如果是 union join sql等节点输出，没有实际的id，只有名称
     */
    private String sourceFieldId;

    /**
     * 界面配置
     */
    private String targetFieldId;

    /**
     * 界面配置
     */
    private String sourceFieldName;

    /**
     * 界面配置
     */
    private String sourceFieldType;
}
