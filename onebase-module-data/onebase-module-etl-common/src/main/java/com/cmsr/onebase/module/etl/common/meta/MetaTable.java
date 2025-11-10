package com.cmsr.onebase.module.etl.common.meta;

import lombok.Data;

import java.util.List;

@Data
public class MetaTable {

    /**
     * 数据库类型标识（MySQL, PostgreSQL, Oracle等）
     */
    private String databaseType;

    /**
     * 目录（某些数据库支持）
     */
    private String catalog;

    /**
     * 模式/架构
     */
    private String schema;

    /**
     * 表名
     */
    private String name;

    /**
     * 表注释
     */
    private String comment;

    /**
     * 表类型（TABLE, VIEW等）
     */
    private String type;

    /**
 
    /**
     * 列信息列表
     */
    private List<MetaColumn> columns;

}
