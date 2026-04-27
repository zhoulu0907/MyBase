package com.cmsr.onebase.module.metadata.build.service.datasource.vo;

import lombok.Data;

/**
 * @ClassName ColumnQueryVO
 * @Description 数据源字段查询VO，用于Service层参数封装
 * @Author mickey
 * @Date 2025/1/25 16:00
 */
@Data
public class ColumnQueryVO {

    /**
     * 数据源ID
     */
    private String datasourceId;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 数据库模式名(可选)
     */
    private String schemaName;

    /**
     * 构造方法
     *
     * @param datasourceId 数据源ID
     * @param tableName 表名
     * @param schemaName 数据库模式名
     */
    public ColumnQueryVO(String datasourceId, String tableName, String schemaName) {
        this.datasourceId = datasourceId;
        this.tableName = tableName;
        this.schemaName = schemaName;
    }

    /**
     * 默认构造方法
     */
    public ColumnQueryVO() {
    }
}
