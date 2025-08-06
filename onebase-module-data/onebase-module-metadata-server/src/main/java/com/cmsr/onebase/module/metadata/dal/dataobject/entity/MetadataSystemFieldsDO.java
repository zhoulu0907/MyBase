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
     * 是否启用(0:否,1:是)
     */
    private Integer isEnabled;

} 