package com.cmsr.onebase.module.metadata.core.service.datamethod;

import com.cmsr.onebase.module.metadata.core.domain.query.MetadataDataMethodRequestContext;

import java.util.Map;

public interface MetadataDataMethodCoreServiceV2 {


    /**
     * 元数据的系统方法执行逻辑（n个系统方法）
     * @param operationType
     * @param entityId
     * @param data
     * @param methodCode
     * @return
     */
    Map<String, Object> executeProcess(MetadataDataMethodRequestContext methodCoreContext);
}
