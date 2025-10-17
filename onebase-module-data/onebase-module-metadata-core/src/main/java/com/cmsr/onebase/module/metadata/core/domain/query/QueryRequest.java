package com.cmsr.onebase.module.metadata.core.domain.query;

import java.util.List;

import lombok.Data;

/**
 * 查询请求领域模型
 * 
 * @author bty418
 * @date 2025-09-24
 */
@Data
public class QueryRequest {
    
    /**
     * 实体ID
     */
    private Long entityId;
    
    /**
     * 全局AND条件列表，这些条件会与所有 conditionGroups 形成 AND 关系
     * 最终逻辑：(andConditions[0] AND andConditions[1] AND ...) AND ((group1) OR (group2) OR ...)
     */
    private List<QueryCondition> andConditions;
    
    /**
     * 查询条件组：外层为OR关系，内层为AND关系
     */
    private List<List<QueryCondition>> conditionGroups;
    
    /**
     * 排序条件列表
     */
    private List<QueryOrder> orders;
    
    /**
     * 查询数量限制
     */
    private Integer limit;
}
