package com.cmsr.onebase.module.metadata.core.domain.query;

import lombok.Data;

/**
 * 字段数据领域模型
 * 
 * @author bty418
 * @date 2025-09-24
 */
@Data
public class FieldData {
    
    /**
     * 字段ID
     */
    private Long fieldId;
    
    /**
     * 字段名称
     */
    private String fieldName;
    
    /**
     * 显示名称
     */
    private String displayName;
    
    /**
     * 字段类型
     */
    private String fieldType;
    
    /**
     * 字段值
     */
    private Object fieldValue;
}
