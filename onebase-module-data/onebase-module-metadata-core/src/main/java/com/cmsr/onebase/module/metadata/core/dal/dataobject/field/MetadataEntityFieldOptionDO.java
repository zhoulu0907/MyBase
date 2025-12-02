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
     * 关联字段ID（metadata_entity_field.id）
     */
    @Column(value = "field_id", comment = "关联字段ID")
    private Long fieldId;

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
}


