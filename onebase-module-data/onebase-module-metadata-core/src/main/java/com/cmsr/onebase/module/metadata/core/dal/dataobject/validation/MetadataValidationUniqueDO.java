package com.cmsr.onebase.module.metadata.core.dal.dataobject.validation;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字段校验-唯一规则 DO
 * 对应表：metadata_validation_unique
 *
 * @author bty418
 * @date 2025-08-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "metadata_validation_unique")
public class MetadataValidationUniqueDO extends BaseTenantEntity {

    @Column(value = "group_id", comment = "规则组ID")
    private Long groupId;

    @Column(value = "entity_id", comment = "实体ID")
    private Long entityId;

    @Column(value = "field_id", comment = "字段ID")
    private Long fieldId;

    @Column(value = "is_enabled", comment = "是否启用：1-启用，0-禁用")
    private Integer isEnabled;

    @Column(value = "prompt_message", comment = "提示信息")
    private String promptMessage;

    @Column(value = "run_mode", comment = "运行模式")
    private Integer runMode;

    @Column(value = "app_id", comment = "应用ID")
    private Long appId;
}
