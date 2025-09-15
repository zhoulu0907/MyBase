package com.cmsr.onebase.module.metadata.core.dal.dataobject.number;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
/**
 * 自动编号-规则项 DO
 * 对应表：metadata_auto_number_rule_item
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metadata_auto_number_rule_item")
public class MetadataAutoNumberRuleItemDO extends TenantBaseDO {

    public static final String CONFIG_ID = "config_id";
    public static final String ITEM_TYPE = "item_type";
    public static final String ITEM_ORDER = "item_order";
    public static final String FORMAT = "format";
    public static final String TEXT_VALUE = "text_value";
    public static final String REF_FIELD_ID = "ref_field_id";
    public static final String IS_ENABLED = "is_enabled";
    public static final String APP_ID = "app_id";

    @Column(name = CONFIG_ID)
    private Long configId;
    @Column(name = ITEM_TYPE)
    private String itemType;
    @Column(name = ITEM_ORDER)
    private Integer itemOrder;
    @Column(name = FORMAT)
    private String format;
    @Column(name = TEXT_VALUE)
    private String textValue;
    @Column(name = REF_FIELD_ID)
    private Long refFieldId;
    /**
     * 是否启用：1-启用，0-禁用
     * @see CommonStatusEnum
     */
    @Column(name = IS_ENABLED)
    private Integer isEnabled;
    @Column(name = APP_ID)
    private Long appId;
}


