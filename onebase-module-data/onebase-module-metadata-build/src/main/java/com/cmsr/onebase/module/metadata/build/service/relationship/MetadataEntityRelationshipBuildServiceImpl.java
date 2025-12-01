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
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
        // 校验关系类型必须是枚举中定义的值
        validateRelationshipType(createReqVO.getRelationshipType());
        
        // 插入实体关系
        MetadataEntityRelationshipDO entityRelationship = BeanUtils.toBean(createReqVO, MetadataEntityRelationshipDO.class);
        entityRelationship.setSourceEntityId(Long.valueOf(createReqVO.getSourceEntityId()));
        entityRelationship.setTargetEntityId(Long.valueOf(createReqVO.getTargetEntityId()));
        entityRelationship.setAppId(Long.valueOf(createReqVO.getAppId()));
        entityRelationshipRepository.save(entityRelationship);

        return entityRelationship.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEntityRelationship(@Valid EntityRelationshipSaveReqVO updateReqVO) {
        // 校验ID不能为空
        if (!StringUtils.hasText(updateReqVO.getId())) {
            throw new IllegalArgumentException("更新操作必须提供实体关系ID");
        }
        
        // 校验关系类型必须是枚举中定义的值
        validateRelationshipType(updateReqVO.getRelationshipType());
        
        // 校验存在
        validateEntityRelationshipExists(Long.valueOf(updateReqVO.getId()));

        // 更新实体关系
        MetadataEntityRelationshipDO updateObj = BeanUtils.toBean(updateReqVO, MetadataEntityRelationshipDO.class);
        updateObj.setId(Long.valueOf(updateReqVO.getId()));
        updateObj.setSourceEntityId(Long.valueOf(updateReqVO.getSourceEntityId()));
        updateObj.setTargetEntityId(Long.valueOf(updateReqVO.getTargetEntityId()));
        updateObj.setAppId(Long.valueOf(updateReqVO.getAppId()));
        entityRelationshipRepository.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEntityRelationship(Long id) {
        // 校验存在
        validateEntityRelationshipExists(id);

        // 删除实体关系
        entityRelationshipRepository.removeById(id);
    }

    @Override
    public EntityRelationshipRespVO getEntityRelationshipDetail(Long id) {
        if (id == null) {
            log.warn("查询实体关系详情时，ID参数为空");
            throw new IllegalArgumentException("实体关系ID不能为空");
        }

        MetadataEntityRelationshipDO entityRelationship = entityRelationshipRepository.getById(id);
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
        QueryWrapper queryWrapper = entityRelationshipRepository.query();

        // 添加查询条件
        if (pageReqVO.getAppId() != null) {
            queryWrapper.eq(MetadataEntityRelationshipDO::getAppId, pageReqVO.getAppId());
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
            List<MetadataEntityRelationshipDO> existingRelations = entityRelationshipRepository.getRelationshipsByEntityId(entityIdLong);

            // 如果需要过滤appId
            if (StringUtils.hasText(pageReqVO.getAppId())) {
                Long appId = Long.valueOf(pageReqVO.getAppId());
                existingRelations = existingRelations.stream()
                        .filter(r -> appId.equals(r.getAppId()))
                        .toList();
            }

            if (existingRelations.isEmpty()) {
                // 如果找不到任何相关记录，直接返回空结果
                log.info("未找到实体相关关系，实体ID: {}，返回空结果", pageReqVO.getEntityId());
                return new PageResult<>(List.of(), 0L);
            }

            // 使用 OR 条件：(source_entity_id = entityId OR target_entity_id = entityId)
            queryWrapper.eq(MetadataEntityRelationshipDO::getSourceEntityId, entityIdLong)
                    .or(MetadataEntityRelationshipDO::getTargetEntityId).eq(entityIdLong);

            log.info("查询实体相关关系，实体ID: {}，找到 {} 条相关记录", pageReqVO.getEntityId(), existingRelations.size());
        } else {
            // 如果没有传入 entityId，则使用精确的源实体ID和目标实体ID查询
            if (StringUtils.hasText(pageReqVO.getSourceEntityId())) {
                queryWrapper.eq(MetadataEntityRelationshipDO::getSourceEntityId, Long.valueOf(pageReqVO.getSourceEntityId()));
            }
            if (StringUtils.hasText(pageReqVO.getTargetEntityId())) {
                queryWrapper.eq(MetadataEntityRelationshipDO::getTargetEntityId, Long.valueOf(pageReqVO.getTargetEntityId()));
            }
        }

        if (StringUtils.hasText(pageReqVO.getRelationshipType())) {
            queryWrapper.eq(MetadataEntityRelationshipDO::getRelationshipType, pageReqVO.getRelationshipType());
        }

        queryWrapper.orderBy(MetadataEntityRelationshipDO::getCreateTime, false);

        // 添加分页参数调试日志
        log.info("分页查询参数: pageNo={}, pageSize={}", pageReqVO.getPageNo(), pageReqVO.getPageSize());

        // 分页查询
        Page<MetadataEntityRelationshipDO> page = entityRelationshipRepository.page(
                Page.of(pageReqVO.getPageNo(), pageReqVO.getPageSize()), queryWrapper);

        log.info("分页查询结果: 当前页记录数={}, 总记录数={}",
                page.getRecords().size(), page.getTotalRow());

        // 验证查询结果
        if (page.getRecords() == null || page.getRecords().isEmpty()) {
            log.info("分页查询结果为空，返回空结果");
            return new PageResult<>(List.of(), page.getTotalRow());
        }

        // 转换为响应VO
        List<EntityRelationshipRespVO> respVOList = page.getRecords().stream()
                .map(this::convertToRespVO)
                .filter(vo -> vo != null) // 过滤掉转换失败的VO
                .toList();

        // 验证转换结果
        if (respVOList.size() != page.getRecords().size()) {
            log.warn("数据转换过程中有记录丢失，原始记录数: {}, 转换后记录数: {}",
                    page.getRecords().size(), respVOList.size());
        }

        return new PageResult<>(respVOList, page.getTotalRow());
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
        if (entityRelationshipRepository.getById(id) == null) {
            throw new IllegalArgumentException("实体关系不存在");
        }
    }

    /**
     * 校验关系类型是否有效
     * 关系类型必须是 RelationshipTypeEnum 枚举中定义的值
     *
     * @param relationshipType 关系类型
     */
    private void validateRelationshipType(String relationshipType) {
        if (!StringUtils.hasText(relationshipType)) {
            throw new IllegalArgumentException("关系类型不能为空");
        }
        if (!RelationshipTypeEnum.isValidType(relationshipType)) {
            throw new IllegalArgumentException("无效的关系类型: " + relationshipType + 
                    "，有效值为: " + String.join(", ", RelationshipTypeEnum.getAllTypes()));
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
        List<MetadataBusinessEntityDO> entities = businessEntityService.getBusinessEntityListByDatasourceId(datasourceId);

        if (entities.isEmpty()) {
            return List.of();
        }

        List<Long> entityIds = entities.stream()
                .map(MetadataBusinessEntityDO::getId)
                .toList();

        // 查询涉及这些实体的所有关系 - 使用 OR + IN
        QueryWrapper queryWrapper = entityRelationshipRepository.query()
                .in(MetadataEntityRelationshipDO::getSourceEntityId, entityIds)
                .or(MetadataEntityRelationshipDO::getTargetEntityId).in(entityIds)
                .orderBy(MetadataEntityRelationshipDO::getCreateTime, false);

        List<MetadataEntityRelationshipDO> relationships = entityRelationshipRepository.list(queryWrapper);

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

        // 3. 创建主子关系，使用 SUBTABLE_ONE_TO_MANY 关系类型
        EntityRelationshipSaveReqVO relationshipReqVO = BeanUtils.toBean(createReqVO, EntityRelationshipSaveReqVO.class, req -> {
            req.setRelationName("主子关系");
            req.setSourceEntityId(createReqVO.getParentEntityId());
            req.setTargetEntityId(String.valueOf(childEntityId));
            req.setRelationshipType(RelationshipTypeEnum.SUBTABLE_ONE_TO_MANY.getRelationshipType());
            req.setSourceFieldId(String.valueOf(parentIdFieldId));
            req.setTargetFieldId(String.valueOf(childParentIdFieldId));
            req.setCascadeType(CascadeTypeEnum.ALL.getCascadeType()); // 默认级联新增、删除、查询
            req.setDescription("系统自动创建的主子表关系");
        });

        Long relationshipId = createEntityRelationship(relationshipReqVO);

        // 4. 转换为响应VO
        ParentChildRelationshipRespVO result = new ParentChildRelationshipRespVO();
        result.setId(relationshipId);
        result.setParentEntityId(Long.valueOf(createReqVO.getParentEntityId()));
        result.setChildEntityId(childEntityId);
        result.setSourceFieldId(parentIdFieldId);
        result.setTargetFieldId(childParentIdFieldId);
        result.setRelationshipType(RelationshipTypeEnum.SUBTABLE_ONE_TO_MANY.getRelationshipType());
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
        QueryWrapper queryWrapper = entityRelationshipRepository.query()
                .eq(MetadataEntityRelationshipDO::getSourceEntityId, entityId);
        // 增加关系类型筛选条件
        if (StringUtils.hasText(relationshipType)) {
            queryWrapper.eq(MetadataEntityRelationshipDO::getRelationshipType, relationshipType);
        }
        queryWrapper.orderBy(MetadataEntityRelationshipDO::getCreateTime, false);

        List<MetadataEntityRelationshipDO> relationships = entityRelationshipRepository.list(queryWrapper);

        // 4. 填充父表字段信息
        List<EntityFieldInfoRespVO> parentFields = getEntityFields(entityId);
        result.setParentFields(parentFields);

        // 5. 转换为子表信息列表
        List<ChildEntityInfoRespVO> childEntities = relationships.stream()
                .map(this::convertToChildEntityInfo).filter(Objects::nonNull)
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
        // 获取目标实体信息
        MetadataBusinessEntityDO targetEntity = businessEntityService.getBusinessEntity(relationshipDO.getTargetEntityId());
        // 关联的目标实体为空（不存在或被删除）直接跳过后续处理
        if (targetEntity == null) {
            return null;
        }
        ChildEntityInfoRespVO childInfo = BeanUtils.toBean(relationshipDO, ChildEntityInfoRespVO.class, info -> {
                info.setChildEntityId(relationshipDO.getTargetEntityId());
                info.setRelationshipId(String.valueOf(relationshipDO.getId()));
                info.setRelationshipName(relationshipDO.getRelationName());
                info.setRelationshipType(relationshipDO.getRelationshipType());
                info.setChildEntityName(targetEntity.getDisplayName());
                info.setChildEntityCode(targetEntity.getCode());
                // 设置子表实际表名
                info.setChildTableName(targetEntity.getTableName());
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
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(MetadataBusinessEntityDO::getAppId, appId)
                .orderBy(MetadataBusinessEntityDO::getCreateTime, true);

        List<MetadataBusinessEntityDO> entities = businessEntityService.findAllByConfig(queryWrapper);

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
        List<MetadataEntityRelationshipDO> asSourceRelationships = entityRelationshipRepository.getRelationshipsByMasterEntityId(entityId);

        // 检查是否存在以该实体为目标实体的关系（即该实体作为子表）
        List<MetadataEntityRelationshipDO> asTargetRelationships = entityRelationshipRepository.getRelationshipsBySlaveEntityId(entityId);

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
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(MetadataEntityFieldDO::getEntityId, entityId)
                .orderBy(MetadataEntityFieldDO::getSortOrder, true);

        List<MetadataEntityFieldDO> fields = entityFieldService.findAllByConfig(queryWrapper);

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
    public List<MetadataEntityRelationshipDO> findAllByConfig(QueryWrapper queryWrapper) {
        return entityRelationshipRepository.list(queryWrapper);
    }

    /**
     * 根据字段id删除关联关系 包括字段作为 源字段和目标字段 两种情况
     * @param fieldId 字段ID
     */
    @Override
    public void deleteRelationShipByFieldId(Long fieldId){
        List<MetadataEntityRelationshipDO> relationships = entityRelationshipRepository.getRelationshipsByFieldId(fieldId);
        List<Long> ids = relationships.stream().map(MetadataEntityRelationshipDO::getId).collect(Collectors.toList());
        for(Long id : ids){
            entityRelationshipRepository.removeById(id);
        }
    }

    @Override
    public List<MetadataEntityRelationshipDO> findBySourceEntityIdAndTargetEntityId(Long sourceEntityId, Long targetEntityId) {
        QueryWrapper queryWrapper = entityRelationshipRepository.query()
                .eq(MetadataEntityRelationshipDO::getSourceEntityId, sourceEntityId)
                .eq(MetadataEntityRelationshipDO::getTargetEntityId, targetEntityId)
                .orderBy(MetadataEntityRelationshipDO::getCreateTime, false);
        return entityRelationshipRepository.list(queryWrapper);
    }

    @Override
    public MetadataEntityRelationshipDO findById(Long id) {
        if (id == null) {
            return null;
        }
        return entityRelationshipRepository.getById(id);
    }
}
