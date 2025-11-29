package com.cmsr.onebase.module.metadata.core.dal.dataobject.entity;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum;
import com.cmsr.onebase.module.metadata.core.enums.CommonStatusEnum;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 元数据系统字段维护表 DO
 *
 * @author bty418
 * @date 2025-09-03
 */
@Table(value = "metadata_system_fields")
@Data
@EqualsAndHashCode(callSuper = true)
public class MetadataSystemFieldsDO extends BaseEntity {

    /**
     * 字段名
     */
    @Column(value = "field_name", comment = "字段名")
    private String fieldName;

    /**
     * 字段类型
     */
    @Column(value = "field_type", comment = "字段类型")
    private String fieldType;

    /**
     * 是否为雪花ID：1-是，0-否
     * @see BooleanStatusEnum
     */
    @Column(value = "is_snowflake_id", comment = "是否为雪花ID：1-是，0-否")
    private Integer isSnowflakeId;

    /**
     * 是否必填：1-是，0-否
     * @see BooleanStatusEnum
     */
    @Column(value = "is_required", comment = "是否必填：1-是，0-否")
    private Integer isRequired;

    /**
     * 默认值
     */
    @Column(value = "default_value", comment = "默认值")
    private String defaultValue;

    /**
     * 字段说明
     */
    @Column(value = "description", comment = "字段说明")
    private String description;

    /**
     * 是否启用：1-启用，0-禁用
     * @see CommonStatusEnum
     */
    @Column(value = "is_enabled", comment = "是否启用：1-启用，0-禁用")
    private Integer isEnabled;

    /**
     * 对外展示名称
     */
    @Column(value = "display_name", comment = "对外展示名称")
    private String displayName;

}
