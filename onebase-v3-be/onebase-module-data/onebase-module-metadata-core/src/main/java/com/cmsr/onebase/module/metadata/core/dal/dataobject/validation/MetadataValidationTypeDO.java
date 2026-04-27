package com.cmsr.onebase.module.metadata.core.dal.dataobject.validation;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 元数据校验类型表 DO
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "metadata_validation_type")
public class MetadataValidationTypeDO extends BaseEntity {

    /**
     * 校验类型编码
     */
    @Column(value = "validation_code", comment = "校验类型编码")
    private String validationCode;

    /**
     * 校验类型名称
     */
    @Column(value = "validation_name", comment = "校验类型名称")
    private String validationName;

    /**
     * 校验类型描述
     */
    @Column(value = "validation_desc", comment = "校验类型描述")
    private String validationDesc;

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
