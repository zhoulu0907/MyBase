package com.cmsr.onebase.module.etl.common.graph.conf;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Field {

    /**
     * 界面配置
     */
    private String fieldName;

    /**
     * 界面配置
     * !!!数据库补充!!!
     */
    private String fieldType;

    /**
     * !!!数据库补充!!!
     */
    private Integer length;

    /**
     * !!!数据库补充!!!
     */
    private Integer precision;

    /**
     * !!!数据库补充!!!
     */
    private Integer scale;


}
