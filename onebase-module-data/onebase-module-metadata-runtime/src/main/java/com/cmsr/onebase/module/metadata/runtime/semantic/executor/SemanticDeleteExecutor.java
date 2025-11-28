package com.cmsr.onebase.module.metadata.runtime.semantic.executor;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.runtime.semantic.adapter.SemanticRequestParser;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.RecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.MethodCodeEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticTargetBodyVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.BUSINESS_ENTITY_NOT_EXISTS;

@Component
public class SemanticDeleteExecutor {
    @Resource
    private SemanticDataMethodExecutor semanticDataMethodExecutor;
    @Resource
    private SemanticRequestParser semanticRequestParser;
    @Resource
    private MetadataBusinessEntityCoreService businessEntityCoreService;

    public Boolean execute(Long entityId, Long menuId, String traceId, RecordDTO record) {
        return semanticDataMethodExecutor.executeDelete(entityId, menuId, traceId, record);
    }

    public Boolean execute(String entityCode, Long menuId, String traceId, SemanticTargetBodyVO body) {
        MetadataBusinessEntityDO entity = businessEntityCoreService.getBusinessEntityByCode(entityCode);
        if (entity == null) { throw exception(BUSINESS_ENTITY_NOT_EXISTS); }
        RecordDTO record = semanticRequestParser.parseTarget(entity.getId(), body, menuId, traceId, MethodCodeEnum.DELETE);
        return semanticDataMethodExecutor.executeDelete(entity.getId(), menuId, traceId, record);
    }
}
