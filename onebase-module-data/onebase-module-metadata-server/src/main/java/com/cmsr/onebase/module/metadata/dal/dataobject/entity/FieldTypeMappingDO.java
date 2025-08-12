package com.cmsr.onebase.module.metadata.dal.dataobject.entity;

import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.persistence.Table;
import lombok.*;

/**
 * @ClassName FieldTypeMappingDO
 * @Description 字段类型映射表 DO
 * @Author mickey
 * @Date 2025/1/27 10:30
 */
@Table(name = "metadata_field_type_mapping")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class FieldTypeMappingDO extends BaseDO {

    /**
     * 业务字段类型
     */
    private String businessFieldType;

    /**
     * 业务含义
     */
    private String businessMeaning;

    /**
     * 数据库类型
     */
    private String databaseType;

    /**
     * 数据库中对应的字段
     */
    private String databaseField;

    /**
     * 默认还是备选：0-备选，1-默认
     */
    private Integer isDefault;

    /**
     * 默认长度
     */
    private Integer defaultLength;

    /**
     * 默认小数点后长度
     */
    private Integer defaultDecimalPlaces;

} 