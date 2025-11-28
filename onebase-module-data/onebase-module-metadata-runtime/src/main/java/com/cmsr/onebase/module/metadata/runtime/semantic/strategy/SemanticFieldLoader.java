package com.cmsr.onebase.module.metadata.runtime.semantic.strategy;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.ENTITY_FIELD_NOT_EXISTS;

@Component
public class SemanticFieldLoader {
    @Resource
    private MetadataEntityFieldCoreService metadataEntityFieldService;

    public List<MetadataEntityFieldDO> load(Long entityId) {
        List<MetadataEntityFieldDO> fields = metadataEntityFieldService.getEntityFieldListByEntityId(entityId);
        if (fields == null || fields.isEmpty()) { throw exception(ENTITY_FIELD_NOT_EXISTS); }
        return fields;
    }
}

