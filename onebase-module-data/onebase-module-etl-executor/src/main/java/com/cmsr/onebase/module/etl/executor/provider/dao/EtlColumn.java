package com.cmsr.onebase.module.etl.executor.provider.dao;

import lombok.Data;

@Data
public class EtlColumn {
    private String id;

    private String name;

    private String flinkType;

    private Integer ignoreLength;

    private Integer length;

    private Integer ignorePrecision;

    private Integer precision;

    private Integer ignoreScale;

    private Integer scale;
}
