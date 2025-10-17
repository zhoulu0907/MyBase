package com.cmsr.onebase.module.etl.core.dal.dataobject.sub;

import lombok.Data;

import java.util.List;

@Data
public class MetaTable {

    private String fullyQualifiedName;

    private String keyword;

    private String baseType;

    private String comment;

    private List<MetaColumn> columns;

}
