package com.cmsr.onebase.module.metadata.core.dal.dataobject.validation;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字段校验-子表非空规则 DO
 * 对应表：metadata_validation_child_not_empty
 *
 * @author bty418
 * @date 2025-08-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "metadata_validation_child_not_empty")
public class MetadataValidationChildNotEmptyDO extends BaseBizEntity {

    /**
     * 子表非空校验UUID
     * <p>
     * 用于跨应用、跨版本的唯一标识，与 application_id、version_tag 组成联合唯一约束
     */
    @Column(value = "child_not_empty_uuid", comment = "子表非空校验UUID")
    private String childNotEmptyUuid;

    /**
     * 规则组UUID
     * <p>
     * 关联 metadata_validation_rule_group.group_uuid
     */
    @Column(value = "group_uuid", comment = "规则组UUID")
    private String groupUuid;

    /**
     * 实体UUID
     * <p>
     * 关联 metadata_business_entity.entity_uuid
     */
    @Column(value = "entity_uuid", comment = "实体UUID")
    private String entityUuid;

    /**
     * 字段UUID
     * <p>
     * 关联 metadata_entity_field.field_uuid
     */
    @Column(value = "field_uuid", comment = "字段UUID")
    private String fieldUuid;

    /**
     * 子实体UUID
     * <p>
     * 关联 metadata_business_entity.entity_uuid
     */
    @Column(value = "child_entity_uuid", comment = "子实体UUID")
    private String childEntityUuid;

    @Column(value = "is_enabled", comment = "是否启用：1-启用，0-禁用")
    private Integer isEnabled;

    @Column(value = "min_rows", comment = "最小行数")
    private Integer minRows;

    @Column(value = "prompt_message", comment = "提示信息")
    private String promptMessage;

    // 注意：applicationId 和 versionTag 字段已在父类 BaseBizEntity 中定义，
    // 此处删除重复定义以避免字段冲突问题

}
