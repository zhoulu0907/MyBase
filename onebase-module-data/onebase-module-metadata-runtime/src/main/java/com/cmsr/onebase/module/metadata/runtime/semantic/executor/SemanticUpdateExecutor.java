package com.cmsr.onebase.module.metadata.runtime.semantic.executor;

import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRecordDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
public class SemanticUpdateExecutor {
    @Resource
    private SemanticDataMethodExecutor semanticDataMethodExecutor;
    @Resource
    private MetadataBusinessEntityCoreService businessEntityCoreService;

    public Map<String, Object> execute(Long entityId, Long menuId, String traceId, SemanticRecordDTO record) {
        return semanticDataMethodExecutor.executeUpdate(entityId, menuId, traceId, record);
    }

    // public Map<String, Object> execute(String entityCode, Long menuId, String traceId, SemanticMergeBodyVO body) {
    //     MetadataBusinessEntityDO entity = businessEntityCoreService.getBusinessEntityByCode(entityCode);
    //     if (entity == null) { throw exception(BUSINESS_ENTITY_NOT_EXISTS); }
    //     SemanticRecordDTO record = semanticRequestParser.parseMerge(entity, body, menuId, traceId, SemanticMethodCodeEnum.UPDATE);
    //     return semanticDataMethodExecutor.executeUpdate(entity.getId(), menuId, traceId, record);
    // }
}
