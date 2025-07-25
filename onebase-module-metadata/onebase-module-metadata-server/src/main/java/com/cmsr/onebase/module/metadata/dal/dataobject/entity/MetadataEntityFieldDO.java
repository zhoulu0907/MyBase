package com.cmsr.onebase.module.metadata.dal.dataobject.entity;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

/**
 * 实体字段表 DO
 */
@TableName(value = "metadata_entity_field")
@KeySequence("metadata_entity_field_seq")
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
     * 是否系统字段
     */
    private Boolean isSystemField;

    /**
     * 是否主键
     */
    private Boolean isPrimaryKey;

    /**
     * 是否必填
     */
    private Boolean isRequired;

    /**
     * 是否唯一
     */
    private Boolean isUnique;

    /**
     * 是否允许空值
     */
    private Boolean allowNull;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 校验规则配置
     */
    private Long validationRulesId;

    /**
     * 运行模式：0 编辑态，1 运行态
     */
    private Integer runMode;

    /**
     * 应用ID
     */
    private Long appId;

    /**
     * 版本锁标识
     */
    private Integer lockVersion;

}
