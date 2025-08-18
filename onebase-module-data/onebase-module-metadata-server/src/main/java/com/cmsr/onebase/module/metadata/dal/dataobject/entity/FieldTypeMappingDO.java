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

    // 列名常量
    public static final String BUSINESS_FIELD_TYPE    = "business_field_type";
    public static final String BUSINESS_MEANING       = "business_meaning";
    public static final String DATABASE_TYPE          = "database_type";
    public static final String DATABASE_FIELD         = "database_field";
    public static final String IS_DEFAULT             = "is_default";
    public static final String DEFAULT_LENGTH         = "default_length";
    public static final String DEFAULT_DECIMAL_PLACES = "default_decimal_places";

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