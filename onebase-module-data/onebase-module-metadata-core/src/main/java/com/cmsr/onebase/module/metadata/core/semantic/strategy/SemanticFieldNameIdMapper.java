package com.cmsr.onebase.module.metadata.core.semantic.strategy;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Deprecated
public class SemanticFieldNameIdMapper {
    @Resource
    private MetadataEntityFieldCoreService metadataEntityFieldService;

    public Map<String, Object> convertNameToId(String entityUuid, Map<String, Object> data) {
        if (data == null) { return new HashMap<>(); }
        Map<String, Object> result = new HashMap<>();
        List<MetadataEntityFieldDO> fields = metadataEntityFieldService.getEntityFieldListByEntityUuid(entityUuid);
        Map<String, String> nameToId = new HashMap<>();
        for (MetadataEntityFieldDO f : fields) {
            if (f.getFieldName() != null) { nameToId.put(f.getFieldName(), f.getFieldUuid()); }
        }
        for (Map.Entry<String,Object> e : data.entrySet()) {
            String key = e.getKey();
            Object val = e.getValue();
            String id = nameToId.get(key);
            if (id != null) { result.put(id, val); } else { result.put(key, val); }
        }
        return result;
    }
}

