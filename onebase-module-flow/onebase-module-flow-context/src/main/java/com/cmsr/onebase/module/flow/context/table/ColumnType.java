package com.cmsr.onebase.module.flow.context.table;

/**
 * @Author：huangjie
 * @Date：2025/12/25 9:52
 */
public enum ColumnType {

    SIMPLE( "简单字段"),

    SUBTABLE("子表");

    private String name;

    ColumnType(String name) {
        this.name = name;
    }
}
