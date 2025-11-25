package com.cmsr.onebase.module.metadata.build.service.relationship;

import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityRelationshipRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.BusinessEntitySaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.CascadeTypeRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.EntityRelationshipPageReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.EntityRelationshipRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.EntityRelationshipSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.ParentChildRelationshipSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.ParentChildRelationshipRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.RelationshipTypeRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.EntityWithChildrenRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.ChildEntityInfoRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.AppEntitiesRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.EntityInfoRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.EntityFieldInfoRespVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.core.enums.CascadeTypeEnum;
import com.cmsr.onebase.module.metadata.core.enums.RelationshipTypeEnum;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.build.service.entity.MetadataBusinessEntityBuildService;
import com.cmsr.onebase.module.metadata.build.service.entity.MetadataEntityFieldBuildService;
import com.cmsr.onebase.module.metadata.build.service.entity.vo.EntityFieldQueryVO;
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
import java.util.stream.Collectors;

/**
 * 实体关系 Service 实现类
 *
 * @author bty418
 * @date 2025-01-25
 */
@Service
@Slf4j
public class MetadataEntityRelationshipBuildServiceImpl implements MetadataEntityRelationshipBuildService {

    @Resource
    private MetadataEntityRelationshipRepository entityRelationshipRepository;

    @Resource
    private MetadataBusinessEntityCoreService businessEntityService;

    @Resource
    private MetadataBusinessEntityBuildService businessEntityBuildService;

    @Resource
    private MetadataEntityFieldBuildService entityFieldService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEntityRelationship(@Valid EntityRelationshipSaveReqVO createReqVO) {
        // 插入实体关系
        MetadataEntityRelationshipDO entityRelationship = BeanUtils.toBean(createReqVO, MetadataEntityRelationshipDO.class);
        entityRelationship.setSourceEntityId(Long.valueOf(createReqVO.getSourceEntityId()));
        entityRelationship.setTargetEntityId(Long.valueOf(createReqVO.getTargetEntityId()));
        entityRelationship.setAppId(Long.valueOf(createReqVO.getAppId()));
        entityRelationshipRepository.insert(entityRelationship);

        return entityRelationship.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEntityRelationship(@Valid EntityRelationshipSaveReqVO updateReqVO) {
        // 校验ID不能为空
        if (!StringUtils.hasText(updateReqVO.getId())) {
            throw new IllegalArgumentException("更新操作必须提供实体关系ID");
        }
        
        // 校验存在
        validateEntityRelationshipExists(Long.valueOf(updateReqVO.getId()));

        // 更新实体关系
        MetadataEntityRelationshipDO updateObj = BeanUtils.toBean(updateReqVO, MetadataEntityRelationshipDO.class);
        updateObj.setId(Long.valueOf(updateReqVO.getId()));
        updateObj.setSourceEntityId(Long.valueOf(updateReqVO.getSourceEntityId()));
        updateObj.setTargetEntityId(Long.valueOf(updateReqVO.getTargetEntityId()));
        updateObj.setAppId(Long.valueOf(updateReqVO.getAppId()));
        entityRelationshipRepository.update(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEntityRelationship(Long id) {
        // 校验存在
        validateEntityRelationshipExists(id);

        // 删除实体关系
        entityRelationshipRepository.deleteById(id);
    }

    @Override
    public EntityRelationshipRespVO getEntityRelationshipDetail(Long id) {
        if (id == null) {
            log.warn("查询实体关系详情时，ID参数为空");
            throw new IllegalArgumentException("实体关系ID不能为空");
        }

        MetadataEntityRelationshipDO entityRelationship = entityRelationshipRepository.findById(id);
        if (entityRelationship == null) {
            log.warn("未找到ID为{}的实体关系", id);
            throw new IllegalArgumentException("实体关系不存在");
        }

        EntityRelationshipRespVO result = convertToRespVO(entityRelationship);

        // 验证转换结果
        if (result == null || result.getId() == null) {
            log.error("转换实体关系详情失败，原始ID: {}, 转换结果: {}", id, result);
            throw new RuntimeException("实体关系数据转换失败");
        }

        return result;
    }

    @Override
    public PageResult<EntityRelationshipRespVO> getEntityRelationshipPage(EntityRelationshipPageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        // 添加查询条件
        if (pageReqVO.getAppId() != null) {
            configStore.and(MetadataEntityRelationshipDO.APP_ID, pageReqVO.getAppId());
        }

        // 添加调试日志，查看 entityId 的值
        log.info("接收到的请求参数: entityId={}, appId={}, pageNo={}, pageSize={}",
                pageReqVO.getEntityId(), pageReqVO.getAppId(), pageReqVO.getPageNo(), pageReqVO.getPageSize());
        log.info("entityId为空检查: entityId==null:{}, StringUtils.hasText:{}",
                pageReqVO.getEntityId() == null, StringUtils.hasText(pageReqVO.getEntityId()));

        // 优先处理 entityId 参数 - 查询与该实体相关的所有关系（无论作为源实体还是目标实体）
        if (StringUtils.hasText(pageReqVO.getEntityId())) {
            Long entityIdLong = Long.valueOf(pageReqVO.getEntityId());
            log.info("解析实体ID: 字符串={}, Long={}", pageReqVO.getEntityId(), entityIdLong);

            // 先检查该实体是否存在任何关联关系
            DefaultConfigStore checkConfigStore = new DefaultConfigStore();
            if (pageReqVO.getAppId() != null) {
                checkConfigStore.and(MetadataEntityRelationshipDO.APP_ID, pageReqVO.getAppId());
            }
            // 使用正确的嵌套OR语法
            checkConfigStore.and(new DefaultConfigStore()
                    .or(MetadataEntityRelationshipDO.SOURCE_ENTITY_ID, entityIdLong)
                    .or(MetadataEntityRelationshipDO.TARGET_ENTITY_ID, entityIdLong));

            // 查询是否存在相关记录
            List<MetadataEntityRelationshipDO> existingRelations = entityRelationshipRepository.findAllByConfig(checkConfigStore);

            if (existingRelations.isEmpty()) {
                // 如果找不到任何相关记录，直接返回空结果
                log.info("未找到实体相关关系，实体ID: {}，返回空结果", pageReqVO.getEntityId());
                return new PageResult<>(List.of(), 0L);
            }

            // 使用嵌套 OR 条件：(source_entity_id = entityId OR target_entity_id = entityId)
            configStore.and(new DefaultConfigStore()
                    .or(MetadataEntityRelationshipDO.SOURCE_ENTITY_ID, entityIdLong)
                    .or(MetadataEntityRelationshipDO.TARGET_ENTITY_ID, entityIdLong));

            log.info("查询实体相关关系，实体ID: {}，找到 {} 条相关记录", pageReqVO.getEntityId(), existingRelations.size());
        } else {
            // 如果没有传入 entityId，则使用精确的源实体ID和目标实体ID查询
            if (StringUtils.hasText(pageReqVO.getSourceEntityId())) {
                configStore.and(MetadataEntityRelationshipDO.SOURCE_ENTITY_ID, Long.valueOf(pageReqVO.getSourceEntityId()));
            }
            if (StringUtils.hasText(pageReqVO.getTargetEntityId())) {
                configStore.and(MetadataEntityRelationshipDO.TARGET_ENTITY_ID, Long.valueOf(pageReqVO.getTargetEntityId()));
            }
        }

        if (StringUtils.hasText(pageReqVO.getRelationshipType())) {
            configStore.and(MetadataEntityRelationshipDO.RELATIONSHIP_TYPE, pageReqVO.getRelationshipType());
        }

        configStore.order("create_time", Order.TYPE.DESC);

        // 添加分页参数调试日志
        log.info("分页查询参数: pageNo={}, pageSize={}", pageReqVO.getPageNo(), pageReqVO.getPageSize());

        // 分页查询
        PageResult<MetadataEntityRelationshipDO> pageResult = entityRelationshipRepository.findPageWithConditions(
            configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());

        log.info("分页查询结果: 当前页记录数={}, 总记录数={}",
                pageResult.getList().size(), pageResult.getTotal());

        // 验证查询结果
        if (pageResult.getList() == null || pageResult.getList().isEmpty()) {
            log.info("分页查询结果为空，返回空结果");
            return new PageResult<>(List.of(), pageResult.getTotal());
        }

        // 转换为响应VO
        List<EntityRelationshipRespVO> respVOList = pageResult.getList().stream()
                .map(this::convertToRespVO)
                .filter(vo -> vo != null) // 过滤掉转换失败的VO
                .toList();

        // 验证转换结果
        if (respVOList.size() != pageResult.getList().size()) {
            log.warn("数据转换过程中有记录丢失，原始记录数: {}, 转换后记录数: {}",
                    pageResult.getList().size(), respVOList.size());
        }

        return new PageResult<>(respVOList, pageResult.getTotal());
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
        if (entityRelationshipRepository.findById(id) == null) {
            throw new IllegalArgumentException("实体关系不存在");
        }
    }

    /**
     * 转换为响应VO
     */
    private EntityRelationshipRespVO convertToRespVO(MetadataEntityRelationshipDO relationshipDO) {
        if (relationshipDO == null) {
            log.warn("转换响应VO时，输入参数 relationshipDO 为空");
            return null;
        }

        EntityRelationshipRespVO result = BeanUtils.toBean(relationshipDO, EntityRelationshipRespVO.class);

        // 确保关键字段正确设置
        if (result.getId() == null && relationshipDO.getId() != null) {
            result.setId(relationshipDO.getId());
            log.debug("手动设置响应VO的id字段: {}", relationshipDO.getId());
        }

        // 查询源实体和目标实体的名称
        result.setSourceEntityName(getEntityNameById(relationshipDO.getSourceEntityId()));
        result.setTargetEntityName(getEntityNameById(relationshipDO.getTargetEntityId()));

        // 查询源字段和目标字段的名称
        result.setSourceFieldName(getFieldNameById(relationshipDO.getSourceFieldId()));
        result.setTargetFieldName(getFieldNameById(relationshipDO.getTargetFieldId()));

        // 查询源字段和目标字段的展示名称
        result.setSourceFieldDisplayName(getFieldDisplayNameById(relationshipDO.getSourceFieldId()));
        result.setTargetFieldDisplayName(getFieldDisplayNameById(relationshipDO.getTargetFieldId()));

        // 验证关键字段
        if (result.getId() == null) {
            log.error("转换后的响应VO中id字段为空，原始DO的id: {}", relationshipDO.getId());
        }

        return result;
    }

    /**
     * 转换关系类型枚举为响应VO
     */
    private RelationshipTypeRespVO convertToRelationshipTypeRespVO(RelationshipTypeEnum relationshipTypeEnum) {
        return BeanUtils.toBean(relationshipTypeEnum, RelationshipTypeRespVO.class, respVO -> {
            respVO.setRelationshipType(relationshipTypeEnum.getRelationshipType());
            respVO.setDisplayName(relationshipTypeEnum.getDisplayName());
            respVO.setDescription(relationshipTypeEnum.getDescription());
        });
    }

    /**
     * 转换级联类型枚举为响应VO
     */
    private CascadeTypeRespVO convertToCascadeTypeRespVO(CascadeTypeEnum cascadeTypeEnum) {
        return BeanUtils.toBean(cascadeTypeEnum, CascadeTypeRespVO.class, respVO -> {
            respVO.setCascadeType(cascadeTypeEnum.getCascadeType());
            respVO.setDisplayName(cascadeTypeEnum.getDisplayName());
            respVO.setDescription(cascadeTypeEnum.getDescription());
        });
    }

    @Override
    public List<EntityRelationshipRespVO> getRelationshipsByDatasourceId(Long datasourceId) {
        // 首先获取该数据源下的所有实体ID
        DefaultConfigStore entityConfigStore = new DefaultConfigStore();
        entityConfigStore.and(MetadataBusinessEntityDO.DATASOURCE_ID, datasourceId);
        List<MetadataBusinessEntityDO> entities = businessEntityService.findAllByConfig(entityConfigStore);

        if (entities.isEmpty()) {
            return List.of();
        }

        List<Long> entityIds = entities.stream()
                .map(MetadataBusinessEntityDO::getId)
                .toList();

        // 查询涉及这些实体的所有关系 - 使用嵌套 OR + IN，避免 Anyline 生成非法 SQL
        DefaultConfigStore relationshipConfigStore = new DefaultConfigStore();
        ConfigStore orStore = new DefaultConfigStore()
                .or(Compare.IN, MetadataEntityRelationshipDO.SOURCE_ENTITY_ID, entityIds)
                .or(Compare.IN, MetadataEntityRelationshipDO.TARGET_ENTITY_ID, entityIds);
        relationshipConfigStore.and(orStore);
        relationshipConfigStore.order("create_time", Order.TYPE.DESC);

        List<MetadataEntityRelationshipDO> relationships = entityRelationshipRepository.findAllByConfig(relationshipConfigStore);

        return relationships.stream()
                .map(this::convertToRespVO)
                .filter(vo -> vo != null) // 过滤掉转换失败的VO
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
            MetadataBusinessEntityDO entity = businessEntityService.getBusinessEntity(entityId);
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
        if (!StringUtils.hasText(fieldId)) {
            return null;
        }

        try {
            DefaultConfigStore configStore = new DefaultConfigStore();
            configStore.and("id", Long.valueOf(fieldId));
            MetadataEntityFieldDO field = entityFieldService.getEntityField(fieldId);
            return field != null ? field.getFieldName() : null;
        } catch (NumberFormatException e) {
            log.warn("无效的字段ID: {}", fieldId);
            return null;
        } catch (Exception e) {
            log.warn("获取字段名称失败，字段ID: {}, 错误: {}", fieldId, e.getMessage());
            return null;
        }
    }

    /**
     * 根据字段ID获取字段展示名称
     *
     * @param fieldId 字段ID
     * @return 字段展示名称
     */
    private String getFieldDisplayNameById(String fieldId) {
        if (!StringUtils.hasText(fieldId)) {
            return null;
        }

        try {
            DefaultConfigStore configStore = new DefaultConfigStore();
            configStore.and("id", Long.valueOf(fieldId));
            MetadataEntityFieldDO field = entityFieldService.getEntityField(fieldId);
            return field != null ? field.getDisplayName() : null;
        } catch (NumberFormatException e) {
            log.warn("无效的字段ID: {}", fieldId);
            return null;
        } catch (Exception e) {
            log.warn("获取字段展示名称失败，字段ID: {}, 错误: {}", fieldId, e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ParentChildRelationshipRespVO createParentChildRelationship(@Valid ParentChildRelationshipSaveReqVO createReqVO) {
        log.info("开始创建主子关系，主表实体ID: {}, 子表实体ID: {}", createReqVO.getParentEntityId(), createReqVO.getChildEntityId());

        // 1. 获取或创建子表实体
        Long childEntityId;
        if (StringUtils.hasText(createReqVO.getChildEntityId())) {
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
        EntityRelationshipSaveReqVO relationshipReqVO = BeanUtils.toBean(createReqVO, EntityRelationshipSaveReqVO.class, req -> {
            req.setRelationName("主子关系");
            req.setSourceEntityId(createReqVO.getParentEntityId());
            req.setTargetEntityId(String.valueOf(childEntityId));
            req.setRelationshipType(RelationshipTypeEnum.ONE_TO_MANY.getRelationshipType());
            req.setSourceFieldId(String.valueOf(parentIdFieldId));
            req.setTargetFieldId(String.valueOf(childParentIdFieldId));
            req.setCascadeType(CascadeTypeEnum.ALL.getCascadeType()); // 默认级联新增、删除、查询
            req.setDescription("系统自动创建的主子关系");
        });

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
        // 获取主表实体信息
        MetadataBusinessEntityDO parentEntity = businessEntityService.getBusinessEntity(Long.valueOf(createReqVO.getParentEntityId()));
        if (parentEntity == null) {
            throw new IllegalArgumentException("主表实体不存在，实体ID: " + createReqVO.getParentEntityId());
        }
        
        log.info("创建子表实体，主表ID: {}, 主表数据源ID: {}", parentEntity.getId(), parentEntity.getDatasourceId());
        
        BusinessEntitySaveReqVO entityReqVO = BeanUtils.toBean(createReqVO, BusinessEntitySaveReqVO.class, req -> {
            req.setDisplayName(createReqVO.getChildTableName());
            req.setCode(createReqVO.getChildTableCode());
            req.setDescription(createReqVO.getChildTableDescription());
            req.setEntityType(1); // 自建表
            req.setRunMode(0); // 默认运行模式
            // 关键修复：使用主表的datasourceId，确保主子表在同一个数据源下
            req.setDatasourceId(String.valueOf(parentEntity.getDatasourceId()));
        });

        Long childEntityId = businessEntityBuildService.createBusinessEntity(entityReqVO);
        log.info("子表实体创建成功，子表ID: {}, 数据源ID: {}", childEntityId, parentEntity.getDatasourceId());
        
        return childEntityId;
    }

    /**
     * 获取实体的id字段ID
     *
     * @param entityId 实体ID
     * @return id字段ID
     */
    private Long getEntityIdField(Long entityId) {
        // 构建查询条件，查找指定实体的id字段
        EntityFieldQueryVO queryVO = new EntityFieldQueryVO();
        queryVO.setEntityId(String.valueOf(entityId));
        queryVO.setKeyword("id"); // 使用关键字搜索id字段

        List<MetadataEntityFieldDO> fields = entityFieldService.getEntityFieldListByConditions(queryVO);

        // 查找字段名为"id"的字段
        MetadataEntityFieldDO idField = fields.stream()
                .filter(field -> "id".equals(field.getFieldName()))
                .findFirst()
                .orElse(null);

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
        // 构建查询条件，查找指定实体的parent_id字段
        EntityFieldQueryVO queryVO = new EntityFieldQueryVO();
        queryVO.setEntityId(String.valueOf(childEntityId));
        queryVO.setKeyword("parent_id"); // 使用关键字搜索parent_id字段

        List<MetadataEntityFieldDO> fields = entityFieldService.getEntityFieldListByConditions(queryVO);

        // 查找字段名为"parent_id"的字段
        MetadataEntityFieldDO parentIdField = fields.stream()
                .filter(field -> "parent_id".equals(field.getFieldName()))
                .findFirst()
                .orElse(null);

        if (parentIdField != null) {
            return parentIdField.getId();
        }

        throw new IllegalArgumentException("子表实体未找到parent_id字段，请先为子表添加parent_id字段，实体ID: " + childEntityId);
    }

    @Override
    public EntityWithChildrenRespVO getEntityWithChildrenById(Long entityId, String relationshipType) {
        // 1. 获取实体基本信息
        MetadataBusinessEntityDO entity = businessEntityService.getBusinessEntity(entityId);
        if (entity == null) {
            throw new IllegalArgumentException("实体不存在，实体ID: " + entityId);
        }

        // 2. 创建响应VO
        EntityWithChildrenRespVO result = BeanUtils.toBean(entity, EntityWithChildrenRespVO.class, res -> {
            res.setEntityId(entity.getId());
            res.setEntityName(entity.getDisplayName());
            res.setEntityCode(entity.getCode());
            // 设置实际表名
            res.setTableName(entity.getTableName());
        });

        // 3. 查询以该实体为源实体的所有关系（即该实体作为父表的关系）
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataEntityRelationshipDO.SOURCE_ENTITY_ID, entityId);
        // 增加关系类型筛选条件
        if (StringUtils.hasText(relationshipType)) {
            configStore.and(MetadataEntityRelationshipDO.RELATIONSHIP_TYPE, relationshipType);
        }
        configStore.order("create_time", Order.TYPE.DESC);

        List<MetadataEntityRelationshipDO> relationships = entityRelationshipRepository.findAllByConfig(configStore);

        // 4. 填充父表字段信息
        List<EntityFieldInfoRespVO> parentFields = getEntityFields(entityId);
        result.setParentFields(parentFields);

        // 5. 转换为子表信息列表
        List<ChildEntityInfoRespVO> childEntities = relationships.stream()
                .map(this::convertToChildEntityInfo)
                .toList();

        result.setChildEntities(childEntities);

        log.info("查询实体及其关联子表成功，实体ID: {}, 关系类型筛选: {}, 关联子表数量: {}, 父表字段数量: {}",
                entityId, relationshipType, childEntities.size(), parentFields.size());
        return result;
    }

    /**
     * 转换关系DO为子表信息
     *
     * @param relationshipDO 关系DO
     * @return 子表信息
     */
    private ChildEntityInfoRespVO convertToChildEntityInfo(MetadataEntityRelationshipDO relationshipDO) {
        ChildEntityInfoRespVO childInfo = BeanUtils.toBean(relationshipDO, ChildEntityInfoRespVO.class, info -> {
            info.setChildEntityId(relationshipDO.getTargetEntityId());
            info.setRelationshipId(String.valueOf(relationshipDO.getId()));
            info.setRelationshipName(relationshipDO.getRelationName());
            info.setRelationshipType(relationshipDO.getRelationshipType());

            // 获取目标实体信息
            MetadataBusinessEntityDO targetEntity = businessEntityService.getBusinessEntity(relationshipDO.getTargetEntityId());
            if (targetEntity != null) {
                info.setChildEntityName(targetEntity.getDisplayName());
                info.setChildEntityCode(targetEntity.getCode());
                // 设置子表实际表名
                info.setChildTableName(targetEntity.getTableName());
            }
        });

        // 获取字段名称
        childInfo.setSourceFieldName(getFieldNameById(relationshipDO.getSourceFieldId()));
        childInfo.setTargetFieldName(getFieldNameById(relationshipDO.getTargetFieldId()));

        // 填充子表字段信息
        List<EntityFieldInfoRespVO> childFields = getEntityFields(relationshipDO.getTargetEntityId());
        childInfo.setChildFields(childFields);

        return childInfo;
    }

    @Override
    public AppEntitiesRespVO getAppEntitiesWithFields(Long appId) {
        log.info("开始查询应用实体及字段信息，应用ID: {}", appId);

        // 1. 根据appId查询该应用下的所有实体
        DefaultConfigStore entityConfigStore = new DefaultConfigStore();
        entityConfigStore.and(MetadataBusinessEntityDO.APP_ID, appId);
        entityConfigStore.order("create_time", Order.TYPE.ASC);

        List<MetadataBusinessEntityDO> entities = businessEntityService.findAllByConfig(entityConfigStore);

        if (entities.isEmpty()) {
            log.info("应用下未找到任何实体，应用ID: {}", appId);
            return new AppEntitiesRespVO().setEntities(List.of());
        }

        // 2. 转换为响应VO
        List<EntityInfoRespVO> entityInfoList = entities.stream()
                .map(this::convertToEntityInfo)
                .toList();

        AppEntitiesRespVO result = new AppEntitiesRespVO().setEntities(entityInfoList);

        log.info("查询应用实体及字段信息完成，应用ID: {}, 实体数量: {}", appId, entityInfoList.size());
        return result;
    }

    /**
     * 转换实体DO为实体信息VO
     *
     * @param entityDO 实体DO
     * @return 实体信息VO
     */
    private EntityInfoRespVO convertToEntityInfo(MetadataBusinessEntityDO entityDO) {
        return BeanUtils.toBean(entityDO, EntityInfoRespVO.class, entityInfo -> {
            entityInfo.setEntityId(String.valueOf(entityDO.getId()));
            entityInfo.setEntityName(entityDO.getDisplayName());
            // 设置实际表名
            entityInfo.setTableName(entityDO.getTableName());

            // 判断实体类型 - 查询是否存在以该实体为源实体的关系来判断是否为主表
            entityInfo.setEntityType(determineEntityType(entityDO.getId()));

            // 查询该实体的所有字段
            List<EntityFieldInfoRespVO> fields = getEntityFields(entityDO.getId());
            entityInfo.setFields(fields);
        });
    }

    /**
     * 判断实体类型（主表/子表）
     *
     * @param entityId 实体ID
     * @return 实体类型
     */
    private String determineEntityType(Long entityId) {
        // 检查是否存在以该实体为源实体的关系（即该实体作为主表）
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataEntityRelationshipDO.SOURCE_ENTITY_ID, entityId);

        List<MetadataEntityRelationshipDO> asSourceRelationships = entityRelationshipRepository.findAllByConfig(configStore);

        // 检查是否存在以该实体为目标实体的关系（即该实体作为子表）
        DefaultConfigStore targetConfigStore = new DefaultConfigStore();
        targetConfigStore.and(MetadataEntityRelationshipDO.TARGET_ENTITY_ID, entityId);

        List<MetadataEntityRelationshipDO> asTargetRelationships = entityRelationshipRepository.findAllByConfig(targetConfigStore);

        if (!asSourceRelationships.isEmpty() && asTargetRelationships.isEmpty()) {
            return "主表";
        } else if (asSourceRelationships.isEmpty() && !asTargetRelationships.isEmpty()) {
            return "子表";
        } else if (!asSourceRelationships.isEmpty() && !asTargetRelationships.isEmpty()) {
            return "主子表";
        } else {
            return "独立表";
        }
    }

    /**
     * 获取实体的所有字段信息
     *
     * @param entityId 实体ID
     * @return 字段信息列表
     */
    private List<EntityFieldInfoRespVO> getEntityFields(Long entityId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataEntityFieldDO.ENTITY_ID, entityId);
        configStore.order(MetadataEntityFieldDO.SORT_ORDER, Order.TYPE.ASC);

        List<MetadataEntityFieldDO> fields = entityFieldService.findAllByConfig(configStore);

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
        });
    }

    @Override
    public List<MetadataEntityRelationshipDO> findAllByConfig(DefaultConfigStore configStore) {
        return entityRelationshipRepository.findAllByConfig(configStore);
    }

    /**
     * 根据字段id删除关联关系 包括字段作为 源字段和目标字段 两种情况
     * @param fieldId
     * @return
     */
    public void deleteRelationShipByFieldId(Long fieldId){
        List<MetadataEntityRelationshipDO> relationships = entityRelationshipRepository.getRelationshipsByFieldId(fieldId);
        List<Long> ids = relationships.stream().map(MetadataEntityRelationshipDO::getId).collect(Collectors.toList());
        for(Long id : ids){
            entityRelationshipRepository.deleteById(id);
        }
    }

    @Override
    public List<MetadataEntityRelationshipDO> findBySourceEntityIdAndTargetEntityId(Long sourceEntityId, Long targetEntityId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataEntityRelationshipDO.SOURCE_ENTITY_ID, sourceEntityId);
        configStore.and(MetadataEntityRelationshipDO.TARGET_ENTITY_ID, targetEntityId);
        configStore.order("create_time", Order.TYPE.DESC);
        return entityRelationshipRepository.findAllByConfig(configStore);
    }
}
