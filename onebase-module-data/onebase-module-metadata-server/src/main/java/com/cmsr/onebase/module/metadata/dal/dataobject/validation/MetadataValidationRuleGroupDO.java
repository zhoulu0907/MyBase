package com.cmsr.onebase.module.metadata.dal.dataobject.validation;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 规则组表 DO
 *
 * @author bty418
 * @date 2025-08-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metadata_validation_rule_group")
public class MetadataValidationRuleGroupDO extends TenantBaseDO {

    // 列名常量
    public static final String RG_NAME     = "rg_name";
    public static final String RG_DESC     = "rg_desc";
    public static final String RG_STATUS   = "rg_status";
    public static final String VAL_METHOD  = "val_method";
    public static final String POP_PROMPT  = "pop_prompt";
    public static final String POP_TYPE    = "pop_type";

    public MetadataValidationRuleGroupDO setId(Long id) {
        super.setId(id);
        return this;
    }

    /**
     * 规则组名称，如"客户信用评级规则"
     */
    private String rgName;

    /**
     * 规则组描述
     */
    private String rgDesc;

    /**
     * 状态："ACTIVE"（启用）/"INACTIVE"（禁用）
     */
    private String rgStatus;

    /**
     * 校验方式，如：满足条件时，不允许提交表单，并弹窗提示
     */
    private String valMethod;

    /**
     * 弹窗提示内容
     */
    private String popPrompt;


    /**
     * 弹窗类型，如：短提示弹窗，长提示弹窗等
     */
    private String popType;

}
