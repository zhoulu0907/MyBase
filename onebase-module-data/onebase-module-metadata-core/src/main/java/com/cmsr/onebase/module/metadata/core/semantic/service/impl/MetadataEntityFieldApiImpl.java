package com.cmsr.onebase.module.metadata.core.semantic.service.impl;

import com.cmsr.onebase.module.metadata.api.entity.MetadataEntityFieldApi;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldQueryReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldRespDTO;

import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeRespDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldIdsReqDTO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
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

    @Resource
    private MetadataBusinessEntityCoreService metadataBusinessEntityCoreService;

    @Override
    public List<EntityFieldRespDTO> getEntityFieldList(@Valid @RequestBody EntityFieldQueryReqDTO reqDTO) {
        Long entityId = reqDTO.getEntityId();
        List<MetadataEntityFieldDO> fields = metadataEntityFieldService.getEntityFieldListByEntityId(entityId);
        if (fields == null || fields.isEmpty()) {
            return new ArrayList<>();
        }

        // 预取实体信息，用于补充 entityDisplayName 与 tableName
        MetadataBusinessEntityDO entity = metadataBusinessEntityCoreService.getBusinessEntity(entityId);
        String entityDisplayName = entity != null ? entity.getDisplayName() : null;
        String tableName = entity != null ? entity.getTableName() : null;

        // 按请求条件过滤（与 build 层保持语义一致的简化实现）
        List<MetadataEntityFieldDO> filtered = new ArrayList<>();
        for (MetadataEntityFieldDO f : fields) {
            // isSystemField 过滤
            if (reqDTO.getIsSystemField() != null) {
                Integer sys = f.getIsSystemField();
                if (sys == null || !reqDTO.getIsSystemField().equals(sys)) {
                    continue;
                }
            }
            // keyword 模糊匹配 field_name 或 display_name
            if (reqDTO.getKeyword() != null && !reqDTO.getKeyword().trim().isEmpty()) {
                String kw = reqDTO.getKeyword().trim().toLowerCase();
                String name = f.getFieldName() != null ? f.getFieldName().toLowerCase() : "";
                String dname = f.getDisplayName() != null ? f.getDisplayName().toLowerCase() : "";
                if (!(name.contains(kw) || dname.contains(kw))) {
                    continue;
                }
            }
            // fieldCode 模糊匹配
            if (reqDTO.getFieldCode() != null && !reqDTO.getFieldCode().trim().isEmpty()) {
                String codeFilter = reqDTO.getFieldCode().trim().toLowerCase();
                String code = f.getFieldCode() != null ? f.getFieldCode().toLowerCase() : "";
                if (!code.contains(codeFilter)) {
                    continue;
                }
            }
            // isPerson=1 时，仅保留人员字段（字段类型 USER）
            if (reqDTO.getIsPerson() != null && reqDTO.getIsPerson() == 1) {
                String type = f.getFieldType();
                if (type == null || !"USER".equalsIgnoreCase(type)) {
                    continue;
                }
            }

            filtered.add(f);
        }

        // DO -> DTO 映射，并补充 entityDisplayName 与 tableName
        List<EntityFieldRespDTO> result = new ArrayList<>(filtered.size());
        for (MetadataEntityFieldDO f : filtered) {
            result.add(convertToRespDTO(f, entityDisplayName, tableName));
        }
        return result;
    }

    @Override
    public List<EntityFieldJdbcTypeRespDTO> getFieldJdbcTypes(@Valid @RequestBody EntityFieldJdbcTypeReqDTO reqDTO) {
        List<Long> fieldIds = reqDTO != null ? reqDTO.getFieldIds() : null;
        Map<Long, Map<String, String>> fieldTypeInfo = metadataEntityFieldService
                .getFieldJdbcTypesWithFieldType(fieldIds);

        List<EntityFieldJdbcTypeRespDTO> result = new ArrayList<>();
        for (Map.Entry<Long, Map<String, String>> entry : fieldTypeInfo.entrySet()) {
            Long fieldId = entry.getKey();
            Map<String, String> typeInfo = entry.getValue();
            String jdbcType = typeInfo.get("jdbcType");
            String fieldType = typeInfo.get("fieldType");
            String fieldName = typeInfo.get("fieldName");

            EntityFieldJdbcTypeRespDTO dto = new EntityFieldJdbcTypeRespDTO();
            dto.setFieldId(fieldId);
            dto.setFieldName(fieldName);
            dto.setJdbcType(jdbcType);
            dto.setFieldType(fieldType);
            result.add(dto);
        }

        return result;
    }

    @Override
    public List<EntityFieldRespDTO> getEntityFieldsByIds(@Valid @RequestBody EntityFieldIdsReqDTO reqDTO) {
        List<Long> fieldIds = reqDTO != null ? reqDTO.getFieldIds() : null;
        if (fieldIds == null || fieldIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<MetadataEntityFieldDO> fields = metadataEntityFieldService.getEntityFieldListByIds(fieldIds);
        if (fields == null || fields.isEmpty()) {
            return new ArrayList<>();
        }
        // 按 entityUuid 分组，批量获取实体信息减少重复查询
        Map<String, List<MetadataEntityFieldDO>> groupByEntity = new java.util.HashMap<>();
        for (MetadataEntityFieldDO f : fields) {
            if (f.getEntityUuid() == null) {
                continue;
            }
            groupByEntity.computeIfAbsent(f.getEntityUuid(), k -> new ArrayList<>()).add(f);
        }
        Map<String, MetadataBusinessEntityDO> entityCache = new java.util.HashMap<>();
        List<EntityFieldRespDTO> result = new ArrayList<>(fields.size());
        for (MetadataEntityFieldDO f : fields) {
            MetadataBusinessEntityDO entity = null;
            if (f.getEntityUuid() != null) {
                entity = entityCache.computeIfAbsent(f.getEntityUuid(),
                        uuid -> metadataBusinessEntityCoreService.getBusinessEntityByUuid(uuid));
            }
            String entityDisplayName = entity != null ? entity.getDisplayName() : null;
            String tableName = entity != null ? entity.getTableName() : null;
            result.add(convertToRespDTO(f, entityDisplayName, tableName));
        }
        return result;
    }

    /**
     * DO -> DTO 转换公共方法，便于多处复用
     * 
     * @param f                 字段DO
     * @param entityDisplayName 实体显示名称
     * @param tableName         表名
     * @return EntityFieldRespDTO
     */
    private EntityFieldRespDTO convertToRespDTO(MetadataEntityFieldDO f, String entityDisplayName, String tableName) {
        EntityFieldRespDTO dto = new EntityFieldRespDTO();
        dto.setId(f.getId());
        // 通过entityUuid获取实体ID（如果需要）
        if (f.getEntityUuid() != null) {
            MetadataBusinessEntityDO entity = metadataBusinessEntityCoreService.getBusinessEntityByUuid(f.getEntityUuid());
            if (entity != null) {
                dto.setEntityId(entity.getId());
            }
        }
        dto.setEntityDisplayName(entityDisplayName);
        dto.setTableName(tableName);
        dto.setFieldName(f.getFieldName());
        dto.setDisplayName(f.getDisplayName());
        dto.setFieldType(f.getFieldType());
        dto.setDecimalPlaces(f.getDecimalPlaces());
        dto.setDefaultValue(f.getDefaultValue());
        dto.setFieldUuid(f.getFieldUuid());
        dto.setDescription(f.getDescription());
        dto.setIsRequired(f.getIsRequired());
        dto.setIsUnique(f.getIsUnique());
        dto.setIsSystemField(f.getIsSystemField());
        dto.setIsPrimaryKey(f.getIsPrimaryKey());
        dto.setSortOrder(f.getSortOrder());
        dto.setFieldCode(f.getFieldCode());
        dto.setVersionTag(f.getVersionTag());
        dto.setApplicationId(f.getApplicationId());
        dto.setCreateTime(f.getCreateTime());
        dto.setUpdateTime(f.getUpdateTime());
        return dto;
    }

    @Override
    public long countByDictTypeId(Long dictTypeId) {
        return metadataEntityFieldService.countByDictTypeId(dictTypeId);
    }

}
