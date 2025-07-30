package com.cmsr.onebase.module.metadata.service.entity.vo;

import lombok.Data;

/**
 * @ClassName EntityFieldQueryVO
 * @Description 实体字段查询VO，用于Service层参数封装
 * @Author mickey
 * @Date 2025/1/25 16:00
 */
@Data
public class EntityFieldQueryVO {

    /**
     * 实体ID
     */
    private Long entityId;

    /**
     * 是否系统字段
     */
    private Boolean isSystemField;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 构造方法
     *
     * @param entityId 实体ID
     * @param isSystemField 是否系统字段
     * @param keyword 搜索关键词
     */
    public EntityFieldQueryVO(Long entityId, Boolean isSystemField, String keyword) {
        this.entityId = entityId;
        this.isSystemField = isSystemField;
        this.keyword = keyword;
    }

    /**
     * 默认构造方法
     */
    public EntityFieldQueryVO() {
    }
} 