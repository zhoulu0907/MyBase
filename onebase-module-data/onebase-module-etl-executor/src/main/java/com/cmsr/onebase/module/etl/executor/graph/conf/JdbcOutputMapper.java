package com.cmsr.onebase.module.etl.executor.graph.conf;

import lombok.Data;

@Data
public class JdbcOutputMapper {

    private String sourceFieldId;

    private String targetFieldId;

    private String sourceFieldName;

    private String sourceFieldType;
}
