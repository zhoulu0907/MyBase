package com.cmsr.onebase.module.metadata.service.relationship;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.BusinessEntitySaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.CascadeTypeRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.EntityRelationshipPageReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.EntityRelationshipRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.EntityRelationshipSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.ParentChildRelationshipSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.ParentChildRelationshipRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.relationship.vo.RelationshipTypeRespVO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.enums.CascadeTypeEnum;
import com.cmsr.onebase.module.metadata.enums.RelationshipTypeEnum;
import com.cmsr.onebase.module.metadata.service.entity.MetadataBusinessEntityService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
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

    @Resource
    private MetadataBusinessEntityService businessEntityService;

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
        
        // 查询源实体和目标实体的名称
        result.setSourceEntityName(getEntityNameById(entityRelationship.getSourceEntityId()));
        result.setTargetEntityName(getEntityNameById(entityRelationship.getTargetEntityId()));
        
        // 查询源字段和目标字段的名称
        result.setSourceFieldName(getFieldNameById(entityRelationship.getSourceFieldId()));
        result.setTargetFieldName(getFieldNameById(entityRelationship.getTargetFieldId()));
        
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
        
        // 查询源实体和目标实体的名称
        result.setSourceEntityName(getEntityNameById(relationshipDO.getSourceEntityId()));
        result.setTargetEntityName(getEntityNameById(relationshipDO.getTargetEntityId()));
        
        // 查询源字段和目标字段的名称
        result.setSourceFieldName(getFieldNameById(relationshipDO.getSourceFieldId()));
        result.setTargetFieldName(getFieldNameById(relationshipDO.getTargetFieldId()));
        
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

        // 查询涉及这些实体的所有关系 - 使用嵌套 OR + IN，避免 Anyline 生成非法 SQL
        DefaultConfigStore relationshipConfigStore = new DefaultConfigStore();
        ConfigStore orStore = new DefaultConfigStore()
                .or(Compare.IN, "source_entity_id", entityIds)
                .or(Compare.IN, "target_entity_id", entityIds);
        relationshipConfigStore.and(orStore);
        relationshipConfigStore.order("create_time", Order.TYPE.DESC);

        List<MetadataEntityRelationshipDO> relationships = dataRepository.findAllByConfig(
                MetadataEntityRelationshipDO.class, relationshipConfigStore);

        return relationships.stream()
                .map(this::convertToRespVO)
                .toList();
    }

    /**
     * 根据实体ID获取实体名称
     *
     * @param entityId 实体ID
     * @return 实体名称
     */
    private String getEntityNameById(Long entityId) {
        if (entityId == null) {
            return null;
        }
        
        try {
            DefaultConfigStore configStore = new DefaultConfigStore();
            configStore.and("id", entityId);
            MetadataBusinessEntityDO entity = dataRepository.findOne(MetadataBusinessEntityDO.class, configStore);
            return entity != null ? entity.getDisplayName() : null;
        } catch (Exception e) {
            log.warn("获取实体名称失败，实体ID: {}, 错误: {}", entityId, e.getMessage());
            return null;
        }
    }

    /**
     * 根据字段ID获取字段名称
     *
     * @param fieldId 字段ID
     * @return 字段名称
     */
    private String getFieldNameById(String fieldId) {
        if (fieldId == null) {
            return null;
        }
        
        try {
            DefaultConfigStore configStore = new DefaultConfigStore();
            configStore.and("id", Long.valueOf(fieldId));
            MetadataEntityFieldDO field = dataRepository.findOne(MetadataEntityFieldDO.class, configStore);
            return field != null ? field.getFieldName() : null;
        } catch (NumberFormatException e) {
            log.warn("无效的字段ID: {}", fieldId);
            return null;
        } catch (Exception e) {
            log.warn("获取字段名称失败，字段ID: {}, 错误: {}", fieldId, e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ParentChildRelationshipRespVO createParentChildRelationship(@Valid ParentChildRelationshipSaveReqVO createReqVO) {
        log.info("开始创建主子关系，主表实体ID: {}, 子表实体ID: {}", createReqVO.getParentEntityId(), createReqVO.getChildEntityId());
        
        // 1. 获取或创建子表实体
        Long childEntityId;
        if (createReqVO.getChildEntityId() != null) {
            // 选择已有的子表
            childEntityId = Long.valueOf(createReqVO.getChildEntityId());
            log.info("使用已有子表，实体ID: {}", childEntityId);
        } else {
            // 创建新的子表实体
            childEntityId = createNewChildEntity(createReqVO);
            log.info("创建新子表成功，实体ID: {}", childEntityId);
        }

        // 2. 获取主表的id字段和子表的parent_id字段
        Long parentIdFieldId = getEntityIdField(Long.valueOf(createReqVO.getParentEntityId()));
        Long childParentIdFieldId = getOrCreateParentIdField(childEntityId);

        // 3. 创建主子关系
        EntityRelationshipSaveReqVO relationshipReqVO = new EntityRelationshipSaveReqVO();
        relationshipReqVO.setRelationName("主子关系");
        relationshipReqVO.setSourceEntityId(createReqVO.getParentEntityId());
        relationshipReqVO.setTargetEntityId(String.valueOf(childEntityId));
        relationshipReqVO.setRelationshipType(RelationshipTypeEnum.ONE_TO_MANY.getRelationshipType());
        relationshipReqVO.setSourceFieldId(String.valueOf(parentIdFieldId));
        relationshipReqVO.setTargetFieldId(String.valueOf(childParentIdFieldId));
        relationshipReqVO.setCascadeType(CascadeTypeEnum.ALL.getCascadeType()); // 默认级联新增、删除、查询
        relationshipReqVO.setDescription("系统自动创建的主子关系");
        relationshipReqVO.setAppId(createReqVO.getAppId());

        Long relationshipId = createEntityRelationship(relationshipReqVO);
        
        // 4. 转换为响应VO
        ParentChildRelationshipRespVO result = new ParentChildRelationshipRespVO();
        result.setId(relationshipId);
        result.setParentEntityId(Long.valueOf(createReqVO.getParentEntityId()));
        result.setChildEntityId(childEntityId);
        result.setSourceFieldId(parentIdFieldId);
        result.setTargetFieldId(childParentIdFieldId);
        result.setRelationshipType(RelationshipTypeEnum.ONE_TO_MANY.getRelationshipType());
        result.setCascadeType(CascadeTypeEnum.ALL.getCascadeType());
        result.setAppId(Long.valueOf(createReqVO.getAppId()));

        // 设置实体名称
        result.setParentEntityName(getEntityNameById(Long.valueOf(createReqVO.getParentEntityId())));
        result.setChildEntityName(getEntityNameById(childEntityId));
        result.setSourceFieldName("id");
        result.setTargetFieldName("parent_id");

        log.info("主子关系创建成功，关系ID: {}", relationshipId);
        return result;
    }

    /**
     * 创建新的子表实体
     *
     * @param createReqVO 创建请求VO
     * @return 子表实体ID
     */
    private Long createNewChildEntity(ParentChildRelationshipSaveReqVO createReqVO) {
        BusinessEntitySaveReqVO entityReqVO = new BusinessEntitySaveReqVO();
        entityReqVO.setDisplayName(createReqVO.getChildTableName());
        entityReqVO.setCode(createReqVO.getChildTableCode());
        entityReqVO.setDescription(createReqVO.getChildTableDescription());
        entityReqVO.setEntityType(1); // 自建表
        entityReqVO.setDatasourceId(createReqVO.getDatasourceId());
        entityReqVO.setAppId(createReqVO.getAppId());
        entityReqVO.setRunMode(0); // 默认运行模式

        return businessEntityService.createBusinessEntity(entityReqVO);
    }

    /**
     * 获取实体的id字段ID
     *
     * @param entityId 实体ID
     * @return id字段ID
     */
    private Long getEntityIdField(Long entityId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("entity_id", entityId);
        configStore.and("field_name", "id");
        
        MetadataEntityFieldDO idField = dataRepository.findOne(MetadataEntityFieldDO.class, configStore);
        if (idField == null) {
            throw new IllegalArgumentException("主表实体未找到id字段，实体ID: " + entityId);
        }
        
        return idField.getId();
    }

    /**
     * 获取或创建parent_id字段
     *
     * @param childEntityId 子表实体ID
     * @return parent_id字段ID
     */
    private Long getOrCreateParentIdField(Long childEntityId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("entity_id", childEntityId);
        configStore.and("field_name", "parent_id");
        
        MetadataEntityFieldDO parentIdField = dataRepository.findOne(MetadataEntityFieldDO.class, configStore);
        if (parentIdField != null) {
            return parentIdField.getId();
        }

        // TODO: 如果parent_id字段不存在，需要创建该字段
        // 这里需要调用字段创建服务来创建parent_id字段
        // 暂时抛出异常，提示用户手动创建
        throw new IllegalArgumentException("子表实体未找到parent_id字段，请先为子表添加parent_id字段，实体ID: " + childEntityId);
    }

} 