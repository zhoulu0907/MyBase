package com.cmsr.onebase.module.metadata.build.service.entity.vo;

import lombok.Data;

/**
 * 实体字段查询VO，用于Service层参数封装
 *
 * @author mickey
 * @date 2025/1/25
 */
@Data
public class EntityFieldQueryVO {

    /**
     * 实体ID
     */
    private String entityId;

    /**
     * 实体UUID
     */
    private String entityUuid;

    /**
     * 表名（与entityId二选一）
     */
    private String tableName;

    /**
     * 字段名（精确过滤）
     */
    private String fieldName;

    /**
     * 是否系统字段：0-是，1-不是
     */
    private Integer isSystemField;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 字段编码
     */
    private String fieldCode;

    /**
     * 是否是人员字段：0-不是，1-是
     */
    private Integer isPerson;

    /**
     * 构造方法
     *
     * @param entityId 实体ID
     * @param isSystemField 是否系统字段：0-是，1-不是
     * @param keyword 搜索关键词
     */
    public EntityFieldQueryVO(String entityId, Integer isSystemField, String keyword) {
        this.entityId = entityId;
        this.isSystemField = isSystemField;
        this.keyword = keyword;
    }

    /**
     * 完整构造方法
     *
     * @param entityId 实体ID
     * @param isSystemField 是否系统字段：0-是，1-不是
     * @param keyword 搜索关键词
     * @param fieldCode 字段编码
     */
    public EntityFieldQueryVO(String entityId, Integer isSystemField, String keyword, String fieldCode) {
        this.entityId = entityId;
        this.isSystemField = isSystemField;
        this.keyword = keyword;
        this.fieldCode = fieldCode;
    }

    /**
     * 默认构造方法
     */
    public EntityFieldQueryVO() {
    }
}
