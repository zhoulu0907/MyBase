package com.cmsr.onebase.module.metadata.dal.dataobject.validation;

import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 元数据校验类型表 DO
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metadata_validation_type")
public class MetadataValidationTypeDO extends BaseDO {

    public MetadataValidationTypeDO setId(Long id) {
        super.setId(id);
        return this;
    }

    /**
     * 校验类型编码
     */
    private String validationCode;

    /**
     * 校验类型名称
     */
    private String validationName;

    /**
     * 校验类型描述
     */
    private String validationDesc;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 状态：0-启用，1-禁用
     */
    private Integer status;

    /**
     * 类型（给应用过滤用）
     */
    private Integer type;

}
