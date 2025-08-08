package com.cmsr.onebase.module.metadata.service.relationship;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.CascadeTypeRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.EntityRelationshipPageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.EntityRelationshipRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.EntityRelationshipSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.RelationshipTypeRespVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.enums.CascadeTypeEnum;
import com.cmsr.onebase.module.metadata.enums.RelationshipTypeEnum;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.enums.ErrorCodeConstants.ENTITY_RELATIONSHIP_NOT_EXISTS;

/**
 * 实体关系 Service 实现类
 *
 * @author bty418
 * @date 2025-01-25
 */
@Service
@Slf4j
public class MetadataEntityRelationshipServiceImpl implements MetadataEntityRelationshipService {

    @Resource
    private DataRepository dataRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEntityRelationship(@Valid EntityRelationshipSaveReqVO createReqVO) {
        // 插入实体关系
        MetadataEntityRelationshipDO entityRelationship = BeanUtils.toBean(createReqVO, MetadataEntityRelationshipDO.class);
        entityRelationship.setSourceEntityId(Long.valueOf(createReqVO.getSourceEntityId()));
        entityRelationship.setTargetEntityId(Long.valueOf(createReqVO.getTargetEntityId()));
        entityRelationship.setAppId(Long.valueOf(createReqVO.getAppId()));
        dataRepository.insert(entityRelationship);
        
        return entityRelationship.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEntityRelationship(@Valid EntityRelationshipSaveReqVO updateReqVO) {
        // 校验存在
        validateEntityRelationshipExists(Long.valueOf(updateReqVO.getId()));

        // 更新实体关系
        MetadataEntityRelationshipDO updateObj = BeanUtils.toBean(updateReqVO, MetadataEntityRelationshipDO.class);
        updateObj.setId(Long.valueOf(updateReqVO.getId()));
        updateObj.setSourceEntityId(Long.valueOf(updateReqVO.getSourceEntityId()));
        updateObj.setTargetEntityId(Long.valueOf(updateReqVO.getTargetEntityId()));
        updateObj.setAppId(Long.valueOf(updateReqVO.getAppId()));
        dataRepository.update(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEntityRelationship(Long id) {
        // 校验存在
        validateEntityRelationshipExists(id);
        
        // 删除实体关系
        dataRepository.deleteById(MetadataEntityRelationshipDO.class, id);
    }

    @Override
    public EntityRelationshipRespVO getEntityRelationshipDetail(Long id) {
        MetadataEntityRelationshipDO entityRelationship = dataRepository.findById(MetadataEntityRelationshipDO.class, id);
        if (entityRelationship == null) {
            throw exception(ENTITY_RELATIONSHIP_NOT_EXISTS);
        }

        EntityRelationshipRespVO result = BeanUtils.toBean(entityRelationship, EntityRelationshipRespVO.class);
        
        // 这里可以添加关联查询，获取源实体和目标实体的名称、字段名称等
        // 为了简化，暂时使用占位符
        result.setSourceEntityName("源实体名称");
        result.setTargetEntityName("目标实体名称");
        result.setSourceFieldName("源字段名称");
        result.setTargetFieldName("目标字段名称");
        
        return result;
    }

    @Override
    public PageResult<EntityRelationshipRespVO> getEntityRelationshipPage(EntityRelationshipPageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        
        // 添加查询条件
        if (pageReqVO.getAppId() != null) {
            configStore.and("app_id", pageReqVO.getAppId());
        }
        if (pageReqVO.getSourceEntityId() != null) {
            configStore.and("source_entity_id", pageReqVO.getSourceEntityId());
        }
        if (pageReqVO.getTargetEntityId() != null) {
            configStore.and("target_entity_id", pageReqVO.getTargetEntityId());
        }
        if (StringUtils.hasText(pageReqVO.getRelationshipType())) {
            configStore.and("relationship_type", pageReqVO.getRelationshipType());
        }
        
        configStore.order("create_time", Order.TYPE.DESC);
        
        // 分页查询
        PageResult<MetadataEntityRelationshipDO> pageResult = dataRepository.findPageWithConditions(
            MetadataEntityRelationshipDO.class, configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
        
        // 转换为响应VO
        return new PageResult<>(
            pageResult.getList().stream().map(this::convertToRespVO).toList(),
            pageResult.getTotal()
        );
    }

    @Override
    public List<RelationshipTypeRespVO> getRelationshipTypes() {
        return Arrays.stream(RelationshipTypeEnum.values())
                .map(this::convertToRelationshipTypeRespVO)
                .toList();
    }

    @Override
    public List<CascadeTypeRespVO> getCascadeTypes() {
        return Arrays.stream(CascadeTypeEnum.values())
                .map(this::convertToCascadeTypeRespVO)
                .toList();
    }

    /**
     * 校验实体关系是否存在
     */
    private void validateEntityRelationshipExists(Long id) {
        if (dataRepository.findById(MetadataEntityRelationshipDO.class, id) == null) {
            throw exception(ENTITY_RELATIONSHIP_NOT_EXISTS);
        }
    }

    /**
     * 转换为响应VO
     */
    private EntityRelationshipRespVO convertToRespVO(MetadataEntityRelationshipDO relationshipDO) {
        EntityRelationshipRespVO result = BeanUtils.toBean(relationshipDO, EntityRelationshipRespVO.class);
        // 这里可以添加关联查询，获取实体名称和字段名称
        result.setSourceEntityName("源实体名称");
        result.setTargetEntityName("目标实体名称");
        result.setSourceFieldName("源字段名称");
        result.setTargetFieldName("目标字段名称");
        return result;
    }

    /**
     * 转换关系类型枚举为响应VO
     */
    private RelationshipTypeRespVO convertToRelationshipTypeRespVO(RelationshipTypeEnum relationshipTypeEnum) {
        RelationshipTypeRespVO respVO = new RelationshipTypeRespVO();
        respVO.setRelationshipType(relationshipTypeEnum.getRelationshipType());
        respVO.setDisplayName(relationshipTypeEnum.getDisplayName());
        respVO.setDescription(relationshipTypeEnum.getDescription());
        return respVO;
    }

    /**
     * 转换级联类型枚举为响应VO
     */
    private CascadeTypeRespVO convertToCascadeTypeRespVO(CascadeTypeEnum cascadeTypeEnum) {
        CascadeTypeRespVO respVO = new CascadeTypeRespVO();
        respVO.setCascadeType(cascadeTypeEnum.getCascadeType());
        respVO.setDisplayName(cascadeTypeEnum.getDisplayName());
        respVO.setDescription(cascadeTypeEnum.getDescription());
        return respVO;
    }

    @Override
    public List<EntityRelationshipRespVO> getRelationshipsByDatasourceId(Long datasourceId) {
        // 首先获取该数据源下的所有实体ID
        DefaultConfigStore entityConfigStore = new DefaultConfigStore();
        entityConfigStore.and("datasource_id", datasourceId);
        List<MetadataBusinessEntityDO> entities = dataRepository.findAllByConfig(
                MetadataBusinessEntityDO.class, entityConfigStore);

        if (entities.isEmpty()) {
            return List.of();
        }

        List<Long> entityIds = entities.stream()
                .map(MetadataBusinessEntityDO::getId)
                .toList();

        // 查询涉及这些实体的所有关系 - 使用OR查询条件
        DefaultConfigStore relationshipConfigStore = new DefaultConfigStore();
        relationshipConfigStore.and("(source_entity_id in ? or target_entity_id in ?)", entityIds, entityIds);
        relationshipConfigStore.order("create_time", Order.TYPE.DESC);

        List<MetadataEntityRelationshipDO> relationships = dataRepository.findAllByConfig(
                MetadataEntityRelationshipDO.class, relationshipConfigStore);

        return relationships.stream()
                .map(this::convertToRespVO)
                .toList();
    }

} 