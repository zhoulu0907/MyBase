package com.cmsr.onebase.module.etl.common.meta;

import lombok.Data;

import java.util.List;

@Data
public class TableMeta {

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
     * 列信息列表
     */
    private List<ColumnMeta> columns;

}
