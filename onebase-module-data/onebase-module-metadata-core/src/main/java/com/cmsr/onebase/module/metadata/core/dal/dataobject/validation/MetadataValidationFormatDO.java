package com.cmsr.onebase.module.metadata.core.dal.dataobject.validation;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字段校验-格式规则 DO（内置格式/自定义正则）
 * 对应表：metadata_validation_format
 *
 * @author bty418
 * @date 2025-08-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "metadata_validation_format")
public class MetadataValidationFormatDO extends BaseBizEntity {

    /**
     * 格式校验UUID
     * <p>
     * 用于跨应用、跨版本的唯一标识，与 application_id、version_tag 组成联合唯一约束
     */
    @Column(value = "format_uuid", comment = "格式校验UUID")
    private String formatUuid;

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

    @Column(value = "is_enabled", comment = "是否启用：1-启用，0-禁用")
    private Integer isEnabled;

    @Column(value = "format_code", comment = "格式编码：REGEX/EMAIL/MOBILE/...")
    private String formatCode;

    @Column(value = "regex_pattern", comment = "正则表达式")
    private String regexPattern;

    @Column(value = "flags", comment = "正则标志：i/m/s")
    private String flags;

    @Column(value = "prompt_message", comment = "提示信息")
    private String promptMessage;

    @Column(value = "version_tag", comment = "版本标识")
    private Long versionTag;

    @Column(value = "application_id", comment = "应用ID")
    private Long applicationId;

}
