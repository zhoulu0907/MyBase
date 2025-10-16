package com.cmsr.onebase.module.metadata.core.domain.query;

import lombok.Data;

/**
 * 查询排序领域模型
 * 
 * @author bty418
 * @date 2025-09-24
 */
@Data
public class QueryOrder {
    
    /**
     * 字段ID
     */
    private Long fieldId;
    
    /**
     * 字段名称
     */
    private String fieldName;
    
    /**
     * 排序方向：ASC/DESC
     */
    private String direction;
}
