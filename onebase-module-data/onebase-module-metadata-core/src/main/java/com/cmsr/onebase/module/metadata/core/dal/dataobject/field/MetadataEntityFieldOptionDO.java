package com.cmsr.onebase.module.metadata.core.dal.dataobject.field;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
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
public class MetadataEntityFieldOptionDO extends BaseBizEntity {

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
     * 版本标识
     * <p>
     * 用于跨应用、跨版本的唯一标识，与 application_id 组成联合唯一约束
     */
    @Column(value = "version_tag", comment = "版本标识")
    private Long versionTag;

}

