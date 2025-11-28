package com.cmsr.onebase.module.metadata.core.dal.dataobject.validation;

import com.cmsr.onebase.framework.orm.entity.BaseTenantEntity;
import com.cmsr.onebase.module.metadata.core.enums.ValidationStatusEnum;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 规则定义表 DO
 *
 * @author bty418
 * @date 2025-08-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "metadata_validation_rule_definition")
public class MetadataValidationRuleDefinitionDO extends BaseTenantEntity {

    /**
     * 所属规则组ID，关联metadata_validation_rule_group表的id
     */
    @Column(value = "group_id", comment = "所属规则组ID")
    private Long groupId;

    /**
     * 父规则ID，用于层级关系；顶级规则为NULL
     */
    @Column(value = "parent_rule_id", comment = "父规则ID")
    private Long parentRuleId;

    /**
     * 关联的业务实体ID，关联metadata_business_entity表的id
     */
    @Column(value = "entity_id", comment = "关联的业务实体ID")
    private Long entityId;

    /**
     * 关联的实体字段ID，关联metadata_entity_field表的id
     */
    @Column(value = "field_id", comment = "关联的实体字段ID")
    private Long fieldId;

    /**
     * 逻辑类型："LOGIC"（逻辑操作符）/"CONDITION"（条件判断）
     */
    @Column(value = "logic_type", comment = "逻辑类型")
    private String logicType;

    /**
     * logic_type="CONDITION"时，取值为">"/"<"/"="/"IN"/"BETWEEN"等
     */
    @Column(value = "operator", comment = "操作符")
    private String operator;

    /**
     * 当logic_type="LOGIC"时，取值为"AND"/"OR"
     */
    @Column(value = "logic_operator", comment = "逻辑操作符")
    private String logicOperator;

    /**
     * 条件字段编码，如"AGE"、"INCOME"、"CUSTOMER_LEVEL"（仅logic_type="CONDITION"时有效）
     */
    @Column(value = "field_code", comment = "条件字段编码")
    private String fieldCode;

    /**
     * 条件值引用（单值条件或范围表达式的第一个）
     */
    @Column(value = "field_value", comment = "条件值引用")
    private Long fieldValue;

    /**
     * 条件值引用2（单值条件或范围表达式的第二个）
     */
    @Column(value = "field_value2", comment = "条件值引用2")
    private Long fieldValue2;

    /**
     * 状态：1-激活，0-非激活
     * @see ValidationStatusEnum
     */
    @Column(value = "status", comment = "状态：1-激活，0-非激活")
    private Integer status;

}
