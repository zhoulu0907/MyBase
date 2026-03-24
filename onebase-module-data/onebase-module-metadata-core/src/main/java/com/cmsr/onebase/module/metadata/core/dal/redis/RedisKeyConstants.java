package com.cmsr.onebase.module.metadata.core.dal.redis;

/**
 * Metadata 模块 Redis Key 常量定义
 *
 * @author onebase
 * @since 2026-03-24
 */
public interface RedisKeyConstants {

    /**
     * 实体字段列表缓存
     * <p>
     * KEY 格式：entity_fields:{entityUuid}
     * VALUE 数据类型：List&lt;MetadataEntityFieldDO&gt;
     * 过期时间：30 分钟
     */
    String ENTITY_FIELD_LIST = "entity_fields#30m";

    /**
     * 实体字段缓存（单个字段）
     * <p>
     * KEY 格式：entity_field:{fieldUuid}
     * VALUE 数据类型：MetadataEntityFieldDO
     * 过期时间：30 分钟
     */
    String ENTITY_FIELD = "entity_field#30m";

    /**
     * 业务实体缓存
     * <p>
     * KEY 格式：business_entity:{entityUuid}
     * VALUE 数据类型：MetadataBusinessEntityDO
     * 过期时间：30 分钟
     */
    String BUSINESS_ENTITY = "business_entity#30m";

    /**
     * 实体字段选项缓存
     * <p>
     * KEY 格式：entity_field_options:{fieldUuid}
     * VALUE 数据类型：List&lt;MetadataEntityFieldOptionDO&gt;
     * 过期时间：30 分钟
     */
    String ENTITY_FIELD_OPTIONS = "entity_field_options#30m";
}