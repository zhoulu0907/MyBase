package com.cmsr.onebase.module.etl.build.service.datasource.vo;

import lombok.Data;

@Data
public class ColumnDefine {

    private String fieldFqn;

    private String fieldName;

    private String displayName;

    // flink type
    private String fieldType;

}
