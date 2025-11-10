package com.cmsr.onebase.module.etl.common.entity;

import lombok.Data;

@Data
public class EtlColumn {

    private String id;

    private String name;

    private String originType;

    private Integer ignoreLength;

    private Integer length;

    private Integer ignorePrecision;

    private Integer precision;

    private Integer ignoreScale;

    private Integer scale;
}
