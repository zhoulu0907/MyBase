package com.cmsr.onebase.module.metadata.core.dal.dataobject.field;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 实体字段-选项值（用于单选/多选） DO
 *
 * <p>对应表：metadata_entity_field_option</p>
 *
 * @author bty418
 * @date 2025-08-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "metadata_entity_field_option")
public class MetadataEntityFieldOptionDO extends BaseTenantEntity {

    /**
     * 选项UUID
     * <p>
     * 用于跨应用、跨版本的唯一标识，与 application_id、version_tag 组成联合唯一约束
     */
    @Column(value = "option_uuid", comment = "选项UUID")
    private String optionUuid;

    /**
     * 关联字段UUID（metadata_entity_field.field_uuid）
     */
    @Column(value = "field_uuid", comment = "关联字段UUID")
    private String fieldUuid;

    /**
     * 选项显示名称
     */
    @Column(value = "option_label", comment = "选项显示名称")
    private String optionLabel;

    /**
     * 选项值（字段内唯一）
     */
    @Column(value = "option_value", comment = "选项值")
    private String optionValue;

    /**
     * 选项排序
     */
    @Column(value = "option_order", comment = "选项排序")
    private Integer optionOrder;

    /**
     * 是否启用：1-启用，0-禁用
     */
    @Column(value = "is_enabled", comment = "是否启用：1-启用，0-禁用")
    private Integer isEnabled;

    /**
     * 说明
     */
    @Column(value = "description", comment = "说明")
    private String description;

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

    // ==================== 向后兼容方法 ====================
    
    /**
     * 获取字段ID（兼容旧代码）
     * @deprecated 请使用 {@link #getFieldUuid()} 代替
     * @return 字段UUID
     */
    @Deprecated
    public String getFieldId() {
        return this.fieldUuid;
    }

    /**
     * 设置字段ID（兼容旧代码）
     * @deprecated 请使用 {@link #setFieldUuid(String)} 代替
     * @param fieldId 字段UUID
     */
    @Deprecated
    public void setFieldId(String fieldId) {
        this.fieldUuid = fieldId;
    }

    /**
     * 设置字段ID（兼容旧代码，Long类型）
     * @deprecated 请使用 {@link #setFieldUuid(String)} 代替
     * @param fieldId 字段ID（将被忽略，因为已改用UUID）
     */
    @Deprecated
    public void setFieldId(Long fieldId) {
        // 为兼容旧代码，不做任何操作
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
    }
}


