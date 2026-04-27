package com.cmsr.onebase.module.metadata.core.domain.query;

import java.util.List;

import lombok.Data;

/**
 * 查询条件领域模型
 * 
 * @author bty418
 * @date 2025-09-24
 */
@Data
public class QueryCondition {
    
    /**
     * 字段ID
     */
    private Long fieldId;
    
    /**
     * 字段名称
     */
    private String fieldName;
    
    /**
     * 操作符
     */
    private String operator;
    
    /**
     * 条件值列表
     */
    private List<String> fieldValues;
    
    /**
     * 字段类型
     */
    private String fieldType;
}
