package com.cmsr.onebase.module.metadata.build.service.entity;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.*;
import com.cmsr.onebase.module.metadata.build.service.entity.vo.EntityFieldQueryVO;
import com.cmsr.onebase.module.metadata.build.service.number.AutoNumberRuleBuildService;
import com.cmsr.onebase.module.metadata.build.service.component.MetadataComponentFieldTypeBuildService;
import com.cmsr.onebase.module.metadata.build.service.relationship.MetadataEntityRelationshipBuildService;
import com.cmsr.onebase.module.metadata.build.service.validation.MetadataPermitRefOtftBuildService;
import com.cmsr.onebase.module.metadata.build.service.validation.MetadataValidationTypeBuildService;
import com.cmsr.onebase.module.metadata.build.service.validation.MetadataValidationRequiredBuildService;
import com.cmsr.onebase.module.metadata.build.service.validation.MetadataValidationUniqueBuildService;
import com.cmsr.onebase.module.metadata.build.service.validation.MetadataValidationLengthBuildService;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.field.MetadataEntityFieldOptionDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataPermitRefOtftDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationTypeDO;
import com.cmsr.onebase.module.metadata.core.util.StatusEnumUtil;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
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
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.anyline.entity.Compare;
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
            "parent_id"
    );

    @Resource
    private MetadataEntityFieldRepository metadataEntityFieldRepository;
    @Resource
    private MetadataEntityRelationshipBuildService metadataEntityRelationshipBuildService;
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
    private ModelMapper modelMapper;

    @Override
    public List<FieldTypeConfigRespVO> getFieldTypes() {
        // 从MetadataComponentFieldTypeDO中读取字段类型配置，替代原来的枚举方式
        return componentFieldTypeService.getFieldTypeConfigs();
    }

    @Override
    public List<EntityFieldValidationTypesRespVO> getFieldValidationTypes(@Valid EntityFieldValidationTypesReqVO reqVO) {
        List<String> rawFieldIds = reqVO.getFieldIdList();
        if (rawFieldIds == null || rawFieldIds.isEmpty()) {
            return new ArrayList<>();
        }
        // 过滤空/null/纯空白并去重
        List<Long> fieldIds = rawFieldIds.stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .map(s -> {
                    try { return Long.valueOf(s.trim()); } catch (NumberFormatException e) { return null; }
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (fieldIds.isEmpty()) {
            // 没有任何有效字段ID，直接返回空
            return new ArrayList<>();
        }

        // 1) 批量查询字段，获取 fieldId -> fieldType 映射
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and(Compare.IN, "id", fieldIds);
        cs.and("deleted", 0);
        List<MetadataEntityFieldDO> fields = metadataEntityFieldRepository.findAllByConfig(cs);
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
                vo.setFieldId(e.getKey());
                vo.setFieldTypeCode(e.getValue());
                vo.setValidationTypes(new ArrayList<>());
                return vo;
            }).collect(Collectors.toList());
        }
        // 2) 多次单表查询 + 组装（使用已有服务与 Anyline 仓储风格接口）
        // 2.1 查询字段类型（按 code 过滤）
        DefaultConfigStore typeCs = new DefaultConfigStore();
        typeCs.and("deleted", 0);
        typeCs.and(Compare.IN, "field_type_code", new java.util.ArrayList<>(typeCodes));
        DataSet typeDs = anylineService.querys("metadata_component_field_type", typeCs);
        Map<Long, String> typeIdToCode = new HashMap<>();
        for (DataRow row : typeDs) {
            Long idVal = null;
            try { idVal = row.getLong("id"); } catch (Exception ignore) {}
            String codeVal = row.getString("field_type_code");
            if (idVal != null && codeVal != null && !codeVal.isBlank()) {
                typeIdToCode.put(idVal, codeVal);
            }
        }

        Map<String, List<EntityFieldValidationTypesRespVO.ValidationTypeItem>> typeToValidation = new HashMap<>();
        if (!typeIdToCode.isEmpty()) {
            // 2.2 查询关联表（字段类型ID -> 校验类型ID + 排序）
            List<MetadataPermitRefOtftDO> relations =
                    permitRefOtftService.listByFieldTypeIds(typeIdToCode.keySet());

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
                Map<Long, MetadataValidationTypeDO> vtMap =
                        validationTypeService.getByIds(vtIds);
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
                    if (ftCode == null || vtId == null) { continue; }
                    String code = vtIdToCode.get(vtId);
                    if (code == null) { continue; }
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
            vo.setFieldId(e.getKey());
            vo.setFieldTypeCode(e.getValue());
            vo.setValidationTypes(typeToValidation.getOrDefault(e.getValue(), new ArrayList<>()));
            result.add(vo);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EntityFieldBatchCreateRespVO batchCreateEntityFields(@Valid EntityFieldBatchCreateReqVO reqVO) {
        EntityFieldBatchCreateRespVO result = new EntityFieldBatchCreateRespVO();
        List<String> fieldIds = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        // 获取业务实体和数据源信息（用于批量创建物理表字段）
        MetadataBusinessEntityDO businessEntity = null;
        MetadataDatasourceDO datasource = null;
        try {
            businessEntity = metadataBusinessEntityCoreService.getBusinessEntity(Long.valueOf(reqVO.getEntityId()));
            log.info("获取到业务实体: {}, 表名: {}, 数据源ID: {}",
                businessEntity != null ? businessEntity.getId() : "null",
                businessEntity != null ? businessEntity.getTableName() : "null",
                businessEntity != null ? businessEntity.getDatasourceId() : "null");

            if (businessEntity != null && businessEntity.getTableName() != null &&
                !businessEntity.getTableName().trim().isEmpty()) {
                datasource = metadataDatasourceBuildService.getDatasource(businessEntity.getDatasourceId());
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
            entityField.setEntityId(Long.valueOf(reqVO.getEntityId()));
            entityField.setFieldName(fieldItem.getFieldName());
            entityField.setDisplayName(fieldItem.getDisplayName());
            entityField.setFieldType(fieldItem.getFieldType());
            entityField.setDescription(fieldItem.getDescription());
            // 使用新的枚举值：1-是，0-否
            entityField.setIsRequired(fieldItem.getIsRequired() != null ? fieldItem.getIsRequired() : StatusEnumUtil.NO); // 默认0-不是必填
            entityField.setIsUnique(fieldItem.getIsUnique() != null ? fieldItem.getIsUnique() : StatusEnumUtil.NO); // 默认0-不是唯一
            entityField.setIsRequired(fieldItem.getIsRequired() != null ? fieldItem.getIsRequired() : StatusEnumUtil.NO); // 默认0-非必填
            entityField.setDefaultValue(fieldItem.getDefaultValue());
            entityField.setSortOrder(fieldItem.getSortOrder() != null ? fieldItem.getSortOrder() : 0);
            entityField.setIsSystemField(StatusEnumUtil.NO); // 0-不是系统字段
            entityField.setIsPrimaryKey(StatusEnumUtil.NO); // 0-不是主键
            entityField.setAppId(Long.valueOf(reqVO.getAppId()));
            // 设置默认运行模式，防止后续约束/自动编号处理中出现空指针
            entityField.setRunMode(0);

            metadataEntityFieldRepository.insert(entityField);
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
        DefaultConfigStore configStore = new DefaultConfigStore();

        if (queryVO.getEntityId() != null && !queryVO.getEntityId().trim().isEmpty()) {
            configStore.and(MetadataEntityFieldDO.ENTITY_ID, Long.valueOf(queryVO.getEntityId().trim()));
        }
        if (queryVO.getKeyword() != null && !queryVO.getKeyword().trim().isEmpty()) {
            configStore.and(Compare.LIKE, MetadataEntityFieldDO.FIELD_NAME, "%" + queryVO.getKeyword() + "%")
                    .or(Compare.LIKE, MetadataEntityFieldDO.DISPLAY_NAME, "%" + queryVO.getKeyword() + "%");
        }
        if (queryVO.getIsSystemField() != null) {
            configStore.and(MetadataEntityFieldDO.IS_SYSTEM_FIELD, queryVO.getIsSystemField());
        }
        if (queryVO.getFieldCode() != null && !queryVO.getFieldCode().trim().isEmpty()) {
            configStore.and(Compare.LIKE, MetadataEntityFieldDO.FIELD_CODE, "%" + queryVO.getFieldCode() + "%");
        }

        // isPerson=1 时，限定人员字段（USER）并补充 creator/updater 两个系统字段
        List<MetadataEntityFieldDO> baseList;
        configStore.order(MetadataEntityFieldDO.SORT_ORDER, Order.TYPE.ASC);
        configStore.order("create_time", Order.TYPE.DESC);

        if (queryVO.getIsPerson() != null && queryVO.getIsPerson() == 1) {
            // 限定字段类型为 USER
            DefaultConfigStore personStore = new DefaultConfigStore();
            personStore.and(MetadataEntityFieldDO.ENTITY_ID, Long.valueOf(queryVO.getEntityId().trim()));
            personStore.and(MetadataEntityFieldDO.FIELD_TYPE, "USER");
            // 透传其它条件
            if (queryVO.getKeyword() != null && !queryVO.getKeyword().trim().isEmpty()) {
                personStore.and(Compare.LIKE, MetadataEntityFieldDO.FIELD_NAME, "%" + queryVO.getKeyword() + "%")
                        .or(Compare.LIKE, MetadataEntityFieldDO.DISPLAY_NAME, "%" + queryVO.getKeyword() + "%");
            }
            if (queryVO.getFieldCode() != null && !queryVO.getFieldCode().trim().isEmpty()) {
                personStore.and(Compare.LIKE, MetadataEntityFieldDO.FIELD_CODE, "%" + queryVO.getFieldCode() + "%");
            }
            personStore.order(MetadataEntityFieldDO.SORT_ORDER, Order.TYPE.ASC);
            personStore.order("create_time", Order.TYPE.DESC);

            baseList = metadataEntityFieldRepository.findAllByConfig(personStore);

            // 追加 creator、updater 系统字段（若存在）并去重
            if (queryVO.getEntityId() != null) {
                Long eid = Long.valueOf(queryVO.getEntityId().trim());
                LinkedHashMap<String, MetadataEntityFieldDO> map = new LinkedHashMap<>();
                for (MetadataEntityFieldDO f : baseList) {
                    String key = f.getId() != null ? String.valueOf(f.getId()) : f.getFieldName();
                    map.putIfAbsent(key, f);
                }

                MetadataEntityFieldDO creator = metadataEntityFieldRepository.getEntityFieldByName(eid, "creator");
                if (creator != null) {
                    String key = creator.getId() != null ? String.valueOf(creator.getId()) : creator.getFieldName();
                    map.putIfAbsent(key, creator);
                }
                MetadataEntityFieldDO updater = metadataEntityFieldRepository.getEntityFieldByName(eid, "updater");
                if (updater != null) {
                    String key = updater.getId() != null ? String.valueOf(updater.getId()) : updater.getFieldName();
                    map.putIfAbsent(key, updater);
                }

                baseList = new java.util.ArrayList<>(map.values());
                // 最终再按 sort_order asc, create_time desc 排序一次
                baseList.sort((a,b) -> {
                    int s1 = a.getSortOrder() != null ? a.getSortOrder() : 0;
                    int s2 = b.getSortOrder() != null ? b.getSortOrder() : 0;
                    if (s1 != s2) return Integer.compare(s1, s2);
                    if (a.getCreateTime() == null && b.getCreateTime() == null) return 0;
                    if (a.getCreateTime() == null) return 1;
                    if (b.getCreateTime() == null) return -1;
                    return b.getCreateTime().compareTo(a.getCreateTime());
                });
            }
            return baseList;
        }

        // 默认逻辑
        return metadataEntityFieldRepository.findAllByConfig(configStore);
    }

    @Override
    public EntityFieldDetailRespVO getEntityFieldDetail(String id) {
        Long longId = Long.valueOf(id);
        MetadataEntityFieldDO entityField = metadataEntityFieldRepository.findById(longId);
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
            result.setOptions(fieldOptionService.listByFieldId(entityField.getId()).stream().map(o -> {
                FieldOptionRespVO v = new FieldOptionRespVO();
                v.setId(String.valueOf(o.getId()));
                v.setFieldId(o.getFieldId());
                v.setOptionLabel(o.getOptionLabel());
                v.setOptionValue(o.getOptionValue());
                v.setOptionOrder(o.getOptionOrder());
                v.setIsEnabled(o.getIsEnabled());
                v.setDescription(o.getDescription());
                return v;
            }).toList());
        }
        FieldConstraintRespVO cr = fieldConstraintService.getFieldConstraintConfig(entityField.getId());
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
                    DefaultConfigStore configStore = new DefaultConfigStore();
                    configStore.and("id", Long.valueOf(firstFieldId.trim()));
                    MetadataEntityFieldDO firstField = metadataEntityFieldRepository.findOne(configStore);
                    if (firstField != null) {
                        businessEntity = metadataBusinessEntityCoreService.getBusinessEntity(firstField.getEntityId());
                        if (businessEntity != null && businessEntity.getTableName() != null &&
                            !businessEntity.getTableName().trim().isEmpty()) {
                            datasource = metadataDatasourceBuildService.getDatasource(businessEntity.getDatasourceId());
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
            MetadataEntityFieldDO existingField = metadataEntityFieldRepository.findById(Long.valueOf(fieldItem.getId()));
            if (existingField == null) {
                failureCount++;
                continue;
            }
            if (fieldItem.getDisplayName() != null && !fieldItem.getDisplayName().trim().isEmpty()) {
                validateEntityFieldDisplayNameUnique(fieldItem.getId(), existingField.getEntityId().toString(), fieldItem.getDisplayName());
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


            metadataEntityFieldRepository.update(updateObj);
            successCount++;

            // 同步到物理表
            if (businessEntity != null && datasource != null) {
                try {
                    // 需要获取完整的字段信息进行更新
                    DefaultConfigStore configStore = new DefaultConfigStore();
                    configStore.and("id", Long.valueOf(fieldItem.getId()));
                    MetadataEntityFieldDO fullFieldInfo = metadataEntityFieldRepository.findOne(configStore);
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
            validateEntityFieldExists(sortItem.getFieldId());

            // 更新排序
            MetadataEntityFieldDO updateObj = new MetadataEntityFieldDO();
            updateObj.setId(Long.valueOf(sortItem.getFieldId()));
            updateObj.setSortOrder(sortItem.getSortOrder());
            metadataEntityFieldRepository.update(updateObj);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EntityFieldBatchSaveRespVO batchSaveEntityFields(@Valid EntityFieldBatchSaveReqVO reqVO) {
        EntityFieldBatchSaveRespVO resp = new EntityFieldBatchSaveRespVO();

        // 1. 获取实体与数据源
        MetadataBusinessEntityDO businessEntity = metadataBusinessEntityCoreService.getBusinessEntity(Long.valueOf(reqVO.getEntityId()));
        if (businessEntity == null) {
            throw new IllegalArgumentException("业务实体不存在");
        }
        MetadataDatasourceDO datasource = null;
        if (businessEntity.getTableName() != null && !businessEntity.getTableName().trim().isEmpty()) {
            datasource = metadataDatasourceBuildService.getDatasource(businessEntity.getDatasourceId());
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
                DefaultConfigStore cs = new DefaultConfigStore();
                cs.and("id", Long.valueOf(item.getId()));
                MetadataEntityFieldDO existing = metadataEntityFieldRepository.findOne(cs);

                // 关键安全校验：验证字段归属，防止跨实体/跨应用删除
                if (existing == null) {
                    throw new IllegalArgumentException("字段不存在: " + item.getId());
                }
                if (!existing.getEntityId().equals(Long.valueOf(reqVO.getEntityId()))) {
                    log.error("安全校验失败：尝试跨实体删除字段。fieldId={}, 字段归属entityId={}, 请求entityId={}", 
                             item.getId(), existing.getEntityId(), reqVO.getEntityId());
                    throw new IllegalArgumentException("字段不属于当前实体，禁止跨实体删除");
                }
                if (!existing.getAppId().equals(Long.valueOf(reqVO.getAppId()))) {
                    log.error("安全校验失败：尝试跨应用删除字段。fieldId={}, 字段归属appId={}, 请求appId={}", 
                             item.getId(), existing.getAppId(), reqVO.getAppId());
                    throw new IllegalArgumentException("字段不属于当前应用，禁止跨应用删除");
                }

                // 实体是否允许改表结构
                validateEntityAllowModifyStructure(existing.getEntityId());

                // 先删子配置（选项、约束、自动编号、校验规则）
                if (existing != null) {
                    try {
                        fieldOptionService.deleteByFieldId(existing.getId());
                        fieldConstraintService.deleteByFieldId(existing.getId());
                        autoNumberConfigBuildService.deleteByFieldId(existing.getId());
                        
                        // 删除校验规则
                        validationRequiredService.deleteByFieldId(existing.getId());
                        validationUniqueService.deleteByFieldId(existing.getId());
                        validationLengthService.deleteByFieldId(existing.getId());

                        // 删除实体间关联关系
                        metadataEntityRelationshipBuildService.deleteRelationShipByFieldId(Long.valueOf(item.getId()));
                    } catch (Exception ignore) {}
                }

                // 先删库记录
                metadataEntityFieldRepository.deleteById(Long.valueOf(item.getId()));

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
                DefaultConfigStore cs = new DefaultConfigStore();
                cs.and("id", Long.valueOf(item.getId()));
                MetadataEntityFieldDO origin = metadataEntityFieldRepository.findOne(cs);

                // 关键安全校验：验证字段归属，防止跨实体/跨应用操作
                if (origin == null) {
                    throw new IllegalArgumentException("字段不存在: " + item.getId());
                }
                if (!origin.getEntityId().equals(Long.valueOf(reqVO.getEntityId()))) {
                    log.error("安全校验失败：尝试跨实体操作字段。fieldId={}, 字段归属entityId={}, 请求entityId={}", 
                             item.getId(), origin.getEntityId(), reqVO.getEntityId());
                    throw new IllegalArgumentException("字段不属于当前实体，禁止跨实体操作");
                }
                if (!origin.getAppId().equals(Long.valueOf(reqVO.getAppId()))) {
                    log.error("安全校验失败：尝试跨应用操作字段。fieldId={}, 字段归属appId={}, 请求appId={}", 
                             item.getId(), origin.getAppId(), reqVO.getAppId());
                    throw new IllegalArgumentException("字段不属于当前应用，禁止跨应用操作");
                }

                // 名称唯一性（若改名）
                String newName = item.getFieldName() != null ? item.getFieldName() : origin.getFieldName();
                validateEntityFieldNameUnique(item.getId(), origin.getEntityId().toString(), newName);
                String newDisplayName = item.getDisplayName() != null ? item.getDisplayName() : origin.getDisplayName();
                validateEntityFieldDisplayNameUnique(item.getId(), origin.getEntityId().toString(), newDisplayName);

                validateEntityAllowModifyStructure(origin.getEntityId());

                Integer maxLength = extractMaxLength(item);

                // 组装更新对象（只覆盖非空字段）
                MetadataEntityFieldDO upd = new MetadataEntityFieldDO();
                upd.setId(origin.getId());
                upd.setEntityId(origin.getEntityId());
                if (item.getFieldName() != null) upd.setFieldName(item.getFieldName());
                if (item.getDisplayName() != null) upd.setDisplayName(item.getDisplayName());
                if (item.getFieldType() != null) upd.setFieldType(item.getFieldType());
                if (maxLength != null) {
                    upd.setDataLength(maxLength);
                }
                if (item.getDecimalPlaces() != null) upd.setDecimalPlaces(item.getDecimalPlaces());
                if (item.getDefaultValue() != null) upd.setDefaultValue(item.getDefaultValue());
                if (item.getDescription() != null) upd.setDescription(item.getDescription());
                if (item.getIsRequired() != null) upd.setIsRequired(item.getIsRequired());
                if (item.getIsUnique() != null) upd.setIsUnique(item.getIsUnique());
                if (item.getSortOrder() != null) upd.setSortOrder(item.getSortOrder());
                if (item.getIsSystemField() != null) upd.setIsSystemField(item.getIsSystemField());
                if (item.getDictTypeId() != null) {
                    upd.setDictTypeId(item.getDictTypeId());
                }

                metadataEntityFieldRepository.update(upd);

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
                    DefaultConfigStore cs2 = new DefaultConfigStore();
                    cs2.and("id", origin.getId());
                    MetadataEntityFieldDO full = metadataEntityFieldRepository.findOne(cs2);
                    PhysicalTableOperation alterOp = new PhysicalTableOperation();
                    alterOp.setOperationType("ALTER");
                    alterOp.setFieldInfo(full);
                    physicalTableOps.add(alterOp);
                }
                resp.getUpdatedIds().add(item.getId());

                // 同步选项、约束和自动编号（使用智能更新逻辑）
                Long fieldId = origin.getId();
                DefaultConfigStore cs2 = new DefaultConfigStore();
                cs2.and("id", origin.getId());
                MetadataEntityFieldDO full = metadataEntityFieldRepository.findOne(cs2);
                processFieldRelatedData(fieldId, full, item.getOptions(), item.getConstraints(), item.getAutoNumber());
                
                // 特别处理：如果 isRequired 字段发生了变更，需要额外同步到 MetadataValidationRequiredDO
                if (item.getIsRequired() != null && !item.getIsRequired().equals(origin.getIsRequired())) {
                    processRequiredValidation(fieldId, full);
                }
                
                // 特别处理：如果 isUnique 字段发生了变更，需要额外同步到 MetadataValidationUniqueDO
                if (item.getIsUnique() != null && !item.getIsUnique().equals(origin.getIsUnique())) {
                    processUniqueValidation(fieldId, full);
                }
                
                // 特别处理：如果 dataLength 字段发生了变更，需要额外同步到 MetadataValidationLengthDO
                boolean hasConstraintsLengthConfig = item.getConstraints() != null && 
                    (item.getConstraints().getLengthEnabled() != null || 
                     item.getConstraints().getMinLength() != null || 
                     item.getConstraints().getMaxLength() != null ||
                     StringUtils.hasText(item.getConstraints().getLengthPrompt()));
                if (maxLength != null && !maxLength.equals(origin.getDataLength()) && !hasConstraintsLengthConfig) {
                    processLengthValidation(fieldId, full);
                }
                
                validateValidationRuleUniqueness(fieldId, origin.getEntityId());
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
                        item.getFieldName(), reqVO.getEntityId(), reqVO.getAppId());

                // 新增字段的情况
                validateEntityFieldNameUnique(null, reqVO.getEntityId(), item.getFieldName());
                validateEntityFieldDisplayNameUnique(null, reqVO.getEntityId(), item.getDisplayName());
                validateEntityAllowModifyStructure(Long.valueOf(reqVO.getEntityId()));

                Integer maxLength = extractMaxLength(item);

                MetadataEntityFieldDO toCreate = new MetadataEntityFieldDO();
                toCreate.setEntityId(Long.valueOf(reqVO.getEntityId()));
                toCreate.setAppId(Long.valueOf(reqVO.getAppId()));
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
                toCreate.setIsSystemField(item.getIsSystemField() != null ? item.getIsSystemField() : StatusEnumUtil.YES);
                toCreate.setIsPrimaryKey(StatusEnumUtil.NO);
                toCreate.setRunMode(0);
                toCreate.setDictTypeId(item.getDictTypeId());

                metadataEntityFieldRepository.insert(toCreate);

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
                processFieldRelatedData(fieldId, toCreate, item.getOptions(), item.getConstraints(), item.getAutoNumber());
                
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
                
                validateValidationRuleUniqueness(fieldId, toCreate.getEntityId());
            }
        }

        // 5. 统一执行所有物理表操作
        if (datasource != null && businessEntity.getTableName() != null && !physicalTableOps.isEmpty()) {
            executePhysicalTableOperations(datasource, businessEntity.getTableName(), physicalTableOps);
        }

        return resp;
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
     * @param item 字段信息
     * @return 已存在的字段，如果不存在则返回null
     */
    private MetadataEntityFieldDO findExistingFieldByCodeOrName(String entityId, EntityFieldUpsertItemVO item) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataEntityFieldDO.ENTITY_ID, Long.valueOf(entityId.trim()));

        // fieldCode字段已注释，跳过根据fieldCode查找逻辑
        // 直接根据fieldName查找

        // 其次根据fieldName查找
        if (item.getFieldName() != null && !item.getFieldName().trim().isEmpty()) {
            DefaultConfigStore nameConfigStore = new DefaultConfigStore();
            nameConfigStore.and(MetadataEntityFieldDO.ENTITY_ID, Long.valueOf(entityId.trim()));
            nameConfigStore.and(MetadataEntityFieldDO.FIELD_NAME, item.getFieldName());
            
            // 显式添加租户条件，确保多租户隔离（避免依赖Anyline自动拦截器）
            // 这是一个关键的安全措施，防止跨租户查询导致数据混乱
            nameConfigStore.and("deleted", 0);
            
            MetadataEntityFieldDO existingField = metadataEntityFieldRepository.findOne(nameConfigStore);
            if (existingField != null) {
                return existingField;
            }
        }

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEntityField(@Valid EntityFieldSaveReqVO createReqVO) {
        // 校验字段名不能与系统保留字段冲突
        validateFieldNameNotSystemReserved(createReqVO.getFieldName());
        // 校验字段名唯一性
        validateEntityFieldNameUnique(null, createReqVO.getEntityId(), createReqVO.getFieldName());
        validateEntityFieldDisplayNameUnique(null, createReqVO.getEntityId(), createReqVO.getDisplayName());

        // 校验实体类型是否允许修改表结构
        validateEntityAllowModifyStructure(Long.valueOf(createReqVO.getEntityId()));

        // 插入实体字段
        MetadataEntityFieldDO entityField = BeanUtils.toBean(createReqVO, MetadataEntityFieldDO.class);
        entityField.setEntityId(Long.valueOf(createReqVO.getEntityId()));
        entityField.setAppId(Long.valueOf(createReqVO.getAppId()));
        entityField.setIsSystemField(1); // 手动创建的字段不是系统字段：1-不是
        entityField.setRunMode(0); // 默认编辑态
        entityField.setStatus(0); // 默认开启
        // 如果没有提供fieldCode，则根据fieldName生成
        if (entityField.getFieldCode() == null || entityField.getFieldCode().trim().isEmpty()) {
            entityField.setFieldCode(generateFieldCode(createReqVO.getFieldName()));
        }

        metadataEntityFieldRepository.insert(entityField);

        // 同步到物理表 - 失败时直接抛出异常回滚事务
        try {
            MetadataBusinessEntityDO businessEntity = metadataBusinessEntityCoreService.getBusinessEntity(Long.valueOf(createReqVO.getEntityId()));
            if (businessEntity != null && businessEntity.getTableName() != null &&
                !businessEntity.getTableName().trim().isEmpty()) {
                MetadataDatasourceDO datasource = metadataDatasourceBuildService.getDatasource(businessEntity.getDatasourceId());
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
        metadataEntityFieldRepository.insert(entityField);
        return entityField.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEntityField(@Valid EntityFieldSaveReqVO updateReqVO) {
        // 校验存在
        validateEntityFieldExists(updateReqVO.getId());
        // 校验字段名不能与系统保留字段冲突
        validateFieldNameNotSystemReserved(updateReqVO.getFieldName());
        // 校验字段名唯一性
        validateEntityFieldNameUnique(updateReqVO.getId(), updateReqVO.getEntityId(), updateReqVO.getFieldName());
    validateEntityFieldDisplayNameUnique(updateReqVO.getId(), updateReqVO.getEntityId(), updateReqVO.getDisplayName());
        // 校验实体类型是否允许修改表结构
        validateEntityAllowModifyStructure(Long.valueOf(updateReqVO.getEntityId()));

        // 更新实体字段
        MetadataEntityFieldDO updateObj = BeanUtils.toBean(updateReqVO, MetadataEntityFieldDO.class);
        updateObj.setId(Long.valueOf(updateReqVO.getId()));
        updateObj.setEntityId(Long.valueOf(updateReqVO.getEntityId()));
        updateObj.setAppId(Long.valueOf(updateReqVO.getAppId()));
        // 如果没有提供fieldCode，则根据fieldName生成
        if (updateObj.getFieldCode() == null || updateObj.getFieldCode().trim().isEmpty()) {
            updateObj.setFieldCode(generateFieldCode(updateReqVO.getFieldName()));
        }
        metadataEntityFieldRepository.update(updateObj);

        // 同步到物理表
        try {
            MetadataBusinessEntityDO businessEntity = metadataBusinessEntityCoreService.getBusinessEntity(Long.valueOf(updateReqVO.getEntityId()));
            if (businessEntity != null && businessEntity.getTableName() != null &&
                !businessEntity.getTableName().trim().isEmpty()) {
                MetadataDatasourceDO datasource = metadataDatasourceBuildService.getDatasource(businessEntity.getDatasourceId());
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
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and("id", longId);
        MetadataEntityFieldDO existingField = metadataEntityFieldRepository.findOne(configStore);

        if (existingField != null) {
            // 校验实体类型是否允许修改表结构
            validateEntityAllowModifyStructure(existingField.getEntityId());
        }

        // 先删除子配置（选项、约束、自动编号）
        if (existingField != null) {
            fieldOptionService.deleteByFieldId(existingField.getId());
            fieldConstraintService.deleteByFieldId(existingField.getId());
            autoNumberConfigBuildService.deleteByFieldId(existingField.getId());
            validationRequiredService.deleteByFieldId(existingField.getId());
            validationUniqueService.deleteByFieldId(existingField.getId());
            validationLengthService.deleteByFieldId(existingField.getId());
        }

        // 删除实体字段
        metadataEntityFieldRepository.deleteById(longId);

        // 从物理表删除字段
        if (existingField != null) {
            try {
                MetadataBusinessEntityDO businessEntity = metadataBusinessEntityCoreService.getBusinessEntity(existingField.getEntityId());
                if (businessEntity != null && businessEntity.getTableName() != null &&
                    !businessEntity.getTableName().trim().isEmpty()) {
                    MetadataDatasourceDO datasource = metadataDatasourceBuildService.getDatasource(businessEntity.getDatasourceId());
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
        if (metadataEntityFieldRepository.findById(longId) == null) {
            throw exception(ENTITY_FIELD_NOT_EXISTS);
        }
    }

    private void validateEntityFieldNameUnique(String id, String entityId, String fieldName) {
        Long longEntityId = Long.valueOf(entityId);
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataEntityFieldDO.ENTITY_ID, longEntityId);
        configStore.and(MetadataEntityFieldDO.FIELD_NAME, fieldName);
        if (id != null && !id.trim().isEmpty()) {
            Long longId = Long.valueOf(id.trim());
            configStore.and(Compare.NOT_EQUAL, "id", longId);
        }

        long count = metadataEntityFieldRepository.countByConfig(configStore);
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
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataEntityFieldDO.ENTITY_ID, longEntityId);
        configStore.and(MetadataEntityFieldDO.DISPLAY_NAME, displayName.trim());
        if (id != null && !id.trim().isEmpty()) {
            Long longId = Long.valueOf(id.trim());
            configStore.and(Compare.NOT_EQUAL, "id", longId);
        }

        long count = metadataEntityFieldRepository.countByConfig(configStore);
        if (count > 0) {
            throw exception(ENTITY_FIELD_DISPLAY_NAME_DUPLICATE, displayName.trim());
        }
    }

    @Override
    public MetadataEntityFieldDO getEntityField(String id) {
    Long longId = Long.valueOf(id.trim());
        return metadataEntityFieldRepository.findById(longId);
    }

    @Override
    public PageResult<MetadataEntityFieldDO> getEntityFieldPage(EntityFieldPageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        // 添加查询条件
        if (pageReqVO.getEntityId() != null && !pageReqVO.getEntityId().trim().isEmpty()) {
            configStore.and(MetadataEntityFieldDO.ENTITY_ID, Long.valueOf(pageReqVO.getEntityId().trim()));
        }
        if (pageReqVO.getFieldName() != null && !pageReqVO.getFieldName().trim().isEmpty()) {
            configStore.and(Compare.LIKE, MetadataEntityFieldDO.FIELD_NAME, "%" + pageReqVO.getFieldName() + "%");
        }
        if (pageReqVO.getDisplayName() != null && !pageReqVO.getDisplayName().trim().isEmpty()) {
            configStore.and(Compare.LIKE, MetadataEntityFieldDO.DISPLAY_NAME, "%" + pageReqVO.getDisplayName() + "%");
        }
        if (pageReqVO.getFieldType() != null && !pageReqVO.getFieldType().trim().isEmpty()) {
            configStore.and(MetadataEntityFieldDO.FIELD_TYPE, pageReqVO.getFieldType());
        }
        if (pageReqVO.getIsSystemField() != null) {
            configStore.and(MetadataEntityFieldDO.IS_SYSTEM_FIELD, pageReqVO.getIsSystemField());
        }
        if (pageReqVO.getIsPrimaryKey() != null) {
            configStore.and(MetadataEntityFieldDO.IS_PRIMARY_KEY, pageReqVO.getIsPrimaryKey());
        }
        if (pageReqVO.getIsRequired() != null) {
            configStore.and(MetadataEntityFieldDO.IS_REQUIRED, pageReqVO.getIsRequired());
        }
        if (pageReqVO.getRunMode() != null) {
            configStore.and(MetadataEntityFieldDO.RUN_MODE, pageReqVO.getRunMode());
        }
        if (pageReqVO.getAppId() != null && !pageReqVO.getAppId().trim().isEmpty()) {
            configStore.and(MetadataEntityFieldDO.APP_ID, Long.valueOf(pageReqVO.getAppId().trim()));
        }
        if (pageReqVO.getFieldCode() != null && !pageReqVO.getFieldCode().trim().isEmpty()) {
            configStore.and(Compare.LIKE, MetadataEntityFieldDO.FIELD_CODE, "%" + pageReqVO.getFieldCode() + "%");
        }

    // 添加排序：按照字段排序优先（倒序），然后按创建时间倒序
        configStore.order(MetadataEntityFieldDO.SORT_ORDER, Order.TYPE.DESC);
        configStore.order("create_time", Order.TYPE.DESC);

        // 分页查询
        return metadataEntityFieldRepository.findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
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
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.order(MetadataEntityFieldDO.SORT_ORDER, Order.TYPE.ASC);
        configStore.order("create_time", Order.TYPE.DESC);
        return metadataEntityFieldRepository.findAllByConfig(configStore);
    }

    @Override
    public List<MetadataEntityFieldDO> getEntityFieldListByEntityId(String entityId) {
    Long longEntityId = Long.valueOf(entityId.trim());
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataEntityFieldDO.ENTITY_ID, longEntityId);
        configStore.order(MetadataEntityFieldDO.SORT_ORDER, Order.TYPE.ASC);
        configStore.order("create_time", Order.TYPE.DESC);
        return metadataEntityFieldRepository.findAllByConfig(configStore);
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
    Long longEntityId = Long.valueOf(entityId.trim());
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataEntityFieldDO.ENTITY_ID, longEntityId);
        List<MetadataEntityFieldDO> fields = metadataEntityFieldRepository.findAllByConfig(configStore);

        // 获取业务实体信息，用于批量删除物理表字段
        MetadataBusinessEntityDO businessEntity = null;
        MetadataDatasourceDO datasource = null;
        try {
            businessEntity = metadataBusinessEntityCoreService.getBusinessEntity(Long.valueOf(entityId));
                            if (businessEntity != null && businessEntity.getTableName() != null &&
                    !businessEntity.getTableName().trim().isEmpty()) {
                    datasource = metadataDatasourceBuildService.getDatasource(businessEntity.getDatasourceId());
                }
        } catch (Exception e) {
            log.error("获取业务实体信息失败: {}", e.getMessage(), e);
        }

        for (MetadataEntityFieldDO field : fields) {
            // 删除数据库记录
            fieldOptionService.deleteByFieldId(field.getId());
            fieldConstraintService.deleteByFieldId(field.getId());
            autoNumberConfigBuildService.deleteByFieldId(field.getId());
            validationRequiredService.deleteByFieldId(field.getId());
            validationUniqueService.deleteByFieldId(field.getId());
            validationLengthService.deleteByFieldId(field.getId());
            metadataEntityFieldRepository.deleteById(field.getId());

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
     */
    private void addColumnToTable(MetadataDatasourceDO datasource, String tableName, MetadataEntityFieldDO field) {
        try {
            log.info("开始为表 {} 添加列 {}, 数据源: {} ({})",
                tableName, field.getFieldName(),
                datasource.getDatasourceName(), datasource.getDatasourceType());
            log.info("数据源配置: {}", datasource.getConfig());

            // 使用TenantUtils.executeIgnore包装操作，忽略租户条件
            TenantUtils.executeIgnore(() -> {
                // 创建 AnylineService 实例
                AnylineService<?> service = temporaryDatasourceService.createTemporaryService(datasource);

                // 验证连接的数据库
                String currentDb = getCurrentDatabase(service);
                log.info("当前连接的数据库: {}, 期望连接的数据库: onebase_business", currentDb);

                // 首先检查表是否存在
                if (!checkTableExists(service, tableName)) {
                    log.warn("表 {} 不存在，尝试重新创建该表", tableName);
                    // 表不存在，直接抛出异常
                    String errorMessage = "表 " + tableName + " 不存在，请先创建表。这通常是由于业务实体创建时表创建失败导致的数据不一致问题。";
                    log.error("添加字段失败: {}", errorMessage);
                    throw new RuntimeException(errorMessage);
                }

                // 检查列是否已存在
                if (checkColumnExists(service, tableName, field.getFieldName())) {
                    log.warn("列 {} 已存在于表 {} 中，跳过添加操作", field.getFieldName(), tableName);
                    return null;
                }

                // 生成添加列 DDL
                String addColumnDDL = generateAddColumnDDL(tableName, field);

                // 针对DM数据库：拆分DDL语句，分别执行ALTER TABLE和COMMENT ON COLUMN
                // DM数据库不支持在同一批处理中执行ALTER TABLE和COMMENT语句
                String[] sqlStatements = addColumnDDL.split(";\n");
                for (String sql : sqlStatements) {
                    if (sql != null && !sql.trim().isEmpty()) {
                        String trimmedSql = sql.trim();
                        log.debug("执行DDL语句: {}", trimmedSql);
                        service.execute(trimmedSql);
                    }
                }

                log.info("成功为表 {} 添加列: {}", tableName, field.getFieldName());
                return null;
            });
        } catch (Exception e) {
            log.error("为表 {} 添加列 {} 失败: {}", tableName, field.getFieldName(), e.getMessage(), e);
            throw new RuntimeException("添加列失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查表是否存在
     * <p>
     * 使用Anyline元数据API，自动适配不同数据库（PostgreSQL、达梦、金仓等）
     * 避免手动拼接SQL和硬编码LIMIT语法
     */
    private boolean checkTableExists(AnylineService<?> service, String tableName) {
        try {
            log.info("检查表是否存在 - 表名: {}", tableName);

            // 关键修复：清除Anyline的元数据缓存
            // Anyline会缓存表结构信息(默认缓存24小时)，导致刚创建的表查询不到
            // 使用CacheProxy.clear()清除缓存，强制重新从数据库查询最新的表结构
            try {
                org.anyline.proxy.CacheProxy.clear();
                log.debug("已清除Anyline元数据缓存");
            } catch (Exception e) {
                log.warn("清除Anyline元数据缓存失败: {}", e.getMessage());
            }

            // 使用Anyline元数据API，跨数据库兼容
            // Anyline会自动处理不同数据库的元数据查询和标识符大小写问题
            Table<?> table = service.metadata().table(tableName);
            boolean exists = (table != null);

            if (exists) {
                log.info("表 {} 存在", tableName);
            } else {
                log.debug("表 {} 不存在", tableName);
            }

            return exists;
        } catch (Exception e) {
            // 捕获异常视为表不存在
            log.debug("检查表 {} 时发生异常: {}", tableName, e.getMessage());
            return false;
        }
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
     * @param service AnylineService实例
     * @param tableName 表名
     * @param columnName 列名
     * @return 如果列存在返回true，否则返回false
     */
    private boolean checkColumnExists(AnylineService<?> service, String tableName, String columnName) {
        try {
            log.info("检查列是否存在 - 表名: {}, 列名: {}", tableName, columnName);

            // 关键修复：清除Anyline的元数据缓存
            // Anyline会缓存表结构信息(默认缓存24小时)，导致刚添加的列查询不到
            // 使用CacheProxy.clear()清除缓存，强制重新从数据库查询最新的表结构
            try {
                org.anyline.proxy.CacheProxy.clear();
                log.debug("已清除Anyline元数据缓存");
            } catch (Exception e) {
                log.warn("清除Anyline元数据缓存失败: {}", e.getMessage());
            }

            // 使用Anyline元数据API，完全跨数据库兼容
            // Anyline会自动处理不同数据库的元数据查询、标识符大小写问题
            Table<?> table = service.metadata().table(tableName);
            if (table == null) {
                log.info("表 {} 不存在，因此列 {} 也不存在", tableName, columnName);
                return false;
            }

            // 获取所有列
            LinkedHashMap<String, Column> columns = table.getColumns();
            if (columns == null || columns.isEmpty()) {
                log.warn("表 {} 的列信息为空，无法检查列 {} 是否存在", tableName, columnName);
                return false;
            }
            
            log.debug("表 {} 共有 {} 列", tableName, columns.size());

            // 先尝试精确匹配
            Column column = table.getColumn(columnName);
            if (column != null) {
                log.info("通过精确匹配找到列: {} (实际列名: {})", columnName, column.getName());
                log.info("已存在的列详情: 表名={}, 列名={}, 数据类型={}", 
                        table.getName(), column.getName(), column.getTypeName());
                return true;
            }
            
            log.debug("精确匹配列 {} 失败，尝试小写匹配", columnName);

            // 如果精确匹配失败，尝试小写匹配（PostgreSQL默认将不带引号的标识符转为小写）
            String lowerColumnName = columnName.toLowerCase();
            column = table.getColumn(lowerColumnName);
            if (column != null) {
                log.info("通过小写匹配找到列: {} (实际列名: {})", columnName, column.getName());
                log.info("已存在的列详情: 表名={}, 列名={}, 数据类型={}", 
                        table.getName(), column.getName(), column.getTypeName());
                return true;
            }
            
            log.debug("小写匹配列 {} 失败，尝试忽略大小写遍历匹配", columnName);
            
            // 如果小写匹配也失败，最后尝试忽略大小写遍历匹配（处理其他数据库的大小写问题）
            for (Column col : columns.values()) {
                if (col.getName().equalsIgnoreCase(columnName)) {
                    log.info("通过忽略大小写遍历找到列: {} (实际列名: {})", columnName, col.getName());
                    log.info("已存在的列详情: 表名={}, 列名={}, 数据类型={}", 
                            table.getName(), col.getName(), col.getTypeName());
                    return true;
                }
            }

            log.info("列 {} 在表 {} 中不存在", columnName, tableName);
            return false;
        } catch (Exception e) {
            log.error("检查列 {} 在表 {} 中是否存在时发生错误: {}", columnName, tableName, e.getMessage(), e);
            // 发生异常时返回false，让调用方处理
            // 避免使用备用的硬编码SQL，保持跨数据库兼容性
            return false;
        }
    }

    /**
     * 简单转义 SQL 字面量中的单引号，防止拼接语句时语法错误。
     * 仅用于把受信任的标识符作为字符串常量参与比较，不用于通用拼接或用户输入。
     *
     * @param value 待转义的值
     * @return 转义后的值（将 ' 替换为 ''）
     */


    /**
     * 修改表中的列
     * 
     * 说明：即使field有ID，也需要检查物理表中列是否真实存在
     * 因为可能存在元数据与物理表不一致的情况（如之前物理表操作失败、表被手动重建等）
     */
    private void alterColumnInTable(MetadataDatasourceDO datasource, String tableName, MetadataEntityFieldDO field) {
        try {
            // 使用TenantUtils.executeIgnore包装操作，忽略租户条件
            TenantUtils.executeIgnore(() -> {
                // 创建 AnylineService 实例
                AnylineService<?> service = temporaryDatasourceService.createTemporaryService(datasource);
                
                // 先校验表是否存在
                if (!checkTableExists(service, tableName)) {
                    throw new RuntimeException("表 " + tableName + " 不存在，请先创建表");
                }

                // 关键修复：必须检查列是否存在，避免元数据与物理表不一致导致的问题
                boolean columnExists = checkColumnExists(service, tableName, field.getFieldName());
                
                if (!columnExists) {
                    // 列不存在，应该使用ADD操作而非ALTER
                    log.warn("准备修改表 {} 的列 {} 时发现列不存在（字段ID: {}），将改为新增列操作", 
                            tableName, field.getFieldName(), field.getId());
                    addColumnToTable(datasource, tableName, field);
                } else {
                    // 列存在，正常执行ALTER操作
                    log.info("准备修改表 {} 的列: {}, 字段ID: {}", tableName, field.getFieldName(), field.getId());
                    
                    // 生成并执行修改列 DDL（传入数据库类型以生成兼容的SQL）
                    String alterColumnDDL = generateAlterColumnDDL(datasource.getDatasourceType(), tableName, field);
                    service.execute(alterColumnDDL);

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
     * 重命名表中的列
     */
    private void renameColumnInTable(MetadataDatasourceDO datasource, String tableName, String oldName, String newName) {
        try {
            TenantUtils.executeIgnore(() -> {
                AnylineService<?> service = temporaryDatasourceService.createTemporaryService(datasource);

                if (!checkTableExists(service, tableName)) {
                    throw new RuntimeException("表 " + tableName + " 不存在，请先创建表");
                }

                boolean oldExists = checkColumnExists(service, tableName, oldName);
                boolean newExists = checkColumnExists(service, tableName, newName);
                if (!oldExists) {
                    log.warn("重命名列时发现旧列不存在：{}.{} -> {}.{}，跳过重命名", tableName, oldName, tableName, newName);
                    return null;
                }
                if (newExists) {
                    log.info("目标列已存在：{}.{}，跳过重命名", tableName, newName);
                    return null;
                }

                String sql = "ALTER TABLE \"" + tableName + "\" RENAME COLUMN \"" + oldName + "\" TO \"" + newName + "\";";
                service.execute(sql);
                log.info("已将表 {} 的列 {} 重命名为 {}", tableName, oldName, newName);
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
     * 先检查列是否存在，存在才执行删除，以兼容达梦等数据库
     */
    private void dropColumnFromTable(MetadataDatasourceDO datasource, String tableName, String fieldName) {
        try {
            // 使用TenantUtils.executeIgnore包装操作，忽略租户条件
            TenantUtils.executeIgnore(() -> {
                // 创建 AnylineService 实例
                AnylineService<?> service = temporaryDatasourceService.createTemporaryService(datasource);

                // 检查列是否存在
                boolean exists = checkColumnExists(service, tableName, fieldName);
                if (!exists) {
                    log.info("列 {} 不存在于表 {}，跳过删除操作", fieldName, tableName);
                    return null;
                }

                // 生成删除列 DDL
                String dropColumnDDL = generateDropColumnDDL(tableName, fieldName);

                // 执行删除列语句
                service.execute(dropColumnDDL);

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
     * @param tableName 表名
     * @param field 字段信息
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
                       .append("\" TYPE ").append(columnType).append(";\n");
                    
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
     * @param tableName 表名
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
     * @param fieldType 字段类型
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
     * @param entityId 实体ID
     */
    private void validateEntityAllowModifyStructure(Long entityId) {
        // 获取业务实体信息
        MetadataBusinessEntityDO businessEntity = metadataBusinessEntityCoreService.getBusinessEntity(Long.valueOf(entityId));
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
    public List<MetadataEntityFieldDO> findAllByConfig(DefaultConfigStore configStore) {
        return metadataEntityFieldRepository.findAllByConfig(configStore);
    }

    // ==================== 新增方法实现：处理包含自动编号的业务逻辑 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EntityFieldRespVO createEntityFieldWithRelated(@Valid EntityFieldSaveReqVO reqVO) {
        Long id = createEntityField(reqVO);
        MetadataEntityFieldDO entityField = getEntityField(String.valueOf(id));

        // 处理选项、约束和自动编号
        processFieldRelatedData(id, entityField, reqVO.getOptions(), reqVO.getConstraints(), reqVO.getAutoNumber());

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
            processFieldRelatedData(fieldId, entityField, reqVO.getOptions(), reqVO.getConstraints(), reqVO.getAutoNumber());
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
        Long fieldId = Long.valueOf(id);
        MetadataAutoNumberConfigDO config = autoNumberConfigBuildService.getByFieldId(fieldId);
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

        return result;
    }

    /**
     * 处理字段相关数据（选项、约束、自动编号）
     */
    private void processFieldRelatedData(Long fieldId, MetadataEntityFieldDO entityField,
                                       List<FieldOptionRespVO> options,
                                       FieldConstraintRespVO constraints,
                                       AutoNumberConfigReqVO autoNumber) {
        // 处理选项：仅当未关联字典类型时才处理自定义选项（智能更新版本）
        // 如果 dictTypeId 不为 null，说明复用系统字典，忽略 options 参数
        if (entityField != null && entityField.getDictTypeId() == null && options != null) {
            // 获取现有选项
            List<MetadataEntityFieldOptionDO> existingOptions = fieldOptionService.listByFieldId(fieldId);
            Map<Long, MetadataEntityFieldOptionDO> existingOptionsMap = existingOptions.stream()
                .collect(java.util.stream.Collectors.toMap(MetadataEntityFieldOptionDO::getId, o -> o, (a, b) -> a));
            
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
                            updateObj.setFieldId(fieldId);
                            updateObj.setOptionLabel(opt.getOptionLabel());
                            updateObj.setOptionValue(opt.getOptionValue());
                            updateObj.setOptionOrder(opt.getOptionOrder());
                            updateObj.setIsEnabled(opt.getIsEnabled());
                            updateObj.setDescription(opt.getDescription());
                            updateObj.setAppId(entityField.getAppId());
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
                    log.info("发现已存在相同值的选项，自动转换为更新操作: fieldId={}, optionValue={}, existingId={}", 
                             fieldId, opt.getOptionValue(), existingByValue.getId());
                    MetadataEntityFieldOptionDO updateObj = new MetadataEntityFieldOptionDO();
                    updateObj.setId(existingByValue.getId());
                    updateObj.setFieldId(fieldId);
                    updateObj.setOptionLabel(opt.getOptionLabel());
                    updateObj.setOptionValue(opt.getOptionValue());
                    updateObj.setOptionOrder(opt.getOptionOrder());
                    updateObj.setIsEnabled(opt.getIsEnabled());
                    updateObj.setDescription(opt.getDescription());
                    updateObj.setAppId(entityField.getAppId());
                    fieldOptionService.update(updateObj);
                    processedOptionIds.add(existingByValue.getId());
                } else {
                    // 确实是新选项，新增
                    MetadataEntityFieldOptionDO d = new MetadataEntityFieldOptionDO();
                    d.setFieldId(fieldId);
                    d.setOptionLabel(opt.getOptionLabel());
                    d.setOptionValue(opt.getOptionValue());
                    d.setOptionOrder(opt.getOptionOrder());
                    d.setIsEnabled(opt.getIsEnabled());
                    d.setDescription(opt.getDescription());
                    d.setAppId(entityField.getAppId());
                    fieldOptionService.create(d);
                    log.debug("新增字段选项: fieldId={}, optionValue={}", fieldId, opt.getOptionValue());
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
            fieldOptionService.deleteByFieldId(fieldId);
        }

        // 处理约束 - 修复：只有当 constraints 不为空对象时才执行删除和处理
        // 空对象判断：所有字段都为 null 时视为空对象，不应该删除现有约束
        if (constraints != null && !isConstraintsEmpty(constraints)) {
            fieldConstraintService.deleteByFieldId(fieldId);
            processFieldConstraints(fieldId, entityField, constraints);
        }

        // 处理自动编号（使用智能更新版本）
        if (autoNumber != null) {
            processAutoNumberConfig(fieldId, entityField, autoNumber);
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
    private void processFieldConstraints(Long fieldId, MetadataEntityFieldDO entityField, FieldConstraintRespVO constraints) {
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
            req.setFieldId(fieldId);
            req.setConstraintType("LENGTH_RANGE");
            req.setMinLength(constraints.getMinLength());
            req.setMaxLength(constraints.getMaxLength());
            req.setPromptMessage(constraints.getLengthPrompt());
            Integer enabledValue = constraints.getLengthEnabled();
            if (enabledValue == null && (hasLengthRange || hasLengthPrompt)) {
                enabledValue = CommonStatusEnum.ENABLED.getStatus();
            }
            req.setIsEnabled(enabledValue);
            req.setRunMode(entityField != null && entityField.getRunMode() != null ? entityField.getRunMode() : 0);
            req.setAppId(entityField != null ? entityField.getAppId() : null);
            fieldConstraintService.saveFieldConstraintConfig(req);
        }

        // 正则 - 只有当正则表达式不为空且启用时才创建REGEX约束
        if (constraints.getRegexPattern() != null && !constraints.getRegexPattern().trim().isEmpty() &&
                constraints.getRegexEnabled() != null && constraints.getRegexEnabled() == 1) {
            FieldConstraintSaveReqVO req = new FieldConstraintSaveReqVO();
            req.setFieldId(fieldId);
            req.setConstraintType("REGEX");
            req.setRegexPattern(constraints.getRegexPattern());
            req.setPromptMessage(constraints.getRegexPrompt());
            req.setIsEnabled(constraints.getRegexEnabled());
            req.setRunMode(entityField != null && entityField.getRunMode() != null ? entityField.getRunMode() : 0);
            req.setAppId(entityField != null ? entityField.getAppId() : null);
            fieldConstraintService.saveFieldConstraintConfig(req);
        }

        // 必填（与 isRequired 联动）
        if (entityField != null && entityField.getIsRequired() != null) {
            // 只有当 isRequired = 1 时才创建约束配置，为0时删除已有配置
            if (entityField.getIsRequired() == 1) {
                // 原有的字段约束逻辑
                FieldConstraintSaveReqVO req = new FieldConstraintSaveReqVO();
                req.setFieldId(fieldId);
                req.setConstraintType("REQUIRED");
                req.setIsEnabled(entityField.getIsRequired());
                req.setPromptMessage(null);
                req.setRunMode(entityField.getRunMode() != null ? entityField.getRunMode() : 0);
                req.setAppId(entityField.getAppId());
                fieldConstraintService.saveFieldConstraintConfig(req);
            } else {
                // isRequired = 0 时删除已有的必填约束配置
                fieldConstraintService.delete(fieldId, "REQUIRED");
            }
        }

        // 唯一（与 isUnique 联动）
        if (entityField != null && entityField.getIsUnique() != null) {
            // 只有当 isUnique = 1 时才创建约束配置，为0时删除已有配置
            if (entityField.getIsUnique() == 1) {
                FieldConstraintSaveReqVO req = new FieldConstraintSaveReqVO();
                req.setFieldId(fieldId);
                req.setConstraintType("UNIQUE");
                req.setIsEnabled(entityField.getIsUnique());
                req.setPromptMessage(null);
                req.setRunMode(entityField.getRunMode() != null ? entityField.getRunMode() : 0);
                req.setAppId(entityField.getAppId());
                fieldConstraintService.saveFieldConstraintConfig(req);
            } else {
                // isUnique = 0 时删除已有的唯一性约束配置
                fieldConstraintService.delete(fieldId, "UNIQUE");
            }
        }
    }

    /**
     * 处理自动编号配置（智能更新版本）
     *
     * @param fieldId 字段ID
     * @param entityField 字段实体
     * @param autoNumber 自动编号配置
     */
    private void processAutoNumberConfig(Long fieldId, MetadataEntityFieldDO entityField, AutoNumberConfigReqVO autoNumber) {
        // 获取现有配置
        MetadataAutoNumberConfigDO existingConfig = autoNumberConfigBuildService.getByFieldId(fieldId);
        
        // 使用新的枚举值：1-启用，0-禁用
        if (autoNumber.getIsEnabled() != null && CommonStatusEnum.isEnabled(autoNumber.getIsEnabled())) {
            // 构建配置对象
            MetadataAutoNumberConfigDO config = new MetadataAutoNumberConfigDO();
            if (existingConfig != null) {
                // 更新：保留原有ID
                config.setId(existingConfig.getId());
            }
            config.setFieldId(fieldId);
            config.setIsEnabled(autoNumber.getIsEnabled());
            config.setNumberMode(autoNumber.getNumberMode());
            config.setDigitWidth(autoNumber.getDigitWidth());
            config.setOverflowContinue(autoNumber.getOverflowContinue());
            config.setInitialValue(autoNumber.getInitialValue() != null ? autoNumber.getInitialValue() : 1L);
            config.setResetCycle(autoNumber.getResetCycle());
            config.setResetOnInitialChange(autoNumber.getResetOnInitialChange() != null ? autoNumber.getResetOnInitialChange() : 0);
            config.setRunMode(entityField != null && entityField.getRunMode() != null ? entityField.getRunMode() : 0);
            config.setAppId(entityField != null ? entityField.getAppId() : null);

            Long configId = autoNumberConfigBuildService.upsert(config);

            // 处理规则项（智能更新：根据ID进行精确匹配更新）
            if (autoNumber.getRuleItems() != null) {
                // 获取现有规则项，按 ID 建立映射
                List<MetadataAutoNumberRuleItemDO> existingRules = autoNumberRuleBuildService.listByConfigId(configId);
                Map<Long, MetadataAutoNumberRuleItemDO> existingRulesMap = existingRules.stream()
                    .collect(java.util.stream.Collectors.toMap(
                        MetadataAutoNumberRuleItemDO::getId, 
                        r -> r, 
                        (a, b) -> a
                    ));
                
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
                            rule.setConfigId(configId);
                            rule.setItemType(ruleReq.getItemType());
                            rule.setItemOrder(ruleReq.getItemOrder());
                            rule.setFormat(ruleReq.getFormat());
                            
                            // 兼容性处理：TEXT类型的规则项支持从format字段获取文本值
                            String textValue = ruleReq.getTextValue();
                            if ("TEXT".equalsIgnoreCase(ruleReq.getItemType()) && textValue == null && ruleReq.getFormat() != null) {
                                textValue = ruleReq.getFormat();
                            }
                            rule.setTextValue(textValue);
                            
                            // 兼容性处理：FIELD_REF类型的规则项支持从format字段获取引用字段ID
                            Long refFieldId = ruleReq.getRefFieldId();
                            if ("FIELD_REF".equalsIgnoreCase(ruleReq.getItemType()) && refFieldId == null && ruleReq.getFormat() != null) {
                                try {
                                    refFieldId = Long.parseLong(ruleReq.getFormat());
                                    log.info("FIELD_REF规则项从format字段解析出引用字段ID: {}", refFieldId);
                                } catch (NumberFormatException e) {
                                    log.warn("FIELD_REF规则项的format字段无法解析为字段ID: {}", ruleReq.getFormat());
                                }
                            }
                            rule.setRefFieldId(refFieldId);
                            rule.setIsEnabled(ruleReq.getIsEnabled() != null ? ruleReq.getIsEnabled() : StatusEnumUtil.ENABLED);
                            rule.setAppId(entityField != null ? entityField.getAppId() : null);
                            
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
                    rule.setConfigId(configId);
                    rule.setItemType(ruleReq.getItemType());
                    rule.setItemOrder(ruleReq.getItemOrder());
                    rule.setFormat(ruleReq.getFormat());
                    
                    // 兼容性处理：TEXT类型的规则项支持从format字段获取文本值
                    String textValue = ruleReq.getTextValue();
                    if ("TEXT".equalsIgnoreCase(ruleReq.getItemType()) && textValue == null && ruleReq.getFormat() != null) {
                        textValue = ruleReq.getFormat();
                    }
                    rule.setTextValue(textValue);
                    
                    // 兼容性处理：FIELD_REF类型的规则项支持从format字段获取引用字段ID
                    Long refFieldId = ruleReq.getRefFieldId();
                    if ("FIELD_REF".equalsIgnoreCase(ruleReq.getItemType()) && refFieldId == null && ruleReq.getFormat() != null) {
                        try {
                            refFieldId = Long.parseLong(ruleReq.getFormat());
                            log.info("FIELD_REF规则项从format字段解析出引用字段ID: {}", refFieldId);
                        } catch (NumberFormatException e) {
                            log.warn("FIELD_REF规则项的format字段无法解析为字段ID: {}", ruleReq.getFormat());
                        }
                    }
                    rule.setRefFieldId(refFieldId);
                    rule.setIsEnabled(ruleReq.getIsEnabled() != null ? ruleReq.getIsEnabled() : StatusEnumUtil.ENABLED);
                    rule.setAppId(entityField != null ? entityField.getAppId() : null);

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
                autoNumberConfigBuildService.deleteByFieldId(fieldId);
                log.info("删除字段 {} 的自动编号配置", fieldId);
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
            var options = fieldOptionService.listByFieldId(field.getId());
            if (options != null && !options.isEmpty()) {
                List<FieldOptionRespVO> optionVOs = options.stream().map(o -> {
                    FieldOptionRespVO item = new FieldOptionRespVO();
                    item.setId(o.getId() != null ? String.valueOf(o.getId()) : null);
                    item.setFieldId(o.getFieldId());
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
        FieldConstraintRespVO constraintVO = fieldConstraintService.getFieldConstraintConfig(field.getId());
        if (constraintVO != null) {
            vo.setConstraints(constraintVO);
        }

    // 填充自动编号完整配置（规则项）
    MetadataAutoNumberConfigDO config = autoNumberConfigBuildService.getByFieldId(field.getId());
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
    }

    /**
     * 转换自动编号配置DO为响应VO
     */
    private AutoNumberConfigRespVO convertToAutoNumberConfigRespVO(MetadataAutoNumberConfigDO config) {
        AutoNumberConfigRespVO vo = new AutoNumberConfigRespVO();
        vo.setId(config.getId());
        vo.setFieldId(config.getFieldId());
        vo.setIsEnabled(config.getIsEnabled());
        vo.setNumberMode(config.getNumberMode());
        vo.setDigitWidth(config.getDigitWidth());
        vo.setOverflowContinue(config.getOverflowContinue());
        vo.setInitialValue(config.getInitialValue());
        vo.setResetCycle(config.getResetCycle());
        vo.setResetOnInitialChange(config.getResetOnInitialChange());
        vo.setRunMode(config.getRunMode());
        vo.setAppId(config.getAppId());
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
        vo.setConfigId(rule.getConfigId());
        vo.setItemType(rule.getItemType());
        vo.setItemOrder(rule.getItemOrder());
        vo.setFormat(rule.getFormat());
        vo.setTextValue(rule.getTextValue());
        vo.setRefFieldId(rule.getRefFieldId());
        vo.setIsEnabled(rule.getIsEnabled());
        vo.setAppId(rule.getAppId());
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
        vo.setEntityId(field.getEntityId() != null ? String.valueOf(field.getEntityId()) : null);
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
        vo.setRunMode(field.getRunMode());
        vo.setAppId(field.getAppId() != null ? String.valueOf(field.getAppId()) : null);
        vo.setStatus(field.getStatus());
        vo.setFieldCode(field.getFieldCode());
        vo.setDictTypeId(field.getDictTypeId());
        // 注意：options、constraints、autoNumberConfig 将在 populateFieldRelatedData 中填充
        return vo;
    }

    /**
     * 处理长度校验，同步到 MetadataValidationLengthDO
     * 根据TODO需求：数据长度除了在MetadataEntityFieldDO中存储相关的信息，还需要在MetadataValidationLengthDO也储存一份
     * MetadataEntityFieldDO 只存最大程度，如果MetadataValidationLengthDO已经有数据了，那么只需保证maxLength和 MetadataEntityFieldDO中dataLength一致即可
     * 如果没有数据，那么新增一条记录，新增的时候MetadataValidationRuleGroupDO和MetadataValidationUniqueDO都需要新增数据
     * rg_name可以用display_name+field_name+长度进行拼接，然后同一个字段只能有一个唯一校验
     */
    private void processLengthValidation(Long fieldId, MetadataEntityFieldDO entityField) {
        try {
            // 检查是否已存在长度校验规则
            var existingValidation = validationLengthService.getByFieldIdWithRgName(fieldId);
            
            if (entityField.getDataLength() != null && entityField.getDataLength() > 0) {
                // 需要同步长度校验
                if (existingValidation != null) {
                    // 如果已经有数据了，那么只需保证maxLength和 MetadataEntityFieldDO中dataLength一致即可
                    var updateReqVO = new com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationLengthUpdateReqVO();
                    Long targetGroupId = existingValidation.getGroupId();
                    if (targetGroupId == null) {
                        var existingDO = validationLengthService.getByFieldId(fieldId);
                        if (existingDO != null) {
                            targetGroupId = existingDO.getGroupId();
                        }
                    }
                    if (targetGroupId == null) {
                        log.warn("长度校验同步失败，字段ID: {}, 缺少规则组ID，跳过更新", fieldId);
                        return;
                    }
                    updateReqVO.setId(targetGroupId);
                    updateReqVO.setMaxLength(entityField.getDataLength()); // 最大长度与dataLength保持一致
                    updateReqVO.setMinLength(existingValidation.getMinLength()); // 保持原有最小长度
                    updateReqVO.setIsEnabled(1); // 启用长度校验
                    updateReqVO.setRgName(existingValidation.getRgName()); // 保持原有规则组名称
                    updateReqVO.setPromptMessage(existingValidation.getPromptMessage()); // 保持原有提示信息
                    updateReqVO.setTrimBefore(existingValidation.getTrimBefore()); // 保持原有设置
                    updateReqVO.setRunMode(entityField.getRunMode() != null ? entityField.getRunMode() : 0);
                    validationLengthService.update(updateReqVO);
                } else {
                    // 如果没有数据，那么新增一条记录
                    var saveReqVO = new com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationLengthSaveReqVO();
                    saveReqVO.setEntityId(entityField.getEntityId());
                    saveReqVO.setFieldId(fieldId);
                    saveReqVO.setMaxLength(entityField.getDataLength()); // 最大长度与dataLength保持一致
                    saveReqVO.setMinLength(null); // 最小长度默认为null，允许为空
                    saveReqVO.setIsEnabled(1); // 启用长度校验
                    
                    // 使用统一的规则组命名方法
                    String rgName = buildLengthRuleGroupName(fieldId);
                    saveReqVO.setRgName(rgName);
                    
                    // 生成默认提示语：{字段展示名称}长度不能超过X个字符
                    String fieldDisplayName = entityField.getDisplayName() != null && !entityField.getDisplayName().trim().isEmpty() 
                        ? entityField.getDisplayName() 
                        : (entityField.getFieldName() != null ? entityField.getFieldName() : "字段");
                    String promptMsg = String.format("%s长度不能超过%d个字符", fieldDisplayName, entityField.getDataLength());
                    saveReqVO.setPromptMessage(promptMsg);
                    // 设置popPrompt确保errorMessage字段能正确返回
                    saveReqVO.setPopPrompt(promptMsg);
                    saveReqVO.setRunMode(entityField.getRunMode() != null ? entityField.getRunMode() : 0);
                    
                    validationLengthService.create(saveReqVO);
                }
            } else {
                // 不需要长度校验，如果存在则删除
                if (existingValidation != null) {
                    validationLengthService.deleteByFieldId(fieldId);
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
            var existingValidation = validationRequiredService.getByFieldIdWithRgName(fieldId);
            
            if (entityField.getIsRequired() != null && entityField.getIsRequired() == 1) {
                // 需要启用必填校验
                if (existingValidation != null) {
                    // 如果已经有数据了，那么只需保证is_enabled和 MetadataEntityFieldDO中isRequired一致即可
                    var updateReqVO = new com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRequiredUpdateReqVO();
                    Long targetGroupId = existingValidation.getGroupId();
                    if (targetGroupId == null) {
                        var existingDO = validationRequiredService.getByFieldId(fieldId);
                        if (existingDO != null) {
                            targetGroupId = existingDO.getGroupId();
                        }
                    }
                    if (targetGroupId == null) {
                        log.warn("必填校验同步失败，字段ID: {}, 缺少规则组ID，跳过更新", fieldId);
                        return;
                    }
                    updateReqVO.setId(targetGroupId);
                    updateReqVO.setIsEnabled(entityField.getIsRequired());
                    updateReqVO.setRgName(existingValidation.getRgName()); // 保持原有规则组名称
                    updateReqVO.setPromptMessage(existingValidation.getPromptMessage()); // 保持原有提示信息
                    validationRequiredService.update(updateReqVO);
                } else {
                    // 如果没有数据，那么新增一条记录
                    var saveReqVO = new com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRequiredSaveReqVO();
                    saveReqVO.setEntityId(entityField.getEntityId());
                    saveReqVO.setFieldId(fieldId);
                    saveReqVO.setIsEnabled(entityField.getIsRequired());
                    
                    // rg_name可以用display_name+field_name+必填校验进行拼接
                    String rgName = buildRequiredRuleGroupName(fieldId);
                    saveReqVO.setRgName(rgName);
                    
                    // 生成默认提示语：{字段展示名称}为必填项
                    String fieldDisplayName = entityField.getDisplayName() != null && !entityField.getDisplayName().trim().isEmpty() 
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
                if (existingValidation != null) {
                    validationRequiredService.deleteByFieldId(fieldId);
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
            var existingValidation = validationUniqueService.getByFieldIdWithRgName(fieldId);
            
            if (entityField.getIsUnique() != null && entityField.getIsUnique() == 1) {
                // 需要启用唯一性校验
                if (existingValidation != null) {
                    // 如果已经有数据了，那么只需保证is_enabled和 MetadataEntityFieldDO中isUnique一致即可
                    var updateReqVO = new com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationUniqueUpdateReqVO();
                    Long targetGroupId = existingValidation.getGroupId();
                    if (targetGroupId == null) {
                        var existingDO = validationUniqueService.getByFieldId(fieldId);
                        if (existingDO != null) {
                            targetGroupId = existingDO.getGroupId();
                        }
                    }
                    if (targetGroupId == null) {
                        log.warn("唯一校验同步失败，字段ID: {}, 缺少规则组ID，跳过更新", fieldId);
                        return;
                    }
                    updateReqVO.setId(targetGroupId);
                    updateReqVO.setIsEnabled(entityField.getIsUnique());
                    updateReqVO.setRgName(existingValidation.getRgName()); // 保持原有规则组名称
                    updateReqVO.setPromptMessage(existingValidation.getPromptMessage()); // 保持原有提示信息
                    validationUniqueService.update(updateReqVO);
                } else {
                    // 如果没有数据，那么新增一条记录
                    var saveReqVO = new com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationUniqueSaveReqVO();
                    saveReqVO.setEntityId(entityField.getEntityId());
                    saveReqVO.setFieldId(fieldId);
                    saveReqVO.setIsEnabled(entityField.getIsUnique());
                    
                    // rg_name可以用display_name+field_name+唯一校验进行拼接
                    String rgName = buildUniqueRuleGroupName(fieldId);
                    saveReqVO.setRgName(rgName);
                    
                    // 生成默认提示语：{字段展示名称}必须唯一
                    String fieldDisplayName = entityField.getDisplayName() != null && !entityField.getDisplayName().trim().isEmpty() 
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
                if (existingValidation != null) {
                    validationUniqueService.deleteByFieldId(fieldId);
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
     * @param fieldId 字段ID
     * @param validationType 校验类型（REQUIRED/UNIQUE/LENGTH/RANGE/FORMAT/CHILD_NOT_EMPTY/SELF_DEFINED）
     * @return 规则组名称
     */
    private String buildRuleGroupName(Long fieldId, String validationType) {
        try {
            // 获取字段信息
            DefaultConfigStore cs = new DefaultConfigStore();
            cs.and("id", fieldId);
            MetadataEntityFieldDO field = metadataEntityFieldRepository.findOne(cs);
            if (field == null) {
                log.warn("构建规则组名称失败，字段不存在: fieldId={}", fieldId);
                return getValidationTypeName(validationType) + "-未知字段-未知实体";
            }
            
            // 获取实体信息
            MetadataBusinessEntityDO entity = metadataBusinessEntityCoreService.getBusinessEntity(field.getEntityId());
            
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
     * @param fieldId 字段ID
     * @param entityId 实体ID
     * @throws IllegalArgumentException 如果校验规则违反唯一性约束
     */
    private void validateValidationRuleUniqueness(Long fieldId, Long entityId) {
        if (fieldId == null || entityId == null) {
            return;
        }

        // 获取字段信息用于错误提示
        DefaultConfigStore cs = new DefaultConfigStore();
        cs.and("id", fieldId);
        MetadataEntityFieldDO field = metadataEntityFieldRepository.findOne(cs);
        String fieldDisplayName = field != null && field.getDisplayName() != null 
            ? field.getDisplayName() 
            : (field != null && field.getFieldName() != null ? field.getFieldName() : "未知字段");

        // 检查必填校验规则
        List<com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRequiredDO> requiredList = 
            validationRequiredRepository.findByFieldId(fieldId);
        long enabledRequiredCount = requiredList.stream()
            .filter(r -> r.getIsEnabled() != null && r.getIsEnabled() == 1)
            .count();
        if (enabledRequiredCount > 1) {
            throw new IllegalArgumentException(String.format(
                "字段【%s】存在多条生效的必填校验规则，同一字段的同一种校验类型只能有一条生效规则", 
                fieldDisplayName
            ));
        }

        // 检查唯一性校验规则
        List<com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationUniqueDO> uniqueList = 
            validationUniqueRepository.findByFieldId(fieldId);
        long enabledUniqueCount = uniqueList.stream()
            .filter(u -> u.getIsEnabled() != null && u.getIsEnabled() == 1)
            .count();
        if (enabledUniqueCount > 1) {
            throw new IllegalArgumentException(String.format(
                "字段【%s】存在多条生效的唯一性校验规则，同一字段的同一种校验类型只能有一条生效规则", 
                fieldDisplayName
            ));
        }

        // 检查长度校验规则
        List<com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationLengthDO> lengthList = 
            validationLengthRepository.findByFieldId(fieldId);
        long enabledLengthCount = lengthList.stream()
            .filter(l -> l.getIsEnabled() != null && l.getIsEnabled() == 1)
            .count();
        if (enabledLengthCount > 1) {
            throw new IllegalArgumentException(String.format(
                "字段【%s】存在多条生效的长度校验规则，同一字段的同一种校验类型只能有一条生效规则", 
                fieldDisplayName
            ));
        }

        // 检查格式校验规则
        List<com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationFormatDO> formatList = 
            validationFormatRepository.findByFieldId(fieldId);
        long enabledFormatCount = formatList.stream()
            .filter(f -> f.getIsEnabled() != null && f.getIsEnabled() == 1)
            .count();
        if (enabledFormatCount > 1) {
            throw new IllegalArgumentException(String.format(
                "字段【%s】存在多条生效的格式校验规则，同一字段的同一种校验类型只能有一条生效规则", 
                fieldDisplayName
            ));
        }

        log.debug("字段【{}】(ID: {})的校验规则唯一性检查通过", fieldDisplayName, fieldId);
    }

}
