package com.cmsr.onebase.module.metadata.dal.dataobject.validation;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

/**
 * 校验规则表 DO
 */
@TableName(value = "metadata_validation_rule")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataValidationRuleDO extends TenantBaseDO {

    public MetadataValidationRuleDO setId(Long id) {
        super.setId(id);
        return this;
    }

    /**
     * 规则名称
     */
    private String validationName;

    /**
     * 规则编码
     */
    private String validationCode;

    /**
     * 关联实体ID
     */
    private Long entityId;

    /**
     * 关联字段ID
     */
    private Long fieldId;

    /**
     * 校验条件(=,<,>,<=,>=,LIKE,IN,NOT IN,BETWEEN,NOT BETWEEN,IS NULL,IS NOT NULL,etc)
     */
    private String validationCondition;

    /**
     * 校验类型(字段，变量等)
     */
    private String validationType;

    /**
     * 校验比较对象
     */
    private String validationTargetObject;

    /**
     * 校验表达式
     */
    private String validationExpression;

    /**
     * 错误提示信息
     */
    private String errorMessage;

    /**
     * 校验时机(更新时,新增时)
     */
    private String validationTiming;

    /**
     * 执行顺序
     */
    private Integer sortOrder;

    /**
     * 运行模式：0 编辑态，1 运行态
     */
    private Integer runMode;

    /**
     * 应用ID
     */
    private Long appId;



}
