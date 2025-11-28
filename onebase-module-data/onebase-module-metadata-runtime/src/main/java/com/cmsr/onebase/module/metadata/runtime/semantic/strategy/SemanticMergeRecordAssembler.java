package com.cmsr.onebase.module.metadata.runtime.semantic.strategy;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.runtime.semantic.adapter.SemanticRequestParser;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticMethodCodeEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.vo.SemanticMergeBodyVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.BUSINESS_ENTITY_NOT_EXISTS;

@Component
public class SemanticMergeRecordAssembler {

    @Resource
    private SemanticRequestParser semanticRequestParser;

    @Resource
    private MetadataBusinessEntityCoreService businessEntityCoreService;

    public SemanticRecordDTO assemble(String tableName, SemanticMergeBodyVO body, Long menuId, String traceId) {
        MetadataBusinessEntityDO entity = businessEntityCoreService.getBusinessEntityByTableName(tableName);
        if (entity == null) { throw exception(BUSINESS_ENTITY_NOT_EXISTS); }
        return semanticRequestParser.parseMerge(entity, body, menuId, traceId, SemanticMethodCodeEnum.CREATE);
    }
}

