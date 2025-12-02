package com.cmsr.onebase.module.metadata.core.dal.dataobject.entity;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum;
import com.cmsr.onebase.module.metadata.core.enums.CommonStatusEnum;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 实体字段表 DO
 *
 * @author bty418
 * @date 2025-01-27
 */
@Table(value = "metadata_entity_field")
@Data
@EqualsAndHashCode(callSuper = true)
public class MetadataEntityFieldDO extends BaseTenantEntity {

    /**
     * 字段UUID
     * <p>
     * 用于跨应用、跨版本的唯一标识，与 application_id、version_tag 组成联合唯一约束
     */
    @Column(value = "field_uuid", comment = "字段UUID")
    private String fieldUuid;

    /**
     * 实体UUID
     * <p>
     * 关联 metadata_business_entity.entity_uuid
     */
    @Column(value = "entity_uuid", comment = "实体UUID")
    private String entityUuid;

    /**
     * 字段名称
     */
    @Column(value = "field_name", comment = "字段名称")
    private String fieldName;

    /**
     * 显示名称
     */
    @Column(value = "display_name", comment = "显示名称")
    private String displayName;

    /**
     * 字段类型
     */
    @Column(value = "field_type", comment = "字段类型")
    private String fieldType;

    /**
     * 数据长度
     */
    @Column(value = "data_length", comment = "数据长度")
    private Integer dataLength;

    /**
     * 小数位数
     */
    @Column(value = "decimal_places", comment = "小数位数")
    private Integer decimalPlaces;

    /**
     * 默认值
     */
    @Column(value = "default_value", comment = "默认值")
    private String defaultValue;

    /**
     * 字段描述
     */
    @Column(value = "description", comment = "字段描述")
    private String description;

    /**
     * 是否系统字段：1-是，0-不是
     * @see BooleanStatusEnum
     */
    @Column(value = "is_system_field", comment = "是否系统字段：1-是，0-不是")
    private Integer isSystemField;

    /**
     * 是否主键：1-是，0-不是
     * @see BooleanStatusEnum
     */
    @Column(value = "is_primary_key", comment = "是否主键：1-是，0-不是")
    private Integer isPrimaryKey;

    /**
     * 是否必填：1-是，0-不是
     * @see BooleanStatusEnum
     */
    @Column(value = "is_required", comment = "是否必填：1-是，0-不是")
    private Integer isRequired;

    /**
     * 是否唯一：1-是，0-不是
     * @see BooleanStatusEnum
     */
    @Column(value = "is_unique", comment = "是否唯一：1-是，0-不是")
    private Integer isUnique;

    /**
     * 排序
     */
    @Column(value = "sort_order", comment = "排序")
    private Integer sortOrder;

    /**
     * 校验规则配置
     */
    @Column(value = "validation_rules", comment = "校验规则配置")
    private String validationRules;

    /**
     * 版本标识
     */
    @Column(value = "version_tag", comment = "版本标识")
    private Long versionTag;

    /**
     * 应用ID
     */
    @Column(value = "application_id", comment = "应用ID")
    private Long applicationId;

    /**
     * 应用UUID
     * <p>
     * 关联 application.application_uuid
     */
    @Column(value = "application_uuid", comment = "应用UUID")
    private String applicationUuid;

    /**
     * 字段状态：1-开启，0-关闭
     * @see CommonStatusEnum
     */
    @Column(value = "status", comment = "字段状态：1-开启，0-关闭")
    private Integer status;

    /**
     * 字段编码
     */
    @Column(value = "field_code", comment = "字段编码")
    private String fieldCode;

    /**
     * 关联的字典类型ID(system_dict_type.id)
     * 用于SELECT/MULTI_SELECT字段复用系统字典,为null时使用自定义选项
     */
    @Column(value = "dict_type_id", comment = "关联的字典类型ID")
    private Long dictTypeId;

    // ==================== 向后兼容方法 ====================
    
    /**
     * 获取实体ID（兼容旧代码）
     * @deprecated 请使用 {@link #getEntityUuid()} 代替
     * @return 实体UUID
     */
    @Deprecated
    public String getEntityId() {
        return this.entityUuid;
    }

    /**
     * 设置实体ID（兼容旧代码）
     * @deprecated 请使用 {@link #setEntityUuid(String)} 代替
     * @param entityId 实体UUID
     */
    @Deprecated
    public void setEntityId(String entityId) {
        this.entityUuid = entityId;
    }

    /**
     * 设置实体ID（兼容旧代码，Long类型）
     * @deprecated 请使用 {@link #setEntityUuid(String)} 代替
     * @param entityId 实体ID（将被忽略，因为已改用UUID）
     */
    @Deprecated
    public void setEntityId(Long entityId) {
        // 为兼容旧代码，不做任何操作
        // 新代码应该使用 setEntityUuid()
    }

    /**
     * 获取应用ID（兼容旧代码）
     * @deprecated 请使用 {@link #getApplicationUuid()} 代替
     * @return 应用UUID
     */
    @Deprecated
    public String getApplicationId() {
        return this.applicationUuid;
    }

    /**
     * 设置应用ID（兼容旧代码）
     * @deprecated 请使用 {@link #setApplicationUuid(String)} 代替
     * @param applicationId 应用UUID
     */
    @Deprecated
    public void setApplicationId(String applicationId) {
        this.applicationUuid = applicationId;
    }

    /**
     * 设置应用ID（兼容旧代码，Long类型）
     * @deprecated 请使用 {@link #setApplicationUuid(String)} 代替
     * @param applicationId 应用ID（将被忽略，因为已改用UUID）
     */
    @Deprecated
    public void setApplicationId(Long applicationId) {
        // 为兼容旧代码，不做任何操作
        // 新代码应该使用 setApplicationUuid()
    }

}
