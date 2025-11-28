package com.cmsr.onebase.module.metadata.runtime.semantic.strategy;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SemanticFieldNameIdMapper {
    @Resource
    private MetadataEntityFieldCoreService metadataEntityFieldService;

    public Map<String, Object> convertNameToId(Long entityId, Map<String, Object> data) {
        if (data == null) { return new HashMap<>(); }
        Map<String, Object> result = new HashMap<>();
        List<MetadataEntityFieldDO> fields = metadataEntityFieldService.getEntityFieldListByEntityId(entityId);
        Map<String, Long> nameToId = new HashMap<>();
        for (MetadataEntityFieldDO f : fields) {
            if (f.getFieldName() != null) { nameToId.put(f.getFieldName(), f.getId()); }
        }
        for (Map.Entry<String,Object> e : data.entrySet()) {
            String key = e.getKey();
            Object val = e.getValue();
            Long id = nameToId.get(key);
            if (id != null) { result.put(String.valueOf(id), val); } else { result.put(key, val); }
        }
        return result;
    }
}

