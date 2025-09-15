package com.cmsr.onebase.module.metadata.core.dal.dataobject.entity;

import com.cmsr.onebase.framework.data.base.BaseDO;
import com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum;
import com.cmsr.onebase.module.metadata.core.enums.CommonStatusEnum;

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

    /**
     * 系统字段显示名称
     *
     * 业务说明：
     * 1. 此字段用于在实体创建时设置系统字段的显示名称
     * 2. 当新建实体时，系统会自动从此表查询系统字段配置
     * 3. 查询到的displayName值会作为EntityField表中的display_name字段值
     * 4. 需要在EntityService的创建逻辑中确保此字段值正确传递
     *
     * TODO: 验证实体创建和修改逻辑中是否正确使用此字段
     */
    /**
     * 对外展示名称
     */
    private String displayName;

}
