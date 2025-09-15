package com.cmsr.onebase.module.metadata.core.dal.dataobject.validation;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 字段校验-必填规则 DO
 * 对应表：metadata_validation_required
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metadata_validation_required")
public class MetadataValidationRequiredDO extends TenantBaseDO {

    // 列名常量
    public static final String GROUP_ID       = "group_id";
    public static final String ENTITY_ID      = "entity_id";
    public static final String FIELD_ID       = "field_id";
    public static final String IS_ENABLED     = "is_enabled";
    public static final String PROMPT_MESSAGE = "prompt_message";
    public static final String RUN_MODE       = "run_mode";
    public static final String APP_ID         = "app_id";

    public MetadataValidationRequiredDO setId(Long id) {
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
