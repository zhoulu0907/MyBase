package com.cmsr.onebase.module.metadata.dal.dataobject.entity;

import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.persistence.Table;
import lombok.*;

/**
 * @ClassName MetadataSystemFieldsDO
 * @Description 元数据系统字段维护表 DO
 * @Author mickey
 * @Date 2025/1/27 10:30
 */
@Table(name = "metadata_system_fields")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetadataSystemFieldsDO extends BaseDO {

    // 列名常量
    public static final String FIELD_NAME       = "field_name";
    public static final String FIELD_TYPE       = "field_type";
    public static final String IS_SNOWFLAKE_ID  = "is_snowflake_id";
    public static final String IS_REQUIRED      = "is_required";
    public static final String DEFAULT_VALUE    = "default_value";
    public static final String DESCRIPTION      = "description";
    public static final String IS_ENABLED      = "is_enabled";

    /**
     * id
     */
    private Long id;

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 字段类型
     */
    private String fieldType;

    /**
     * 是否为雪花ID(0:否,1:是)
     */
    private Integer isSnowflakeId;

    /**
     * 是否必填(0:否,1:是)
     */
    private Integer isRequired;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 字段说明
     */
    private String description;

    /**
     * 是否启用(0:是,1:否)
     */
    private Integer isEnabled;

} 