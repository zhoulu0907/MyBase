package com.cmsr.onebase.module.metadata.dal.dataobject.validation;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 规则定义表 DO
 *
 * @author bty418
 * @date 2025-08-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metadata_validation_rule_definition")
public class MetadataValidationRuleDefinitionDO extends TenantBaseDO {

    // 列名常量
    public static final String GROUP_ID        = "group_id";
    public static final String PARENT_RULE_ID  = "parent_rule_id";
    public static final String ENTITY_ID       = "entity_id";
    public static final String FIELD_ID        = "field_id";
    public static final String LOGIC_TYPE      = "logic_type";
    public static final String OPERATOR        = "operator";
    public static final String LOGIC_OPERATOR  = "logic_operator";
    public static final String FIELD_CODE      = "field_code";
    public static final String FIELD_VALUE     = "field_value";
    public static final String FIELD_VALUE2    = "field_value2";
    public static final String STATUS          = "status";

    public MetadataValidationRuleDefinitionDO setId(Long id) {
        super.setId(id);
        return this;
    }

    /**
     * 所属规则组ID，关联metadata_validation_rule_group表的id
     */
    private Long groupId;



    /**
     * 父规则ID，用于层级关系；顶级规则为NULL
     */
    private Long parentRuleId;

    /**
     * 关联的业务实体ID，关联metadata_business_entity表的id
     */
    private Long entityId;

    /**
     * 关联的实体字段ID，关联metadata_entity_field表的id
     */
    private Long fieldId;

    /**
     * 逻辑类型："LOGIC"（逻辑操作符）/"CONDITION"（条件判断）
     */
    private String logicType;

    /**
     * logic_type="CONDITION"时，取值为">"/"<"/"="/"IN"/"BETWEEN"等
     */
    private String operator;

    /**
     * 当logic_type="LOGIC"时，取值为"AND"/"OR"
     */
    private String logicOperator;

    /**
     * 条件字段编码，如"AGE"、"INCOME"、"CUSTOMER_LEVEL"（仅logic_type="CONDITION"时有效）
     */
    private String fieldCode;

    /**
     * 条件值引用（单值条件或范围表达式的第一个）
     */
    private Long fieldValue;

    /**
     * 条件值引用2（单值条件或范围表达式的第二个）
     */
    private Long fieldValue2;

    /**
     * 状态："ACTIVE"/"INACTIVE"
     */
    private String status;

}
