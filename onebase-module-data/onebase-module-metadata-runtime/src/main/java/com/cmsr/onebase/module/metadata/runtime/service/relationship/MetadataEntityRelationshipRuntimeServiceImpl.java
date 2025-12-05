package com.cmsr.onebase.module.metadata.runtime.service.relationship;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityRelationshipRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.core.util.MetadataIdUuidConverter;
import com.cmsr.onebase.module.metadata.runtime.controller.app.relationship.vo.ChildEntityInfoRespVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.relationship.vo.EntityFieldInfoRespVO;
import com.cmsr.onebase.module.metadata.runtime.controller.app.relationship.vo.EntityWithChildrenRespVO;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 运行态 - 实体关系服务实现类
 *
 * @author matianyu
 * @date 2025-12-04
 */
@Service
@Slf4j
public class MetadataEntityRelationshipRuntimeServiceImpl implements MetadataEntityRelationshipRuntimeService {

    @Resource
    private MetadataEntityRelationshipRepository entityRelationshipRepository;

    @Resource
    private MetadataBusinessEntityCoreService businessEntityService;

    @Resource
    private MetadataEntityFieldCoreService entityFieldService;

    @Resource
    private MetadataIdUuidConverter idUuidConverter;

    @Override
    public EntityWithChildrenRespVO getEntityWithChildrenById(Long entityId, String relationshipType) {
        // 1. 获取实体基本信息
        MetadataBusinessEntityDO entity = businessEntityService.getBusinessEntity(entityId);
        if (entity == null) {
            throw new IllegalArgumentException("实体不存在，实体ID: " + entityId);
        }

        // 2. 获取实体的UUID（兼容历史数据：如果entityUuid为空，则通过ID转换获取）
        String entityUuid = entity.getEntityUuid();
        if (!StringUtils.hasText(entityUuid)) {
            entityUuid = idUuidConverter.toEntityUuid(String.valueOf(entityId));
            log.info("实体entityUuid为空，通过ID转换获取，entityId: {}, entityUuid: {}", entityId, entityUuid);
        }

        // 3. 创建响应VO
        final String finalEntityUuidForResult = entityUuid;
        EntityWithChildrenRespVO result = BeanUtils.toBean(entity, EntityWithChildrenRespVO.class, res -> {
            res.setEntityId(entity.getId());
            res.setEntityUuid(finalEntityUuidForResult);
            res.setEntityName(entity.getDisplayName());
            res.setEntityCode(entity.getCode());
            res.setTableName(entity.getTableName());
        });

        // 4. 查询以该实体为源实体的所有关系（即该实体作为父表的关系）
        final String finalEntityUuid = entityUuid;
        QueryWrapper queryWrapper = entityRelationshipRepository.query()
                .eq(MetadataEntityRelationshipDO::getSourceEntityUuid, finalEntityUuid);
        
        // 增加关系类型筛选条件
        if (StringUtils.hasText(relationshipType)) {
            queryWrapper.eq(MetadataEntityRelationshipDO::getRelationshipType, relationshipType);
        }
        queryWrapper.orderBy(MetadataEntityRelationshipDO::getCreateTime, false);

        List<MetadataEntityRelationshipDO> relationships = entityRelationshipRepository.list(queryWrapper);

        // 5. 填充父表字段信息（使用转换后的entityUuid）
        List<EntityFieldInfoRespVO> parentFields = getEntityFields(finalEntityUuid);
        result.setParentFields(parentFields);

        // 6. 转换为子表信息列表
        List<ChildEntityInfoRespVO> childEntities = relationships.stream()
                .map(this::convertToChildEntityInfo)
                .filter(Objects::nonNull)
                .toList();

        result.setChildEntities(childEntities);

        log.info("查询实体及其关联子表成功，实体ID: {}, entityUuid: {}, 关系类型筛选: {}, 关联子表数量: {}, 父表字段数量: {}",
                entityId, finalEntityUuid, relationshipType, childEntities.size(), parentFields.size());
        return result;
    }

    /**
     * 转换关系DO为子表信息
     *
     * @param relationshipDO 关系DO
     * @return 子表信息
     */
    private ChildEntityInfoRespVO convertToChildEntityInfo(MetadataEntityRelationshipDO relationshipDO) {
        // 获取目标实体信息（使用UUID查询）
        MetadataBusinessEntityDO targetEntity = businessEntityService.getBusinessEntityByUuid(relationshipDO.getTargetEntityUuid());
        
        // 关联的目标实体为空（不存在或被删除）直接跳过后续处理
        if (targetEntity == null) {
            return null;
        }
        
        ChildEntityInfoRespVO childInfo = BeanUtils.toBean(relationshipDO, ChildEntityInfoRespVO.class, info -> {
            info.setChildEntityId(targetEntity.getId());
            info.setChildEntityUuid(relationshipDO.getTargetEntityUuid());
            info.setRelationshipId(String.valueOf(relationshipDO.getId()));
            info.setRelationshipName(relationshipDO.getRelationName());
            info.setRelationshipType(relationshipDO.getRelationshipType());
            info.setChildEntityName(targetEntity.getDisplayName());
            info.setChildEntityCode(targetEntity.getCode());
            info.setChildTableName(targetEntity.getTableName());
        });

        // 获取字段名称
        childInfo.setSourceFieldName(getFieldNameByUuid(relationshipDO.getSourceFieldUuid()));
        childInfo.setTargetFieldName(getFieldNameByUuid(relationshipDO.getTargetFieldUuid()));

        // 填充子表字段信息
        List<EntityFieldInfoRespVO> childFields = getEntityFields(relationshipDO.getTargetEntityUuid());
        childInfo.setChildFields(childFields);

        return childInfo;
    }

    /**
     * 根据字段UUID获取字段名称
     *
     * @param fieldUuid 字段UUID
     * @return 字段名称
     */
    private String getFieldNameByUuid(String fieldUuid) {
        if (!StringUtils.hasText(fieldUuid)) {
            return null;
        }
        MetadataEntityFieldDO field = entityFieldService.getEntityFieldByUuid(fieldUuid);
        return field != null ? field.getFieldName() : null;
    }

    /**
     * 根据实体UUID获取字段信息列表
     *
     * @param entityUuid 实体UUID
     * @return 字段信息列表
     */
    private List<EntityFieldInfoRespVO> getEntityFields(String entityUuid) {
        List<MetadataEntityFieldDO> fields = entityFieldService.getEntityFieldListByEntityUuid(entityUuid);

        return fields.stream()
                .map(this::convertToFieldInfo)
                .toList();
    }

    /**
     * 转换字段DO为字段信息VO
     *
     * @param fieldDO 字段DO
     * @return 字段信息VO
     */
    private EntityFieldInfoRespVO convertToFieldInfo(MetadataEntityFieldDO fieldDO) {
        return BeanUtils.toBean(fieldDO, EntityFieldInfoRespVO.class, fieldInfo -> {
            fieldInfo.setFieldId(String.valueOf(fieldDO.getId()));
            fieldInfo.setFieldUuid(fieldDO.getFieldUuid());
        });
    }

}
