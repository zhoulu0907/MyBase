package com.cmsr.onebase.module.metadata.dal.dataobject.entity;

import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.metadata.enums.BooleanStatusEnum;
import com.cmsr.onebase.module.metadata.enums.CommonStatusEnum;

import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
/**
 * 元数据系统字段维护表 DO
 *
 * @author bty418
 * @date 2025-09-03
 */
@Table(name = "metadata_system_fields")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataSystemFieldsDO extends BaseDO {

    // 列名常量
    public static final String FIELD_NAME       = "field_name";
    public static final String FIELD_TYPE       = "field_type";
    public static final String IS_SNOWFLAKE_ID  = "is_snowflake_id";
    public static final String IS_REQUIRED      = "is_required";
    public static final String DEFAULT_VALUE    = "default_value";
    public static final String DESCRIPTION      = "description";
    public static final String IS_ENABLED      = "is_enabled";
    public static final String DISPLAY_NAME      = "display_name";

    // 基础字段 id、createTime、updateTime 等来自父类 BaseDO

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 字段类型
     */
    private String fieldType;

    /**
     * 是否为雪花ID：1-是，0-否
     * @see BooleanStatusEnum
     */
    private Integer isSnowflakeId;

    /**
     * 是否必填：1-是，0-否
     * @see BooleanStatusEnum
     */
    private Integer isRequired;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 字段说明
     */
    private String description;

    /**
     * 是否启用：1-启用，0-禁用
     * @see CommonStatusEnum
     */
    private Integer isEnabled;

    // TODO: 我在数据库中新加了这个字段。在新建实体时，会默认添加系统字段，会从此表中查询系统字段的信息然后插入到 metadata_entity_field 表中，displayName 的值复用此字段；需要检查实体的新建与修改的逻辑。
    /**
     * 对外展示名称
     */
    private String displayName;

} 