package com.cmsr.onebase.module.metadata.core.service.entity.impl;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataComponentFieldTypeRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataComponentFieldTypeDO;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
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

    @Override
    public Long createEntityField(@Valid MetadataEntityFieldDO entityField) {
        metadataEntityFieldRepository.save(entityField);
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
    public List<MetadataEntityFieldDO> getEntityFieldListByEntityId(Long entityId) {
        QueryWrapper queryWrapper = metadataEntityFieldRepository.query()
                .eq(MetadataEntityFieldDO::getEntityId, entityId)
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
        QueryWrapper queryWrapper = metadataEntityFieldRepository.query();
        if (entityId != null) {
            queryWrapper.eq(MetadataEntityFieldDO::getEntityId, entityId);
        }
        queryWrapper.orderBy(MetadataEntityFieldDO::getSortOrder, true)
                .orderBy(MetadataEntityFieldDO::getCreateTime, false);
        Page<MetadataEntityFieldDO> page = metadataEntityFieldRepository.page(Page.of(pageNum, pageSize), queryWrapper);
        return new PageResult<>(page.getRecords(), page.getTotalRow());
    }

    @Override
    public MetadataEntityFieldDO getEntityFieldByCode(String fieldCode, Long entityId) {
        QueryWrapper queryWrapper = metadataEntityFieldRepository.query()
                .eq(MetadataEntityFieldDO::getFieldCode, fieldCode)
                .eq(MetadataEntityFieldDO::getEntityId, entityId);
        return metadataEntityFieldRepository.getOne(queryWrapper);
    }

    @Override
    public int batchCreateEntityFields(@Valid List<MetadataEntityFieldDO> entityFields) {
        if (entityFields == null || entityFields.isEmpty()) {
            return 0;
        }

        for (MetadataEntityFieldDO field : entityFields) {
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
