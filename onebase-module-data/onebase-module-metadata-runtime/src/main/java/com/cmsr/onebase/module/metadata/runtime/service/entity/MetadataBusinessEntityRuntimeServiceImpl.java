package com.cmsr.onebase.module.metadata.runtime.service.entity;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataBusinessEntityRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldOptionRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityRelationshipRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.field.MetadataEntityFieldOptionDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo.ChildEntityWithFieldsRespVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo.EntityFieldRespVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo.EntityWithFieldsBatchQueryReqVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo.EntityWithFieldsRespVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo.FieldOptionRespVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo.SimpleEntityRespVO;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 运行态 - 业务实体服务实现类
 *
 * @author matianyu
 * @date 2025-12-15
 */
@Service
@Slf4j
public class MetadataBusinessEntityRuntimeServiceImpl implements MetadataBusinessEntityRuntimeService {

    @Resource
    private MetadataBusinessEntityRepository metadataBusinessEntityRepository;

    @Resource
    private MetadataEntityRelationshipRepository metadataEntityRelationshipRepository;

    @Resource
    private MetadataEntityFieldRepository metadataEntityFieldRepository;

    @Resource
    private MetadataEntityFieldOptionRepository metadataEntityFieldOptionRepository;

    @Override
    public List<SimpleEntityRespVO> getSimpleEntityListByAppId(Long appId) {
        log.info("开始查询应用实体列表，应用ID: {}", appId);

        // 1. 根据appId查询该应用下的所有实体
        List<MetadataBusinessEntityDO> entities = metadataBusinessEntityRepository.getSimpleEntityListByAppId(appId);

        if (entities.isEmpty()) {
            log.info("应用下未找到任何实体，应用ID: {}", appId);
            return List.of();
        }

        // 2. 转换为简单实体信息VO
        List<SimpleEntityRespVO> result = entities.stream()
                .map(this::convertToSimpleEntity)
                .toList();

        // 3. 查询应用下的所有关系，判断主子表类型
        List<MetadataEntityRelationshipDO> relationships = metadataEntityRelationshipRepository.findByApplicationId(appId);

        // 提取所有作为源实体的UUID集合
        Set<String> sourceEntityUuids = relationships.stream()
                .map(MetadataEntityRelationshipDO::getSourceEntityUuid)
                .collect(Collectors.toSet());

        // 提取所有作为目标实体的UUID集合
        Set<String> targetEntityUuids = relationships.stream()
                .map(MetadataEntityRelationshipDO::getTargetEntityUuid)
                .collect(Collectors.toSet());

        // 构建实体UUID到关系类型的映射
        Map<String, Set<String>> entityRelationshipTypesMap = new HashMap<>();
        for (MetadataEntityRelationshipDO rel : relationships) {
            String type = rel.getRelationshipType();
            if (type != null) {
                // 作为源实体
                if (rel.getSourceEntityUuid() != null) {
                    entityRelationshipTypesMap.computeIfAbsent(rel.getSourceEntityUuid(), k -> new HashSet<>()).add(type);
                }
                // 作为目标实体
                if (rel.getTargetEntityUuid() != null) {
                    entityRelationshipTypesMap.computeIfAbsent(rel.getTargetEntityUuid(), k -> new HashSet<>()).add(type);
                }
            }
        }

        // 遍历实体列表，设置关系类型
        for (SimpleEntityRespVO entity : result) {
            String uuid = entity.getEntityUuid();
            boolean isSource = sourceEntityUuids.contains(uuid);
            boolean isTarget = targetEntityUuids.contains(uuid);

            if (isSource && !isTarget) {
                // 只在source出现且未在target出现 -> 主表
                entity.setRelationType("MASTER");
            } else if (isTarget) {
                // 只要在target出现 -> 子表
                entity.setRelationType("SLAVE");
            } else {
                // 都没有 -> 无关系
                entity.setRelationType("NONE");
            }

            // 设置关联关系类型列表
            entity.setRelationshipTypes(entityRelationshipTypesMap.getOrDefault(uuid, new HashSet<>()));
        }

        log.info("查询应用实体列表完成，应用ID: {}, 实体数量: {}", appId, result.size());
        return result;
    }

    /**
     * 转换实体DO为简单实体信息VO
     *
     * @param entityDO 实体DO
     * @return 简单实体信息VO
     */
    private SimpleEntityRespVO convertToSimpleEntity(MetadataBusinessEntityDO entityDO) {
        return BeanUtils.toBean(entityDO, SimpleEntityRespVO.class, simpleEntity -> {
            simpleEntity.setEntityId(entityDO.getId());
            simpleEntity.setEntityUuid(entityDO.getEntityUuid());
            simpleEntity.setEntityName(entityDO.getDisplayName());
            simpleEntity.setTableName(entityDO.getTableName());
        });
    }

    @Override
    public List<EntityWithFieldsRespVO> getEntitiesWithFullFields(EntityWithFieldsBatchQueryReqVO reqVO) {
        log.info("开始批量查询实体及完整字段信息, entityUuids: {}, tableNames: {}",
                reqVO.getEntityUuids(), reqVO.getTableNames());

        // 1. 参数校验：entityUuids和tableNames至少传一个
        List<String> entityUuids = reqVO.getEntityUuids();
        List<String> tableNames = reqVO.getTableNames();

        if ((entityUuids == null || entityUuids.isEmpty()) && (tableNames == null || tableNames.isEmpty())) {
            log.warn("entityUuids和tableNames都为空，返回空列表");
            return List.of();
        }

        // 2. 查询实体列表
        List<MetadataBusinessEntityDO> entities;
        if (entityUuids != null && !entityUuids.isEmpty()) {
            // 优先使用entityUuids查询
            QueryWrapper queryWrapper = metadataBusinessEntityRepository.query()
                    .in(MetadataBusinessEntityDO::getEntityUuid, entityUuids);
            entities = metadataBusinessEntityRepository.list(queryWrapper);
        } else {
            // 使用tableNames查询
            QueryWrapper queryWrapper = metadataBusinessEntityRepository.query()
                    .in(MetadataBusinessEntityDO::getTableName, tableNames);
            entities = metadataBusinessEntityRepository.list(queryWrapper);
        }

        if (entities.isEmpty()) {
            log.info("未找到匹配的实体");
            return List.of();
        }

        // 3. 转换为响应VO
        List<EntityWithFieldsRespVO> result = new ArrayList<>();
        for (MetadataBusinessEntityDO entity : entities) {
            EntityWithFieldsRespVO respVO = convertToEntityWithFieldsRespVO(entity);
            result.add(respVO);
        }

        log.info("批量查询实体及字段信息完成，共查询到 {} 个实体", result.size());
        return result;
    }

    /**
     * 将实体DO转换为带完整字段信息的响应VO
     *
     * @param entity 实体DO
     * @return 实体及字段信息响应VO
     */
    private EntityWithFieldsRespVO convertToEntityWithFieldsRespVO(MetadataBusinessEntityDO entity) {
        EntityWithFieldsRespVO respVO = new EntityWithFieldsRespVO();
        respVO.setEntityId(entity.getId());
        respVO.setEntityUuid(entity.getEntityUuid());
        respVO.setEntityName(entity.getDisplayName());
        respVO.setEntityCode(entity.getCode());
        respVO.setTableName(entity.getTableName());

        // 1. 查询实体的完整字段信息
        List<EntityFieldRespVO> fields = getEntityFieldListWithOptions(entity.getEntityUuid());
        respVO.setFields(fields);

        // 2. 查询以该实体为源实体的所有关系（即该实体作为父表的关系）
        QueryWrapper relationshipQueryWrapper = metadataEntityRelationshipRepository.query()
                .eq(MetadataEntityRelationshipDO::getSourceEntityUuid, entity.getEntityUuid())
                .orderBy(MetadataEntityRelationshipDO::getCreateTime, false);
        List<MetadataEntityRelationshipDO> relationships = metadataEntityRelationshipRepository.list(relationshipQueryWrapper);

        // 3. 转换为子表信息列表
        List<ChildEntityWithFieldsRespVO> childEntities = new ArrayList<>();
        for (MetadataEntityRelationshipDO relationship : relationships) {
            ChildEntityWithFieldsRespVO childVO = convertToChildEntityWithFieldsRespVO(relationship);
            if (childVO != null) {
                childEntities.add(childVO);
            }
        }
        respVO.setChildEntities(childEntities);

        return respVO;
    }

    /**
     * 将关系DO转换为子表实体及字段信息VO
     *
     * @param relationship 关系DO
     * @return 子表实体及字段信息VO
     */
    private ChildEntityWithFieldsRespVO convertToChildEntityWithFieldsRespVO(MetadataEntityRelationshipDO relationship) {
        // 获取子表实体信息
        MetadataBusinessEntityDO childEntity = metadataBusinessEntityRepository.getByEntityUuid(relationship.getTargetEntityUuid());
        if (childEntity == null) {
            log.warn("子表实体不存在，关系ID: {}, 目标实体UUID: {}",
                    relationship.getId(), relationship.getTargetEntityUuid());
            return null;
        }

        ChildEntityWithFieldsRespVO childVO = new ChildEntityWithFieldsRespVO();
        childVO.setChildEntityId(childEntity.getId());
        childVO.setChildEntityUuid(childEntity.getEntityUuid());
        childVO.setChildEntityName(childEntity.getDisplayName());
        childVO.setChildEntityCode(childEntity.getCode());
        childVO.setChildTableName(childEntity.getTableName());

        // 设置关系信息
        childVO.setRelationshipId(relationship.getId());
        childVO.setRelationshipUuid(relationship.getRelationshipUuid());
        childVO.setRelationshipName(relationship.getRelationName());
        childVO.setRelationshipType(relationship.getRelationshipType());

        // 通过字段UUID查询字段名称
        String sourceFieldName = getFieldNameByUuid(relationship.getSourceFieldUuid());
        String targetFieldName = getFieldNameByUuid(relationship.getTargetFieldUuid());
        childVO.setSourceFieldName(sourceFieldName);
        childVO.setTargetFieldName(targetFieldName);

        // 查询子表的完整字段信息
        List<EntityFieldRespVO> childFields = getEntityFieldListWithOptions(childEntity.getEntityUuid());
        childVO.setChildFields(childFields);

        return childVO;
    }

    /**
     * 根据实体UUID查询字段列表（包含字段选项）
     *
     * @param entityUuid 实体UUID
     * @return 字段响应VO列表
     */
    private List<EntityFieldRespVO> getEntityFieldListWithOptions(String entityUuid) {
        // 查询字段列表
        QueryWrapper fieldQueryWrapper = metadataEntityFieldRepository.query()
                .eq(MetadataEntityFieldDO::getEntityUuid, entityUuid)
                .orderBy(MetadataEntityFieldDO::getSortOrder, true);
        List<MetadataEntityFieldDO> fieldDOList = metadataEntityFieldRepository.list(fieldQueryWrapper);

        if (fieldDOList.isEmpty()) {
            return List.of();
        }

        // 收集所有字段UUID，批量查询选项
        List<String> fieldUuids = fieldDOList.stream()
                .map(MetadataEntityFieldDO::getFieldUuid)
                .filter(uuid -> uuid != null && !uuid.isEmpty())
                .toList();

        // 批量查询字段选项
        Map<String, List<FieldOptionRespVO>> fieldOptionsMap = new HashMap<>();
        if (!fieldUuids.isEmpty()) {
            QueryWrapper optionQueryWrapper = metadataEntityFieldOptionRepository.query()
                    .in(MetadataEntityFieldOptionDO::getFieldUuid, fieldUuids);
            List<MetadataEntityFieldOptionDO> optionDOList = metadataEntityFieldOptionRepository.list(optionQueryWrapper);

            // 按字段UUID分组
            for (MetadataEntityFieldOptionDO optionDO : optionDOList) {
                FieldOptionRespVO optionVO = BeanUtils.toBean(optionDO, FieldOptionRespVO.class);
                fieldOptionsMap.computeIfAbsent(optionDO.getFieldUuid(), k -> new ArrayList<>()).add(optionVO);
            }
        }

        // 转换为响应VO
        List<EntityFieldRespVO> result = new ArrayList<>();
        for (MetadataEntityFieldDO fieldDO : fieldDOList) {
            EntityFieldRespVO fieldVO = convertToEntityFieldRespVO(fieldDO);
            // 设置字段选项
            if (fieldDO.getFieldUuid() != null && fieldOptionsMap.containsKey(fieldDO.getFieldUuid())) {
                fieldVO.setOptions(fieldOptionsMap.get(fieldDO.getFieldUuid()));
            }
            result.add(fieldVO);
        }

        return result;
    }

    /**
     * 将字段DO转换为响应VO
     *
     * @param fieldDO 字段DO
     * @return 字段响应VO
     */
    private EntityFieldRespVO convertToEntityFieldRespVO(MetadataEntityFieldDO fieldDO) {
        EntityFieldRespVO fieldVO = new EntityFieldRespVO();
        fieldVO.setId(String.valueOf(fieldDO.getId()));
        fieldVO.setFieldUuid(fieldDO.getFieldUuid());
        fieldVO.setEntityUuid(fieldDO.getEntityUuid());
        fieldVO.setFieldName(fieldDO.getFieldName());
        fieldVO.setDisplayName(fieldDO.getDisplayName());
        fieldVO.setFieldType(fieldDO.getFieldType());
        fieldVO.setDataLength(fieldDO.getDataLength());
        fieldVO.setDecimalPlaces(fieldDO.getDecimalPlaces());
        fieldVO.setDefaultValue(fieldDO.getDefaultValue());
        fieldVO.setDescription(fieldDO.getDescription());
        fieldVO.setIsSystemField(fieldDO.getIsSystemField());
        fieldVO.setIsPrimaryKey(fieldDO.getIsPrimaryKey());
        fieldVO.setIsRequired(fieldDO.getIsRequired());
        fieldVO.setIsUnique(fieldDO.getIsUnique());
        fieldVO.setSortOrder(fieldDO.getSortOrder());
        fieldVO.setVersionTag(fieldDO.getVersionTag());
        fieldVO.setApplicationId(fieldDO.getApplicationId());
        fieldVO.setStatus(fieldDO.getStatus());
        fieldVO.setFieldCode(fieldDO.getFieldCode());
        fieldVO.setDictTypeId(fieldDO.getDictTypeId());
        return fieldVO;
    }

    /**
     * 根据字段UUID获取字段名称
     *
     * @param fieldUuid 字段UUID
     * @return 字段名称
     */
    private String getFieldNameByUuid(String fieldUuid) {
        if (fieldUuid == null || fieldUuid.isEmpty()) {
            return null;
        }
        MetadataEntityFieldDO field = metadataEntityFieldRepository.getByFieldUuid(fieldUuid);
        return field != null ? field.getFieldName() : null;
    }
}
