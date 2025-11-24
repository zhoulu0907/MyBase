package com.cmsr.onebase.module.metadata.core.service.entity.impl;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataComponentFieldTypeRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataComponentFieldTypeDO;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
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
        metadataEntityFieldRepository.insert(entityField);
        return entityField.getId();
    }

    @Override
    public void updateEntityField(@Valid MetadataEntityFieldDO entityField) {
        metadataEntityFieldRepository.update(entityField);
    }

    @Override
    public void deleteEntityField(Long id) {
        metadataEntityFieldRepository.deleteById(id);
    }

    @Override
    public MetadataEntityFieldDO getEntityField(Long id) {
        return metadataEntityFieldRepository.findById(id);
    }

    @Override
    public List<MetadataEntityFieldDO> getEntityFieldListByEntityId(Long entityId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("entity_id", entityId);
        configStore.order("sort_order", "ASC");
        configStore.order("create_time", "DESC");
        return metadataEntityFieldRepository.findAllByConfig(configStore);
    }

    @Override
    public List<MetadataEntityFieldDO> getEntityFieldListByIds(List<Long> fieldIds) {
        if (fieldIds == null || fieldIds.isEmpty()) {
            return Collections.emptyList();
        }
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(Compare.IN, "id", fieldIds);
        cs.and("deleted", 0);
        // 不强制排序按传入顺序，仍按 sort_order + create_time 保持与实体字段列表接口一致
        cs.order("sort_order", "ASC");
        cs.order("create_time", "DESC");
        List<MetadataEntityFieldDO> list = metadataEntityFieldRepository.findAllByConfig(cs);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        // 为了按照入参顺序返回，可进行一次稳定排序(可选)。此处保留原有排序语义，不再调整顺序。
        return list;
    }

    @Override
    public PageResult<MetadataEntityFieldDO> getEntityFieldPage(int pageNum, int pageSize, Long entityId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        if (entityId != null) {
            configStore.and("entity_id", entityId);
        }
        configStore.order("sort_order", "ASC");
        configStore.order("create_time", "DESC");
        return metadataEntityFieldRepository.findPageWithConditions(configStore, pageNum, pageSize);
    }

    @Override
    public MetadataEntityFieldDO getEntityFieldByCode(String fieldCode, Long entityId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("field_code", fieldCode);
        configStore.and("entity_id", entityId);
        return metadataEntityFieldRepository.findOne(configStore);
    }

    @Override
    public int batchCreateEntityFields(@Valid List<MetadataEntityFieldDO> entityFields) {
        if (entityFields == null || entityFields.isEmpty()) {
            return 0;
        }

        for (MetadataEntityFieldDO field : entityFields) {
            metadataEntityFieldRepository.insert(field);
        }
        return entityFields.size();
    }

    @Override
    public Map<Long, String> getFieldJdbcTypes(List<Long> fieldIds) {
        if (fieldIds == null || fieldIds.isEmpty()) {
            return Collections.emptyMap();
        }

        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(Compare.IN, "id", fieldIds);
        cs.and("deleted", 0);
        List<MetadataEntityFieldDO> fields = metadataEntityFieldRepository.findAllByConfig(cs);
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

        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(Compare.IN, "id", fieldIds);
        cs.and("deleted", 0);
        List<MetadataEntityFieldDO> fields = metadataEntityFieldRepository.findAllByConfig(cs);
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
