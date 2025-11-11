package com.cmsr.onebase.module.etl.common.entity;

import lombok.Data;

import java.util.List;

@Data
public class TableData {

    /**
     * 目录（某些数据库支持）
     */
    private String catalogName;

    /**
     * 模式/架构
     */
    private String schemaName;

    /**
     * 表名
     */
    private String name;
 
    /**
     * 列信息列表
     */
    private List<ColumnData> columns;

}
