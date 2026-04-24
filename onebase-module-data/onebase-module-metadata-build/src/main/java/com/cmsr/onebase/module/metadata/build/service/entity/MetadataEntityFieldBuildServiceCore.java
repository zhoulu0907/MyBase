package com.cmsr.onebase.module.metadata.build.service.entity;


import com.cmsr.onebase.framework.aynline.AnylineDdlHelper;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.common.util.string.UuidUtils;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.*;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.EntityRelationshipSaveReqVO;
import com.cmsr.onebase.module.metadata.build.service.component.MetadataComponentFieldTypeBuildService;
import com.cmsr.onebase.module.metadata.build.service.datasource.MetadataDatasourceBuildService;
import com.cmsr.onebase.module.metadata.build.service.entity.vo.EntityFieldQueryVO;
import com.cmsr.onebase.module.metadata.build.service.field.MetadataEntityFieldConstraintBuildService;
import com.cmsr.onebase.module.metadata.build.service.field.MetadataEntityFieldOptionBuildService;
import com.cmsr.onebase.module.metadata.build.service.number.AutoNumberConfigBuildService;
import com.cmsr.onebase.module.metadata.build.service.number.AutoNumberRuleBuildService;
import com.cmsr.onebase.module.metadata.build.service.relationship.MetadataEntityRelationshipBuildService;
import com.cmsr.onebase.module.metadata.build.service.validation.*;
import com.cmsr.onebase.module.metadata.core.dal.database.*;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataComponentFieldTypeDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataSystemFieldsDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.field.MetadataEntityFieldOptionDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberConfigDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberResetLogDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberRuleItemDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberStateDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataPermitRefOtftDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationTypeDO;
import com.cmsr.onebase.module.metadata.core.enums.*;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.service.number.AutoNumberRuleEngine;
import com.cmsr.onebase.module.metadata.core.util.MetadataIdUuidConverter;
import com.cmsr.onebase.module.metadata.core.util.StatusEnumUtil;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.metadata.Column;
import org.anyline.metadata.type.DatabaseType;
import org.anyline.service.AnylineService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.*;
/**
 * Split segment of metadata build service implementation.
 */
@Slf4j
public abstract class MetadataEntityFieldBuildServiceCore extends MetadataEntityFieldBuildServiceBatchSaveSupport {
    public List<FieldTypeConfigRespVO> getFieldTypes() {
        // 从MetadataComponentFieldTypeDO中读取字段类型配置，替代原来的枚举方式
        return componentFieldTypeService.getFieldTypeConfigs();
    }

    @Override
    public List<EntityFieldValidationTypesRespVO> getFieldValidationTypes(
            @Valid EntityFieldValidationTypesReqVO reqVO) {
        List<Long> fieldIds = resolveValidationFieldIds(reqVO);
        if (fieldIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<MetadataEntityFieldDO> fields = metadataEntityFieldRepository.list(
                QueryWrapper.create().in(MetadataEntityFieldDO::getId, fieldIds));
        Map<Long, String> fieldIdToType = buildFieldIdToType(fields);
        Map<Long, String> fieldIdToUuid = buildFieldIdToUuid(fields);
        if (fieldIdToType.isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> typeCodes = fieldIdToType.values().stream()
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.toSet());
        Map<String, List<ValidationTypeItemRespVO>> typeToValidation = typeCodes.isEmpty()
                ? new HashMap<>()
                : loadValidationItemsByFieldType(typeCodes);
        return buildFieldValidationTypeResponses(fieldIdToType, fieldIdToUuid, typeToValidation);
    }

    protected List<Long> resolveValidationFieldIds(EntityFieldValidationTypesReqVO reqVO) {
        if (reqVO.getTableName() != null && !reqVO.getTableName().trim().isEmpty()) {
            return resolveValidationFieldIdsByTableName(reqVO.getTableName().trim());
        }
        return resolveValidationFieldIdsByRawIds(reqVO.getFieldIdList());
    }

    protected List<Long> resolveValidationFieldIdsByTableName(String tableName) {
        MetadataBusinessEntityDO entity = metadataBusinessEntityRepository.getOne(
                metadataBusinessEntityRepository.query().eq(MetadataBusinessEntityDO::getTableName, tableName));
        if (entity == null) {
            log.warn("根据tableName查询实体失败，实体不存在: tableName={}", tableName);
            return new ArrayList<>();
        }

        return metadataEntityFieldRepository.list(metadataEntityFieldRepository.query()
                        .eq(MetadataEntityFieldDO::getEntityUuid, entity.getEntityUuid()))
                .stream()
                .map(MetadataEntityFieldDO::getId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    protected List<Long> resolveValidationFieldIdsByRawIds(List<String> rawFieldIds) {
        if (rawFieldIds == null || rawFieldIds.isEmpty()) {
            return new ArrayList<>();
        }
        return rawFieldIds.stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .map(String::trim)
                .map(this::resolveValidationFieldId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    protected Long resolveValidationFieldId(String identifier) {
        try {
            return idUuidConverter.resolveFieldId(identifier);
        } catch (Exception ex) {
            log.warn("解析字段标识符失败，已跳过: {}", identifier, ex);
            return null;
        }
    }

    protected Map<Long, String> buildFieldIdToType(List<MetadataEntityFieldDO> fields) {
        return fields.stream()
                .filter(f -> f.getId() != null)
                .collect(Collectors.toMap(MetadataEntityFieldDO::getId, MetadataEntityFieldDO::getFieldType));
    }

    protected Map<Long, String> buildFieldIdToUuid(List<MetadataEntityFieldDO> fields) {
        return fields.stream()
                .filter(f -> f.getId() != null && f.getFieldUuid() != null)
                .collect(Collectors.toMap(MetadataEntityFieldDO::getId, MetadataEntityFieldDO::getFieldUuid));
    }

    protected Map<String, List<ValidationTypeItemRespVO>> loadValidationItemsByFieldType(Set<String> typeCodes) {
        QueryWrapper typeQueryWrapper = componentFieldTypeRepository.query()
                .where(MetadataComponentFieldTypeDO::getFieldTypeCode).in(typeCodes);
        List<MetadataComponentFieldTypeDO> fieldTypes = componentFieldTypeRepository.list(typeQueryWrapper);
        Map<Long, String> typeIdToCode = fieldTypes.stream()
                .filter(ft -> ft.getId() != null && ft.getFieldTypeCode() != null && !ft.getFieldTypeCode().isBlank())
                .collect(Collectors.toMap(
                        MetadataComponentFieldTypeDO::getId,
                        MetadataComponentFieldTypeDO::getFieldTypeCode
                ));

        Map<String, List<ValidationTypeItemRespVO>> typeToValidation = new HashMap<>();
        if (typeIdToCode.isEmpty()) {
            return typeToValidation;
        }

        List<MetadataPermitRefOtftDO> relations = permitRefOtftService.listByFieldTypeIds(typeIdToCode.keySet());
        Map<Long, MetadataValidationTypeDO> validationTypes = validationTypeService.getByIds(
                relations.stream()
                        .map(MetadataPermitRefOtftDO::getValidationTypeId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet()));

        for (MetadataPermitRefOtftDO rel : relations) {
            appendValidationItem(typeToValidation, typeIdToCode, validationTypes, rel);
        }
        sortValidationItems(typeToValidation);
        return typeToValidation;
    }

    protected void appendValidationItem(Map<String, List<ValidationTypeItemRespVO>> typeToValidation,
            Map<Long, String> typeIdToCode,
            Map<Long, MetadataValidationTypeDO> validationTypes,
            MetadataPermitRefOtftDO rel) {
        String fieldTypeCode = rel.getFieldTypeId() != null ? typeIdToCode.get(rel.getFieldTypeId()) : null;
        MetadataValidationTypeDO validationType = rel.getValidationTypeId() != null
                ? validationTypes.get(rel.getValidationTypeId())
                : null;
        if (fieldTypeCode == null || validationType == null || validationType.getValidationCode() == null) {
            return;
        }

        ValidationTypeItemRespVO item = new ValidationTypeItemRespVO();
        item.setCode(validationType.getValidationCode());
        item.setName(validationType.getValidationName());
        item.setDescription(validationType.getValidationDesc());
        item.setSortOrder(rel.getSortOrder());
        typeToValidation.computeIfAbsent(fieldTypeCode, k -> new ArrayList<>()).add(item);
    }

    protected void sortValidationItems(Map<String, List<ValidationTypeItemRespVO>> typeToValidation) {
        for (List<ValidationTypeItemRespVO> list : typeToValidation.values()) {
            list.sort((a, b) -> Integer.compare(
                    a.getSortOrder() != null ? a.getSortOrder() : 0,
                    b.getSortOrder() != null ? b.getSortOrder() : 0));
        }
    }

    protected List<EntityFieldValidationTypesRespVO> buildFieldValidationTypeResponses(
            Map<Long, String> fieldIdToType,
            Map<Long, String> fieldIdToUuid,
            Map<String, List<ValidationTypeItemRespVO>> typeToValidation) {
        List<EntityFieldValidationTypesRespVO> result = new ArrayList<>();
        for (Map.Entry<Long, String> e : fieldIdToType.entrySet()) {
            EntityFieldValidationTypesRespVO vo = new EntityFieldValidationTypesRespVO();
            Long fieldId = e.getKey();
            vo.setFieldId(fieldId);
            vo.setFieldUuid(fieldIdToUuid.get(fieldId));
            vo.setFieldTypeCode(e.getValue());
            vo.setValidationTypes(typeToValidation.getOrDefault(e.getValue(), new ArrayList<>()));
            result.add(vo);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EntityFieldBatchCreateRespVO batchCreateEntityFields(@Valid EntityFieldBatchCreateReqVO reqVO) {
        // ID转UUID兼容处理：支持前端传入entityId(实际为reqVO.getEntityId())或entityUuid
        String resolvedEntityUuid = idUuidConverter.resolveEntityUuid(reqVO.getEntityId(), null);
        reqVO.setEntityId(resolvedEntityUuid);

        EntityFieldBatchCreateRespVO result = new EntityFieldBatchCreateRespVO();
        List<String> fieldIds = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        // 获取业务实体和数据源信息（用于批量创建物理表字段）
        MetadataBusinessEntityDO businessEntity = null;
        MetadataDatasourceDO datasource = null;
        try {
            businessEntity = metadataBusinessEntityCoreService.getBusinessEntityByUuid(reqVO.getEntityId());
            log.info("获取到业务实体: {}, 表名: {}, 数据源UUID: {}",
                    businessEntity != null ? businessEntity.getId() : "null",
                    businessEntity != null ? businessEntity.getTableName() : "null",
                    businessEntity != null ? businessEntity.getDatasourceUuid() : "null");

            if (businessEntity != null && businessEntity.getTableName() != null &&
                    !businessEntity.getTableName().trim().isEmpty()) {
                datasource = metadataDatasourceBuildService.getDatasourceByUuid(businessEntity.getDatasourceUuid());
                log.info("获取到数据源: {}, 数据源名称: {}, 数据源类型: {}",
                        datasource != null ? datasource.getId() : "null",
                        datasource != null ? datasource.getDatasourceName() : "null",
                        datasource != null ? datasource.getDatasourceType() : "null");
            }
        } catch (Exception e) {
            log.error("获取业务实体信息失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取业务实体信息失败: " + e.getMessage(), e);
        }

        for (EntityFieldCreateItemVO fieldItem : reqVO.getFields()) {
            // 直接执行创建逻辑，异常由全局统一处理
            validateFieldNameNotDatabaseKeyword(fieldItem.getFieldName());
            validateEntityFieldNameUnique(null, reqVO.getEntityId(), fieldItem.getFieldName());
            validateEntityFieldDisplayNameUnique(null, reqVO.getEntityId(), fieldItem.getDisplayName());
            // 创建字段及数据库插入操作
            MetadataEntityFieldDO entityField = new MetadataEntityFieldDO();
            entityField.setEntityUuid(reqVO.getEntityId());
            entityField.setFieldName(fieldItem.getFieldName());
            entityField.setDisplayName(fieldItem.getDisplayName());
            entityField.setFieldType(fieldItem.getFieldType());
            entityField.setDescription(fieldItem.getDescription());
            // 使用新的枚举值：1-是，0-否
            entityField
                    .setIsRequired(fieldItem.getIsRequired() != null ? fieldItem.getIsRequired() : StatusEnumUtil.NO); // 默认0-不是必填
            entityField.setIsUnique(fieldItem.getIsUnique() != null ? fieldItem.getIsUnique() : StatusEnumUtil.NO); // 默认0-不是唯一
            entityField
                    .setIsRequired(fieldItem.getIsRequired() != null ? fieldItem.getIsRequired() : StatusEnumUtil.NO); // 默认0-非必填
            entityField.setDefaultValue(fieldItem.getDefaultValue());
            entityField.setSortOrder(fieldItem.getSortOrder() != null ? fieldItem.getSortOrder() : 0);
            entityField.setIsSystemField(StatusEnumUtil.NO); // 0-不是系统字段
            entityField.setIsPrimaryKey(StatusEnumUtil.NO); // 0-不是主键
            entityField.setApplicationId(reqVO.getApplicationId() != null ? Long.parseLong(reqVO.getApplicationId()) : null);
            // 设置默认运行模式，防止后续约束/自动编号处理中出现空指针
            entityField.setVersionTag(0L);
            // 生成字段UUID（如果为空）
            if (entityField.getFieldUuid() == null || entityField.getFieldUuid().isEmpty()) {
                entityField.setFieldUuid(UuidUtils.getUuid());
            }

            metadataEntityFieldRepository.save(entityField);
            fieldIds.add(entityField.getId().toString());
            successCount++;

            // 同步到物理表 - 失败时直接抛出异常回滚事务
            if (businessEntity != null && datasource != null) {
                try {
                    addColumnToTable(datasource, businessEntity.getTableName(), entityField);
                } catch (Exception e) {
                    log.error("批量创建字段时同步到物理表失败，字段: {} - {}", entityField.getFieldName(), e.getMessage(), e);
                    throw new RuntimeException("添加列失败: " + e.getMessage(), e);
                }
            }
        }

        result.setSuccessCount(successCount);
        result.setFailureCount(failureCount);
        result.setFieldIds(fieldIds);
        return result;
    }

    @Override
    public List<MetadataEntityFieldDO> getEntityFieldListByConditions(EntityFieldQueryVO queryVO) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        String entityUuid = resolveEntityUuidForFieldQuery(queryVO);
        if (entityUuid == null) {
            log.warn("getEntityFieldListByConditions: 未指定实体标识条件（entityUuid/entityId/tableName），返回空列表");
            return java.util.Collections.emptyList();
        }

        queryWrapper.eq(MetadataEntityFieldDO::getEntityUuid, entityUuid);
        applyEntityFieldFilters(queryWrapper, queryVO);
        applyDefaultFieldOrdering(queryWrapper);

        if (Integer.valueOf(1).equals(queryVO.getIsPerson())) {
            return queryPersonFieldsWithSystemFields(entityUuid, queryVO);
        }
        return metadataEntityFieldRepository.list(queryWrapper);
    }

    protected String resolveEntityUuidForFieldQuery(EntityFieldQueryVO queryVO) {
        if (queryVO.getEntityUuid() != null && !queryVO.getEntityUuid().trim().isEmpty()) {
            return queryVO.getEntityUuid().trim();
        }
        if (queryVO.getEntityId() != null && !queryVO.getEntityId().trim().isEmpty()) {
            return idUuidConverter.toEntityUuid(queryVO.getEntityId().trim());
        }
        if (queryVO.getTableName() == null || queryVO.getTableName().trim().isEmpty()) {
            return null;
        }

        MetadataBusinessEntityDO entity = metadataBusinessEntityRepository.getOne(
                metadataBusinessEntityRepository.query()
                        .eq(MetadataBusinessEntityDO::getTableName, queryVO.getTableName().trim()));
        return entity != null ? entity.getEntityUuid() : null;
    }

    protected void applyEntityFieldFilters(QueryWrapper queryWrapper, EntityFieldQueryVO queryVO) {
        if (queryVO.getFieldName() != null && !queryVO.getFieldName().trim().isEmpty()) {
            queryWrapper.eq(MetadataEntityFieldDO::getFieldName, queryVO.getFieldName().trim());
        }
        applyKeywordFilter(queryWrapper, queryVO.getKeyword());
        if (queryVO.getIsSystemField() != null) {
            queryWrapper.eq(MetadataEntityFieldDO::getIsSystemField, queryVO.getIsSystemField());
        }
        if (queryVO.getFieldCode() != null && !queryVO.getFieldCode().trim().isEmpty()) {
            queryWrapper.like(MetadataEntityFieldDO::getFieldCode, queryVO.getFieldCode());
        }
    }

    protected void applyKeywordFilter(QueryWrapper queryWrapper, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }
        QueryColumn fieldNameCol = new QueryColumn("field_name");
        QueryColumn displayNameCol = new QueryColumn("display_name");
        queryWrapper.and(QueryCondition.createEmpty().and(fieldNameCol.like(keyword).or(displayNameCol.like(keyword))));
    }

    protected void applyDefaultFieldOrdering(QueryWrapper queryWrapper) {
        queryWrapper.orderBy(MetadataEntityFieldDO::getSortOrder, true);
        queryWrapper.orderBy(MetadataEntityFieldDO::getCreateTime, false);
    }

    protected List<MetadataEntityFieldDO> queryPersonFieldsWithSystemFields(String entityUuid, EntityFieldQueryVO queryVO) {
        QueryWrapper personWrapper = QueryWrapper.create()
                .eq(MetadataEntityFieldDO::getEntityUuid, entityUuid)
                .eq(MetadataEntityFieldDO::getFieldType, MetadataFieldTypeCodeEnum.USER.getCode());
        applyKeywordFilter(personWrapper, queryVO.getKeyword());
        if (queryVO.getFieldCode() != null && !queryVO.getFieldCode().trim().isEmpty()) {
            personWrapper.like(MetadataEntityFieldDO::getFieldCode, queryVO.getFieldCode());
        }
        applyDefaultFieldOrdering(personWrapper);

        List<MetadataEntityFieldDO> fields = metadataEntityFieldRepository.list(personWrapper);
        appendSystemUserFields(entityUuid, fields);
        sortFieldsForResponse(fields);
        return fields;
    }

    protected void appendSystemUserFields(String entityUuid, List<MetadataEntityFieldDO> fields) {
        LinkedHashMap<String, MetadataEntityFieldDO> map = new LinkedHashMap<>();
        for (MetadataEntityFieldDO field : fields) {
            map.putIfAbsent(fieldIdentityKey(field), field);
        }
        appendSystemUserField(entityUuid, "creator", map);
        appendSystemUserField(entityUuid, "updater", map);
        fields.clear();
        fields.addAll(map.values());
    }

    protected void appendSystemUserField(String entityUuid, String fieldName, Map<String, MetadataEntityFieldDO> fields) {
        MetadataEntityFieldDO field = metadataEntityFieldRepository.getEntityFieldByName(entityUuid, fieldName);
        if (field != null) {
            fields.putIfAbsent(fieldIdentityKey(field), field);
        }
    }

    protected String fieldIdentityKey(MetadataEntityFieldDO field) {
        return field.getId() != null ? String.valueOf(field.getId()) : field.getFieldName();
    }

    protected void sortFieldsForResponse(List<MetadataEntityFieldDO> fields) {
        fields.sort((a, b) -> {
            int sortCompare = Integer.compare(
                    a.getSortOrder() != null ? a.getSortOrder() : 0,
                    b.getSortOrder() != null ? b.getSortOrder() : 0);
            if (sortCompare != 0) {
                return sortCompare;
            }
            if (a.getCreateTime() == null && b.getCreateTime() == null) {
                return 0;
            }
            if (a.getCreateTime() == null) {
                return 1;
            }
            if (b.getCreateTime() == null) {
                return -1;
            }
            return b.getCreateTime().compareTo(a.getCreateTime());
        });
    }

    @Override
    public EntityFieldDetailRespVO getEntityFieldDetail(String id) {
        Long longId = Long.valueOf(id);
        MetadataEntityFieldDO entityField = metadataEntityFieldRepository.getById(longId);
        if (entityField == null) {
            throw exception(ENTITY_FIELD_NOT_EXISTS);
        }

        EntityFieldDetailRespVO result = BeanUtils.toBean(entityField, EntityFieldDetailRespVO.class);
        // 设置fieldUuid和entityUuid
        result.setFieldUuid(entityField.getFieldUuid());
        result.setEntityUuid(entityField.getEntityUuid());

        // 获取实体名称（这里简化处理，实际项目中可能需要关联查询）
        result.setEntityName("实体名称");

        // 获取校验规则（旧）
        result.setValidationRules(new ArrayList<>());

        // 选项与约束补充
        // 仅当字段类型为单/多选时返回选项
        if ("SINGLE_SELECT".equalsIgnoreCase(entityField.getFieldType())
                || "MULTI_SELECT".equalsIgnoreCase(entityField.getFieldType())
                || "PICKLIST".equalsIgnoreCase(entityField.getFieldType())) {
            result.setOptions(fieldOptionService.listByFieldId(entityField.getFieldUuid()).stream().map(o -> {
                FieldOptionRespVO v = new FieldOptionRespVO();
                v.setId(String.valueOf(o.getId()));
                v.setFieldUuid(o.getFieldUuid());
                v.setOptionUuid(o.getOptionUuid());
                v.setOptionLabel(o.getOptionLabel());
                v.setOptionValue(o.getOptionValue());
                v.setOptionOrder(o.getOptionOrder());
                v.setIsEnabled(o.getIsEnabled());
                v.setDescription(o.getDescription());
                return v;
            }).toList());
        }
        FieldConstraintRespVO cr = fieldConstraintService.getFieldConstraintConfig(entityField.getFieldUuid());
        if (cr != null) {
            result.setConstraints(cr);
        }

        // 自动编号（详情页返回在 getEntityFieldDetailWithFullConfig 中补充完整配置）

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EntityFieldBatchUpdateRespVO batchUpdateEntityFields(@Valid EntityFieldBatchUpdateReqVO reqVO) {
        EntityFieldBatchUpdateRespVO result = new EntityFieldBatchUpdateRespVO();
        int successCount = 0;
        int failureCount = 0;

        // 获取业务实体和数据源信息（用于批量更新物理表字段）
        MetadataBusinessEntityDO businessEntity = null;
        MetadataDatasourceDO datasource = null;
        try {
            // 获取第一个字段的entityId来确定业务实体
            if (!reqVO.getFields().isEmpty()) {
                String firstFieldId = reqVO.getFields().get(0).getId();
                if (firstFieldId != null && !firstFieldId.trim().isEmpty()) {
                    MetadataEntityFieldDO firstField = metadataEntityFieldRepository.getById(Long.valueOf(firstFieldId.trim()));
                    if (firstField != null) {
                        businessEntity = metadataBusinessEntityCoreService.getBusinessEntityByUuid(firstField.getEntityUuid());
                        if (businessEntity != null && businessEntity.getTableName() != null &&
                                !businessEntity.getTableName().trim().isEmpty()) {
                            datasource = metadataDatasourceBuildService.getDatasourceByUuid(businessEntity.getDatasourceUuid());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取业务实体信息失败: {}", e.getMessage(), e);
        }

        for (EntityFieldUpdateItemVO fieldItem : reqVO.getFields()) {
            // 校验字段存在
            validateEntityFieldExists(fieldItem.getId());
            MetadataEntityFieldDO existingField = metadataEntityFieldRepository
                    .getById(Long.valueOf(fieldItem.getId()));
            if (existingField == null) {
                failureCount++;
                continue;
            }
            if (fieldItem.getDisplayName() != null && !fieldItem.getDisplayName().trim().isEmpty()) {
                validateEntityFieldDisplayNameUnique(fieldItem.getId(), existingField.getEntityUuid(),
                        fieldItem.getDisplayName());
            }
            // 更新字段
            MetadataEntityFieldDO updateObj = new MetadataEntityFieldDO();
            updateObj.setId(Long.valueOf(fieldItem.getId()));
            if (fieldItem.getDisplayName() != null && !fieldItem.getDisplayName().trim().isEmpty()) {
                updateObj.setDisplayName(fieldItem.getDisplayName());
            }
            if (fieldItem.getDescription() != null && !fieldItem.getDescription().trim().isEmpty()) {
                updateObj.setDescription(fieldItem.getDescription());
            }
            if (fieldItem.getIsRequired() != null) {
                updateObj.setIsRequired(fieldItem.getIsRequired());
            }

            metadataEntityFieldRepository.updateById(updateObj);
            successCount++;

            // 同步到物理表
            if (businessEntity != null && datasource != null) {
                try {
                    // 需要获取完整的字段信息进行更新
                    MetadataEntityFieldDO fullFieldInfo = metadataEntityFieldRepository.getById(Long.valueOf(fieldItem.getId()));
                    if (fullFieldInfo != null) {
                        alterColumnInTable(datasource, businessEntity.getTableName(), fullFieldInfo);
                    }
                } catch (Exception e) {
                    log.error("批量更新字段时同步到物理表失败，字段ID: {} - {}", fieldItem.getId(), e.getMessage(), e);
                    failureCount++;
                    // 不抛出异常，继续更新其他字段
                }
            }
        }

        result.setSuccessCount(successCount);
        result.setFailureCount(failureCount);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSortEntityFields(@Valid EntityFieldBatchSortReqVO reqVO) {
        for (EntityFieldSortItemVO sortItem : reqVO.getFieldSorts()) {
            // 校验字段存在
            validateEntityFieldExists(sortItem.getFieldUuid());

            // 更新排序 - 使用fieldUuid查找
            MetadataEntityFieldDO field = metadataEntityFieldRepository.getByFieldUuid(sortItem.getFieldUuid());
            if (field != null) {
                MetadataEntityFieldDO updateObj = new MetadataEntityFieldDO();
                updateObj.setId(field.getId());
                updateObj.setSortOrder(sortItem.getSortOrder());
                metadataEntityFieldRepository.updateById(updateObj);
            }
        }
    }

}
