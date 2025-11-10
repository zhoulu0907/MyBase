package com.cmsr.onebase.module.etl.common.meta;

import lombok.Data;

import java.util.List;

@Data
public class MetaTable {

    private String baseType;

    private List<MetaColumn> columns;

}
