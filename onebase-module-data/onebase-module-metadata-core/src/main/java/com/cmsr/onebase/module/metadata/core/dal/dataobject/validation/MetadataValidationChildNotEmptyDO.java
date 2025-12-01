package com.cmsr.onebase.module.metadata.core.dal.dataobject.validation;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
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
public class MetadataValidationChildNotEmptyDO extends BaseTenantEntity {

    @Column(value = "group_id", comment = "规则组ID")
    private Long groupId;

    @Column(value = "entity_id", comment = "实体ID")
    private Long entityId;

    @Column(value = "field_id", comment = "字段ID")
    private Long fieldId;

    @Column(value = "child_entity_id", comment = "子实体ID")
    private Long childEntityId;

    @Column(value = "is_enabled", comment = "是否启用：1-启用，0-禁用")
    private Integer isEnabled;

    @Column(value = "min_rows", comment = "最小行数")
    private Integer minRows;

    @Column(value = "prompt_message", comment = "提示信息")
    private String promptMessage;

    @Column(value = "version_tag", comment = "版本标识")
    private Long versionTag;

    @Column(value = "application_id", comment = "应用ID")
    private Long applicationId;
}
