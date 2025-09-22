package com.cmsr.onebase.module.metadata.build.service.datasource.vo;

import lombok.Data;

/**
 * @ClassName TableQueryVO
 * @Description 数据源表查询VO，用于Service层参数封装
 * @Author mickey
 * @Date 2025/1/25 16:00
 */
@Data
public class TableQueryVO {

    /**
     * 数据源ID
     */
    private String datasourceId;

    /**
     * 数据库模式名(可选)
     */
    private String schemaName;

    /**
     * 表名搜索关键词(可选)
     */
    private String keyword;

    /**
     * 构造方法
     *
     * @param datasourceId 数据源ID
     * @param schemaName 数据库模式名
     * @param keyword 表名搜索关键词
     */
    public TableQueryVO(String datasourceId, String schemaName, String keyword) {
        this.datasourceId = datasourceId;
        this.schemaName = schemaName;
        this.keyword = keyword;
    }

    /**
     * 默认构造方法
     */
    public TableQueryVO() {
    }
}
