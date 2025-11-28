package com.cmsr.onebase.module.metadata.runtime.semantic.executor;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.runtime.semantic.adapter.SemanticRequestParser;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticMethodCodeEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticTargetBodyVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.BUSINESS_ENTITY_NOT_EXISTS;

@Component
public class SemanticDetailExecutor {
    @Resource
    private SemanticDataMethodExecutor semanticDataMethodExecutor;
    @Resource
    private SemanticRequestParser semanticRequestParser;
    @Resource
    private MetadataBusinessEntityCoreService businessEntityCoreService;

    public Map<String, Object> execute(Long entityId, Long menuId, String traceId, SemanticRecordDTO record) {
        return semanticDataMethodExecutor.executeDetail(entityId, menuId, traceId, record);
    }

    public Map<String, Object> execute(String entityCode, Long menuId, String traceId, SemanticTargetBodyVO body) {
        MetadataBusinessEntityDO entity = businessEntityCoreService.getBusinessEntityByCode(entityCode);
        if (entity == null) { throw exception(BUSINESS_ENTITY_NOT_EXISTS); }
        SemanticRecordDTO record = semanticRequestParser.parseTarget(entity, body, menuId, traceId, SemanticMethodCodeEnum.GET);
        return semanticDataMethodExecutor.executeDetail(entity.getId(), menuId, traceId, record);
    }
}
