package com.cmsr.onebase.module.metadata.runtime.semantic.strategy;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.runtime.semantic.adapter.SemanticRequestParser;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.RecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.MethodCodeEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticMergeBodyVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.BUSINESS_ENTITY_NOT_EXISTS;

@Component
public class MergeRecordAssembler {

    @Resource
    private SemanticRequestParser semanticRequestParser;

    @Resource
    private MetadataBusinessEntityCoreService businessEntityCoreService;

    public RecordDTO assemble(String entityCode, SemanticMergeBodyVO body, Long menuId, String traceId) {
        MetadataBusinessEntityDO entity = businessEntityCoreService.getBusinessEntityByCode(entityCode);
        if (entity == null) { throw exception(BUSINESS_ENTITY_NOT_EXISTS); }
        return semanticRequestParser.parseMerge(entity.getId(), body, menuId, traceId, MethodCodeEnum.CREATE);
    }
}

