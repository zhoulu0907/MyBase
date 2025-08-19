package com.cmsr.onebase.module.metadata.dal.dataobject.field;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metadata_entity_field_option")
public class MetadataEntityFieldOptionDO extends TenantBaseDO {

    // 列名常量
    public static final String FIELD_ID = "field_id";
    public static final String OPTION_LABEL = "option_label";
    public static final String OPTION_VALUE = "option_value";
    public static final String OPTION_ORDER = "option_order";
    public static final String IS_ENABLED = "is_enabled";
    public static final String DESCRIPTION = "description";
    public static final String APP_ID = "app_id";

    /**
     * 关联字段ID（metadata_entity_field.id）
     */
    @Column(name = FIELD_ID)
    private Long fieldId;

    /**
     * 选项显示名称
     */
    @Column(name = OPTION_LABEL)
    private String optionLabel;

    /**
     * 选项值（字段内唯一）
     */
    @Column(name = OPTION_VALUE)
    private String optionValue;

    /**
     * 选项排序
     */
    @Column(name = OPTION_ORDER)
    private Integer optionOrder;

    /**
     * 是否启用：0-是，1-否
     */
    @Column(name = IS_ENABLED)
    private Integer isEnabled;

    /**
     * 说明
     */
    @Column(name = DESCRIPTION)
    private String description;

    /**
     * 应用ID
     */
    @Column(name = APP_ID)
    private Long appId;
}


