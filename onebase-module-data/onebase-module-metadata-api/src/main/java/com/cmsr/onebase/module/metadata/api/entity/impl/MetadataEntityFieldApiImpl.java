package com.cmsr.onebase.module.metadata.api.entity.impl;

import com.cmsr.onebase.module.metadata.api.entity.MetadataEntityFieldApi;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldQueryReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldRespDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeRespDTO;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 实体字段管理 API 默认实现
 *
 * @author matianyu
 * @date 2025-09-12
 */
@Service
public class MetadataEntityFieldApiImpl implements MetadataEntityFieldApi {

    @Resource
    private MetadataEntityFieldCoreService metadataEntityFieldService;

    @Override
    public List<EntityFieldRespDTO> getEntityFieldList(@Valid @RequestBody EntityFieldQueryReqDTO reqDTO) {
        // TODO: 这里需要实现查询逻辑，包括DO到DTO的转换
        throw new UnsupportedOperationException("此方法需要在后续实现");
    }

    @Override
    public List<EntityFieldJdbcTypeRespDTO> getFieldJdbcTypes(@Valid @RequestBody EntityFieldJdbcTypeReqDTO reqDTO) {
        List<Long> fieldIds = reqDTO != null ? reqDTO.getFieldIds() : null;
        Map<Long, Map<String, String>> fieldTypeInfo = metadataEntityFieldService.getFieldJdbcTypesWithFieldType(fieldIds);
        
        List<EntityFieldJdbcTypeRespDTO> result = new ArrayList<>();
        for (Map.Entry<Long, Map<String, String>> entry : fieldTypeInfo.entrySet()) {
            Long fieldId = entry.getKey();
            Map<String, String> typeInfo = entry.getValue();
            String jdbcType = typeInfo.get("jdbcType");
            String fieldType = typeInfo.get("fieldType");
            
            EntityFieldJdbcTypeRespDTO dto = new EntityFieldJdbcTypeRespDTO();
            dto.setFieldId(fieldId);
            dto.setJdbcType(jdbcType);
            dto.setFieldType(fieldType);
            result.add(dto);
        }
        
        return result;
    }
}
