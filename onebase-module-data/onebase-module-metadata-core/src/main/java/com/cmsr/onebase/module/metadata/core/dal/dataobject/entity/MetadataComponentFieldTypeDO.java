package com.cmsr.onebase.module.metadata.core.dal.dataobject.entity;

import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 元数据组件字段类型表 DO
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metadata_component_field_type")
public class MetadataComponentFieldTypeDO extends BaseDO {

    // 列名常量
    public static final String FIELD_TYPE_CODE = "field_type_code";
    public static final String FIELD_TYPE_NAME = "field_type_name";
    public static final String FIELD_TYPE_DESC = "field_type_desc";
    public static final String DATA_TYPE        = "data_type";
    public static final String SORT_ORDER      = "sort_order";
    public static final String STATUS          = "status";
    public static final String TYPE            = "type";

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
     * jdbc数据类型
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
