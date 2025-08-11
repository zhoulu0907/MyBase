package com.cmsr.onebase.module.metadata.dal.dataobject.entity;

import com.cmsr.onebase.framework.data.base.BaseDO;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 元数据组件字段类型表 DO
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metadata_component_field_type")
public class MetadataComponentFieldTypeDO extends BaseDO {

    public MetadataComponentFieldTypeDO setId(Long id) {
        super.setId(id);
        return this;
    }

    /**
     * 字段类型编码
     */
    private String fieldTypeCode;

    /**
     * 字段类型名称
     */
    private String fieldTypeName;

    /**
     * 字段类型描述
     */
    private String fieldTypeDesc;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;

    /**
     * 类型（给应用过滤用）
     */
    private Integer type;

}
