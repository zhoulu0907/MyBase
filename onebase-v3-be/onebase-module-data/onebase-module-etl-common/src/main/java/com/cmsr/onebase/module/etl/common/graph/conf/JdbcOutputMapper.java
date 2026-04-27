package com.cmsr.onebase.module.etl.common.graph.conf;

import lombok.Data;

@Data
public class JdbcOutputMapper {


    /**
     * 界面配置
     */
    private String targetFieldName;

    /**
     * 界面配置
     */
    private String sourceFieldFqn;

    /**
     * 界面配置
     */
    private String sourceFieldName;

    /**
     * 界面配置
     */
    private String sourceFieldType;

    /**
     * 数据库补充
     */
    private String targetFieldType;

    /**
     * 数据库补充
     */
    private Integer targetFieldLength;

    /**
     * 数据库补充
     */
    private Integer targetFieldPrecision;

    /**
     * 数据库补充
     */
    private Integer targetFieldScale;


}
