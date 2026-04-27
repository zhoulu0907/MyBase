package com.cmsr.onebase.module.metadata.core.semantic.strategy;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticPageBodyVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.BUSINESS_ENTITY_NOT_EXISTS;

@Component
@Deprecated
public class SemanticPageRecordAssembler {

    // @Resource
    // private SemanticRequestParser semanticRequestParser;

    @Resource
    private MetadataBusinessEntityCoreService businessEntityCoreService;

    public SemanticRecordDTO assemble(String entityCode, SemanticPageBodyVO body, Long menuId, String traceId) {
        MetadataBusinessEntityDO entity = businessEntityCoreService.getBusinessEntityByCode(entityCode);
        if (entity == null) { throw exception(BUSINESS_ENTITY_NOT_EXISTS); }
        // return semanticRequestParser.parsePage(entity, body, menuId, traceId, SemanticMethodCodeEnum.GET_PAGE);
        return null;
    }
}
