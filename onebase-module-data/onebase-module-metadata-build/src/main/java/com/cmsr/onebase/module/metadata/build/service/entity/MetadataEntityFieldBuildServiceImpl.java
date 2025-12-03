package com.cmsr.onebase.module.metadata.build.service.entity;

import com.cmsr.onebase.framework.aynline.AnylineDdlHelper;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.flow.context.enums.FieldTypeEnum;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.*;
import com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo.EntityRelationshipSaveReqVO;
import com.cmsr.onebase.module.metadata.build.service.entity.vo.EntityFieldQueryVO;
import com.cmsr.onebase.module.metadata.build.service.number.AutoNumberRuleBuildService;
import com.cmsr.onebase.module.metadata.build.service.component.MetadataComponentFieldTypeBuildService;
import com.cmsr.onebase.module.metadata.build.service.relationship.MetadataEntityRelationshipBuildService;
import com.cmsr.onebase.module.metadata.build.service.validation.MetadataPermitRefOtftBuildService;
import com.cmsr.onebase.module.metadata.build.service.validation.MetadataValidationTypeBuildService;
import com.cmsr.onebase.module.metadata.build.service.validation.MetadataValidationRequiredBuildService;
import com.cmsr.onebase.module.metadata.build.service.validation.MetadataValidationUniqueBuildService;
import com.cmsr.onebase.module.metadata.build.service.validation.MetadataValidationLengthBuildService;
import com.cmsr.onebase.module.metadata.build.service.validation.MetadataValidationRuleGroupBuildService;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.field.MetadataEntityFieldOptionDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataPermitRefOtftDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationTypeDO;
import com.cmsr.onebase.module.metadata.core.enums.RelationshipTypeEnum;
import com.cmsr.onebase.module.metadata.core.util.StatusEnumUtil;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationFormatRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationLengthRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRequiredRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationUniqueRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.TemporaryDatasourceService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.build.service.datasource.MetadataDatasourceBuildService;
import com.cmsr.onebase.module.metadata.build.service.field.MetadataEntityFieldOptionBuildService;
import com.cmsr.onebase.module.metadata.build.service.field.MetadataEntityFieldConstraintBuildService;
import com.cmsr.onebase.module.metadata.build.service.number.AutoNumberConfigBuildService;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberConfigDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.number.MetadataAutoNumberRuleItemDO;
import com.cmsr.onebase.module.metadata.core.enums.BusinessEntityTypeEnum;
import com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum;
import com.cmsr.onebase.module.metadata.core.enums.CommonStatusEnum;
import com.cmsr.onebase.module.metadata.core.util.MetadataIdUuidConverter;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import com.mybatisflex.core.query.QueryWrapper;
import org.anyline.entity.DataSet;
import org.anyline.entity.DataRow;

import org.anyline.metadata.Column;
import org.anyline.metadata.Table;
import org.anyline.metadata.type.DatabaseType;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Objects;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.BUSINESS_ENTITY_NOT_EXISTS;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.ENTITY_FIELD_NOT_EXISTS;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.ENTITY_FIELD_NAME_DUPLICATE;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.ENTITY_FIELD_DISPLAY_NAME_DUPLICATE;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.ENTITY_FIELD_NAME_IS_SYSTEM_RESERVED;

/**
 * 实体字段 Service 实现类
 */
@Service
@Slf4j
public class MetadataEntityFieldBuildServiceImpl implements MetadataEntityFieldBuildService {

    @Resource
    private MetadataEntityRelationshipBuildService metadataEntityRelationshipBuildService;

    /**
     * 系统保留字段名列表
     */
    private static final Set<String> SYSTEM_RESERVED_FIELD_NAMES = Set.of(
            "id",
            "owner_id",
            "owner_dept",
            "creator",
            "updater",
            "created_time",
            "updated_time",
            "lock_version",
            "deleted",
            "parent_id");

    @Resource
    private MetadataEntityFieldRepository metadataEntityFieldRepository;
    @Resource
    private TemporaryDatasourceService temporaryDatasourceService;
    @Resource
    private MetadataBusinessEntityCoreService metadataBusinessEntityCoreService;
    @Resource
    private MetadataDatasourceBuildService metadataDatasourceBuildService;
    @Resource
    private MetadataEntityFieldOptionBuildService fieldOptionService;
    @Resource
    private MetadataEntityFieldConstraintBuildService fieldConstraintService;
    @Resource
    private AutoNumberConfigBuildService autoNumberConfigBuildService;
    @Resource
    private AutoNumberRuleBuildService autoNumberRuleBuildService;
    @Resource
    private MetadataComponentFieldTypeBuildService componentFieldTypeService;
    @Resource
    private AnylineService<?> anylineService;
    @Resource
    private MetadataPermitRefOtftBuildService permitRefOtftService;
    @Resource
    private MetadataValidationTypeBuildService validationTypeService;
    @Resource
    private MetadataValidationRequiredBuildService validationRequiredService;
    @Resource
    private MetadataValidationUniqueBuildService validationUniqueService;
    @Resource
    private MetadataValidationLengthBuildService validationLengthService;
    @Resource
    private MetadataValidationRequiredRepository validationRequiredRepository;
    @Resource
    private MetadataValidationUniqueRepository validationUniqueRepository;
    @Resource
    private MetadataValidationLengthRepository validationLengthRepository;
    @Resource
    private MetadataValidationFormatRepository validationFormatRepository;
    @Resource
    private MetadataValidationRuleGroupBuildService validationRuleGroupService;

    @Resource
    private MetadataIdUuidConverter idUuidConverter;

    @Resource
    private ModelMapper modelMapper;

    @Override
    public List<FieldTypeConfigRespVO> getFieldTypes() {
        // 从MetadataComponentFieldTypeDO中读取字段类型配置，替代原来的枚举方式
        return componentFieldTypeService.getFieldTypeConfigs();
    }

    @Override
    public List<EntityFieldValidationTypesRespVO> getFieldValidationTypes(
            @Valid EntityFieldValidationTypesReqVO reqVO) {
        List<String> rawFieldIds = reqVO.getFieldIdList();
        if (rawFieldIds == null || rawFieldIds.isEmpty()) {
            return new ArrayList<>();
        }
        // 过滤空/null/纯空白并去重
        List<Long> fieldIds = rawFieldIds.stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .map(s -> {
                    try {
                        return Long.valueOf(s.trim());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (fieldIds.isEmpty()) {
            // 没有任何有效字段ID，直接返回空
            return new ArrayList<>();
        }

        // 1) 批量查询字段，获取 fieldId -> fieldType 映射
        QueryWrapper queryWrapper = QueryWrapper.create()
                .in(MetadataEntityFieldDO::getId, fieldIds);
        List<MetadataEntityFieldDO> fields = metadataEntityFieldRepository.list(queryWrapper);
        Map<String, String> fieldIdToType = fields.stream()
                .collect(Collectors.toMap(f -> String.valueOf(f.getId()), MetadataEntityFieldDO::getFieldType));

        if (fieldIdToType.isEmpty()) {
            return new ArrayList<>();
        }

        // 提取所有用到的字段类型编码
        Set<String> typeCodes = fieldIdToType.values().stream()
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.toSet());

        if (typeCodes.isEmpty()) {
            // 没有可用类型，直接返回空列表结构
            return fieldIdToType.entrySet().stream().map(e -> {
                EntityFieldValidationTypesRespVO vo = new EntityFieldValidationTypesRespVO();
                vo.setFieldUuid(e.getKey());
                vo.setFieldTypeCode(e.getValue());
                vo.setValidationTypes(new ArrayList<>());
                return vo;
            }).collect(Collectors.toList());
        }
        // 2) 多次单表查询 + 组装（使用 AnylineService 查询组件字段类型）
        // 2.1 查询字段类型（按 code 过滤）- 使用 AnylineService 查询，因为没有对应的 Repository
        String typeCodeList = String.join("','", typeCodes);
        DataSet typeDs = anylineService.querys("metadata_component_field_type", 
                "deleted = 0 AND field_type_code IN ('" + typeCodeList + "')");
        Map<Long, String> typeIdToCode = new HashMap<>();
        for (DataRow row : typeDs) {
            Long idVal = null;
            try {
                idVal = row.getLong("id");
            } catch (Exception ignore) {
            }
            String codeVal = row.getString("field_type_code");
            if (idVal != null && codeVal != null && !codeVal.isBlank()) {
                typeIdToCode.put(idVal, codeVal);
            }
        }

        Map<String, List<EntityFieldValidationTypesRespVO.ValidationTypeItem>> typeToValidation = new HashMap<>();
        if (!typeIdToCode.isEmpty()) {
            // 2.2 查询关联表（字段类型ID -> 校验类型ID + 排序）
            List<MetadataPermitRefOtftDO> relations = permitRefOtftService.listByFieldTypeIds(typeIdToCode.keySet());

            // 收集所有用到的校验类型ID
            Set<Long> vtIds = new java.util.HashSet<>();
            for (var rel : relations) {
                if (rel.getValidationTypeId() != null) {
                    vtIds.add(rel.getValidationTypeId());
                }
            }

            Map<Long, String> vtIdToCode = new HashMap<>();
            Map<Long, String> vtIdToName = new HashMap<>();
            Map<Long, String> vtIdToDesc = new HashMap<>();

            if (!vtIds.isEmpty()) {
                // 2.3 查询校验类型详情
                Map<Long, MetadataValidationTypeDO> vtMap = validationTypeService.getByIds(vtIds);
                for (var entry : vtMap.entrySet()) {
                    Long idVal = entry.getKey();
                    var vo = entry.getValue();
                    if (idVal != null && vo != null) {
                        vtIdToCode.put(idVal, vo.getValidationCode());
                        vtIdToName.put(idVal, vo.getValidationName());
                        vtIdToDesc.put(idVal, vo.getValidationDesc());
                    }
                }

                // 2.4 组装按照字段类型code聚合的校验类型列表
                for (var rel : relations) {
                    Long ftId = rel.getFieldTypeId();
                    Long vtId = rel.getValidationTypeId();
                    Integer sort = rel.getSortOrder();
                    String ftCode = ftId != null ? typeIdToCode.get(ftId) : null;
                    if (ftCode == null || vtId == null) {
                        continue;
                    }
                    String code = vtIdToCode.get(vtId);
                    if (code == null) {
                        continue;
                    }
                    EntityFieldValidationTypesRespVO.ValidationTypeItem item = new EntityFieldValidationTypesRespVO.ValidationTypeItem();
                    item.setCode(code);
                    item.setName(vtIdToName.get(vtId));
                    item.setDescription(vtIdToDesc.get(vtId));
                    item.setSortOrder(sort);
                    typeToValidation.computeIfAbsent(ftCode, k -> new ArrayList<>()).add(item);
                }

                // 2.5 确保每个列表按 sort_order 升序
                for (List<EntityFieldValidationTypesRespVO.ValidationTypeItem> list : typeToValidation.values()) {
                    list.sort((a, b) -> {
                        Integer s1 = a.getSortOrder() != null ? a.getSortOrder() : 0;
                        Integer s2 = b.getSortOrder() != null ? b.getSortOrder() : 0;
                        return Integer.compare(s1, s2);
                    });
                }
            }
        }

        // 3) 按 fieldId 组装返回
        List<EntityFieldValidationTypesRespVO> result = new ArrayList<>();
        for (Map.Entry<String, String> e : fieldIdToType.entrySet()) {
            EntityFieldValidationTypesRespVO vo = new EntityFieldValidationTypesRespVO();
            vo.setFieldUuid(e.getKey());
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

        if (queryVO.getEntityId() != null && !queryVO.getEntityId().trim().isEmpty()) {
            queryWrapper.eq(MetadataEntityFieldDO::getEntityUuid, queryVO.getEntityId().trim());
        }
        if (queryVO.getKeyword() != null && !queryVO.getKeyword().trim().isEmpty()) {
            queryWrapper.like(MetadataEntityFieldDO::getFieldName, queryVO.getKeyword())
                    .or(MetadataEntityFieldDO::getDisplayName).like(queryVO.getKeyword());
        }
        if (queryVO.getIsSystemField() != null) {
            queryWrapper.eq(MetadataEntityFieldDO::getIsSystemField, queryVO.getIsSystemField());
        }
        if (queryVO.getFieldCode() != null && !queryVO.getFieldCode().trim().isEmpty()) {
            queryWrapper.like(MetadataEntityFieldDO::getFieldCode, queryVO.getFieldCode());
        }

        // isPerson=1 时，限定人员字段（USER）并补充 creator/updater 两个系统字段
        List<MetadataEntityFieldDO> baseList;
        queryWrapper.orderBy(MetadataEntityFieldDO::getSortOrder, true);
        queryWrapper.orderBy(MetadataEntityFieldDO::getCreateTime, false);

        if (queryVO.getIsPerson() != null && queryVO.getIsPerson() == 1) {
            // 限定字段类型为 USER
            QueryWrapper personWrapper = QueryWrapper.create();
            personWrapper.eq(MetadataEntityFieldDO::getEntityUuid, queryVO.getEntityId().trim());
            personWrapper.eq(MetadataEntityFieldDO::getFieldType, "USER");
            // 透传其它条件
            if (queryVO.getKeyword() != null && !queryVO.getKeyword().trim().isEmpty()) {
                personWrapper.like(MetadataEntityFieldDO::getFieldName, queryVO.getKeyword())
                        .or(MetadataEntityFieldDO::getDisplayName).like(queryVO.getKeyword());
            }
            if (queryVO.getFieldCode() != null && !queryVO.getFieldCode().trim().isEmpty()) {
                personWrapper.like(MetadataEntityFieldDO::getFieldCode, queryVO.getFieldCode());
            }
            personWrapper.orderBy(MetadataEntityFieldDO::getSortOrder, true);
            personWrapper.orderBy(MetadataEntityFieldDO::getCreateTime, false);

            baseList = metadataEntityFieldRepository.list(personWrapper);

            // 追加 creator、updater 系统字段（若存在）并去重
            if (queryVO.getEntityId() != null) {
                String entityUuid = queryVO.getEntityId().trim();
                LinkedHashMap<String, MetadataEntityFieldDO> map = new LinkedHashMap<>();
                for (MetadataEntityFieldDO f : baseList) {
                    String key = f.getId() != null ? String.valueOf(f.getId()) : f.getFieldName();
                    map.putIfAbsent(key, f);
                }

                MetadataEntityFieldDO creator = metadataEntityFieldRepository.getEntityFieldByName(entityUuid, "creator");
                if (creator != null) {
                    String key = creator.getId() != null ? String.valueOf(creator.getId()) : creator.getFieldName();
                    map.putIfAbsent(key, creator);
                }
                MetadataEntityFieldDO updater = metadataEntityFieldRepository.getEntityFieldByName(entityUuid, "updater");
                if (updater != null) {
                    String key = updater.getId() != null ? String.valueOf(updater.getId()) : updater.getFieldName();
                    map.putIfAbsent(key, updater);
                }

                baseList = new java.util.ArrayList<>(map.values());
                // 最终再按 sort_order asc, create_time desc 排序一次
                baseList.sort((a, b) -> {
                    int s1 = a.getSortOrder() != null ? a.getSortOrder() : 0;
                    int s2 = b.getSortOrder() != null ? b.getSortOrder() : 0;
                    if (s1 != s2)
                        return Integer.compare(s1, s2);
                    if (a.getCreateTime() == null && b.getCreateTime() == null)
                        return 0;
                    if (a.getCreateTime() == null)
                        return 1;
                    if (b.getCreateTime() == null)
                        return -1;
                    return b.getCreateTime().compareTo(a.getCreateTime());
                });
            }
            return baseList;
        }

        // 默认逻辑
        return metadataEntityFieldRepository.list(queryWrapper);
    }

    @Override
    public EntityFieldDetailRespVO getEntityFieldDetail(String id) {
        Long longId = Long.valueOf(id);
        MetadataEntityFieldDO entityField = metadataEntityFieldRepository.getById(longId);
        if (entityField == null) {
            throw exception(ENTITY_FIELD_NOT_EXISTS);
        }

        EntityFieldDetailRespVO result = BeanUtils.toBean(entityField, EntityFieldDetailRespVO.class);

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EntityFieldBatchSaveRespVO batchSaveEntityFields(@Valid EntityFieldBatchSaveReqVO reqVO) {
        // ID转UUID兼容处理：支持前端传入entityId(实际为reqVO.getEntityId())或entityUuid
        // 注意：reqVO.getEntityId()字段名虽然叫entityId，但实际可能传入UUID或Long ID
        String resolvedEntityUuid = idUuidConverter.resolveEntityUuid(reqVO.getEntityId(), null);
        reqVO.setEntityId(resolvedEntityUuid);

        EntityFieldBatchSaveRespVO resp = new EntityFieldBatchSaveRespVO();

        // 1. 获取实体与数据源
        MetadataBusinessEntityDO businessEntity = metadataBusinessEntityCoreService
                .getBusinessEntityByUuid(reqVO.getEntityId());
        if (businessEntity == null) {
            throw new IllegalArgumentException("业务实体不存在");
        }
        MetadataDatasourceDO datasource = null;
        if (businessEntity.getTableName() != null && !businessEntity.getTableName().trim().isEmpty()) {
            datasource = metadataDatasourceBuildService.getDatasourceByUuid(businessEntity.getDatasourceUuid());
        }

        // 1.5 校验本次提交的字段名是否有重复
        validateFieldNameDuplicationInBatch(reqVO.getItems());

        // 用于收集需要执行的物理表操作
        List<PhysicalTableOperation> physicalTableOps = new java.util.ArrayList<>();

        // 2. 先删除
        for (EntityFieldUpsertItemVO item : reqVO.getItems()) {
            if (Boolean.TRUE.equals(item.getIsDeleted())) {
                // 删除必须提供有效ID（非空非空白）
                if (item.getId() == null || item.getId().trim().isEmpty()) {
                    throw new IllegalArgumentException("删除操作必须提供字段ID");
                }
                // 校验存在
                validateEntityFieldExists(item.getId());

                // 获取字段完整信息用于物理删除
                MetadataEntityFieldDO existing = metadataEntityFieldRepository.getById(Long.valueOf(item.getId()));

                // 关键安全校验：验证字段归属，防止跨实体/跨应用删除
                if (existing == null) {
                    throw new IllegalArgumentException("字段不存在: " + item.getId());
                }
                if (!existing.getEntityUuid().equals(reqVO.getEntityId())) {
                    log.error("安全校验失败：尝试跨实体删除字段。fieldId={}, 字段归属entityUuid={}, 请求entityId={}",
                            item.getId(), existing.getEntityUuid(), reqVO.getEntityId());
                    throw new IllegalArgumentException("字段不属于当前实体，禁止跨实体删除");
                }
                if (!String.valueOf(existing.getApplicationId()).equals(reqVO.getApplicationId())) {
                    log.error("安全校验失败：尝试跨应用删除字段。fieldId={}, 字段归属appId={}, 请求appId={}",
                            item.getId(), existing.getApplicationId(), reqVO.getApplicationId());
                    throw new IllegalArgumentException("字段不属于当前应用，禁止跨应用删除");
                }

                // 实体是否允许改表结构
                validateEntityAllowModifyStructure(existing.getEntityUuid());

                // 先删子配置（选项、约束、自动编号、校验规则）
                if (existing != null) {
                    try {
                        fieldOptionService.deleteByFieldId(existing.getFieldUuid());
                        fieldConstraintService.deleteByFieldId(existing.getFieldUuid());
                        autoNumberConfigBuildService.deleteByFieldId(existing.getFieldUuid());

                        // 删除校验规则
                        validationRequiredService.deleteByFieldId(existing.getFieldUuid());
                        validationUniqueService.deleteByFieldId(existing.getFieldUuid());
                        validationLengthService.deleteByFieldId(existing.getFieldUuid());

                        // 删除实体间关联关系
                        metadataEntityRelationshipBuildService.deleteRelationShipByFieldId(existing.getFieldUuid());
                    } catch (Exception ignore) {}
                }

                // 先删库记录
                metadataEntityFieldRepository.removeById(Long.valueOf(item.getId()));

                // 收集物理表删除操作
                if (datasource != null && businessEntity.getTableName() != null && existing != null) {
                    PhysicalTableOperation op = new PhysicalTableOperation();
                    op.setOperationType("DROP");
                    op.setFieldName(existing.getFieldName());
                    physicalTableOps.add(op);
                }
                resp.getDeletedIds().add(item.getId());
            }
        }

        // 3. 再更新
        for (EntityFieldUpsertItemVO item : reqVO.getItems()) {
            // 更新：要求提供有效ID（非空非空白）
            if (!Boolean.TRUE.equals(item.getIsDeleted()) && item.getId() != null && !item.getId().trim().isEmpty()) {
                validateEntityFieldExists(item.getId());

                // 拉取原字段
                MetadataEntityFieldDO origin = metadataEntityFieldRepository.getById(Long.valueOf(item.getId()));

                // 关键安全校验：验证字段归属，防止跨实体/跨应用操作
                if (origin == null) {
                    throw new IllegalArgumentException("字段不存在: " + item.getId());
                }
                if (!origin.getEntityUuid().equals(reqVO.getEntityId())) {
                    log.error("安全校验失败：尝试跨实体操作字段。fieldId={}, 字段归属entityUuid={}, 请求entityId={}",
                            item.getId(), origin.getEntityUuid(), reqVO.getEntityId());
                    throw new IllegalArgumentException("字段不属于当前实体，禁止跨实体操作");
                }
                if (!String.valueOf(origin.getApplicationId()).equals(reqVO.getApplicationId())) {
                    log.error("安全校验失败：尝试跨应用操作字段。fieldId={}, 字段归属appId={}, 请求appId={}",
                            item.getId(), origin.getApplicationId(), reqVO.getApplicationId());
                    throw new IllegalArgumentException("字段不属于当前应用，禁止跨应用操作");
                }

                // 名称唯一性（若改名）
                String newName = item.getFieldName() != null ? item.getFieldName() : origin.getFieldName();
                validateEntityFieldNameUnique(item.getId(), origin.getEntityUuid(), newName);
                String newDisplayName = item.getDisplayName() != null ? item.getDisplayName() : origin.getDisplayName();
                validateEntityFieldDisplayNameUnique(item.getId(), origin.getEntityUuid(), newDisplayName);

                validateEntityAllowModifyStructure(origin.getEntityUuid());

                Integer maxLength = extractMaxLength(item);

                // 组装更新对象（只覆盖非空字段）
                MetadataEntityFieldDO upd = new MetadataEntityFieldDO();
                upd.setId(origin.getId());
                upd.setEntityUuid(origin.getEntityUuid());
                if (item.getFieldName() != null)
                    upd.setFieldName(item.getFieldName());
                if (item.getDisplayName() != null)
                    upd.setDisplayName(item.getDisplayName());
                if (item.getFieldType() != null)
                    upd.setFieldType(item.getFieldType());
                if (maxLength != null) {
                    upd.setDataLength(maxLength);
                }
                if (item.getDecimalPlaces() != null)
                    upd.setDecimalPlaces(item.getDecimalPlaces());
                if (item.getDefaultValue() != null)
                    upd.setDefaultValue(item.getDefaultValue());
                if (item.getDescription() != null)
                    upd.setDescription(item.getDescription());
                if (item.getIsRequired() != null)
                    upd.setIsRequired(item.getIsRequired());
                if (item.getIsUnique() != null)
                    upd.setIsUnique(item.getIsUnique());
                if (item.getSortOrder() != null)
                    upd.setSortOrder(item.getSortOrder());
                if (item.getIsSystemField() != null)
                    upd.setIsSystemField(item.getIsSystemField());
                if (item.getDictTypeId() != null) {
                    upd.setDictTypeId(item.getDictTypeId());
                }

                metadataEntityFieldRepository.updateById(upd);

                // 收集物理表更新操作
                if (datasource != null && businessEntity.getTableName() != null) {
                    // 若字段名变更，先收集重命名操作
                    if (item.getFieldName() != null && !item.getFieldName().equals(origin.getFieldName())) {
                        PhysicalTableOperation renameOp = new PhysicalTableOperation();
                        renameOp.setOperationType("RENAME");
                        renameOp.setOldFieldName(origin.getFieldName());
                        renameOp.setFieldName(item.getFieldName());
                        physicalTableOps.add(renameOp);
                    }

                    // 收集ALTER操作
                    MetadataEntityFieldDO full = metadataEntityFieldRepository.getById(origin.getId());
                    PhysicalTableOperation alterOp = new PhysicalTableOperation();
                    alterOp.setOperationType("ALTER");
                    alterOp.setFieldInfo(full);
                    physicalTableOps.add(alterOp);
                }
                resp.getUpdatedIds().add(item.getId());

                // 同步选项、约束和自动编号（使用智能更新逻辑）
                Long fieldId = origin.getId();
                String fieldUuid = origin.getFieldUuid();
                MetadataEntityFieldDO fullForRelated = metadataEntityFieldRepository.getById(origin.getId());
                processFieldRelatedData(fieldUuid, fullForRelated, item.getOptions(), item.getConstraints(), item.getAutoNumber());

                // 特别处理：如果 isRequired 字段发生了变更，需要额外同步到 MetadataValidationRequiredDO
                if (item.getIsRequired() != null && !item.getIsRequired().equals(origin.getIsRequired())) {
                    processRequiredValidation(fieldId, fullForRelated);
                }

                // 特别处理：如果 isUnique 字段发生了变更，需要额外同步到 MetadataValidationUniqueDO
                if (item.getIsUnique() != null && !item.getIsUnique().equals(origin.getIsUnique())) {
                    processUniqueValidation(fieldId, fullForRelated);
                }

                // 特别处理：如果 dataLength 字段发生了变更，需要额外同步到 MetadataValidationLengthDO
                boolean hasConstraintsLengthConfig = item.getConstraints() != null &&
                        (item.getConstraints().getLengthEnabled() != null ||
                                item.getConstraints().getMinLength() != null ||
                                item.getConstraints().getMaxLength() != null ||
                                StringUtils.hasText(item.getConstraints().getLengthPrompt()));
                if (maxLength != null && !maxLength.equals(origin.getDataLength()) && !hasConstraintsLengthConfig) {
                    processLengthValidation(fieldId, fullForRelated);
                }

                validateValidationRuleUniqueness(fullForRelated.getFieldUuid(), origin.getEntityUuid());

                // 特别处理：如果数据选择类型的字段发生了变化，需要额外同步到关联关系表中
                if ("DATA_SELECTION".equalsIgnoreCase(item.getFieldType()) 
                        || "MULTI_DATA_SELECTION".equalsIgnoreCase(item.getFieldType())) {
                    processEntityRelation(reqVO.getApplicationId(), fieldId, fullForRelated, item);
                }
            }
        }

        // 4. 最后新增
        for (EntityFieldUpsertItemVO item : reqVO.getItems()) {
            // 新增：当未提供ID或ID为空白时进入
            if (!Boolean.TRUE.equals(item.getIsDeleted()) && (item.getId() == null || item.getId().trim().isEmpty())) {
                // 【安全策略优化】不再通过 fieldName 自动匹配现有字段进行更新
                // 原因：fieldName 在跨应用、跨表中不具备唯一性，存在安全风险
                // 策略：更新操作必须明确提供字段 ID，未提供 ID 时一律作为新增处理
                log.info("未提供字段ID，将作为新增字段处理: fieldName={}, entityId={}, appId={}",
                        item.getFieldName(), reqVO.getEntityId(), reqVO.getApplicationId());

                // 新增字段的情况
                validateEntityFieldNameUnique(null, reqVO.getEntityId(), item.getFieldName());
                validateEntityFieldDisplayNameUnique(null, reqVO.getEntityId(), item.getDisplayName());
                validateEntityAllowModifyStructure(reqVO.getEntityId());

                Integer maxLength = extractMaxLength(item);

                MetadataEntityFieldDO toCreate = new MetadataEntityFieldDO();
                toCreate.setEntityUuid(reqVO.getEntityId());
                toCreate.setApplicationId(reqVO.getApplicationId() != null ? Long.parseLong(reqVO.getApplicationId()) : null);
                toCreate.setFieldName(item.getFieldName());
                toCreate.setDisplayName(item.getDisplayName());
                toCreate.setFieldType(item.getFieldType());
                toCreate.setDataLength(maxLength);
                toCreate.setDecimalPlaces(item.getDecimalPlaces());
                toCreate.setDefaultValue(item.getDefaultValue());
                toCreate.setDescription(item.getDescription());
                toCreate.setIsRequired(item.getIsRequired());
                toCreate.setIsUnique(item.getIsUnique());
                toCreate.setSortOrder(item.getSortOrder());
                toCreate.setFieldCode(generateFieldCode(item.getFieldName()));
                toCreate.setIsSystemField(
                        item.getIsSystemField() != null ? item.getIsSystemField() : StatusEnumUtil.YES);
                toCreate.setIsPrimaryKey(StatusEnumUtil.NO);
                toCreate.setVersionTag(0L);
                toCreate.setDictTypeId(item.getDictTypeId());

                metadataEntityFieldRepository.save(toCreate);

                // 收集物理表新增操作
                if (datasource != null && businessEntity.getTableName() != null) {
                    PhysicalTableOperation addOp = new PhysicalTableOperation();
                    addOp.setOperationType("ADD");
                    addOp.setFieldInfo(toCreate);
                    physicalTableOps.add(addOp);
                }
                resp.getCreatedIds().add(String.valueOf(toCreate.getId()));

                // 同步选项、约束和自动编号（使用智能更新逻辑）
                Long fieldId = toCreate.getId();
                String fieldUuid = toCreate.getFieldUuid();
                processFieldRelatedData(fieldUuid, toCreate, item.getOptions(), item.getConstraints(),
                        item.getAutoNumber());

                if (item.getIsRequired() != null && item.getIsRequired() == 1) {
                    processRequiredValidation(fieldId, toCreate);
                }

                if (item.getIsUnique() != null && item.getIsUnique() == 1) {
                    processUniqueValidation(fieldId, toCreate);
                }

                boolean hasConstraintsLengthConfig = item.getConstraints() != null &&
                        (item.getConstraints().getLengthEnabled() != null ||
                                item.getConstraints().getMinLength() != null ||
                                item.getConstraints().getMaxLength() != null ||
                                StringUtils.hasText(item.getConstraints().getLengthPrompt()));
                if (maxLength != null && maxLength > 0 && !hasConstraintsLengthConfig) {
                    processLengthValidation(fieldId, toCreate);
                }

                validateValidationRuleUniqueness(toCreate.getFieldUuid(), toCreate.getEntityUuid());

                // 特别处理：如果数据选择类型的字段发生了变化，需要额外同步到关联关系表中
                if ("DATA_SELECTION".equalsIgnoreCase(item.getFieldType())
                        || "MULTI_DATA_SELECTION".equalsIgnoreCase(item.getFieldType())) {
                    processEntityRelation(reqVO.getApplicationId(), fieldId, toCreate, item);
                }


            }
        }

        // 5. 统一执行所有物理表操作
        if (datasource != null && businessEntity.getTableName() != null && !physicalTableOps.isEmpty()) {
            executePhysicalTableOperations(datasource, businessEntity.getTableName(), physicalTableOps);
        }

        return resp;
    }

    private void processEntityRelation(String appId, Long fieldId, MetadataEntityFieldDO full, EntityFieldUpsertItemVO item) {
        if (item.getDataSelectionConfig() == null) {
            return;
        }

        Long relationId = item.getDataSelectionConfig().getRelationId();
        String targetEntityUuid = item.getDataSelectionConfig().getTargetEntityUuid();
        String targetFieldUuid = item.getDataSelectionConfig().getTargetFieldUuid();
        
        if (targetEntityUuid == null || targetFieldUuid == null) {
            log.warn("数据选择配置不完整，跳过处理关系。sourceEntityUuid={}, fieldId={}", full.getEntityUuid(), fieldId);
            return;
        }

        // 查询数据库中已存在的关系
        MetadataEntityRelationshipDO existingRelation = null;
        if (relationId != null) {
            // 如果传入了relationId，则直接根据ID查询（更新场景）
            existingRelation = metadataEntityRelationshipBuildService.findById(relationId);
        } else {
            // 如果没有传入relationId，则根据 sourceEntityUuid 和 sourceFieldUuid 查询（新增场景）
            List<MetadataEntityRelationshipDO> existingRelations = 
                    metadataEntityRelationshipBuildService.findBySourceEntityUuidAndTargetEntityUuid(null, full.getEntityUuid());
            if (existingRelations != null && !existingRelations.isEmpty()) {
                // 过滤出 targetFieldUuid 匹配的关系
                String fieldUuid = full.getFieldUuid();
                for (MetadataEntityRelationshipDO rel : existingRelations) {
                    if (rel.getTargetFieldUuid() != null && rel.getTargetFieldUuid().equals(fieldUuid)) {
                        existingRelation = rel;
                        break;
                    }
                }
            }
        }

        // 根据字段类型确定关系类型：DATA_SELECTION 使用 DATA_SELECT，MULTI_DATA_SELECTION 使用 DATA_SELECT_MULTI
        String relationshipType;
        if ("MULTI_DATA_SELECTION".equalsIgnoreCase(item.getFieldType())) {
            relationshipType = RelationshipTypeEnum.DATA_SELECT_MULTI.getRelationshipType();
        } else {
            relationshipType = RelationshipTypeEnum.DATA_SELECT.getRelationshipType();
        }

        // 构建新的关系数据
        EntityRelationshipSaveReqVO r = new EntityRelationshipSaveReqVO();
        r.setRelationName("数据选择关系");
        r.setSourceEntityUuid(targetEntityUuid);
        r.setTargetEntityUuid(full.getEntityUuid());
        r.setRelationshipType(relationshipType);
        r.setSourceFieldUuid(targetFieldUuid);
        r.setTargetFieldUuid(full.getFieldUuid());
        r.setCascadeType("READ");
        r.setDescription("数据选择关系");
        r.setApplicationId(appId);

        // 比较数据：如果数据库中的关系和请求的数据一样，则忽略
        // 注意：前端传的targetEntityUuid/targetFieldUuid实际存储在关系的sourceEntityUuid/sourceFieldUuid字段中
        if (existingRelation != null) {
            boolean isSame = Objects.equals(existingRelation.getSourceEntityUuid(), targetEntityUuid)
                    && Objects.equals(existingRelation.getSourceFieldUuid(), targetFieldUuid);
            
            if (isSame) {
                log.debug("数据选择关系未变化，忽略更新。currentEntityUuid={}, fieldId={}, targetEntityUuid={}, targetFieldUuid={}",
                        full.getEntityUuid(), fieldId, targetEntityUuid, targetFieldUuid);
                return;
            } else {
                // 数据不同，更新关系
                log.info("数据选择关系已变化，更新关系。currentEntityUuid={}, fieldId={}, 原targetEntityUuid={}, 原targetFieldUuid={}, 新targetEntityUuid={}, 新targetFieldUuid={}",
                        full.getEntityUuid(), fieldId, existingRelation.getSourceEntityUuid(), existingRelation.getSourceFieldUuid(),
                        targetEntityUuid, targetFieldUuid);
                r.setId(existingRelation.getId().toString());
                metadataEntityRelationshipBuildService.updateEntityRelationship(r);
            }
        } else {
            // 不存在关系，创建新关系
            log.info("创建新的数据选择关系。sourceEntityUuid={}, fieldId={}, targetEntityUuid={}, targetFieldUuid={}",
                    full.getEntityUuid(), fieldId, targetEntityUuid, targetFieldUuid);
            metadataEntityRelationshipBuildService.createEntityRelationship(r);
        }
    }

    private Integer extractMaxLength(EntityFieldUpsertItemVO item) {
        if (item == null || item.getConstraints() == null) {
            return null;
        }
        Integer maxLength = item.getConstraints().getMaxLength();
        if (maxLength == null || maxLength <= 0) {
            return null;
        }
        return maxLength;
    }

    /**
     * 根据字段编码或字段名查找已存在的字段
     * <p>
     * 注意：此方法会显式添加租户条件，确保多租户隔离的正确性
     *
     * @param entityId 实体ID
     * @param item     字段信息
     * @return 已存在的字段，如果不存在则返回null
     */
    private MetadataEntityFieldDO findExistingFieldByCodeOrName(String entityId, EntityFieldUpsertItemVO item) {
        // fieldCode字段已注释，跳过根据fieldCode查找逻辑
        // 直接根据fieldName查找

        // 其次根据fieldName查找
        if (item.getFieldName() != null && !item.getFieldName().trim().isEmpty()) {
            QueryWrapper nameWrapper = QueryWrapper.create()
                    .eq(MetadataEntityFieldDO::getEntityUuid, entityId.trim())
                    .eq(MetadataEntityFieldDO::getFieldName, item.getFieldName());

            MetadataEntityFieldDO existingField = metadataEntityFieldRepository.getOne(nameWrapper);
            if (existingField != null) {
                return existingField;
            }
        }

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEntityField(@Valid EntityFieldSaveReqVO createReqVO) {
        // ID转UUID兼容处理：支持前端传入entityId或entityUuid
        String resolvedEntityUuid = idUuidConverter.resolveEntityUuid(
                createReqVO.getEntityUuid(), createReqVO.getEntityId());
        createReqVO.setEntityUuid(resolvedEntityUuid);

        // 校验字段名不能与系统保留字段冲突
        validateFieldNameNotSystemReserved(createReqVO.getFieldName());
        // 校验字段名唯一性
        validateEntityFieldNameUnique(null, createReqVO.getEntityUuid(), createReqVO.getFieldName());
        validateEntityFieldDisplayNameUnique(null, createReqVO.getEntityUuid(), createReqVO.getDisplayName());

        // 校验实体类型是否允许修改表结构
        validateEntityAllowModifyStructure(createReqVO.getEntityUuid());

        // 插入实体字段
        MetadataEntityFieldDO entityField = BeanUtils.toBean(createReqVO, MetadataEntityFieldDO.class);
        entityField.setEntityUuid(createReqVO.getEntityUuid());
        entityField.setApplicationId(createReqVO.getApplicationId() != null ? Long.parseLong(createReqVO.getApplicationId()) : null);
        entityField.setIsSystemField(1); // 手动创建的字段不是系统字段：1-不是
        entityField.setVersionTag(0L); // 默认编辑态
        entityField.setStatus(0); // 默认开启
        // 如果没有提供fieldCode，则根据fieldName生成
        if (entityField.getFieldCode() == null || entityField.getFieldCode().trim().isEmpty()) {
            entityField.setFieldCode(generateFieldCode(createReqVO.getFieldName()));
        }

        metadataEntityFieldRepository.save(entityField);

        // 同步到物理表 - 失败时直接抛出异常回滚事务
        try {
            MetadataBusinessEntityDO businessEntity = metadataBusinessEntityCoreService
                    .getBusinessEntityByUuid(createReqVO.getEntityUuid());
            if (businessEntity != null && businessEntity.getTableName() != null &&
                    !businessEntity.getTableName().trim().isEmpty()) {
                MetadataDatasourceDO datasource = metadataDatasourceBuildService
                        .getDatasourceByUuid(businessEntity.getDatasourceUuid());
                if (datasource != null) {
                    addColumnToTable(datasource, businessEntity.getTableName(), entityField);
                }
            }
        } catch (Exception e) {
            log.error("创建字段时同步到物理表失败: {}", e.getMessage(), e);
            throw new RuntimeException("同步物理表失败: " + e.getMessage(), e);
        }

        return entityField.getId();
    }

    @Override
    public Long createEntityFieldInternal(MetadataEntityFieldDO entityField) {
        metadataEntityFieldRepository.save(entityField);
        return entityField.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEntityField(@Valid EntityFieldSaveReqVO updateReqVO) {
        // ID转UUID兼容处理：支持前端传入entityId或entityUuid
        String resolvedEntityUuid = idUuidConverter.resolveEntityUuidOptional(
                updateReqVO.getEntityUuid(), updateReqVO.getEntityId());
        if (resolvedEntityUuid != null) {
            updateReqVO.setEntityUuid(resolvedEntityUuid);
        }

        // 校验存在
        validateEntityFieldExists(updateReqVO.getId());
        // 校验字段名不能与系统保留字段冲突
        validateFieldNameNotSystemReserved(updateReqVO.getFieldName());
        // 校验字段名唯一性
        validateEntityFieldNameUnique(updateReqVO.getId(), updateReqVO.getEntityUuid(), updateReqVO.getFieldName());
        validateEntityFieldDisplayNameUnique(updateReqVO.getId(), updateReqVO.getEntityUuid(),
                updateReqVO.getDisplayName());
        // 校验实体类型是否允许修改表结构
        validateEntityAllowModifyStructure(updateReqVO.getEntityUuid());

        // 更新实体字段
        MetadataEntityFieldDO updateObj = BeanUtils.toBean(updateReqVO, MetadataEntityFieldDO.class);
        updateObj.setId(Long.valueOf(updateReqVO.getId()));
        updateObj.setEntityUuid(updateReqVO.getEntityUuid());
        updateObj.setApplicationId(updateReqVO.getApplicationId() != null ? Long.parseLong(updateReqVO.getApplicationId()) : null);
        // 如果没有提供fieldCode，则根据fieldName生成
        if (updateObj.getFieldCode() == null || updateObj.getFieldCode().trim().isEmpty()) {
            updateObj.setFieldCode(generateFieldCode(updateReqVO.getFieldName()));
        }
        metadataEntityFieldRepository.updateById(updateObj);

        // 同步到物理表
        try {
            MetadataBusinessEntityDO businessEntity = metadataBusinessEntityCoreService
                    .getBusinessEntityByUuid(updateReqVO.getEntityUuid());
            if (businessEntity != null && businessEntity.getTableName() != null &&
                    !businessEntity.getTableName().trim().isEmpty()) {
                MetadataDatasourceDO datasource = metadataDatasourceBuildService
                        .getDatasourceByUuid(businessEntity.getDatasourceUuid());
                if (datasource != null) {
                    alterColumnInTable(datasource, businessEntity.getTableName(), updateObj);
                }
            }
        } catch (Exception e) {
            log.error("更新字段时同步到物理表失败: {}", e.getMessage(), e);
            throw new RuntimeException("更新物理表字段失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEntityField(String id) {
        // 校验存在（容错空白ID）
        validateEntityFieldExists(id);

        // 获取字段信息（在删除前需要获取相关信息用于删除物理表字段）
        Long longId = Long.valueOf(id);
        MetadataEntityFieldDO existingField = metadataEntityFieldRepository.getById(longId);

        if (existingField != null) {
            // 校验实体类型是否允许修改表结构
            validateEntityAllowModifyStructure(existingField.getEntityUuid());
        }

        // 先删除子配置（选项、约束、自动编号）
        if (existingField != null) {
            fieldOptionService.deleteByFieldId(existingField.getFieldUuid());
            fieldConstraintService.deleteByFieldId(existingField.getFieldUuid());
            autoNumberConfigBuildService.deleteByFieldId(existingField.getFieldUuid());
            validationRequiredService.deleteByFieldId(existingField.getFieldUuid());
            validationUniqueService.deleteByFieldId(existingField.getFieldUuid());
            validationLengthService.deleteByFieldId(existingField.getFieldUuid());
        }

        // 删除实体字段
        metadataEntityFieldRepository.removeById(longId);

        // 从物理表删除字段
        if (existingField != null) {
            try {
                MetadataBusinessEntityDO businessEntity = metadataBusinessEntityCoreService
                        .getBusinessEntityByUuid(existingField.getEntityUuid());
                if (businessEntity != null && businessEntity.getTableName() != null &&
                        !businessEntity.getTableName().trim().isEmpty()) {
                    MetadataDatasourceDO datasource = metadataDatasourceBuildService
                            .getDatasourceByUuid(businessEntity.getDatasourceUuid());
                    if (datasource != null) {
                        dropColumnFromTable(datasource, businessEntity.getTableName(), existingField.getFieldName());
                    }
                }
            } catch (Exception e) {
                log.error("删除字段时从物理表删除失败: {}", e.getMessage(), e);
                throw new RuntimeException("删除物理表字段失败: " + e.getMessage(), e);
            }
        }
    }

    private void validateEntityFieldExists(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw exception(ENTITY_FIELD_NOT_EXISTS);
        }
        Long longId;
        try {
            longId = Long.valueOf(id.trim());
        } catch (NumberFormatException e) {
            throw exception(ENTITY_FIELD_NOT_EXISTS);
        }
        if (metadataEntityFieldRepository.getById(longId) == null) {
            throw exception(ENTITY_FIELD_NOT_EXISTS);
        }
    }

    private void validateEntityFieldNameUnique(String id, String entityId, String fieldName) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(MetadataEntityFieldDO::getEntityUuid, entityId)
                .eq(MetadataEntityFieldDO::getFieldName, fieldName);
        if (id != null && !id.trim().isEmpty()) {
            Long longId = Long.valueOf(id.trim());
            queryWrapper.ne(MetadataEntityFieldDO::getId, longId);
        }

        long count = metadataEntityFieldRepository.count(queryWrapper);
        if (count > 0) {
            throw exception(ENTITY_FIELD_NAME_DUPLICATE, fieldName);
        }
    }

    /**
     * 校验字段名是否与系统保留字段冲突
     *
     * @param fieldName 字段名
     */
    private void validateFieldNameNotSystemReserved(String fieldName) {
        if (fieldName == null || fieldName.trim().isEmpty()) {
            return;
        }

        String trimmedFieldName = fieldName.trim();
        if (SYSTEM_RESERVED_FIELD_NAMES.contains(trimmedFieldName)) {
            throw exception(ENTITY_FIELD_NAME_IS_SYSTEM_RESERVED, trimmedFieldName);
        }
    }

    private void validateEntityFieldDisplayNameUnique(String id, String entityId, String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            return;
        }

        Long longEntityId = Long.valueOf(entityId);
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(MetadataEntityFieldDO::getEntityUuid, entityId)
                .eq(MetadataEntityFieldDO::getDisplayName, displayName.trim());
        if (id != null && !id.trim().isEmpty()) {
            Long longId = Long.valueOf(id.trim());
            queryWrapper.ne(MetadataEntityFieldDO::getId, longId);
        }

        long count = metadataEntityFieldRepository.count(queryWrapper);
        if (count > 0) {
            throw exception(ENTITY_FIELD_DISPLAY_NAME_DUPLICATE, displayName.trim());
        }
    }

    @Override
    public MetadataEntityFieldDO getEntityField(String id) {
        Long longId = Long.valueOf(id.trim());
        return metadataEntityFieldRepository.getById(longId);
    }

    @Override
    public MetadataEntityFieldDO getEntityFieldByUuid(String fieldUuid) {
        if (fieldUuid == null || fieldUuid.isEmpty()) {
            return null;
        }
        return metadataEntityFieldRepository.getByFieldUuid(fieldUuid);
    }

    @Override
    public PageResult<MetadataEntityFieldDO> getEntityFieldPage(EntityFieldPageReqVO pageReqVO) {
        QueryWrapper queryWrapper = QueryWrapper.create();

        // 添加查询条件
        if (pageReqVO.getEntityId() != null && !pageReqVO.getEntityId().trim().isEmpty()) {
            queryWrapper.eq(MetadataEntityFieldDO::getEntityUuid, pageReqVO.getEntityId().trim());
        }
        if (pageReqVO.getFieldName() != null && !pageReqVO.getFieldName().trim().isEmpty()) {
            queryWrapper.like(MetadataEntityFieldDO::getFieldName, pageReqVO.getFieldName());
        }
        if (pageReqVO.getDisplayName() != null && !pageReqVO.getDisplayName().trim().isEmpty()) {
            queryWrapper.like(MetadataEntityFieldDO::getDisplayName, pageReqVO.getDisplayName());
        }
        if (pageReqVO.getFieldType() != null && !pageReqVO.getFieldType().trim().isEmpty()) {
            queryWrapper.eq(MetadataEntityFieldDO::getFieldType, pageReqVO.getFieldType());
        }
        if (pageReqVO.getIsSystemField() != null) {
            queryWrapper.eq(MetadataEntityFieldDO::getIsSystemField, pageReqVO.getIsSystemField());
        }
        if (pageReqVO.getIsPrimaryKey() != null) {
            queryWrapper.eq(MetadataEntityFieldDO::getIsPrimaryKey, pageReqVO.getIsPrimaryKey());
        }
        if (pageReqVO.getIsRequired() != null) {
            queryWrapper.eq(MetadataEntityFieldDO::getIsRequired, pageReqVO.getIsRequired());
        }
        if (pageReqVO.getVersionTag() != null) {
            queryWrapper.eq(MetadataEntityFieldDO::getVersionTag, pageReqVO.getVersionTag());
        }
        if (pageReqVO.getApplicationId() != null && !pageReqVO.getApplicationId().trim().isEmpty()) {
            queryWrapper.eq(MetadataEntityFieldDO::getApplicationId, Long.parseLong(pageReqVO.getApplicationId().trim()));
        }
        if (pageReqVO.getFieldCode() != null && !pageReqVO.getFieldCode().trim().isEmpty()) {
            queryWrapper.like(MetadataEntityFieldDO::getFieldCode, pageReqVO.getFieldCode());
        }

        // 添加排序：按照字段排序优先（倒序），然后按创建时间倒序
        queryWrapper.orderBy(MetadataEntityFieldDO::getSortOrder, false);
        queryWrapper.orderBy(MetadataEntityFieldDO::getCreateTime, false);

        // 分页查询
        com.mybatisflex.core.paginate.Page<MetadataEntityFieldDO> page = metadataEntityFieldRepository.page(
                new com.mybatisflex.core.paginate.Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize()),
                queryWrapper);
        
        return new PageResult<>(page.getRecords(), page.getTotalRow());
    }

    @Override
    public PageResult<EntityFieldRespVO> getEntityFieldPageWithRelated(EntityFieldPageReqVO pageReqVO) {
        // 先获取基础分页数据
        PageResult<MetadataEntityFieldDO> pageResult = getEntityFieldPage(pageReqVO);

        // 转换为响应VO并填充关联数据
        List<EntityFieldRespVO> respList = pageResult.getList().stream()
                .map(field -> {
                    EntityFieldRespVO vo = convertToEntityFieldRespVO(field);
                    populateFieldRelatedData(field, vo);
                    return vo;
                })
                .toList();

        // 构建响应
        PageResult<EntityFieldRespVO> result = new PageResult<>();
        result.setTotal(pageResult.getTotal());
        result.setList(respList);
        return result;
    }

    @Override
    public List<MetadataEntityFieldDO> getEntityFieldList() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .orderBy(MetadataEntityFieldDO::getSortOrder, true)
                .orderBy(MetadataEntityFieldDO::getCreateTime, false);
        return metadataEntityFieldRepository.list(queryWrapper);
    }

    @Override
    public List<MetadataEntityFieldDO> getEntityFieldListByEntityId(String entityId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(MetadataEntityFieldDO::getEntityUuid, entityId.trim())
                .orderBy(MetadataEntityFieldDO::getSortOrder, true)
                .orderBy(MetadataEntityFieldDO::getCreateTime, false);
        return metadataEntityFieldRepository.list(queryWrapper);
    }

    /**
     * Long 重载实现，委派给 String 版本
     *
     * @param entityId 实体ID(Long)
     * @return 字段列表
     */
    public List<MetadataEntityFieldDO> getEntityFieldListByEntityId(Long entityId) {
        if (entityId == null) {
            return java.util.Collections.emptyList();
        }
        return getEntityFieldListByEntityId(String.valueOf(entityId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEntityFieldsByEntityId(String entityId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(MetadataEntityFieldDO::getEntityUuid, entityId.trim());
        List<MetadataEntityFieldDO> fields = metadataEntityFieldRepository.list(queryWrapper);

        // 获取业务实体信息，用于批量删除物理表字段
        MetadataBusinessEntityDO businessEntity = null;
        MetadataDatasourceDO datasource = null;
        try {
            businessEntity = metadataBusinessEntityCoreService.getBusinessEntityByUuid(entityId);
            if (businessEntity != null && businessEntity.getTableName() != null &&
                    !businessEntity.getTableName().trim().isEmpty()) {
                datasource = metadataDatasourceBuildService.getDatasourceByUuid(businessEntity.getDatasourceUuid());
            }
        } catch (Exception e) {
            log.error("获取业务实体信息失败: {}", e.getMessage(), e);
        }

        for (MetadataEntityFieldDO field : fields) {
            // 删除数据库记录
            fieldOptionService.deleteByFieldId(field.getFieldUuid());
            fieldConstraintService.deleteByFieldId(field.getFieldUuid());
            autoNumberConfigBuildService.deleteByFieldId(field.getFieldUuid());
            validationRequiredService.deleteByFieldId(field.getFieldUuid());
            validationUniqueService.deleteByFieldId(field.getFieldUuid());
            validationLengthService.deleteByFieldId(field.getFieldUuid());
            metadataEntityFieldRepository.removeById(field.getId());

            // 从物理表删除字段
            if (businessEntity != null && datasource != null) {
                try {
                    dropColumnFromTable(datasource, businessEntity.getTableName(), field.getFieldName());
                } catch (Exception e) {
                    log.error("从物理表删除字段 {} 失败: {}", field.getFieldName(), e.getMessage(), e);
                    // 不抛出异常，继续删除其他字段
                }
            }
        }
    }

    /**
     * 添加列到表
     * <p>
     * 使用 Anyline 原生 API 添加列，自动适配不同数据库（PostgreSQL、达梦、人大金仓等）。
     */
    private void addColumnToTable(MetadataDatasourceDO datasource, String tableName, MetadataEntityFieldDO field) {
        try {
            log.info("开始为表 {} 添加列 {}, 数据源: {} ({})",
                    tableName, field.getFieldName(),
                    datasource.getDatasourceName(), datasource.getDatasourceType());

            // 使用TenantUtils.executeIgnore包装操作，忽略租户条件
            TenantUtils.executeIgnore(() -> {
                // 创建 AnylineService 实例
                AnylineService<?> service = temporaryDatasourceService.createTemporaryService(datasource);

                // 首先检查表是否存在
                if (!AnylineDdlHelper.tableExists(service, tableName)) {
                    String errorMessage = "表 " + tableName + " 不存在，请先创建表。";
                    log.error("添加字段失败: {}", errorMessage);
                    throw new RuntimeException(errorMessage);
                }

                // 检查列是否已存在
                if (AnylineDdlHelper.columnExists(service, tableName, field.getFieldName())) {
                    log.warn("列 {} 已存在于表 {} 中，跳过添加操作", field.getFieldName(), tableName);
                    return null;
                }

                // 使用 Anyline 原生 API 构建 Column 对象
                String columnType = mapFieldType(field.getFieldType(), field.getDataLength());
                boolean nullable = field.getIsRequired() == null || !BooleanStatusEnum.isYes(field.getIsRequired());
                
                Column column = AnylineDdlHelper.buildColumn(
                        tableName,
                        field.getFieldName(),
                        columnType,
                        nullable,
                        formatDefaultValueForAnyline(field.getFieldType(), field.getDefaultValue()),
                        field.getDescription()
                );

                // 使用 Anyline 原生 API 添加列
                AnylineDdlHelper.addColumn(service, column);

                log.info("成功为表 {} 添加列: {}", tableName, field.getFieldName());
                return null;
            });
        } catch (Exception e) {
            log.error("为表 {} 添加列 {} 失败: {}", tableName, field.getFieldName(), e.getMessage(), e);
            throw new RuntimeException("添加列失败: " + e.getMessage(), e);
        }
    }

    /**
     * 格式化默认值用于 Anyline Column 设置
     * <p>
     * 直接返回原始值，不添加引号（Anyline 会自动处理）
     */
    private Object formatDefaultValueForAnyline(String fieldType, String defaultValue) {
        if (defaultValue == null || defaultValue.trim().isEmpty()) {
            return null;
        }
        // Anyline 会自动处理默认值的类型转换和引号，直接返回原始值
        return defaultValue;
    }

    /**
     * 检查表是否存在
     * <p>
     * 使用Anyline元数据API，自动适配不同数据库（PostgreSQL、达梦、金仓等）
     * 避免手动拼接SQL和硬编码LIMIT语法
     */
    private boolean checkTableExists(AnylineService<?> service, String tableName) {
        // 委托给 AnylineDdlHelper 处理
        return AnylineDdlHelper.tableExists(service, tableName);
    }

    /**
     * 获取当前连接的数据库名称（用于调试）
     */
    private String getCurrentDatabase(AnylineService<?> service) {
        try {
            DataSet resultSet = service.querys("SELECT current_database()");
            if (resultSet != null && resultSet.size() > 0) {
                DataRow row = resultSet.getRow(0);
                return row.get("current_database").toString();
            }
        } catch (Exception e) {
            log.debug("获取当前数据库名称失败: {}", e.getMessage());
        }
        return "unknown";
    }

    /**
     * 检查列是否存在于表中
     * <p>
     * 使用Anyline元数据API，自动适配不同数据库（PostgreSQL、达梦、金仓等）
     * 避免硬编码ILIKE、information_schema和LIMIT语法
     * <p>
     * 注意：PostgreSQL会将不带引号的标识符自动转为小写，因此在检查列是否存在时需要
     * 先尝试精确匹配，再尝试小写匹配，最后再进行忽略大小写的模糊匹配
     *
     * @param service    AnylineService实例
     * @param tableName  表名
     * @param columnName 列名
     * @return 如果列存在返回true，否则返回false
     */
    private boolean checkColumnExists(AnylineService<?> service, String tableName, String columnName) {
        // 委托给 AnylineDdlHelper 处理
        return AnylineDdlHelper.columnExists(service, tableName, columnName);
    }

    /**
     * 修改表中的列
     * <p>
     * 采用混合策略：
     * - 达梦(DM)数据库：使用 Anyline 原生 API
     * - PostgreSQL/KingBase：保留手动 DDL 方式，因需要 USING 子句处理类型转换
     * <p>
     * 说明：即使 field 有 ID，也需要检查物理表中列是否真实存在
     * 因为可能存在元数据与物理表不一致的情况（如之前物理表操作失败、表被手动重建等）
     *
     * @param datasource 数据源信息
     * @param tableName  表名
     * @param field      字段信息
     */
    private void alterColumnInTable(MetadataDatasourceDO datasource, String tableName, MetadataEntityFieldDO field) {
        try {
            TenantUtils.executeIgnore(() -> {
                AnylineService<?> service = temporaryDatasourceService.createTemporaryService(datasource);

                // 先校验表是否存在
                if (!AnylineDdlHelper.tableExists(service, tableName)) {
                    throw new RuntimeException("表 " + tableName + " 不存在，请先创建表");
                }

                // 检查列是否存在，避免元数据与物理表不一致导致的问题
                boolean columnExists = AnylineDdlHelper.columnExists(service, tableName, field.getFieldName());

                if (!columnExists) {
                    // 列不存在，应该使用ADD操作而非ALTER
                    log.warn("准备修改表 {} 的列 {} 时发现列不存在（字段ID: {}），将改为新增列操作",
                            tableName, field.getFieldName(), field.getId());
                    addColumnToTable(datasource, tableName, field);
                } else {
                    // 列存在，正常执行ALTER操作
                    log.info("准备修改表 {} 的列: {}, 字段ID: {}", tableName, field.getFieldName(), field.getId());

                    // 根据数据库类型选择不同的策略
                    String datasourceType = datasource.getDatasourceType();
                    DatabaseType dbType = DatabaseType.valueOf(datasourceType);

                    if (dbType == DatabaseType.DM) {
                        // 达梦数据库：使用 Anyline 原生 API
                        alterColumnWithAnyline(service, tableName, field);
                    } else {
                        // PostgreSQL/KingBase：使用手动 DDL（需要 USING 子句处理类型转换）
                        String alterColumnDDL = generateAlterColumnDDL(datasourceType, tableName, field);
                        AnylineDdlHelper.alterColumnWithDDL(service, tableName, field.getFieldName(), alterColumnDDL);
                    }

                    log.info("成功修改表 {} 的列: {}", tableName, field.getFieldName());
                }
                return null;
            });
        } catch (Exception e) {
            log.error("修改表 {} 的列 {} 失败: {}", tableName, field.getFieldName(), e.getMessage(), e);
            throw new RuntimeException("修改列失败", e);
        }
    }

    /**
     * 使用 Anyline 原生 API 修改列（适用于达梦数据库）
     *
     * @param service   Anyline 服务实例
     * @param tableName 表名
     * @param field     字段信息
     */
    private void alterColumnWithAnyline(AnylineService<?> service, String tableName, MetadataEntityFieldDO field) {
        // 将业务字段类型映射为数据库类型
        String dbTypeName = mapFieldType(field.getFieldType(), field.getDataLength());
        // isRequired=1 表示必填，对应 nullable=false
        boolean nullable = field.getIsRequired() == null || !BooleanStatusEnum.isYes(field.getIsRequired());

        // 构建 Anyline Column 对象
        Column column = AnylineDdlHelper.buildColumn(
                field.getFieldName(),
                dbTypeName,
                nullable,
                formatDefaultValueForAnyline(field.getFieldType(), field.getDefaultValue()),
                field.getDescription()
        );

        // 使用 Anyline 原生 API 修改列
        AnylineDdlHelper.alterColumn(service, tableName, column);
    }

    /**
     * 重命名表中的列
     * <p>
     * 使用 Anyline 原生 API 重命名列，自动适配不同数据库。
     */
    private void renameColumnInTable(MetadataDatasourceDO datasource, String tableName, String oldName,
            String newName) {
        try {
            TenantUtils.executeIgnore(() -> {
                AnylineService<?> service = temporaryDatasourceService.createTemporaryService(datasource);

                if (!AnylineDdlHelper.tableExists(service, tableName)) {
                    throw new RuntimeException("表 " + tableName + " 不存在，请先创建表");
                }

                // 使用 Anyline 原生 API 重命名列（内部会检查列是否存在）
                AnylineDdlHelper.renameColumn(service, tableName, oldName, newName);
                return null;
            });
        } catch (Exception e) {
            log.error("重命名表 {} 列 {} 到 {} 失败: {}", tableName, oldName, newName, e.getMessage(), e);
            throw new RuntimeException("重命名列失败", e);
        }
    }

    /**
     * 从表中删除列
     * <p>
     * 使用 Anyline 原生 API 删除列，自动适配不同数据库（PostgreSQL、达梦、人大金仓等）。
     */
    private void dropColumnFromTable(MetadataDatasourceDO datasource, String tableName, String fieldName) {
        try {
            // 使用TenantUtils.executeIgnore包装操作，忽略租户条件
            TenantUtils.executeIgnore(() -> {
                // 创建 AnylineService 实例
                AnylineService<?> service = temporaryDatasourceService.createTemporaryService(datasource);

                // 使用 Anyline 原生 API 删除列（内部会检查列是否存在）
                AnylineDdlHelper.dropColumn(service, tableName, fieldName);

                log.info("成功从表 {} 删除列: {}", tableName, fieldName);
                return null;
            });
        } catch (Exception e) {
            log.error("从表 {} 删除列 {} 失败: {}", tableName, fieldName, e.getMessage(), e);
            throw new RuntimeException("删除列失败", e);
        }
    }

    /**
     * 生成添加字段的DDL语句
     * <p>
     * 注意：调用此方法前必须先使用checkColumnExists()检查列是否存在
     * 不使用IF NOT EXISTS语法以兼容达梦等数据库
     */
    private String generateAddColumnDDL(String tableName, MetadataEntityFieldDO field) {
        StringBuilder ddl = new StringBuilder();
        // 不使用IF NOT EXISTS，因为已在addColumnToTable()中提前检查
        ddl.append("ALTER TABLE \"").append(tableName).append("\" ADD COLUMN \"")
                .append(field.getFieldName()).append("\" ");

        // 字段类型映射
        String columnType = mapFieldType(field.getFieldType(), field.getDataLength());
        ddl.append(columnType);

        // 是否必填 - 使用新的枚举值：1-是，0-否
        if (field.getIsRequired() != null && BooleanStatusEnum.isYes(field.getIsRequired())) {
            ddl.append(" NOT NULL");
        }

        // 默认值 - 根据字段类型正确格式化
        if (field.getDefaultValue() != null && !field.getDefaultValue().trim().isEmpty()) {
            String formattedValue = formatDefaultValue(field.getFieldType(), field.getDefaultValue());
            if (formattedValue != null) {
                ddl.append(" DEFAULT ").append(formattedValue);
            }
        }

        ddl.append(";");

        // 添加字段注释
        if (field.getDescription() != null && !field.getDescription().trim().isEmpty()) {
            ddl.append("\nCOMMENT ON COLUMN \"").append(tableName).append("\".\"")
                    .append(field.getFieldName()).append("\" IS '").append(field.getDescription()).append("';");
        }

        return ddl.toString();
    }

    /**
     * 生成修改字段的DDL语句（跨数据库兼容）
     * <p>
     * 根据不同数据库类型生成对应的ALTER COLUMN语法：
     * - PostgreSQL/KingBase: ALTER COLUMN ... TYPE / SET/DROP NOT NULL
     * - 达梦(DM): MODIFY "column" TYPE NOT NULL/NULL
     *
     * @param datasourceType 数据库类型
     * @param tableName      表名
     * @param field          字段信息
     * @return DDL语句
     */
    private String generateAlterColumnDDL(String datasourceType, String tableName, MetadataEntityFieldDO field) {
        StringBuilder ddl = new StringBuilder();
        String columnType = mapFieldType(field.getFieldType(), field.getDataLength());
        String fieldName = field.getFieldName();

        try {
            DatabaseType dbType = DatabaseType.valueOf(datasourceType);

            switch (dbType) {
                case PostgreSQL:
                case KingBase:
                    // PostgreSQL/金仓：需要分开修改类型和约束
                    // 1. 修改字段类型
                    ddl.append("ALTER TABLE \"").append(tableName)
                            .append("\" ALTER COLUMN \"").append(fieldName)
                            .append("\" TYPE ").append(columnType);

                    // 为需要类型转换的字段添加 USING 子句
                    String usingClause = generateUsingClause(field.getFieldType(), fieldName);
                    if (usingClause != null) {
                        ddl.append(" USING ").append(usingClause);
                    }

                    ddl.append(";\n");

                    // 2. 修改是否允许为空
                    if (field.getIsRequired() != null) {
                        ddl.append("ALTER TABLE \"").append(tableName)
                                .append("\" ALTER COLUMN \"").append(fieldName).append("\"");
                        if (BooleanStatusEnum.isYes(field.getIsRequired())) {
                            ddl.append(" SET NOT NULL;\n");
                        } else {
                            ddl.append(" DROP NOT NULL;\n");
                        }
                    }

                    // 3. 修改默认值 - 根据字段类型正确格式化
                    if (field.getDefaultValue() != null && !field.getDefaultValue().trim().isEmpty()) {
                        String formattedValue = formatDefaultValue(field.getFieldType(), field.getDefaultValue());
                        if (formattedValue != null) {
                            ddl.append("ALTER TABLE \"").append(tableName)
                                    .append("\" ALTER COLUMN \"").append(fieldName)
                                    .append("\" SET DEFAULT ").append(formattedValue).append(";\n");
                        }
                    }
                    break;

                case DM:
                    // 达梦：使用MODIFY，类型和约束一起修改
                    ddl.append("ALTER TABLE \"").append(tableName)
                            .append("\" MODIFY \"").append(fieldName)
                            .append("\" ").append(columnType);

                    // 添加约束
                    if (field.getIsRequired() != null) {
                        if (BooleanStatusEnum.isYes(field.getIsRequired())) {
                            ddl.append(" NOT NULL");
                        } else {
                            ddl.append(" NULL");
                        }
                    }

                    // 添加默认值 - 根据字段类型正确格式化
                    if (field.getDefaultValue() != null && !field.getDefaultValue().trim().isEmpty()) {
                        String formattedValue = formatDefaultValue(field.getFieldType(), field.getDefaultValue());
                        if (formattedValue != null) {
                            ddl.append(" DEFAULT ").append(formattedValue);
                        }
                    }

                    ddl.append(";\n");
                    break;

                default:
                    // 不支持的数据库类型，使用PostgreSQL语法作为默认
                    log.warn("不支持的数据库类型: {}，使用PostgreSQL语法", datasourceType);
                    ddl.append("ALTER TABLE \"").append(tableName)
                            .append("\" ALTER COLUMN \"").append(fieldName)
                            .append("\" TYPE ").append(columnType).append(";\n");
            }

            // 更新字段注释（所有支持的数据库都使用COMMENT ON语法）
            if (field.getDescription() != null && !field.getDescription().trim().isEmpty()) {
                ddl.append("COMMENT ON COLUMN \"").append(tableName).append("\".\"")
                        .append(fieldName).append("\" IS '").append(field.getDescription()).append("';");
            }

        } catch (IllegalArgumentException e) {
            // 数据库类型无效，使用PostgreSQL语法作为默认
            log.warn("无效的数据库类型: {}，使用PostgreSQL语法", datasourceType, e);
            ddl.append("ALTER TABLE \"").append(tableName)
                    .append("\" ALTER COLUMN \"").append(fieldName)
                    .append("\" TYPE ").append(columnType).append(";");
        }

        return ddl.toString();
    }

    /**
     * 生成PostgreSQL/KingBase的USING子句，用于类型转换
     * 使用CASE WHEN进行安全的类型转换，无效数据将被设为NULL
     * 
     * @param fieldType 目标字段类型
     * @param fieldName 字段名
     * @return USING子句，如果不需要则返回null
     */
    private String generateUsingClause(String fieldType, String fieldName) {
        if (fieldType == null) {
            return null;
        }

        String quotedFieldName = "\"" + fieldName + "\"";
        // 先将列转换为TEXT类型，这样可以处理从任何类型（包括已经是目标类型）的转换
        String textFieldName = quotedFieldName + "::text";

        // 需要显式类型转换的字段类型，使用CASE WHEN进行安全转换
        switch (fieldType.toUpperCase()) {
            case "DATETIME":
            case "TIMESTAMP":
                // VARCHAR/TEXT 转 TIMESTAMP：使用CASE WHEN处理无效数据
                // 正则表达式检查是否为有效的时间戳格式
                return "CASE WHEN " + textFieldName + " ~ '^\\\\d{4}-\\\\d{2}-\\\\d{2}' " +
                        "THEN " + textFieldName + "::timestamp " +
                        "ELSE NULL END";

            case "DATE":
                // VARCHAR/TEXT 转 DATE：使用CASE WHEN处理无效数据
                return "CASE WHEN " + textFieldName + " ~ '^\\\\d{4}-\\\\d{2}-\\\\d{2}' " +
                        "THEN " + textFieldName + "::date " +
                        "ELSE NULL END";

            case "NUMBER":
            case "NUMERIC":
            case "DECIMAL":
                // VARCHAR/TEXT 转 NUMERIC：使用CASE WHEN处理无效数据
                // 检查是否为数字格式（支持小数和负数）
                return "CASE WHEN " + textFieldName + " ~ '^-?\\\\d+\\\\.?\\\\d*$' " +
                        "THEN " + textFieldName + "::numeric " +
                        "ELSE NULL END";

            case "INTEGER":
            case "INT":
            case "BIGINT":
                // VARCHAR/TEXT 转 INTEGER：使用CASE WHEN处理无效数据
                // 检查是否为整数格式
                return "CASE WHEN " + textFieldName + " ~ '^-?\\\\d+$' " +
                        "THEN " + textFieldName + "::integer " +
                        "ELSE NULL END";

            case "BOOLEAN":
            case "BOOL":
                // VARCHAR/TEXT 转 BOOLEAN：使用CASE WHEN处理无效数据
                // 支持常见的布尔值表示
                return "CASE WHEN " + textFieldName
                        + " IN ('true', 'false', 't', 'f', '1', '0', 'yes', 'no', 'y', 'n') " +
                        "THEN " + textFieldName + "::boolean " +
                        "ELSE NULL END";

            default:
                // 其他类型通常可以自动转换，不需要USING子句
                return null;
        }
    }

    /**
     * 物理表操作封装类
     */
    @lombok.Data
    private static class PhysicalTableOperation {
        /**
         * 操作类型：ADD、ALTER、DROP、RENAME
         */
        private String operationType;

        /**
         * 字段名（用于ADD、ALTER、DROP操作）
         */
        private String fieldName;

        /**
         * 旧字段名（用于RENAME操作）
         */
        private String oldFieldName;

        /**
         * 字段信息（用于ADD、ALTER操作）
         */
        private MetadataEntityFieldDO fieldInfo;
    }

    /**
     * 统一执行物理表操作
     *
     * @param datasource 数据源信息
     * @param tableName  表名
     * @param operations 待执行的操作列表
     */
    private void executePhysicalTableOperations(MetadataDatasourceDO datasource, String tableName,
            List<PhysicalTableOperation> operations) {
        if (operations == null || operations.isEmpty()) {
            return;
        }

        log.info("开始批量执行物理表操作，表名: {}, 操作数量: {}", tableName, operations.size());

        for (PhysicalTableOperation op : operations) {
            try {
                switch (op.getOperationType()) {
                    case "DROP":
                        dropColumnFromTable(datasource, tableName, op.getFieldName());
                        break;
                    case "RENAME":
                        renameColumnInTable(datasource, tableName, op.getOldFieldName(), op.getFieldName());
                        break;
                    case "ALTER":
                        alterColumnInTable(datasource, tableName, op.getFieldInfo());
                        break;
                    case "ADD":
                        addColumnToTable(datasource, tableName, op.getFieldInfo());
                        break;
                    default:
                        log.warn("未知的物理表操作类型: {}", op.getOperationType());
                }
            } catch (Exception e) {
                log.error("执行物理表操作失败，操作类型: {}, 字段名: {}, 错误: {}",
                        op.getOperationType(), op.getFieldName(), e.getMessage(), e);
                throw new RuntimeException("物理表操作失败: " + e.getMessage(), e);
            }
        }

        log.info("批量执行物理表操作完成，表名: {}", tableName);
    }

    /**
     * 生成删除字段的DDL语句
     * <p>
     * 注意：调用此方法前必须先使用checkColumnExists()检查列是否存在
     * 不使用IF EXISTS语法以兼容达梦等数据库
     */
    private String generateDropColumnDDL(String tableName, String fieldName) {
        // 不使用IF EXISTS，因为已在dropColumnFromTable()中提前检查
        return "ALTER TABLE \"" + tableName + "\" DROP COLUMN \"" + fieldName + "\";";
    }

    /**
     * 字段类型映射
     */
    private String mapFieldType(String fieldType, Integer dataLength) {
        // 使用新的字段类型服务从MetadataComponentFieldTypeDO中读取映射关系
        return componentFieldTypeService.mapFieldTypeToDatabaseType(fieldType, dataLength);
    }

    /**
     * 格式化默认值用于SQL语句
     * 根据字段类型判断是否需要用单引号包裹默认值
     *
     * @param fieldType    字段类型
     * @param defaultValue 默认值
     * @return 格式化后的默认值
     */
    private String formatDefaultValue(String fieldType, String defaultValue) {
        if (defaultValue == null || defaultValue.trim().isEmpty()) {
            return null;
        }

        // 数值类型：不需要单引号
        // NUMBER, INTEGER, DECIMAL, FLOAT, DOUBLE, BIGINT, SMALLINT, TINYINT 等
        if (fieldType.contains("NUMBER") || fieldType.contains("INTEGER") ||
                fieldType.contains("DECIMAL") || fieldType.contains("FLOAT") ||
                fieldType.contains("DOUBLE") || fieldType.contains("BIGINT") ||
                fieldType.contains("SMALLINT") || fieldType.contains("TINYINT") ||
                fieldType.contains("BOOLEAN") || fieldType.contains("BOOL")) {
            return defaultValue;
        }

        // 特殊函数或表达式（如CURRENT_TIMESTAMP、NOW()等）：不需要单引号
        String upperValue = defaultValue.toUpperCase();
        if (upperValue.contains("CURRENT_") || upperValue.contains("NOW(") ||
                upperValue.contains("UUID") || upperValue.contains("NULL")) {
            return defaultValue;
        }

        // 如果已经包含单引号，直接返回
        if (defaultValue.startsWith("'") && defaultValue.endsWith("'")) {
            return defaultValue;
        }

        // 其他类型（TEXT, VARCHAR, CHAR, DATE, DATETIME, TIME, USER等）：需要单引号
        // 对单引号进行转义处理（PostgreSQL使用两个单引号表示一个单引号）
        String escapedValue = defaultValue.replace("'", "''");
        return "'" + escapedValue + "'";
    }

    /**
     * 生成字段编码
     * 将字段名转换为大写，下划线保持不变
     *
     * @param fieldName 字段名
     * @return 字段编码
     */
    private String generateFieldCode(String fieldName) {
        if (fieldName == null || fieldName.trim().isEmpty()) {
            return null;
        }
        return fieldName.toUpperCase();
    }

    /**
     * 校验实体是否允许修改表结构
     *
     * @param entityUuid 实体UUID
     */
    private void validateEntityAllowModifyStructure(String entityUuid) {
        // 获取业务实体信息
        MetadataBusinessEntityDO businessEntity = metadataBusinessEntityCoreService
                .getBusinessEntityByUuid(entityUuid);
        if (businessEntity == null) {
            throw exception(BUSINESS_ENTITY_NOT_EXISTS);
        }

        // 检查实体类型是否允许修改表结构
        if (!BusinessEntityTypeEnum.allowModifyTableStructure(businessEntity.getEntityType())) {
            BusinessEntityTypeEnum entityType = BusinessEntityTypeEnum.getByCode(businessEntity.getEntityType());
            String typeName = entityType != null ? entityType.getName() : "未知类型";
            throw new IllegalArgumentException(
                    String.format("实体类型为 %s (%s)，不允许修改表结构", typeName, businessEntity.getEntityType()));
        }
    }

    @Override
    public List<MetadataEntityFieldDO> findAllByConfig(QueryWrapper queryWrapper) {
        return metadataEntityFieldRepository.list(queryWrapper);
    }

    // ==================== 新增方法实现：处理包含自动编号的业务逻辑 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EntityFieldRespVO createEntityFieldWithRelated(@Valid EntityFieldSaveReqVO reqVO) {
        Long id = createEntityField(reqVO);
        MetadataEntityFieldDO entityField = getEntityField(String.valueOf(id));

        // 处理选项、约束和自动编号
        processFieldRelatedData(entityField.getFieldUuid(), entityField, reqVO.getOptions(), reqVO.getConstraints(), reqVO.getAutoNumber());

        // 手动转换并填充关联数据
        EntityFieldRespVO result = convertToEntityFieldRespVO(entityField);
        populateFieldRelatedData(entityField, result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateEntityFieldWithRelated(@Valid EntityFieldSaveReqVO reqVO) {
        updateEntityField(reqVO);

        if (reqVO.getId() != null) {
            Long fieldId = Long.valueOf(reqVO.getId());
            MetadataEntityFieldDO entityField = getEntityField(String.valueOf(fieldId));

            // 处理选项、约束和自动编号
            processFieldRelatedData(entityField.getFieldUuid(), entityField, reqVO.getOptions(), reqVO.getConstraints(),
                    reqVO.getAutoNumber());
        }

        return true;
    }

    @Override
    public List<EntityFieldRespVO> getEntityFieldListWithRelated(@Valid EntityFieldQueryReqVO reqVO) {
        EntityFieldQueryVO queryVO = modelMapper.map(reqVO, EntityFieldQueryVO.class);
        List<MetadataEntityFieldDO> list = getEntityFieldListByConditions(queryVO);
        List<EntityFieldRespVO> respList = list.stream()
                .map(this::convertToEntityFieldRespVO)
                .toList();

        // 为每个字段补充选项、约束和自动编号信息
        for (int i = 0; i < list.size(); i++) {
            MetadataEntityFieldDO f = list.get(i);
            EntityFieldRespVO v = respList.get(i);
            populateFieldRelatedData(f, v);
        }

        return respList;
    }

    @Override
    public EntityFieldDetailRespVO getEntityFieldDetailWithFullConfig(String id) {
        EntityFieldDetailRespVO result = getEntityFieldDetail(id);

        // 补充完整的自动编号配置信息
        MetadataEntityFieldDO field = metadataEntityFieldRepository.getById(Long.valueOf(id));
        if (field != null) {
            MetadataAutoNumberConfigDO config = autoNumberConfigBuildService.getByFieldId(field.getFieldUuid());
            if (config != null) {
                AutoNumberConfigRespVO autoNumberConfig = convertToAutoNumberConfigRespVO(config);
                // 获取规则项列表
                List<MetadataAutoNumberRuleItemDO> rules = autoNumberConfigBuildService.listRules(config.getId());
                List<AutoNumberRuleItemRespVO> ruleVOs = rules.stream()
                        .map(this::convertToAutoNumberRuleItemRespVO)
                        .toList();
                autoNumberConfig.setRuleItems(ruleVOs);
                result.setAutoNumberConfig(autoNumberConfig);
            }
        }

        return result;
    }

    /**
     * 处理字段相关数据（选项、约束、自动编号）
     */
    private void processFieldRelatedData(String fieldUuid, MetadataEntityFieldDO entityField,
            List<FieldOptionRespVO> options,
            FieldConstraintRespVO constraints,
            AutoNumberConfigReqVO autoNumber) {
        // 处理选项：仅当未关联字典类型时才处理自定义选项（智能更新版本）
        // 如果 dictTypeId 不为 null，说明复用系统字典，忽略 options 参数
        if (entityField != null && entityField.getDictTypeId() == null && options != null) {
            // 获取现有选项
            List<MetadataEntityFieldOptionDO> existingOptions = fieldOptionService.listByFieldId(fieldUuid);
            Map<Long, MetadataEntityFieldOptionDO> existingOptionsMap = existingOptions.stream()
                    .collect(
                            java.util.stream.Collectors.toMap(MetadataEntityFieldOptionDO::getId, o -> o, (a, b) -> a));

            // 创建 optionValue -> option 的映射，用于查找重复选项
            Map<String, MetadataEntityFieldOptionDO> existingOptionsByValue = existingOptions.stream()
                    .collect(java.util.stream.Collectors.toMap(
                            MetadataEntityFieldOptionDO::getOptionValue,
                            o -> o,
                            (a, b) -> a // 如果有重复值，保留第一个
                    ));

            // 用于标记已处理的选项ID
            Set<Long> processedOptionIds = new java.util.HashSet<>();

            for (var opt : options) {
                // 如果提供了ID，尝试更新现有选项
                if (opt.getId() != null && !opt.getId().trim().isEmpty()) {
                    try {
                        Long optionId = Long.valueOf(opt.getId());
                        MetadataEntityFieldOptionDO existing = existingOptionsMap.get(optionId);
                        if (existing != null) {
                            // 更新现有选项
                            MetadataEntityFieldOptionDO updateObj = new MetadataEntityFieldOptionDO();
                            updateObj.setId(optionId);
                            updateObj.setFieldUuid(fieldUuid);
                            updateObj.setOptionLabel(opt.getOptionLabel());
                            updateObj.setOptionValue(opt.getOptionValue());
                            updateObj.setOptionOrder(opt.getOptionOrder());
                            updateObj.setIsEnabled(opt.getIsEnabled());
                            updateObj.setDescription(opt.getDescription());
                            updateObj.setApplicationId(entityField.getApplicationId());
                            fieldOptionService.update(updateObj);
                            processedOptionIds.add(optionId);
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        log.warn("选项ID格式错误，将作为新增处理: {}", opt.getId());
                    }
                }

                // 智能处理：检查是否已存在相同 optionValue 的选项
                // 如果存在，则更新该选项；否则新增
                MetadataEntityFieldOptionDO existingByValue = existingOptionsByValue.get(opt.getOptionValue());
                if (existingByValue != null) {
                    // 找到相同 optionValue 的选项，更新它
                    log.info("发现已存在相同值的选项，自动转换为更新操作: fieldUuid={}, optionValue={}, existingId={}",
                            fieldUuid, opt.getOptionValue(), existingByValue.getId());
                    MetadataEntityFieldOptionDO updateObj = new MetadataEntityFieldOptionDO();
                    updateObj.setId(existingByValue.getId());
                    updateObj.setFieldUuid(fieldUuid);
                    updateObj.setOptionLabel(opt.getOptionLabel());
                    updateObj.setOptionValue(opt.getOptionValue());
                    updateObj.setOptionOrder(opt.getOptionOrder());
                    updateObj.setIsEnabled(opt.getIsEnabled());
                    updateObj.setDescription(opt.getDescription());
                    updateObj.setApplicationId(entityField.getApplicationId());
                    fieldOptionService.update(updateObj);
                    processedOptionIds.add(existingByValue.getId());
                } else {
                    // 确实是新选项，新增
                    MetadataEntityFieldOptionDO d = new MetadataEntityFieldOptionDO();
                    d.setFieldUuid(fieldUuid);
                    d.setOptionLabel(opt.getOptionLabel());
                    d.setOptionValue(opt.getOptionValue());
                    d.setOptionOrder(opt.getOptionOrder());
                    d.setIsEnabled(opt.getIsEnabled());
                    d.setDescription(opt.getDescription());
                    d.setApplicationId(entityField.getApplicationId());
                    fieldOptionService.create(d);
                    log.debug("新增字段选项: fieldUuid={}, optionValue={}", fieldUuid, opt.getOptionValue());
                }
            }

            // 删除未在请求中出现的旧选项
            for (MetadataEntityFieldOptionDO existingOption : existingOptions) {
                if (!processedOptionIds.contains(existingOption.getId())) {
                    fieldOptionService.deleteById(existingOption.getId());
                    log.info("删除未在请求中出现的字段选项: {}", existingOption.getId());
                }
            }
        } else if (entityField != null && entityField.getDictTypeId() != null) {
            // 如果切换到使用字典类型，删除原有的自定义选项
            fieldOptionService.deleteByFieldId(fieldUuid);
        }

        // 处理约束 - 修复：只有当 constraints 不为空对象时才执行删除和处理
        // 空对象判断：所有字段都为 null 时视为空对象，不应该删除现有约束
        if (constraints != null && !isConstraintsEmpty(constraints)) {
            fieldConstraintService.deleteByFieldId(fieldUuid);
            processFieldConstraints(fieldUuid, entityField, constraints);
        }

        // 处理自动编号（使用智能更新版本）
        if (autoNumber != null) {
            processAutoNumberConfig(fieldUuid, entityField, autoNumber);
        }
    }

    /**
     * 判断 FieldConstraintRespVO 是否为空对象
     * 
     * @param constraints 约束对象
     * @return true-空对象（所有字段都为null），false-有实际内容
     */
    private boolean isConstraintsEmpty(FieldConstraintRespVO constraints) {
        if (constraints == null) {
            return true;
        }
        // 检查所有字段是否都为 null
        return constraints.getLengthEnabled() == null
                && constraints.getMinLength() == null
                && constraints.getMaxLength() == null
                && constraints.getLengthPrompt() == null
                && constraints.getRegexEnabled() == null
                && constraints.getRegexPattern() == null
                && constraints.getRegexPrompt() == null;
    }

    /**
     * 处理字段约束
     * 
     * 注意：本方法仅处理字段约束(constraint)配置，不处理validation规则。
     * validation规则的同步由batchSaveEntityFields中的字段变更检测逻辑负责，避免重复处理。
     */
    private void processFieldConstraints(String fieldUuid, MetadataEntityFieldDO entityField,
            FieldConstraintRespVO constraints) {
        if (constraints.getMinLength() != null && constraints.getMaxLength() != null &&
                constraints.getMinLength() > constraints.getMaxLength()) {
            throw new IllegalArgumentException("最小长度不能大于最大长度");
        }

        // 长度
        boolean lengthEnabled = constraints.getLengthEnabled() != null
                && CommonStatusEnum.isEnabled(constraints.getLengthEnabled());
        boolean hasLengthRange = (constraints.getMinLength() != null && constraints.getMinLength() > 0)
                || (constraints.getMaxLength() != null && constraints.getMaxLength() > 0);
        boolean hasLengthPrompt = StringUtils.hasText(constraints.getLengthPrompt());
        boolean hasExplicitEnableFlag = constraints.getLengthEnabled() != null;
        if (lengthEnabled || (!hasExplicitEnableFlag && (hasLengthRange || hasLengthPrompt))) {
            FieldConstraintSaveReqVO req = new FieldConstraintSaveReqVO();
            req.setFieldUuid(fieldUuid);
            req.setConstraintType("LENGTH_RANGE");
            req.setMinLength(constraints.getMinLength());
            req.setMaxLength(constraints.getMaxLength());
            req.setPromptMessage(constraints.getLengthPrompt());
            Integer enabledValue = constraints.getLengthEnabled();
            if (enabledValue == null && (hasLengthRange || hasLengthPrompt)) {
                enabledValue = CommonStatusEnum.ENABLED.getStatus();
            }
            req.setIsEnabled(enabledValue);
            req.setVersionTag(entityField != null && entityField.getVersionTag() != null ? entityField.getVersionTag() : 0L);
            req.setApplicationId(entityField != null ? entityField.getApplicationId() : null);
            fieldConstraintService.saveFieldConstraintConfig(req);
        }

        // 正则 - 只有当正则表达式不为空且启用时才创建REGEX约束
        if (constraints.getRegexPattern() != null && !constraints.getRegexPattern().trim().isEmpty() &&
                constraints.getRegexEnabled() != null && constraints.getRegexEnabled() == 1) {
            FieldConstraintSaveReqVO req = new FieldConstraintSaveReqVO();
            req.setFieldUuid(fieldUuid);
            req.setConstraintType("REGEX");
            req.setRegexPattern(constraints.getRegexPattern());
            req.setPromptMessage(constraints.getRegexPrompt());
            req.setIsEnabled(constraints.getRegexEnabled());
            req.setVersionTag(entityField != null && entityField.getVersionTag() != null ? entityField.getVersionTag() : 0L);
            req.setApplicationId(entityField != null ? entityField.getApplicationId() : null);
            fieldConstraintService.saveFieldConstraintConfig(req);
        }

        // 必填（与 isRequired 联动）
        if (entityField != null && entityField.getIsRequired() != null) {
            // 只有当 isRequired = 1 时才创建约束配置，为0时删除已有配置
            if (entityField.getIsRequired() == 1) {
                // 原有的字段约束逻辑
                FieldConstraintSaveReqVO req = new FieldConstraintSaveReqVO();
                req.setFieldUuid(fieldUuid);
                req.setConstraintType("REQUIRED");
                req.setIsEnabled(entityField.getIsRequired());
                req.setPromptMessage(null);
                req.setVersionTag(entityField.getVersionTag() != null ? entityField.getVersionTag() : 0L);
                req.setApplicationId(entityField.getApplicationId());
                fieldConstraintService.saveFieldConstraintConfig(req);
            } else {
                // isRequired = 0 时删除已有的必填约束配置
                fieldConstraintService.delete(fieldUuid, "REQUIRED");
            }
        }

        // 唯一（与 isUnique 联动）
        if (entityField != null && entityField.getIsUnique() != null) {
            // 只有当 isUnique = 1 时才创建约束配置，为0时删除已有配置
            if (entityField.getIsUnique() == 1) {
                FieldConstraintSaveReqVO req = new FieldConstraintSaveReqVO();
                req.setFieldUuid(fieldUuid);
                req.setConstraintType("UNIQUE");
                req.setIsEnabled(entityField.getIsUnique());
                req.setPromptMessage(null);
                req.setVersionTag(entityField.getVersionTag() != null ? entityField.getVersionTag() : 0L);
                req.setApplicationId(entityField.getApplicationId());
                fieldConstraintService.saveFieldConstraintConfig(req);
            } else {
                // isUnique = 0 时删除已有的唯一性约束配置
                fieldConstraintService.delete(fieldUuid, "UNIQUE");
            }
        }
    }

    /**
     * 处理自动编号配置（智能更新版本）
     *
     * @param fieldId     字段ID
     * @param entityField 字段实体
     * @param autoNumber  自动编号配置
     */
    private void processAutoNumberConfig(String fieldUuid, MetadataEntityFieldDO entityField,
            AutoNumberConfigReqVO autoNumber) {
        // 获取现有配置
        MetadataAutoNumberConfigDO existingConfig = autoNumberConfigBuildService.getByFieldId(fieldUuid);

        // 使用新的枚举值：1-启用，0-禁用
        if (autoNumber.getIsEnabled() != null && CommonStatusEnum.isEnabled(autoNumber.getIsEnabled())) {
            // 构建配置对象
            MetadataAutoNumberConfigDO config = new MetadataAutoNumberConfigDO();
            if (existingConfig != null) {
                // 更新：保留原有ID
                config.setId(existingConfig.getId());
            }
            config.setFieldUuid(fieldUuid);
            config.setIsEnabled(autoNumber.getIsEnabled());
            config.setNumberMode(autoNumber.getNumberMode());
            config.setDigitWidth(autoNumber.getDigitWidth());
            config.setOverflowContinue(autoNumber.getOverflowContinue());
            config.setInitialValue(autoNumber.getInitialValue() != null ? autoNumber.getInitialValue() : 1L);
            config.setResetCycle(autoNumber.getResetCycle());
            config.setResetOnInitialChange(
                    autoNumber.getResetOnInitialChange() != null ? autoNumber.getResetOnInitialChange() : 0);
            config.setVersionTag(entityField != null && entityField.getVersionTag() != null ? entityField.getVersionTag() : 0L);
            config.setApplicationId(null); // AutoNumberConfig使用自己的applicationId管理

            Long configId = autoNumberConfigBuildService.upsert(config);
            // 获取配置的UUID用于后续查询
            MetadataAutoNumberConfigDO savedConfig = autoNumberConfigBuildService.getByFieldId(fieldUuid);
            String configUuid = savedConfig != null ? savedConfig.getConfigUuid() : null;

            // 处理规则项（智能更新：根据ID进行精确匹配更新）
            if (autoNumber.getRuleItems() != null && savedConfig != null) {
                // 获取现有规则项，按 ID 建立映射
                List<MetadataAutoNumberRuleItemDO> existingRules = autoNumberRuleBuildService.listByConfigId(savedConfig.getId());
                Map<Long, MetadataAutoNumberRuleItemDO> existingRulesMap = existingRules.stream()
                        .collect(java.util.stream.Collectors.toMap(
                                MetadataAutoNumberRuleItemDO::getId,
                                r -> r,
                                (a, b) -> a));

                // 用于标记已处理的规则项ID
                Set<Long> processedRuleIds = new java.util.HashSet<>();

                for (AutoNumberRuleItemReqVO ruleReq : autoNumber.getRuleItems()) {
                    // 如果提供了ID，尝试更新现有规则项
                    if (ruleReq.getId() != null) {
                        MetadataAutoNumberRuleItemDO existing = existingRulesMap.get(ruleReq.getId());
                        if (existing != null) {
                            // 更新现有规则项
                            MetadataAutoNumberRuleItemDO rule = new MetadataAutoNumberRuleItemDO();
                            rule.setId(ruleReq.getId());
                            rule.setConfigUuid(configUuid);
                            rule.setItemType(ruleReq.getItemType());
                            rule.setItemOrder(ruleReq.getItemOrder());
                            rule.setFormat(ruleReq.getFormat());

                            // 兼容性处理：TEXT类型的规则项支持从format字段获取文本值
                            String textValue = ruleReq.getTextValue();
                            if ("TEXT".equalsIgnoreCase(ruleReq.getItemType()) && textValue == null
                                    && ruleReq.getFormat() != null) {
                                textValue = ruleReq.getFormat();
                            }
                            rule.setTextValue(textValue);

                            // 兼容性处理：FIELD_REF类型的规则项支持从format字段获取引用字段ID
                            Long refFieldId = ruleReq.getRefFieldId();
                            if ("FIELD_REF".equalsIgnoreCase(ruleReq.getItemType()) && refFieldId == null
                                    && ruleReq.getFormat() != null) {
                                try {
                                    refFieldId = Long.parseLong(ruleReq.getFormat());
                                    log.info("FIELD_REF规则项从format字段解析出引用字段ID: {}", refFieldId);
                                } catch (NumberFormatException e) {
                                    log.warn("FIELD_REF规则项的format字段无法解析为字段ID: {}", ruleReq.getFormat());
                                }
                            }
                            rule.setRefFieldUuid(ruleReq.getRefFieldUuid());
                            rule.setIsEnabled(
                                    ruleReq.getIsEnabled() != null ? ruleReq.getIsEnabled() : StatusEnumUtil.ENABLED);
                            rule.setApplicationId(null); // RuleItem使用自己的applicationId管理

                            autoNumberRuleBuildService.update(rule);
                            processedRuleIds.add(ruleReq.getId());
                            log.info("更新自动编号规则项，id={}, configId={}, itemOrder={}",
                                    ruleReq.getId(), configId, ruleReq.getItemOrder());
                            continue;
                        } else {
                            log.warn("请求中的规则项ID不存在，将作为新增处理，id={}", ruleReq.getId());
                        }
                    }

                    // 新增规则项（未提供ID或ID不存在的情况）
                    MetadataAutoNumberRuleItemDO rule = new MetadataAutoNumberRuleItemDO();
                    rule.setConfigUuid(configUuid);
                    rule.setItemType(ruleReq.getItemType());
                    rule.setItemOrder(ruleReq.getItemOrder());
                    rule.setFormat(ruleReq.getFormat());

                    // 兼容性处理：TEXT类型的规则项支持从format字段获取文本值
                    String textValue = ruleReq.getTextValue();
                    if ("TEXT".equalsIgnoreCase(ruleReq.getItemType()) && textValue == null
                            && ruleReq.getFormat() != null) {
                        textValue = ruleReq.getFormat();
                    }
                    rule.setTextValue(textValue);

                    // 兼容性处理：FIELD_REF类型的规则项支持从format字段获取引用字段ID
                    Long refFieldId = ruleReq.getRefFieldId();
                    if ("FIELD_REF".equalsIgnoreCase(ruleReq.getItemType()) && refFieldId == null
                            && ruleReq.getFormat() != null) {
                        try {
                            refFieldId = Long.parseLong(ruleReq.getFormat());
                            log.info("FIELD_REF规则项从format字段解析出引用字段ID: {}", refFieldId);
                        } catch (NumberFormatException e) {
                            log.warn("FIELD_REF规则项的format字段无法解析为字段ID: {}", ruleReq.getFormat());
                        }
                    }
                    rule.setRefFieldUuid(ruleReq.getRefFieldUuid());
                    rule.setIsEnabled(ruleReq.getIsEnabled() != null ? ruleReq.getIsEnabled() : StatusEnumUtil.ENABLED);
                    rule.setApplicationId(null); // RuleItem使用自己的applicationId管理
                    rule.setConfigUuid(configUuid);

                    Long newRuleId = autoNumberRuleBuildService.add(rule);
                    log.info("新增自动编号规则项，id={}, configId={}, itemOrder={}",
                            newRuleId, configId, ruleReq.getItemOrder());
                }

                // 删除未在请求中出现的旧规则项
                for (MetadataAutoNumberRuleItemDO existingRule : existingRules) {
                    if (!processedRuleIds.contains(existingRule.getId())) {
                        autoNumberRuleBuildService.deleteById(existingRule.getId());
                        log.info("删除未在请求中出现的自动编号规则项，id={}, itemOrder={}",
                                existingRule.getId(), existingRule.getItemOrder());
                    }
                }
            }
        } else {
            // 禁用或删除配置
            if (existingConfig != null) {
                autoNumberConfigBuildService.deleteByFieldId(fieldUuid);
                log.info("删除字段 {} 的自动编号配置", fieldUuid);
            }
        }
    }

    /**
     * 填充字段相关数据到响应VO
     */
    private void populateFieldRelatedData(MetadataEntityFieldDO field, EntityFieldRespVO vo) {
        // 填充选项信息
        if ("SELECT".equalsIgnoreCase(field.getFieldType()) ||
                "SINGLE_SELECT".equalsIgnoreCase(field.getFieldType()) ||
                "MULTI_SELECT".equalsIgnoreCase(field.getFieldType()) ||
                "PICKLIST".equalsIgnoreCase(field.getFieldType()) ||
                "DATA_SELECTION".equalsIgnoreCase(field.getFieldType()) ||
                "MULTI_USER".equalsIgnoreCase(field.getFieldType()) ||
                "MULTI_DEPARTMENT".equalsIgnoreCase(field.getFieldType()) ||
                "MULTI_DATA_SELECTION".equalsIgnoreCase(field.getFieldType())) {
            var options = fieldOptionService.listByFieldId(field.getFieldUuid());
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
                vo.setOptions(optionVOs);
            }
        }

        // 填充约束信息（使用新表）
        FieldConstraintRespVO constraintVO = fieldConstraintService.getFieldConstraintConfig(field.getFieldUuid());
        if (constraintVO != null) {
            vo.setConstraints(constraintVO);
        }

        // 填充自动编号完整配置（规则项）
        MetadataAutoNumberConfigDO config = autoNumberConfigBuildService.getByFieldId(field.getFieldUuid());
        if (config != null) {
            // 完整配置
            AutoNumberConfigRespVO full = convertToAutoNumberConfigRespVO(config);
            // 规则项
            List<MetadataAutoNumberRuleItemDO> rules = autoNumberConfigBuildService.listRules(config.getId());
            List<AutoNumberRuleItemRespVO> ruleVOs = rules.stream()
                    .map(this::convertToAutoNumberRuleItemRespVO)
                    .toList();
            full.setRuleItems(ruleVOs);
            vo.setAutoNumberConfig(full);
        }

        // 填充数据选择配置
        if ("DATA_SELECTION".equalsIgnoreCase(field.getFieldType())) {
            DataSelectionConfig dataSelectionConfig = buildDataSelectionConfig(field);
            if (dataSelectionConfig != null) {
                vo.setDataSelectionConfig(dataSelectionConfig);
            }
        }
    }

    /**
     * 查询并构建数据选择配置
     *
     * @param field 字段
     * @return 数据选择配置
     */
    private DataSelectionConfig buildDataSelectionConfig(MetadataEntityFieldDO field) {
        if (field == null || field.getId() == null || field.getEntityUuid() == null) {
            return null;
        }

        // 使用 findBySourceEntityUuidAndTargetEntityUuid 方法查询关系
        List<MetadataEntityRelationshipDO> relationships = metadataEntityRelationshipBuildService
                .findBySourceEntityUuidAndTargetEntityUuid(null, field.getEntityUuid());
        
        // 过滤出 targetFieldId 匹配的关系
        MetadataEntityRelationshipDO relationship = null;
        if (relationships != null) {
            for (MetadataEntityRelationshipDO rel : relationships) {
                if (rel.getTargetFieldUuid() != null && rel.getTargetFieldUuid().equals(field.getFieldUuid())) {
                    relationship = rel;
                    break;
                }
            }
        }
        
        if (relationship == null || relationship.getSourceEntityUuid() == null || relationship.getSourceFieldUuid() == null) {
            return null;
        }

        DataSelectionConfig dataSelectionConfig = new DataSelectionConfig();
        dataSelectionConfig.setRelationId(relationship.getId());
        dataSelectionConfig.setTargetEntityUuid(relationship.getSourceEntityUuid());
        dataSelectionConfig.setTargetFieldUuid(relationship.getSourceFieldUuid());
        return dataSelectionConfig;
    }


    /**
     * 转换自动编号配置DO为响应VO
     */
    private AutoNumberConfigRespVO convertToAutoNumberConfigRespVO(MetadataAutoNumberConfigDO config) {
        AutoNumberConfigRespVO vo = new AutoNumberConfigRespVO();
        vo.setId(config.getId());
        vo.setFieldUuid(config.getFieldUuid());
        vo.setIsEnabled(config.getIsEnabled());
        vo.setNumberMode(config.getNumberMode());
        vo.setDigitWidth(config.getDigitWidth());
        vo.setOverflowContinue(config.getOverflowContinue());
        vo.setInitialValue(config.getInitialValue());
        vo.setResetCycle(config.getResetCycle());
        vo.setResetOnInitialChange(config.getResetOnInitialChange());
        vo.setVersionTag(config.getVersionTag());
        vo.setApplicationId(config.getApplicationId());
        vo.setCreateTime(config.getCreateTime());
        vo.setUpdateTime(config.getUpdateTime());
        return vo;
    }

    /**
     * 转换自动编号规则项DO为响应VO
     */
    private AutoNumberRuleItemRespVO convertToAutoNumberRuleItemRespVO(MetadataAutoNumberRuleItemDO rule) {
        AutoNumberRuleItemRespVO vo = new AutoNumberRuleItemRespVO();
        vo.setId(rule.getId());
        vo.setConfigUuid(rule.getConfigUuid());
        vo.setItemType(rule.getItemType());
        vo.setItemOrder(rule.getItemOrder());
        vo.setFormat(rule.getFormat());
        vo.setTextValue(rule.getTextValue());
        vo.setRefFieldUuid(rule.getRefFieldUuid());
        vo.setIsEnabled(rule.getIsEnabled());
        vo.setApplicationId(rule.getApplicationId());
        vo.setCreateTime(rule.getCreateTime());
        vo.setUpdateTime(rule.getUpdateTime());
        return vo;
    }

    /**
     * 手动转换MetadataEntityFieldDO为EntityFieldRespVO
     * 避免ModelMapper的复杂嵌套对象映射冲突
     */
    private EntityFieldRespVO convertToEntityFieldRespVO(MetadataEntityFieldDO field) {
        EntityFieldRespVO vo = new EntityFieldRespVO();
        vo.setId(field.getId() != null ? String.valueOf(field.getId()) : null);
        vo.setEntityUuid(field.getEntityUuid());
        vo.setFieldUuid(field.getFieldUuid());
        vo.setFieldName(field.getFieldName());
        vo.setDisplayName(field.getDisplayName());
        vo.setFieldType(field.getFieldType());
        vo.setDataLength(field.getDataLength());
        vo.setDecimalPlaces(field.getDecimalPlaces());
        vo.setDefaultValue(field.getDefaultValue());
        vo.setDescription(field.getDescription());
        vo.setIsSystemField(field.getIsSystemField());
        vo.setIsPrimaryKey(field.getIsPrimaryKey());
        vo.setIsRequired(field.getIsRequired());
        vo.setIsUnique(field.getIsUnique());
        vo.setSortOrder(field.getSortOrder());
        vo.setValidationRulesId(field.getValidationRules());
        vo.setVersionTag(field.getVersionTag());
        vo.setApplicationId(field.getApplicationId());
        vo.setStatus(field.getStatus());
        vo.setFieldCode(field.getFieldCode());
        vo.setDictTypeId(field.getDictTypeId());
        // 注意：options、constraints、autoNumberConfig 将在 populateFieldRelatedData 中填充
        return vo;
    }

    /**
     * 处理长度校验，同步到 MetadataValidationLengthDO
     * 根据TODO需求：数据长度除了在MetadataEntityFieldDO中存储相关的信息，还需要在MetadataValidationLengthDO也储存一份
     * MetadataEntityFieldDO
     * 只存最大程度，如果MetadataValidationLengthDO已经有数据了，那么只需保证maxLength和
     * MetadataEntityFieldDO中dataLength一致即可
     * 如果没有数据，那么新增一条记录，新增的时候MetadataValidationRuleGroupDO和MetadataValidationUniqueDO都需要新增数据
     * rg_name可以用display_name+field_name+长度进行拼接，然后同一个字段只能有一个唯一校验
     */
    private void processLengthValidation(Long fieldId, MetadataEntityFieldDO entityField) {
        try {
            // 检查是否已存在长度校验规则
            // 优先使用 getByFieldId 获取任何存在的记录，避免因为缺少 RgName 导致重复创建
            var existingDO = validationLengthService.getByFieldId(entityField.getFieldUuid());

            if (entityField.getDataLength() != null && entityField.getDataLength() > 0) {
                // 需要同步长度校验
                if (existingDO != null) {
                    // 如果已经有数据了，那么只需保证maxLength和 MetadataEntityFieldDO中dataLength一致即可
                    var updateReqVO = new com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationLengthUpdateReqVO();
                    String targetGroupUuid = existingDO.getGroupUuid();

                    if (targetGroupUuid == null) {
                        log.warn("长度校验同步失败，字段ID: {}, 缺少规则组UUID，跳过更新", fieldId);
                        return;
                    }
                    // 根据groupUuid获取规则组的数据库主键ID
                    var group = validationRuleGroupService.getValidationRuleGroupByUuid(targetGroupUuid);
                    if (group == null) {
                        log.warn("长度校验同步失败，字段ID: {}, 规则组不存在，跳过更新", fieldId);
                        return;
                    }
                    updateReqVO.setId(group.getId());
                    updateReqVO.setMaxLength(entityField.getDataLength()); // 最大长度与dataLength保持一致
                    updateReqVO.setMinLength(existingDO.getMinLength()); // 保持原有最小长度
                    updateReqVO.setIsEnabled(1); // 启用长度校验
                    updateReqVO.setPopPrompt(existingDO.getPromptMessage()); // 保持原有提示信息
                    updateReqVO.setTrimBefore(existingDO.getTrimBefore()); // 保持原有设置
                    updateReqVO.setVersionTag(entityField.getVersionTag() != null ? entityField.getVersionTag() : 0L);
                    validationLengthService.update(updateReqVO);
                } else {
                    // 如果没有数据，那么新增一条记录
                    var saveReqVO = new com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationLengthSaveReqVO();
                    saveReqVO.setEntityUuid(entityField.getEntityUuid());
                    saveReqVO.setFieldUuid(entityField.getFieldUuid());
                    saveReqVO.setMaxLength(entityField.getDataLength()); // 最大长度与dataLength保持一致
                    saveReqVO.setMinLength(null); // 最小长度默认为null，允许为空
                    saveReqVO.setIsEnabled(1); // 启用长度校验

                    // 使用统一的规则组命名方法
                    String rgName = buildLengthRuleGroupName(fieldId);
                    saveReqVO.setRgName(rgName);

                    // 生成默认提示语：{字段展示名称}长度不能超过X个字符
                    String fieldDisplayName = entityField.getDisplayName() != null
                            && !entityField.getDisplayName().trim().isEmpty()
                                    ? entityField.getDisplayName()
                                    : (entityField.getFieldName() != null ? entityField.getFieldName() : "字段");
                    String promptMsg = String.format("%s长度不能超过%d个字符", fieldDisplayName, entityField.getDataLength());
                    saveReqVO.setPromptMessage(promptMsg);
                    // 设置popPrompt确保errorMessage字段能正确返回
                    saveReqVO.setPopPrompt(promptMsg);
                    saveReqVO.setVersionTag(entityField.getVersionTag() != null ? entityField.getVersionTag() : 0L);

                    validationLengthService.create(saveReqVO);
                }
            } else {
                // 不需要长度校验，如果存在则删除
                if (existingDO != null) {
                    validationLengthService.deleteByFieldId(entityField.getFieldUuid());
                }
            }
        } catch (Exception e) {
            // 记录错误但不影响主流程
            log.warn("处理长度校验时发生异常，字段ID: {}, 错误: {}", fieldId, e.getMessage(), e);
        }
    }

    /**
     * 处理必填校验，同步到 MetadataValidationRequiredDO
     * 根据TODO需求：除了在MetadataEntityFieldDO中存储相关的信息，还需要在MetadataValidationRequiredDO也储存一份
     */
    private void processRequiredValidation(Long fieldId, MetadataEntityFieldDO entityField) {
        try {
            // 检查是否已存在必填校验规则
            // 优先使用 getByFieldId 获取任何存在的记录，避免因为缺少 RgName 导致重复创建
            var existingDO = validationRequiredService.getByFieldId(entityField.getFieldUuid());

            if (entityField.getIsRequired() != null && entityField.getIsRequired() == 1) {
                // 需要启用必填校验
                if (existingDO != null) {
                    // 如果已经有数据了，那么只需保证is_enabled和 MetadataEntityFieldDO中isRequired一致即可
                    var updateReqVO = new com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRequiredUpdateReqVO();
                    String targetGroupUuid = existingDO.getGroupUuid();

                    if (targetGroupUuid == null) {
                        log.warn("必填校验同步失败，字段ID: {}, 缺少规则组UUID，跳过更新", fieldId);
                        return;
                    }
                    // 根据groupUuid获取规则组的数据库主键ID
                    var group = validationRuleGroupService.getValidationRuleGroupByUuid(targetGroupUuid);
                    if (group == null) {
                        log.warn("必填校验同步失败，字段ID: {}, 规则组不存在，跳过更新", fieldId);
                        return;
                    }
                    updateReqVO.setId(group.getId());
                    updateReqVO.setIsEnabled(entityField.getIsRequired());
                    updateReqVO.setPopPrompt(existingDO.getPromptMessage()); // 保持原有提示信息
                    validationRequiredService.update(updateReqVO);
                } else {
                    // 如果没有数据，那么新增一条记录
                    var saveReqVO = new com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRequiredSaveReqVO();
                    saveReqVO.setEntityUuid(entityField.getEntityUuid());
                    saveReqVO.setFieldUuid(entityField.getFieldUuid());
                    saveReqVO.setIsEnabled(entityField.getIsRequired());

                    // rg_name可以用display_name+field_name+必填校验进行拼接
                    String rgName = buildRequiredRuleGroupName(fieldId);
                    saveReqVO.setRgName(rgName);

                    // 生成默认提示语：{字段展示名称}为必填项
                    String fieldDisplayName = entityField.getDisplayName() != null
                            && !entityField.getDisplayName().trim().isEmpty()
                                    ? entityField.getDisplayName()
                                    : (entityField.getFieldName() != null ? entityField.getFieldName() : "此字段");
                    String promptMsg = fieldDisplayName + "为必填项";
                    saveReqVO.setPromptMessage(promptMsg);
                    // 设置popPrompt确保errorMessage字段能正确返回
                    saveReqVO.setPopPrompt(promptMsg);

                    validationRequiredService.create(saveReqVO);
                }
            } else {
                // 不需要必填校验，如果存在则删除
                if (existingDO != null) {
                    validationRequiredService.deleteByFieldId(entityField.getFieldUuid());
                }
            }
        } catch (Exception e) {
            // 记录错误但不影响主流程
            log.warn("处理必填校验时发生异常，字段ID: {}, 错误: {}", fieldId, e.getMessage(), e);
        }
    }

    /**
     * 处理唯一性校验，同步到 MetadataValidationUniqueDO
     * 除了在MetadataEntityFieldDO中存储相关的信息，还需要在MetadataValidationUniqueDO也储存一份
     */
    private void processUniqueValidation(Long fieldId, MetadataEntityFieldDO entityField) {
        try {
            // 检查是否已存在唯一性校验规则
            // 优先使用 getByFieldId 获取任何存在的记录，避免因为缺少 RgName 导致重复创建
            var existingDO = validationUniqueService.getByFieldId(entityField.getFieldUuid());

            if (entityField.getIsUnique() != null && entityField.getIsUnique() == 1) {
                // 需要启用唯一性校验
                if (existingDO != null) {
                    // 如果已经有数据了，那么只需保证is_enabled和 MetadataEntityFieldDO中isUnique一致即可
                    var updateReqVO = new com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationUniqueUpdateReqVO();
                    String targetGroupUuid = existingDO.getGroupUuid();

                    if (targetGroupUuid == null) {
                        log.warn("唯一校验同步失败，字段ID: {}, 缺少规则组UUID，跳过更新", fieldId);
                        return;
                    }
                    // 根据groupUuid获取规则组的数据库主键ID
                    var group = validationRuleGroupService.getValidationRuleGroupByUuid(targetGroupUuid);
                    if (group == null) {
                        log.warn("唯一校验同步失败，字段ID: {}, 规则组不存在，跳过更新", fieldId);
                        return;
                    }
                    updateReqVO.setId(group.getId());
                    updateReqVO.setIsEnabled(entityField.getIsUnique());
                    updateReqVO.setPopPrompt(existingDO.getPromptMessage()); // 保持原有提示信息
                    validationUniqueService.update(updateReqVO);
                } else {
                    // 如果没有数据，那么新增一条记录
                    var saveReqVO = new com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationUniqueSaveReqVO();
                    saveReqVO.setEntityUuid(entityField.getEntityUuid());
                    saveReqVO.setFieldUuid(entityField.getFieldUuid());
                    saveReqVO.setIsEnabled(entityField.getIsUnique());

                    // rg_name可以用display_name+field_name+唯一校验进行拼接
                    String rgName = buildUniqueRuleGroupName(fieldId);
                    saveReqVO.setRgName(rgName);

                    // 生成默认提示语：{字段展示名称}必须唯一
                    String fieldDisplayName = entityField.getDisplayName() != null
                            && !entityField.getDisplayName().trim().isEmpty()
                                    ? entityField.getDisplayName()
                                    : (entityField.getFieldName() != null ? entityField.getFieldName() : "此字段");
                    String promptMsg = fieldDisplayName + "必须唯一";
                    saveReqVO.setPromptMessage(promptMsg);
                    // 设置popPrompt确保errorMessage字段能正确返回
                    saveReqVO.setPopPrompt(promptMsg);

                    validationUniqueService.create(saveReqVO);
                }
            } else {
                // 不需要唯一性校验，如果存在则删除
                if (existingDO != null) {
                    validationUniqueService.deleteByFieldId(entityField.getFieldUuid());
                }
            }
        } catch (Exception e) {
            // 记录错误但不影响主流程
            log.warn("处理唯一性校验时发生异常，字段ID: {}, 错误: {}", fieldId, e.getMessage(), e);
        }
    }

    /**
     * 构建规则组名称
     * 格式：校验类型-字段展示名称-实体展示名称
     * 例如：必填校验-姓名-学生信息表
     *
     * @param fieldId        字段ID
     * @param validationType 校验类型（REQUIRED/UNIQUE/LENGTH/RANGE/FORMAT/CHILD_NOT_EMPTY/SELF_DEFINED）
     * @return 规则组名称
     */
    private String buildRuleGroupName(Long fieldId, String validationType) {
        try {
            // 获取字段信息
            MetadataEntityFieldDO field = metadataEntityFieldRepository.getById(fieldId);
            if (field == null) {
                log.warn("构建规则组名称失败，字段不存在: fieldId={}", fieldId);
                return getValidationTypeName(validationType) + "-未知字段-未知实体";
            }

            // 获取实体信息
            MetadataBusinessEntityDO entity = metadataBusinessEntityCoreService.getBusinessEntityByUuid(field.getEntityUuid());

            // 字段展示名称，优先使用displayName，如果为空则使用fieldName
            String fieldDisplayName = field.getDisplayName() != null && !field.getDisplayName().trim().isEmpty()
                    ? field.getDisplayName()
                    : (field.getFieldName() != null ? field.getFieldName() : "未知字段");

            // 实体展示名称，优先使用displayName，如果为空则使用tableName
            String entityDisplayName = "未知实体";
            if (entity != null) {
                entityDisplayName = entity.getDisplayName() != null && !entity.getDisplayName().trim().isEmpty()
                        ? entity.getDisplayName()
                        : (entity.getTableName() != null ? entity.getTableName() : "未知实体");
            }

            // 校验类型中文名称
            String validationTypeName = getValidationTypeName(validationType);

            // 拼接成最终的规则组名称
            return String.format("%s-%s-%s", validationTypeName, fieldDisplayName, entityDisplayName);
        } catch (Exception e) {
            log.error("构建规则组名称时发生异常，字段ID: {}, 校验类型: {}, 错误: {}", fieldId, validationType, e.getMessage(), e);
            return getValidationTypeName(validationType) + "-未知字段-未知实体";
        }
    }

    /**
     * 获取校验类型的中文名称
     *
     * @param validationType 校验类型英文标识
     * @return 校验类型中文名称
     */
    private String getValidationTypeName(String validationType) {
        if (validationType == null) {
            return "未知校验";
        }

        switch (validationType.toUpperCase()) {
            case "REQUIRED":
                return "必填校验";
            case "UNIQUE":
                return "唯一校验";
            case "LENGTH":
            case "LENGTH_RANGE":
                return "长度校验";
            case "RANGE":
                return "范围校验";
            case "FORMAT":
            case "REGEX":
                return "格式校验";
            case "CHILD_NOT_EMPTY":
                return "子表空行校验";
            case "SELF_DEFINED":
            case "CUSTOM":
                return "自定义校验";
            default:
                return validationType + "校验";
        }
    }

    private String buildRequiredRuleGroupName(Long fieldId) {
        return buildRuleGroupName(fieldId, "REQUIRED");
    }

    private String buildUniqueRuleGroupName(Long fieldId) {
        return buildRuleGroupName(fieldId, "UNIQUE");
    }

    private String buildLengthRuleGroupName(Long fieldId) {
        return buildRuleGroupName(fieldId, "LENGTH");
    }

    private String buildRangeRuleGroupName(Long fieldId) {
        return buildRuleGroupName(fieldId, "RANGE");
    }

    private String buildFormatRuleGroupName(Long fieldId) {
        return buildRuleGroupName(fieldId, "FORMAT");
    }

    private String buildChildNotEmptyRuleGroupName(Long fieldId) {
        return buildRuleGroupName(fieldId, "CHILD_NOT_EMPTY");
    }

    private String buildSelfDefinedRuleGroupName(Long fieldId) {
        return buildRuleGroupName(fieldId, "SELF_DEFINED");
    }

    /**
     * 校验批量提交的字段中是否有重复的字段名
     * 
     * @param items 待保存的字段列表
     * @throws IllegalArgumentException 如果存在重复的字段名
     */
    private void validateFieldNameDuplicationInBatch(List<EntityFieldUpsertItemVO> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        // 统计非删除状态的字段名
        Map<String, Integer> fieldNameCountMap = new HashMap<>();
        List<String> duplicateFieldNames = new ArrayList<>();

        for (EntityFieldUpsertItemVO item : items) {
            // 跳过删除的字段
            if (Boolean.TRUE.equals(item.getIsDeleted())) {
                continue;
            }

            String fieldName = item.getFieldName();
            if (fieldName == null || fieldName.trim().isEmpty()) {
                continue;
            }

            // 校验字段名不能与系统保留字段冲突
            validateFieldNameNotSystemReserved(fieldName);

            // 统计字段名出现次数
            fieldNameCountMap.put(fieldName, fieldNameCountMap.getOrDefault(fieldName, 0) + 1);

            // 如果出现次数大于1，说明重复了
            if (fieldNameCountMap.get(fieldName) > 1 && !duplicateFieldNames.contains(fieldName)) {
                duplicateFieldNames.add(fieldName);
            }
        }

        // 如果有重复的字段名，抛出异常
        if (!duplicateFieldNames.isEmpty()) {
            String duplicateNames = String.join("、", duplicateFieldNames);
            throw new IllegalArgumentException("字段名称重复，同一个实体内字段名称必须唯一：" + duplicateNames);
        }
    }

    /**
     * 校验字段的校验规则唯一性
     * 规则：同一实体的同一字段的同一种校验类型，只能存在一条生效的校验规则
     * 
     * @param fieldUuid  字段UUID
     * @param entityUuid 实体UUID
     * @throws IllegalArgumentException 如果校验规则违反唯一性约束
     */
    private void validateValidationRuleUniqueness(String fieldUuid, String entityUuid) {
        if (fieldUuid == null || entityUuid == null) {
            return;
        }

        // 获取字段信息用于错误提示
        MetadataEntityFieldDO field = metadataEntityFieldRepository.getByFieldUuid(fieldUuid);
        String fieldDisplayName = field != null && field.getDisplayName() != null
                ? field.getDisplayName()
                : (field != null && field.getFieldName() != null ? field.getFieldName() : "未知字段");

        // 检查必填校验规则
        List<com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRequiredDO> requiredList = validationRequiredRepository
                .findByFieldUuid(fieldUuid);
        long enabledRequiredCount = requiredList.stream()
                .filter(r -> r.getIsEnabled() != null && r.getIsEnabled() == 1)
                .count();
        if (enabledRequiredCount > 1) {
            throw new IllegalArgumentException(String.format(
                    "字段【%s】存在多条生效的必填校验规则，同一字段的同一种校验类型只能有一条生效规则",
                    fieldDisplayName));
        }

        // 检查唯一性校验规则
        List<com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationUniqueDO> uniqueList = validationUniqueRepository
                .findByFieldUuid(fieldUuid);
        long enabledUniqueCount = uniqueList.stream()
                .filter(u -> u.getIsEnabled() != null && u.getIsEnabled() == 1)
                .count();
        if (enabledUniqueCount > 1) {
            throw new IllegalArgumentException(String.format(
                    "字段【%s】存在多条生效的唯一性校验规则，同一字段的同一种校验类型只能有一条生效规则",
                    fieldDisplayName));
        }

        // 检查长度校验规则
        List<com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationLengthDO> lengthList = validationLengthRepository
                .findByFieldUuid(fieldUuid);
        long enabledLengthCount = lengthList.stream()
                .filter(l -> l.getIsEnabled() != null && l.getIsEnabled() == 1)
                .count();
        if (enabledLengthCount > 1) {
            throw new IllegalArgumentException(String.format(
                    "字段【%s】存在多条生效的长度校验规则，同一字段的同一种校验类型只能有一条生效规则",
                    fieldDisplayName));
        }

        // 检查格式校验规则
        List<com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationFormatDO> formatList = validationFormatRepository
                .findByFieldUuid(fieldUuid);
        long enabledFormatCount = formatList.stream()
                .filter(f -> f.getIsEnabled() != null && f.getIsEnabled() == 1)
                .count();
        if (enabledFormatCount > 1) {
            throw new IllegalArgumentException(String.format(
                    "字段【%s】存在多条生效的格式校验规则，同一字段的同一种校验类型只能有一条生效规则",
                    fieldDisplayName));
        }

        log.debug("字段【{}】(UUID: {})的校验规则唯一性检查通过", fieldDisplayName, fieldUuid);
    }

}
