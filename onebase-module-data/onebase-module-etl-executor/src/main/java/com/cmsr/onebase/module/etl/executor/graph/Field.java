package com.cmsr.onebase.module.etl.executor.graph;

import lombok.Data;

@Data
public class Field {

    private String fieldId;

    private String fieldName;

    private String fieldType;

    private int size;

    private int scale;

}
