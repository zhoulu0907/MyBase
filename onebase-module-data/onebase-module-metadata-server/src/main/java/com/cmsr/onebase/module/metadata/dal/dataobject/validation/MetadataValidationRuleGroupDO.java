package com.cmsr.onebase.module.metadata.dal.dataobject.validation;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metadata_validation_rule_group")
public class MetadataValidationRuleGroupDO extends TenantBaseDO {

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

}
