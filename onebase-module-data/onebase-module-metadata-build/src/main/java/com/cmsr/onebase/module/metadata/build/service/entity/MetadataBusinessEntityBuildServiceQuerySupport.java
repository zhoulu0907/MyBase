package com.cmsr.onebase.module.metadata.build.service.entity;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.string.UuidUtils;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.IdUtil;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.BusinessEntityPageReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.BusinessEntityRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.BusinessEntitySaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.ChildEntityWithFieldsRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldQueryReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityFieldRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityWithFieldsBatchQueryReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EntityWithFieldsRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.ERDiagramRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.EREntityVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.ERFieldVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.ERRelationshipVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.SimpleEntityRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.FieldOptionRespVO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataSystemFieldsDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.field.MetadataEntityFieldOptionDO;
import com.cmsr.onebase.module.metadata.build.service.field.MetadataEntityFieldOptionBuildService;
import com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataBusinessEntityRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityRelationshipRepository;
import com.cmsr.onebase.module.metadata.build.service.datasource.MetadataDatasourceBuildService;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataAppAndDatasourceCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataSystemFieldsCoreService;
import com.cmsr.onebase.module.metadata.build.service.relationship.MetadataEntityRelationshipBuildService;
import com.cmsr.onebase.module.metadata.core.util.StatusEnumUtil;
import com.cmsr.onebase.module.metadata.core.dal.database.TemporaryDatasourceService;
import com.cmsr.onebase.module.metadata.core.enums.BusinessEntityTypeEnum;
import com.cmsr.onebase.module.metadata.core.enums.MetadataEntityRelationRoleEnum;
import com.cmsr.onebase.module.metadata.core.enums.MetadataFieldTypeCodeEnum;
import com.cmsr.onebase.module.metadata.core.util.MetadataIdUuidConverter;
import com.cmsr.onebase.module.metadata.core.dal.database.FieldTypeMappingRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.FieldTypeMappingDO;
import com.cmsr.onebase.framework.aynline.AnylineDdlHelper;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.metadata.Column;
import org.anyline.metadata.Table;
import org.anyline.service.AnylineService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.BUSINESS_ENTITY_NOT_EXISTS;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.BUSINESS_ENTITY_CODE_DUPLICATE;
/**
 * Split segment of metadata build service implementation.
 */
@Slf4j
public abstract class MetadataBusinessEntityBuildServiceQuerySupport implements MetadataBusinessEntityBuildService {

    protected abstract Long safeParseLong(String str);

    protected abstract void validateEntityType(Integer entityType);

    protected abstract void handleTableNameByEntityType(MetadataBusinessEntityDO businessEntity,
            BusinessEntitySaveReqVO createReqVO);

    @Resource
    protected ModelMapper modelMapper;
    @Resource
    protected MetadataBusinessEntityRepository metadataBusinessEntityRepository;
    @Resource
    protected MetadataEntityRelationshipRepository metadataEntityRelationshipRepository;
    @Resource
    protected MetadataEntityFieldRepository metadataEntityFieldRepository;
    @Resource
    protected MetadataDatasourceBuildService metadataDatasourceBuildService;
    @Resource
    protected MetadataSystemFieldsCoreService metadataSystemFieldsCoreService;
    @Resource
    protected MetadataEntityFieldBuildService metadataEntityFieldBuildService;
    @Resource
    protected MetadataEntityRelationshipBuildService metadataEntityRelationshipBuildService;
    @Resource
    protected TemporaryDatasourceService temporaryDatasourceService;
    @Resource
    protected MetadataAppAndDatasourceCoreService metadataAppAndDatasourceCoreService;
    @Resource
    protected MetadataEntityFieldOptionBuildService fieldOptionService;

    @Resource
    protected MetadataIdUuidConverter idUuidConverter;

    @Resource
    protected AppApplicationApi appApplicationApi;

    @Resource
    protected FieldTypeMappingRepository fieldTypeMappingRepository;

    // 需要加长度参数的数据库类型
    protected static final Set<String> LENGTH_REQUIRED_TYPES = Set.of("VARCHAR", "CHAR", "NVARCHAR", "NCHAR");
    // 需要加精度参数的数据库类型
    protected static final Set<String> PRECISION_REQUIRED_TYPES = Set.of("NUMERIC", "DECIMAL");

    // 系统字段缓存，避免频繁查询数据库
    protected volatile List<MetadataSystemFieldsDO> systemFieldsCache = null;
    protected volatile long lastCacheTime = 0;
    protected static final long CACHE_EXPIRE_TIME = 5 * 60 * 1000; // 缓存5分钟
    public void updateBusinessEntity(@Valid BusinessEntitySaveReqVO updateReqVO) {
        // ID转UUID兼容处理：支持前端传入datasourceId或datasourceUuid
        String resolvedDatasourceUuid = idUuidConverter.resolveDatasourceUuidOptional(
                updateReqVO.getDatasourceUuid(), updateReqVO.getDatasourceId());
        updateReqVO.setDatasourceUuid(resolvedDatasourceUuid);

        // 安全转换 ID 和 appId
        Long id = safeParseLong(updateReqVO.getId());
        Long appId = safeParseLong(updateReqVO.getApplicationId());
        String datasourceUuid = updateReqVO.getDatasourceUuid();
        
        // 校验存在
        if (id != null) {
            validateBusinessEntityExists(id);
        }
        
        // 校验编码唯一性（只有当code不为空时才校验）
        if (CharSequenceUtil.isNotEmpty(updateReqVO.getCode()) && id != null && appId != null) {
            validateBusinessEntityCodeUnique(id, updateReqVO.getCode(), appId);
        }
        
        // 校验实体类型
        validateEntityType(updateReqVO.getEntityType());

        // 更新业务实体
        MetadataBusinessEntityDO updateObj = BeanUtils.toBean(updateReqVO, MetadataBusinessEntityDO.class);
        updateObj.setId(id);
        updateObj.setApplicationId(appId);
        updateObj.setDatasourceUuid(datasourceUuid);

        // 处理code字段：如果为空或空字符串，则生成UUID
        if (CharSequenceUtil.isEmpty(updateReqVO.getCode())) {
            updateObj.setCode(IdUtil.simpleUUID());
        }

        // 根据实体类型处理表名
        handleTableNameByEntityType(updateObj, updateReqVO);

        metadataBusinessEntityRepository.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBusinessEntity(Long id) {
        // 校验存在
        validateBusinessEntityExists(id);
        
        // 先获取实体信息以获取UUID
        MetadataBusinessEntityDO entity = metadataBusinessEntityRepository.getBusinessEntityById(id);
        String entityUuid = entity != null ? entity.getEntityUuid() : null;

        // 删除业务实体
        metadataBusinessEntityRepository.removeById(id);

        // 删除实体关联关系
        if (entityUuid != null) {
            // 构建OR条件并用括号包裹
            QueryColumn sourceCol = new QueryColumn("source_entity_uuid");
            QueryColumn targetCol = new QueryColumn("target_entity_uuid");
            QueryCondition orCondition = sourceCol.eq(entityUuid).or(targetCol.eq(entityUuid));
            QueryCondition wrappedCondition = QueryCondition.createEmpty().and(orCondition);
            
            QueryWrapper relationshipQueryWrapper = metadataEntityRelationshipRepository.query()
                    .and(wrappedCondition);
            List<MetadataEntityRelationshipDO> relationshipDOs = metadataEntityRelationshipRepository.list(relationshipQueryWrapper);
            for(MetadataEntityRelationshipDO relationshipDO : relationshipDOs){
                metadataEntityRelationshipBuildService.deleteEntityRelationship(relationshipDO.getId());
            }
        }
    }

    protected void validateBusinessEntityExists(Long id) {
        if (!metadataBusinessEntityRepository.existsBusinessEntity(id)) {
            throw exception(BUSINESS_ENTITY_NOT_EXISTS);
        }
    }

    protected void validateBusinessEntityCodeUnique(Long id, String code, Long appId) {
        if (!metadataBusinessEntityRepository.isBusinessEntityCodeUnique(id, code, appId)) {
            throw exception(BUSINESS_ENTITY_CODE_DUPLICATE);
        }
    }

    @Override
    public MetadataBusinessEntityDO getBusinessEntity(Long id) {
        return metadataBusinessEntityRepository.getBusinessEntityById(id);
    }

    @Override
    public MetadataBusinessEntityDO getBusinessEntityByUuid(String entityUuid) {
        return metadataBusinessEntityRepository.getByEntityUuid(entityUuid);
    }

    @Override
    public PageResult<MetadataBusinessEntityDO> getBusinessEntityPage(BusinessEntityPageReqVO pageReqVO) {
        QueryWrapper queryWrapper = QueryWrapper.create();

        // 默认不显示中间表（entity_type = 3）
        queryWrapper.ne(MetadataBusinessEntityDO::getEntityType, BusinessEntityTypeEnum.MIDDLE_TABLE.getCode());

        // 添加查询条件
        if (pageReqVO.getDisplayName() != null) {
            queryWrapper.like(MetadataBusinessEntityDO::getDisplayName, pageReqVO.getDisplayName());
        }
        if (pageReqVO.getCode() != null) {
            queryWrapper.like(MetadataBusinessEntityDO::getCode, pageReqVO.getCode());
        }
        if (pageReqVO.getEntityType() != null) {
            queryWrapper.eq(MetadataBusinessEntityDO::getEntityType, pageReqVO.getEntityType());
        }
        if (pageReqVO.getDatasourceId() != null) {
            queryWrapper.eq(MetadataBusinessEntityDO::getDatasourceUuid, pageReqVO.getDatasourceId());
        }
        if (pageReqVO.getVersionTag() != null) {
            queryWrapper.eq(MetadataBusinessEntityDO::getVersionTag, pageReqVO.getVersionTag());
        }
        if (pageReqVO.getApplicationId() != null) {
            queryWrapper.eq(MetadataBusinessEntityDO::getApplicationId, pageReqVO.getApplicationId());
        }
        if (pageReqVO.getStatus() != null) {
            queryWrapper.eq(MetadataBusinessEntityDO::getStatus, pageReqVO.getStatus());
        }

        // 分页查询
        queryWrapper.orderBy(MetadataBusinessEntityDO::getCreateTime, false);

        return metadataBusinessEntityRepository.getBusinessEntityPage(queryWrapper,
            pageReqVO.getPageNo(), pageReqVO.getPageSize());
    }

    @Override
    public List<MetadataBusinessEntityDO> getBusinessEntityList() {
        return metadataBusinessEntityRepository.getBusinessEntityList();
    }

    @Override
    public List<MetadataBusinessEntityDO> findAllByConfig(QueryWrapper queryWrapper) {
        return metadataBusinessEntityRepository.list(queryWrapper);
    }

    @Override
    public MetadataBusinessEntityDO getBusinessEntityByCode(String code) {
        return metadataBusinessEntityRepository.getBusinessEntityByCode(code);
    }

    @Override
    public List<MetadataBusinessEntityDO> getBusinessEntityListByDatasourceUuid(String datasourceUuid) {
        return metadataBusinessEntityRepository.getBusinessEntityListByDatasourceUuid(datasourceUuid);
    }

    @Override
    public ERDiagramRespVO getERDiagramByDatasourceUuid(String datasourceUuid) {
        // 1. 获取数据源信息
        MetadataDatasourceDO datasource = metadataDatasourceBuildService.getDatasourceByUuid(datasourceUuid);
        if (datasource == null) {
            throw new IllegalArgumentException("数据源不存在，UUID: " + datasourceUuid);
        }

        // 2. 获取该数据源下的所有业务实体
        List<MetadataBusinessEntityDO> entities = getBusinessEntityListByDatasourceUuid(datasourceUuid);

        // 3. 构建ER图响应对象
        ERDiagramRespVO result = new ERDiagramRespVO();
        result.setDatasourceId(datasourceUuid);
        result.setDatasourceName(datasource.getDatasourceName());

        // 4. 转换实体信息，包括字段信息
        List<EREntityVO> erEntities = new ArrayList<>();
        for (MetadataBusinessEntityDO entity : entities) {
            EREntityVO erEntity = convertToEREntity(entity);
            erEntities.add(erEntity);
        }
        result.setEntities(erEntities);

        // 5. 构建关联关系（基于外键关系）
        List<ERRelationshipVO> relationships = buildRelationships(entities);

        result.setRelationships(relationships);

        //设主子关系
        Set<String> sourceIds = relationships.stream()
                .map(ERRelationshipVO::getSourceEntityId)
                .collect(Collectors.toSet());
        Set<String> targetIds = relationships.stream()
                .map(ERRelationshipVO::getTargetEntityId)
                .collect(Collectors.toSet());

        for (EREntityVO entity : erEntities) {
            if (sourceIds.contains(entity.getEntityId())) {
                entity.setRelationType(MetadataEntityRelationRoleEnum.PARENT.getCode());
            }
            if (targetIds.contains(entity.getEntityId())) {
                entity.setRelationType(MetadataEntityRelationRoleEnum.CHILD.getCode());
            }
        }
        return result;
    }

    /**
     * 将业务实体转换为ER实体VO
     *
     * @param entity 业务实体DO
     * @return ER实体VO
     */
    protected EREntityVO convertToEREntity(MetadataBusinessEntityDO entity) {
        return BeanUtils.toBean(entity, EREntityVO.class, erEntity -> {
            erEntity.setEntityId(entity.getId().toString());
            erEntity.setEntityUuid(entity.getEntityUuid());
            erEntity.setEntityName(entity.getDisplayName());
            erEntity.setTableName(entity.getTableName());
            erEntity.setDescription(entity.getDescription());
            erEntity.setEntityType(entity.getEntityType().toString());
            erEntity.setStatus(entity.getStatus());

            // 设置默认坐标（前端可以根据需要调整）
            erEntity.setDisplayConfig(entity.getDisplayConfig() != null ? entity.getDisplayConfig() : "{}");
            erEntity.setCode(entity.getCode());

            // 获取字段信息
            List<ERFieldVO> fields = getEntityFields(entity.getId());
            erEntity.setFields(fields);
        });
    }

    /**
     * 获取实体的字段信息
     *
     * @param entityId 实体ID
     * @return 字段VO列表
     */
    protected List<ERFieldVO> getEntityFields(Long entityId) {
        List<MetadataEntityFieldDO> fieldList = metadataEntityFieldBuildService.getEntityFieldListByEntityId(String.valueOf(entityId));
        List<ERFieldVO> erFields = new ArrayList<>();

        for (MetadataEntityFieldDO field : fieldList) {
            ERFieldVO erField = BeanUtils.toBean(field, ERFieldVO.class, result -> {
                // 手动设置 fieldId，因为数据库实体中是 id，而 VO 中是 fieldId
                result.setFieldId(field.getId());
                // 设置 fieldUuid
                result.setFieldUuid(field.getFieldUuid());
            });
            
            // 填充选项信息（单选、多选字段）
            if (MetadataFieldTypeCodeEnum.isOptionLike(field.getFieldType())) {
                List<MetadataEntityFieldOptionDO> options = fieldOptionService.listByFieldId(field.getFieldUuid());
                if (options != null && !options.isEmpty()) {
                    List<FieldOptionRespVO> optionVOs = options.stream().map(o -> {
                        FieldOptionRespVO item = new FieldOptionRespVO();
                        item.setId(o.getId() != null ? String.valueOf(o.getId()) : null);
                        item.setFieldUuid(o.getFieldUuid());
                        item.setOptionLabel(o.getOptionLabel());
                        item.setOptionValue(o.getOptionValue());
                        item.setOptionOrder(o.getOptionOrder());
                        item.setIsEnabled(o.getIsEnabled());
                        item.setDescription(o.getDescription());
                        return item;
                    }).toList();
                    erField.setOptions(optionVOs);
                }
            }
            
            erFields.add(erField);
        }

        return erFields;
    }

    /**
     * 构建实体间的关联关系
     * 基于实际存储的关系数据
     *
     * @param entities 业务实体列表
     * @return 关联关系列表
     */
    protected List<ERRelationshipVO> buildRelationships(List<MetadataBusinessEntityDO> entities) {
        List<ERRelationshipVO> relationships = new ArrayList<>();

        if (entities.isEmpty()) {
            return relationships;
        }

        // 获取该数据源下的所有实体关系
        List<String> entityUuids = entities.stream()
                .map(MetadataBusinessEntityDO::getEntityUuid)
                .toList();

        // 构建OR条件并用括号包裹，确保生成正确的SQL
        // 正确的SQL: WHERE tenant_id = ? AND (application_id = ? AND version_tag = ?) 
        //           AND (source_entity_uuid IN (...) OR target_entity_uuid IN (...))
        QueryColumn sourceCol = new QueryColumn("source_entity_uuid");
        QueryColumn targetCol = new QueryColumn("target_entity_uuid");
        // 构建OR条件
        QueryCondition orCondition = sourceCol.in(entityUuids).or(targetCol.in(entityUuids));
        // 用空条件包裹OR条件，确保括号正确
        QueryCondition wrappedCondition = QueryCondition.createEmpty().and(orCondition);
        
        QueryWrapper relationshipQueryWrapper = metadataEntityRelationshipRepository.query()
                .and(wrappedCondition)
                .orderBy(MetadataEntityRelationshipDO::getCreateTime, false);

        List<MetadataEntityRelationshipDO> relationshipDOs = metadataEntityRelationshipRepository.list(
                relationshipQueryWrapper);

        // 批量收集所有需要查询的字段UUID（性能优化：避免循环单条查询）
        Set<String> fieldUuids = new HashSet<>();
        for (MetadataEntityRelationshipDO rel : relationshipDOs) {
            if (rel.getSourceFieldUuid() != null && !rel.getSourceFieldUuid().isEmpty()) {
                fieldUuids.add(rel.getSourceFieldUuid());
            }
            if (rel.getTargetFieldUuid() != null && !rel.getTargetFieldUuid().isEmpty()) {
                fieldUuids.add(rel.getTargetFieldUuid());
            }
        }

        // 批量查询字段信息，构建缓存Map
        Map<String, MetadataEntityFieldDO> fieldCacheMap = new HashMap<>();
        if (!fieldUuids.isEmpty()) {
            List<MetadataEntityFieldDO> fields = metadataEntityFieldBuildService.getByFieldUuids(fieldUuids);
            for (MetadataEntityFieldDO field : fields) {
                fieldCacheMap.put(field.getFieldUuid(), field);
            }
        }

        // 转换为ER关系VO（使用缓存避免重复查询）
        for (MetadataEntityRelationshipDO relationshipDO : relationshipDOs) {
            ERRelationshipVO relationship = convertToERRelationship(relationshipDO, entities, fieldCacheMap);
            if (relationship != null) {
                relationships.add(relationship);
            }
        }

        return relationships;
    }

    /**
     * 将实体关系DO转换为ER关系VO
     *
     * @param relationshipDO 实体关系DO
     * @param entities 实体列表，用于获取实体名称
     * @param fieldCacheMap 字段缓存Map（UUID -> Field），用于快速查找字段信息
     * @return ER关系VO，如果源或目标实体不存在则返回null
     */
    protected ERRelationshipVO convertToERRelationship(MetadataEntityRelationshipDO relationshipDO,
                                                    List<MetadataBusinessEntityDO> entities,
                                                    Map<String, MetadataEntityFieldDO> fieldCacheMap) {
        // 查找源实体和目标实体（使用UUID匹配）
        MetadataBusinessEntityDO sourceEntity = entities.stream()
                .filter(entity -> entity.getEntityUuid().equals(relationshipDO.getSourceEntityUuid()))
                .findFirst()
                .orElse(null);

        MetadataBusinessEntityDO targetEntity = entities.stream()
                .filter(entity -> entity.getEntityUuid().equals(relationshipDO.getTargetEntityUuid()))
                .findFirst()
                .orElse(null);

        if (sourceEntity == null || targetEntity == null) {
            return null;
        }

        // 从缓存中获取字段信息（避免重复查库）
        MetadataEntityFieldDO sourceField = fieldCacheMap.get(relationshipDO.getSourceFieldUuid());
        MetadataEntityFieldDO targetField = fieldCacheMap.get(relationshipDO.getTargetFieldUuid());

        // 如果字段不存在，记录警告但不抛异常（修复Bug：字段不存在时接口不应报错）
        if (sourceField == null && relationshipDO.getSourceFieldUuid() != null && !relationshipDO.getSourceFieldUuid().isEmpty()) {
            log.warn("ER图构建：源字段不存在，fieldUuid={}", relationshipDO.getSourceFieldUuid());
        }
        if (targetField == null && relationshipDO.getTargetFieldUuid() != null && !relationshipDO.getTargetFieldUuid().isEmpty()) {
            log.warn("ER图构建：目标字段不存在，fieldUuid={}", relationshipDO.getTargetFieldUuid());
        }

        ERRelationshipVO relationship = BeanUtils.toBean(relationshipDO, ERRelationshipVO.class, rel -> {
            rel.setRelationshipId(relationshipDO.getId().toString());
            // 源实体：同时设置ID和UUID
            rel.setSourceEntityId(sourceEntity.getId().toString());
            rel.setSourceEntityUuid(relationshipDO.getSourceEntityUuid());
            rel.setSourceEntityName(sourceEntity.getDisplayName());
            // 源字段：从缓存获取，避免抛异常
            rel.setSourceFieldUuid(relationshipDO.getSourceFieldUuid());
            rel.setSourceFieldId(sourceField != null ? sourceField.getId().toString() : null);
            rel.setSourceFieldName(sourceField != null ? sourceField.getFieldName() : null);
            // 目标实体：同时设置ID和UUID
            rel.setTargetEntityId(targetEntity.getId().toString());
            rel.setTargetEntityUuid(relationshipDO.getTargetEntityUuid());
            rel.setTargetEntityName(targetEntity.getDisplayName());
            // 目标字段：从缓存获取，避免抛异常
            rel.setTargetFieldUuid(relationshipDO.getTargetFieldUuid());
            rel.setTargetFieldId(targetField != null ? targetField.getId().toString() : null);
            rel.setTargetFieldName(targetField != null ? targetField.getFieldName() : null);
        });

        return relationship;
    }

    /**
     * 根据字段UUID获取字段名称
     *
     * @param fieldUuid 字段UUID
     * @return 字段名称
     */
    protected String getFieldNameByUuid(String fieldUuid) {
        if (fieldUuid == null || fieldUuid.isEmpty()) {
            return null;
        }

        try {
            MetadataEntityFieldDO field = metadataEntityFieldBuildService.getEntityFieldByUuid(fieldUuid);
            return field != null ? field.getFieldName() : null;
        } catch (Exception e) {
            log.warn("获取字段名称失败，字段UUID: {}, 错误: {}", fieldUuid, e.getMessage());
            return null;
        }
    }



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
                entity.setRelationType(MetadataEntityRelationRoleEnum.MASTER.getCode());
            } else if (isTarget) {
                // 只要在target出现 -> 子表
                entity.setRelationType(MetadataEntityRelationRoleEnum.SLAVE.getCode());
            } else {
                // 都没有 -> 无关系
                entity.setRelationType(MetadataEntityRelationRoleEnum.NONE.getCode());
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
    protected SimpleEntityRespVO convertToSimpleEntity(MetadataBusinessEntityDO entityDO) {
        return BeanUtils.toBean(entityDO, SimpleEntityRespVO.class, simpleEntity -> {
            simpleEntity.setEntityId(entityDO.getId());
            simpleEntity.setEntityUuid(entityDO.getEntityUuid());
            simpleEntity.setEntityName(entityDO.getDisplayName());
            // 设置实际表名
            simpleEntity.setTableName(entityDO.getTableName());
        });
    }

    @Override
    public BusinessEntityRespVO createBusinessEntityWithResponse(@Valid BusinessEntitySaveReqVO reqVO) {
        Long id = createBusinessEntity(reqVO);
        MetadataBusinessEntityDO businessEntity = getBusinessEntity(id);
        return modelMapper.map(businessEntity, BusinessEntityRespVO.class);
    }

    @Override
    public BusinessEntityRespVO getBusinessEntityDetail(Long id) {
        MetadataBusinessEntityDO businessEntity = getBusinessEntity(id);
        return modelMapper.map(businessEntity, BusinessEntityRespVO.class);
    }

    @Override
    public PageResult<BusinessEntityRespVO> getBusinessEntityPageWithResponse(BusinessEntityPageReqVO pageReqVO) {
        PageResult<MetadataBusinessEntityDO> pageResult = getBusinessEntityPage(pageReqVO);
        PageResult<BusinessEntityRespVO> convertedResult = new PageResult<>();
        convertedResult.setTotal(pageResult.getTotal());
        convertedResult.setList(pageResult.getList().stream()
                .map(entity -> modelMapper.map(entity, BusinessEntityRespVO.class))
                .toList());
        return convertedResult;
    }

    @Override
    public List<BusinessEntityRespVO> getBusinessEntityListByDatasourceUuidWithRelationType(String datasourceUuid) {
        // 1. 获取业务实体列表
        List<MetadataBusinessEntityDO> list = getBusinessEntityListByDatasourceUuid(datasourceUuid);

        // 2. 转换为 VO
        List<BusinessEntityRespVO> result = list.stream()
                .map(entity -> modelMapper.map(entity, BusinessEntityRespVO.class))
                .toList();

        // 3. 填充 relationType 字段
        // relationType 用于标识实体在关系中的角色：PARENT(主表/父表) 或 CHILD(子表)
        if (!result.isEmpty()) {
            // 复用 ER 图服务获取关系信息，保持逻辑一致性
            ERDiagramRespVO erDiagram = getERDiagramByDatasourceUuid(datasourceUuid);
            List<ERRelationshipVO> relationships = erDiagram.getRelationships();

            // 收集所有作为源实体(主表)和目标实体(子表)的ID
            Set<String> sourceIds = relationships.stream()
                    .map(ERRelationshipVO::getSourceEntityId)
                    .collect(Collectors.toSet());
            Set<String> targetIds = relationships.stream()
                    .map(ERRelationshipVO::getTargetEntityId)
                    .collect(Collectors.toSet());

            // 为每个实体设置关系类型
            for (BusinessEntityRespVO entity : result) {
                if (sourceIds.contains(entity.getId())) {
                    entity.setRelationType(MetadataEntityRelationRoleEnum.PARENT.getCode());  // 主表：其他表引用此表
                }
                if (targetIds.contains(entity.getId())) {
                    entity.setRelationType(MetadataEntityRelationRoleEnum.CHILD.getCode());   // 子表：引用其他表的外键
                }
                // 注意：一个实体可能既是某些关系的主表，又是其他关系的子表
                // 在这种情况下，最后设置的值会覆盖前面的值
                // 如果既不是源实体也不是目标实体，relationType 保持 null
            }
        }

        return result;
    }

    /**
     * 重新创建业务实体的物理表
     * 当发现表不存在时，可以调用此方法来重新创建表
     *
     * @param entityId 业务实体ID
     */
    @Transactional(rollbackFor = Exception.class)
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
    protected EntityWithFieldsRespVO convertToEntityWithFieldsRespVO(MetadataBusinessEntityDO entity) {
        EntityWithFieldsRespVO respVO = new EntityWithFieldsRespVO();
        respVO.setEntityId(entity.getId());
        respVO.setEntityUuid(entity.getEntityUuid());
        respVO.setEntityName(entity.getDisplayName());
        respVO.setEntityCode(entity.getCode());
        respVO.setTableName(entity.getTableName());

        // 1. 查询实体的完整字段信息
        EntityFieldQueryReqVO fieldQueryReqVO = new EntityFieldQueryReqVO();
        fieldQueryReqVO.setEntityUuid(entity.getEntityUuid());
        List<EntityFieldRespVO> fields = metadataEntityFieldBuildService.getEntityFieldListWithRelated(fieldQueryReqVO);
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
    protected ChildEntityWithFieldsRespVO convertToChildEntityWithFieldsRespVO(MetadataEntityRelationshipDO relationship) {
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
        EntityFieldQueryReqVO childFieldQueryReqVO = new EntityFieldQueryReqVO();
        childFieldQueryReqVO.setEntityUuid(childEntity.getEntityUuid());
        List<EntityFieldRespVO> childFields = metadataEntityFieldBuildService.getEntityFieldListWithRelated(childFieldQueryReqVO);
        childVO.setChildFields(childFields);

        return childVO;
    }

}
