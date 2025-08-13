package com.cmsr.onebase.module.metadata.dal.dataobject.entity;

import jakarta.persistence.Table;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

/**
 * 实体字段表 DO
 */
@Table(name = "metadata_entity_field")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataEntityFieldDO extends TenantBaseDO {

    public MetadataEntityFieldDO setId(Long id) {
        super.setId(id);
        return this;
    }

    /**
     * 实体ID
     */
    private Long entityId;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 显示名称
     */
    private String displayName;

    /**
     * 字段类型
     */
    private String fieldType;

    /**
     * 数据长度
     */
    private Integer dataLength;

    /**
     * 小数位数
     */
    private Integer decimalPlaces;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 字段描述
     */
    private String description;

    /**
     * 是否系统字段：0-是，1-不是
     */
    private Integer isSystemField;

    /**
     * 是否主键：0-是，1-不是
     */
    private Integer isPrimaryKey;

    /**
     * 是否必填：0-是，1-不是
     */
    private Integer isRequired;

    /**
     * 是否唯一：0-是，1-不是
     */
    private Integer isUnique;

    /**
     * 是否允许空值：0-是，1-不是
     */
    private Integer allowNull;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 校验规则配置
     */
    private String validationRules;

    /**
     * 运行模式：0 编辑态，1 运行态
     */
    private Integer runMode;

    /**
     * 应用ID
     */
    private Long appId;

    /**
     * 字段状态 0：开启，1：关闭
     */
    private Integer status;

    /**
     * 字段编码
     */
    private String fieldCode;

}
