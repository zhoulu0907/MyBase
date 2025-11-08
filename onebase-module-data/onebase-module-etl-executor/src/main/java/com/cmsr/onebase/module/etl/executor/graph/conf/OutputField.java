package com.cmsr.onebase.module.etl.executor.graph.conf;

import lombok.Data;

@Data
public class OutputField {

    private String sourceFieldId;

    /**
     * 源字段名称
     */
    private String sourceFieldName;

    private String targetFieldId;

    /**
     * 源字段名称
     */
    private String targetFieldName;

}
