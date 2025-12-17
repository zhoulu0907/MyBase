package com.cmsr.onebase.module.metadata.core.service.entity.impl;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.string.UuidUtils;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataComponentFieldTypeRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataComponentFieldTypeDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 实体字段 Service 核心实现类
 *
 * @author matianyu
 * @date 2025-09-12
 */
@Service
@Slf4j
public class MetadataEntityFieldCoreServiceImpl implements MetadataEntityFieldCoreService {

    @Resource
    private MetadataEntityFieldRepository metadataEntityFieldRepository;

    @Resource
    private MetadataComponentFieldTypeRepository metadataComponentFieldTypeRepository;

    @Resource
    private MetadataBusinessEntityCoreService metadataBusinessEntityCoreService;

    @Override
    public Long createEntityField(@Valid MetadataEntityFieldDO entityField) {
        // 生成 UUID
        if (entityField.getFieldUuid() == null || entityField.getFieldUuid().isEmpty()) {
            entityField.setFieldUuid(UuidUtils.getUuid());
        }
        metadataEntityFieldRepository.save(entityField);
        log.info("创建实体字段成功，ID: {}，UUID: {}", entityField.getId(), entityField.getFieldUuid());
        return entityField.getId();
    }

    @Override
    public void updateEntityField(@Valid MetadataEntityFieldDO entityField) {
        metadataEntityFieldRepository.updateById(entityField);
    }

    @Override
    public void deleteEntityField(Long id) {
        metadataEntityFieldRepository.removeById(id);
    }

    @Override
    public MetadataEntityFieldDO getEntityField(Long id) {
        return metadataEntityFieldRepository.getById(id);
    }

    @Override
    public MetadataEntityFieldDO getEntityFieldByUuid(String fieldUuid) {
        if (fieldUuid == null || fieldUuid.trim().isEmpty()) {
            return null;
        }
        QueryWrapper queryWrapper = metadataEntityFieldRepository.query()
                .eq(MetadataEntityFieldDO::getFieldUuid, fieldUuid.trim());
        return metadataEntityFieldRepository.getOne(queryWrapper);
    }

    @Override
    public List<MetadataEntityFieldDO> getEntityFieldListByEntityId(Long entityId) {
        // 通过 entityId 获取实体的 entityUuid，然后使用 entityUuid 进行查询
        if (entityId == null) {
            log.warn("getEntityFieldListByEntityId: entityId 为 null，返回空列表");
            return Collections.emptyList();
        }
        
        // 通过 entityId 获取业务实体，从中提取 entityUuid
        MetadataBusinessEntityDO entity = metadataBusinessEntityCoreService.getBusinessEntity(entityId);
        if (entity == null || entity.getEntityUuid() == null) {
            log.warn("getEntityFieldListByEntityId: 无法找到实体或实体UUID为空，entityId={}，返回空列表", entityId);
            return Collections.emptyList();
        }
        
        // 使用 entityUuid 进行查询
        return getEntityFieldListByEntityUuid(entity.getEntityUuid());
    }

    @Override
    public List<MetadataEntityFieldDO> getEntityFieldListByEntityUuid(String entityUuid) {
        if (entityUuid == null || entityUuid.isEmpty()) {
            return Collections.emptyList();
        }
        QueryWrapper queryWrapper = metadataEntityFieldRepository.query()
                .eq(MetadataEntityFieldDO::getEntityUuid, entityUuid)
                .orderBy(MetadataEntityFieldDO::getSortOrder, true)
                .orderBy(MetadataEntityFieldDO::getCreateTime, false);
        return metadataEntityFieldRepository.list(queryWrapper);
    }

    @Override
    public List<MetadataEntityFieldDO> getEntityFieldListByIds(List<Long> fieldIds) {
        if (fieldIds == null || fieldIds.isEmpty()) {
            return Collections.emptyList();
        }
        QueryWrapper queryWrapper = metadataEntityFieldRepository.query()
                .in(MetadataEntityFieldDO::getId, fieldIds)
                .orderBy(MetadataEntityFieldDO::getSortOrder, true)
                .orderBy(MetadataEntityFieldDO::getCreateTime, false);
        List<MetadataEntityFieldDO> list = metadataEntityFieldRepository.list(queryWrapper);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list;
    }

    @Override
    public PageResult<MetadataEntityFieldDO> getEntityFieldPage(int pageNum, int pageSize, Long entityId) {
        // 通过 entityId 获取实体的 entityUuid，然后使用 entityUuid 进行查询
        if (entityId == null) {
            log.warn("getEntityFieldPage: entityId 为 null，返回空页");
            return new PageResult<>(Collections.emptyList(), 0L);
        }
        
        // 通过 entityId 获取业务实体，从中提取 entityUuid
        MetadataBusinessEntityDO entity = metadataBusinessEntityCoreService.getBusinessEntity(entityId);
        if (entity == null || entity.getEntityUuid() == null) {
            log.warn("getEntityFieldPage: 无法找到实体或实体UUID为空，entityId={}，返回空页", entityId);
            return new PageResult<>(Collections.emptyList(), 0L);
        }
        
        QueryWrapper queryWrapper = metadataEntityFieldRepository.query()
                .eq(MetadataEntityFieldDO::getEntityUuid, entity.getEntityUuid())
                .orderBy(MetadataEntityFieldDO::getSortOrder, true)
                .orderBy(MetadataEntityFieldDO::getCreateTime, false);
        Page<MetadataEntityFieldDO> page = metadataEntityFieldRepository.page(Page.of(pageNum, pageSize), queryWrapper);
        return new PageResult<>(page.getRecords(), page.getTotalRow());
    }

    @Override
    public MetadataEntityFieldDO getEntityFieldByCode(String fieldCode, Long entityId) {
        // 通过 entityId 获取实体的 entityUuid，然后使用 entityUuid 进行查询
        if (fieldCode == null || fieldCode.trim().isEmpty()) {
            return null;
        }
        if (entityId == null) {
            log.warn("getEntityFieldByCode: entityId 为 null，无法确定实体范围");
            return null;
        }
        
        // 通过 entityId 获取业务实体，从中提取 entityUuid
        MetadataBusinessEntityDO entity = metadataBusinessEntityCoreService.getBusinessEntity(entityId);
        if (entity == null || entity.getEntityUuid() == null) {
            log.warn("getEntityFieldByCode: 无法找到实体或实体UUID为空，entityId={}", entityId);
            return null;
        }
        
        QueryWrapper queryWrapper = metadataEntityFieldRepository.query()
                .eq(MetadataEntityFieldDO::getFieldCode, fieldCode)
                .eq(MetadataEntityFieldDO::getEntityUuid, entity.getEntityUuid());
        return metadataEntityFieldRepository.getOne(queryWrapper);
    }

    /**
     * 根据字段编码和实体UUID获取字段
     *
     * @param fieldCode 字段编码
     * @param entityUuid 实体UUID
     * @return 实体字段
     */
    public MetadataEntityFieldDO getEntityFieldByCodeAndEntityUuid(String fieldCode, String entityUuid) {
        QueryWrapper queryWrapper = metadataEntityFieldRepository.query()
                .eq(MetadataEntityFieldDO::getFieldCode, fieldCode)
                .eq(MetadataEntityFieldDO::getEntityUuid, entityUuid);
        return metadataEntityFieldRepository.getOne(queryWrapper);
    }

    @Override
    public int batchCreateEntityFields(@Valid List<MetadataEntityFieldDO> entityFields) {
        if (entityFields == null || entityFields.isEmpty()) {
            return 0;
        }

        for (MetadataEntityFieldDO field : entityFields) {
            // 生成 UUID
            if (field.getFieldUuid() == null || field.getFieldUuid().isEmpty()) {
                field.setFieldUuid(UuidUtils.getUuid());
            }
            metadataEntityFieldRepository.save(field);
        }
        return entityFields.size();
    }

    @Override
    public Map<Long, String> getFieldJdbcTypes(List<Long> fieldIds) {
        if (fieldIds == null || fieldIds.isEmpty()) {
            return Collections.emptyMap();
        }

        QueryWrapper queryWrapper = metadataEntityFieldRepository.query()
                .in(MetadataEntityFieldDO::getId, fieldIds);
        List<MetadataEntityFieldDO> fields = metadataEntityFieldRepository.list(queryWrapper);
        if (fields == null || fields.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, String> idToTypeCode = fields.stream()
                .filter(f -> f.getId() != null && f.getFieldType() != null && !f.getFieldType().isBlank())
                .collect(Collectors.toMap(MetadataEntityFieldDO::getId, MetadataEntityFieldDO::getFieldType,
                        (a, b) -> a));

        if (idToTypeCode.isEmpty()) {
            return Collections.emptyMap();
        }

        // 直接使用 core 模块的 Repository 查询字段类型映射，避免循环依赖
        Map<String, String> typeCodeToJdbc = new HashMap<>();
        for (String code : new HashSet<>(idToTypeCode.values())) {
            MetadataComponentFieldTypeDO typeDO = metadataComponentFieldTypeRepository.findByFieldTypeCode(code);
            if (typeDO != null && typeDO.getDataType() != null && !typeDO.getDataType().isBlank()) {
                typeCodeToJdbc.put(code, typeDO.getDataType());
            }
        }

        Map<Long, String> result = new LinkedHashMap<>();
        for (Map.Entry<Long, String> e : idToTypeCode.entrySet()) {
            String jdbc = typeCodeToJdbc.get(e.getValue());
            if (jdbc != null) {
                result.put(e.getKey(), jdbc);
            }
        }
        return result;
    }

    @Override
    public Map<Long, Map<String, String>> getFieldJdbcTypesWithFieldType(List<Long> fieldIds) {
        if (fieldIds == null || fieldIds.isEmpty()) {
            return Collections.emptyMap();
        }

        QueryWrapper queryWrapper = metadataEntityFieldRepository.query()
                .in(MetadataEntityFieldDO::getId, fieldIds);
        List<MetadataEntityFieldDO> fields = metadataEntityFieldRepository.list(queryWrapper);
        if (fields == null || fields.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, String> idToTypeCode = fields.stream()
                .filter(f -> f.getId() != null && f.getFieldType() != null && !f.getFieldType().isBlank())
                .collect(Collectors.toMap(MetadataEntityFieldDO::getId, MetadataEntityFieldDO::getFieldType,
                        (a, b) -> a));

        // 同时构建 id 到 fieldName 的映射
        Map<Long, String> idToFieldName = fields.stream()
                .filter(f -> f.getId() != null && f.getFieldName() != null && !f.getFieldName().isBlank())
                .collect(Collectors.toMap(MetadataEntityFieldDO::getId, MetadataEntityFieldDO::getFieldName,
                        (a, b) -> a));

        if (idToTypeCode.isEmpty()) {
            return Collections.emptyMap();
        }

        // 直接使用 core 模块的 Repository 查询字段类型映射，避免循环依赖
        Map<String, String> typeCodeToJdbc = new HashMap<>();
        for (String code : new HashSet<>(idToTypeCode.values())) {
            MetadataComponentFieldTypeDO typeDO = metadataComponentFieldTypeRepository.findByFieldTypeCode(code);
            if (typeDO != null && typeDO.getDataType() != null && !typeDO.getDataType().isBlank()) {
                typeCodeToJdbc.put(code, typeDO.getDataType());
            }
        }

        Map<Long, Map<String, String>> result = new LinkedHashMap<>();
        for (Map.Entry<Long, String> e : idToTypeCode.entrySet()) {
            String jdbc = typeCodeToJdbc.get(e.getValue());
            if (jdbc != null) {
                Map<String, String> typeInfo = new HashMap<>();
                typeInfo.put("jdbcType", jdbc);
                typeInfo.put("fieldType", e.getValue());
                // 添加字段名称
                String fieldName = idToFieldName.get(e.getKey());
                if (fieldName != null) {
                    typeInfo.put("fieldName", fieldName);
                }
                result.put(e.getKey(), typeInfo);
            }
        }
        return result;
    }

    @Override
    public long countByDictTypeId(Long dictTypeId) {
        return metadataEntityFieldRepository.countByDictTypeId(dictTypeId);
    }
}
