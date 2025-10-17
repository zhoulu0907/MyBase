package com.cmsr.onebase.module.etl.core.dal.dataobject.sub;

import lombok.Data;

@Data
public class MetaSchema {

    private String fullyQualifiedName;

    private String keyword;

    private String comment;
}
