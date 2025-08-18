package com.cmsr.onebase.module.metadata.dal.dataobject.entity;

import jakarta.persistence.Table;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

/**
 * 实体字段表 DO
 *
 * @author bty418
 * @date 2025-01-27
 */
@Table(name = "metadata_entity_field")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataEntityFieldDO extends TenantBaseDO {

    // 列名常量
    public static final String ENTITY_ID        = "entity_id";
    public static final String FIELD_NAME       = "field_name";
    public static final String DISPLAY_NAME     = "display_name";
    public static final String FIELD_TYPE       = "field_type";
    public static final String DATA_LENGTH      = "data_length";
    public static final String DECIMAL_PLACES   = "decimal_places";
    public static final String DEFAULT_VALUE    = "default_value";
    public static final String DESCRIPTION      = "description";
    public static final String IS_SYSTEM_FIELD = "is_system_field";
    public static final String IS_PRIMARY_KEY  = "is_primary_key";
    public static final String IS_REQUIRED     = "is_required";
    public static final String IS_UNIQUE       = "is_unique";
    public static final String ALLOW_NULL      = "allow_null";
    public static final String SORT_ORDER      = "sort_order";
    public static final String VALIDATION_RULES = "validation_rules";
    public static final String RUN_MODE        = "run_mode";
    public static final String APP_ID          = "app_id";
    public static final String STATUS          = "status";
    public static final String FIELD_CODE      = "field_code";

    public MetadataEntityFieldDO setId(Long id) {
        super.setId(id);
        return this;
    }

    /**
     * 实体ID
     */
    private Long entityId;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 显示名称
     */
    private String displayName;

    /**
     * 字段类型
     */
    private String fieldType;

    /**
     * 数据长度
     */
    private Integer dataLength;

    /**
     * 小数位数
     */
    private Integer decimalPlaces;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 字段描述
     */
    private String description;

    /**
     * 是否系统字段：0-是，1-不是
     */
    private Integer isSystemField;

    /**
     * 是否主键：0-是，1-不是
     */
    private Integer isPrimaryKey;

    /**
     * 是否必填：0-是，1-不是
     */
    private Integer isRequired;

    /**
     * 是否唯一：0-是，1-不是
     */
    private Integer isUnique;

    /**
     * 是否允许空值：0-是，1-不是
     */
    private Integer allowNull;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 校验规则配置
     */
    private String validationRules;

    /**
     * 运行模式：0 编辑态，1 运行态
     */
    private Integer runMode;

    /**
     * 应用ID
     */
    private Long appId;

    /**
     * 字段状态 0：开启，1：关闭
     */
    private Integer status;

    /**
     * 字段编码
     */
    private String fieldCode;

}
