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
     * 配置ID
     */
    @Column(value = "config_id", comment = "配置ID")
    private Long configId;

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
     * 引用字段ID
     */
    @Column(value = "ref_field_id", comment = "引用字段ID")
    private Long refFieldId;

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
}


