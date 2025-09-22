package com.cmsr.onebase.module.metadata.core.dal.dataobject.validation;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 字段校验-唯一规则 DO
 * 对应表：metadata_validation_unique
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metadata_validation_unique")
public class MetadataValidationUniqueDO extends TenantBaseDO {

    public static final String GROUP_ID       = "group_id";
    public static final String ENTITY_ID      = "entity_id";
    public static final String FIELD_ID       = "field_id";
    public static final String IS_ENABLED     = "is_enabled";
    public static final String UNIQUE_SCOPE   = "unique_scope";
    public static final String IGNORE_NULL    = "ignore_null";
    public static final String CASE_SENSITIVE = "case_sensitive";
    public static final String PROMPT_MESSAGE = "prompt_message";
    public static final String RUN_MODE       = "run_mode";
    public static final String APP_ID         = "app_id";

    public MetadataValidationUniqueDO setId(Long id) {
        super.setId(id);
        return this;
    }

    private Long groupId;
    private Long entityId;
    private Long fieldId;
    private Integer isEnabled;
    private String promptMessage;
    private Integer runMode;
    private Long appId;
}
