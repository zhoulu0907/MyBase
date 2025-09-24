package com.cmsr.onebase.module.metadata.core.domain.query;

import java.util.List;

import lombok.Data;

/**
 * 查询结果领域模型
 * 
 * @author bty418
 * @date 2025-09-24
 */
@Data
public class QueryResult {
    
    /**
     * 行数据列表（每个RowData包含一行的所有字段数据）
     */
    private List<RowData> rowDataList;
    
    /**
     * 总数量
     */
    private Long total;
}
