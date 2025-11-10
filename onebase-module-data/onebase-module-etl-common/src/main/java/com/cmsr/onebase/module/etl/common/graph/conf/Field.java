package com.cmsr.onebase.module.etl.common.graph.conf;

import lombok.Data;

@Data
public class Field {

    /**
     * 界面配置
     */
    private String fieldId;

    /**
     * 界面配置
     */
    private String fieldName;

    /**
     * 数据库补充
     */
    private String fieldType;

    /**
     * 数据库补充
     */
    private Integer length;

    /**
     * 数据库补充
     */
    private Integer precision;

    /**
     * 数据库补充
     */
    private Integer scale;


}
