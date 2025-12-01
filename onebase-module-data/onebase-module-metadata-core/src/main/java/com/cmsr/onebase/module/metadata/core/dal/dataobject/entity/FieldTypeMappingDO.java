package com.cmsr.onebase.module.metadata.core.dal.dataobject.entity;

import com.cmsr.onebase.framework.orm.entity.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字段类型映射表 DO
 *
 * @author mickey
 * @date 2025/1/27 10:30
 */
@Table(value = "metadata_field_type_mapping")
@Data
@EqualsAndHashCode(callSuper = true)
public class FieldTypeMappingDO extends BaseEntity {

    /**
     * 业务字段类型
     */
    @Column(value = "business_field_type", comment = "业务字段类型")
    private String businessFieldType;

    /**
     * 业务含义
     */
    @Column(value = "business_meaning", comment = "业务含义")
    private String businessMeaning;

    /**
     * 数据库类型
     */
    @Column(value = "database_type", comment = "数据库类型")
    private String databaseType;

    /**
     * 数据库中对应的字段
     */
    @Column(value = "database_field", comment = "数据库中对应的字段")
    private String databaseField;

    /**
     * 默认还是备选：0-备选，1-默认
     */
    @Column(value = "is_default", comment = "默认还是备选：0-备选，1-默认")
    private Integer isDefault;

    /**
     * 默认长度
     */
    @Column(value = "default_length", comment = "默认长度")
    private Integer defaultLength;

    /**
     * 默认小数点后长度
     */
    @Column(value = "default_decimal_places", comment = "默认小数点后长度")
    private Integer defaultDecimalPlaces;

}
