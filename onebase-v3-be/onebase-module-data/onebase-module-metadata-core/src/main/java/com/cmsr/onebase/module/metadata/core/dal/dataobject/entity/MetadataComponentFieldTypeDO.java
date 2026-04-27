package com.cmsr.onebase.module.metadata.core.dal.dataobject.entity;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 元数据组件字段类型表 DO
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "metadata_component_field_type")
public class MetadataComponentFieldTypeDO extends BaseEntity {

    /**
     * 字段类型编码
     */
    @Column(value = "field_type_code", comment = "字段类型编码")
    private String fieldTypeCode;

    /**
     * 字段类型名称
     */
    @Column(value = "field_type_name", comment = "字段类型名称")
    private String fieldTypeName;

    /**
     * 字段类型描述
     */
    @Column(value = "field_type_desc", comment = "字段类型描述")
    private String fieldTypeDesc;

    /**
     * jdbc数据类型
     */
    @Column(value = "data_type", comment = "jdbc数据类型")
    private String dataType;

    /**
     * 排序顺序
     */
    @Column(value = "sort_order", comment = "排序顺序")
    private Integer sortOrder;

    /**
     * 状态：1-启用，0-禁用
     */
    @Column(value = "status", comment = "状态：1-启用，0-禁用")
    private Integer status;

    /**
     * 类型（给应用过滤用）
     */
    @Column(value = "type", comment = "类型")
    private Integer type;

}
