package com.cmsr.onebase.module.metadata.core.dal.dataobject.number;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自动编号-规则项 DO
 * 对应表：metadata_auto_number_rule_item
 *
 * @author bty418
 * @date 2025-08-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "metadata_auto_number_rule_item")
public class MetadataAutoNumberRuleItemDO extends BaseTenantEntity {

    /**
     * 规则项UUID
     * <p>
     * 用于跨应用、跨版本的唯一标识，与 application_id、version_tag 组成联合唯一约束
     */
    @Column(value = "rule_item_uuid", comment = "规则项UUID")
    private String ruleItemUuid;

    /**
     * 配置UUID
     * <p>
     * 关联 metadata_auto_number_config.config_uuid
     */
    @Column(value = "config_uuid", comment = "配置UUID")
    private String configUuid;

    /**
     * 项类型
     */
    @Column(value = "item_type", comment = "项类型")
    private String itemType;

    /**
     * 项顺序
     */
    @Column(value = "item_order", comment = "项顺序")
    private Integer itemOrder;

    /**
     * 格式
     */
    @Column(value = "format", comment = "格式")
    private String format;

    /**
     * 文本值
     */
    @Column(value = "text_value", comment = "文本值")
    private String textValue;

    /**
     * 引用字段UUID
     * <p>
     * 关联 metadata_entity_field.field_uuid
     */
    @Column(value = "ref_field_uuid", comment = "引用字段UUID")
    private String refFieldUuid;

    /**
     * 是否启用：1-启用，0-禁用
     */
    @Column(value = "is_enabled", comment = "是否启用：1-启用，0-禁用")
    private Integer isEnabled;

    /**
     * 应用ID
     */
    @Column(value = "application_id", comment = "应用ID")
    private Long applicationId;

    // ==================== 向后兼容方法 ====================
    
    /**
     * 获取配置ID（兼容旧代码）
     * @deprecated 请使用 {@link #getConfigUuid()} 代替
     * @return 配置UUID
     */
    @Deprecated
    public String getConfigId() {
        return this.configUuid;
    }

    /**
     * 设置配置ID（兼容旧代码）
     * @deprecated 请使用 {@link #setConfigUuid(String)} 代替
     * @param configId 配置UUID
     */
    @Deprecated
    public void setConfigId(String configId) {
        this.configUuid = configId;
    }

    /**
     * 设置配置ID（兼容旧代码，Long类型）
     * @deprecated 请使用 {@link #setConfigUuid(String)} 代替
     * @param configId 配置ID
     */
    @Deprecated
    public void setConfigId(Long configId) {
        this.configUuid = configId != null ? String.valueOf(configId) : null;
    }

    /**
     * 获取引用字段ID（兼容旧代码）
     * @deprecated 请使用 {@link #getRefFieldUuid()} 代替
     * @return 引用字段UUID
     */
    @Deprecated
    public String getRefFieldId() {
        return this.refFieldUuid;
    }

    /**
     * 设置引用字段ID（兼容旧代码）
     * @deprecated 请使用 {@link #setRefFieldUuid(String)} 代替
     * @param refFieldId 引用字段UUID
     */
    @Deprecated
    public void setRefFieldId(String refFieldId) {
        this.refFieldUuid = refFieldId;
    }

    /**
     * 设置引用字段ID（兼容旧代码，Long类型）
     * @deprecated 请使用 {@link #setRefFieldUuid(String)} 代替
     * @param refFieldId 引用字段ID
     */
    @Deprecated
    public void setRefFieldId(Long refFieldId) {
        this.refFieldUuid = refFieldId != null ? String.valueOf(refFieldId) : null;
    }
}


