package com.cmsr.onebase.module.metadata.core.dal.dataobject.validation;

import com.cmsr.onebase.framework.orm.entity.BaseBizEntity;
import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.cmsr.onebase.module.metadata.core.enums.ValidationStatusEnum;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 规则组表 DO
 *
 * @author bty418
 * @date 2025-08-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "metadata_validation_rule_group")
public class MetadataValidationRuleGroupDO extends BaseBizEntity {

    /**
     * 规则组UUID
     * <p>
     * 用于跨应用、跨版本的唯一标识，与 application_id、version_tag 组成联合唯一约束
     */
    @Column(value = "group_uuid", comment = "规则组UUID")
    private String groupUuid;

    /**
     * 规则组名称，如"客户信用评级规则"
     */
    @Column(value = "rg_name", comment = "规则组名称")
    private String rgName;

    /**
     * 规则组描述
     */
    @Column(value = "rg_desc", comment = "规则组描述")
    private String rgDesc;

    /**
     * 状态：1-激活，0-非激活
     * @see ValidationStatusEnum
     */
    @Column(value = "rg_status", comment = "状态：1-激活，0-非激活")
    private Integer rgStatus;

    /**
     * 校验方式，如：满足条件时，不允许提交表单，并弹窗提示
     */
    @Column(value = "val_method", comment = "校验方式")
    private String valMethod;

    /**
     * 弹窗提示内容
     */
    @Column(value = "pop_prompt", comment = "弹窗提示内容")
    private String popPrompt;

    /**
     * 弹窗类型，如：短提示弹窗，长提示弹窗等
     */
    @Column(value = "pop_type", comment = "弹窗类型")
    private String popType;

    /**
     * 校验类型：REQUIRED / UNIQUE / LENGTH / RANGE / FORMAT / CHILD_NOT_EMPTY / SELF_DEFINED
     */
    @Column(value = "validation_type", comment = "校验类型")
    private String validationType;

    /**
     * 实体UUID
     * <p>
     * 关联 metadata_business_entity.entity_uuid
     */
    @Column(value = "entity_uuid", comment = "实体UUID")
    private String entityUuid;

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
