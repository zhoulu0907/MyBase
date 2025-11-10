package com.cmsr.onebase.module.etl.build.service.datasource.vo;

import lombok.Data;

@Data
public class ColumnDefine {

    private String fieldName;

    // flink type
    private String fieldType;

}
