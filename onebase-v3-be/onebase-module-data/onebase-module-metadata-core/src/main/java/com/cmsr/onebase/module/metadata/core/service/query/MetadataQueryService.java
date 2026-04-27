package com.cmsr.onebase.module.metadata.core.service.query;

import com.cmsr.onebase.module.metadata.core.domain.query.QueryRequest;
import com.cmsr.onebase.module.metadata.core.domain.query.QueryResult;

/**
 * 元数据查询服务接口
 * 
 * @author bty418
 * @date 2025-09-24
 */
public interface MetadataQueryService {
    
    /**
     * 根据条件查询数据
     * 
     * @param queryRequest 查询请求
     * @return 查询结果
     */
    QueryResult queryByConditions(QueryRequest queryRequest);
}
