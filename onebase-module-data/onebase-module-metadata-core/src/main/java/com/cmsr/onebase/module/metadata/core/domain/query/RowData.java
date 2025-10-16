package com.cmsr.onebase.module.metadata.core.domain.query;

import java.util.List;

import lombok.Data;

/**
 * 行数据领域模型
 * 
 * @author bty418
 * @date 2025-09-24
 */
@Data
public class RowData {
    
    /**
     * 行中的字段数据列表
     */
    private List<FieldData> fieldDataList;
    
    /**
     * 行标识
     */
    private String rowId;
}
