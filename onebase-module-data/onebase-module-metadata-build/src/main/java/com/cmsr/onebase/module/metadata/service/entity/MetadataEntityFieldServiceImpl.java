package com.cmsr.onebase.module.metadata.service.entity;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.*;
import com.cmsr.onebase.module.metadata.service.entity.vo.EntityFieldQueryVO;
import com.cmsr.onebase.module.metadata.service.number.AutoNumberRuleService;
import com.cmsr.onebase.module.metadata.service.component.MetadataComponentFieldTypeService;
import com.cmsr.onebase.module.metadata.util.StatusEnumUtil;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.dal.database.TemporaryDatasourceService;
import com.cmsr.onebase.module.metadata.service.datasource.MetadataDatasourceService;
import com.cmsr.onebase.module.metadata.service.field.MetadataEntityFieldOptionService;
import com.cmsr.onebase.module.metadata.service.field.MetadataEntityFieldConstraintService;
import com.cmsr.onebase.module.metadata.service.number.AutoNumberConfigService;
import com.cmsr.onebase.module.metadata.dal.dataobject.number.MetadataAutoNumberConfigDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.number.MetadataAutoNumberRuleItemDO;
import com.cmsr.onebase.module.metadata.enums.BusinessEntityTypeEnum;
import com.cmsr.onebase.module.metadata.enums.BooleanStatusEnum;
import com.cmsr.onebase.module.metadata.enums.CommonStatusEnum;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Order;
import org.anyline.entity.Compare;
import org.anyline.entity.DataSet;
import org.anyline.entity.DataRow;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.enums.ErrorCodeConstants.ENTITY_FIELD_NOT_EXISTS;
import static com.cmsr.onebase.module.metadata.enums.ErrorCodeConstants.ENTITY_FIELD_CODE_DUPLICATE;

/**
 * 实体字段 Service 实现类
 */
@Service
@Slf4j
public class MetadataEntityFieldServiceImpl implements MetadataEntityFieldService {

    @Resource
    private MetadataEntityFieldRepository metadataEntityFieldRepository;
    @Resource
    private TemporaryDatasourceService temporaryDatasourceService;
    @Resource
    private MetadataBusinessEntityService metadataBusinessEntityService;
    @Resource
    private MetadataDatasourceService metadataDatasourceService;
    @Resource
    private MetadataEntityFieldOptionService fieldOptionService;
    @Resource
    private MetadataEntityFieldConstraintService fieldConstraintService;
    @Resource
    private AutoNumberConfigService autoNumberConfigService;
    @Resource
    private AutoNumberRuleService autoNumberRuleService;
    @Resource
    private MetadataComponentFieldTypeService componentFieldTypeService;
    @Resource
    private AnylineService<?> anylineService;
    
    @Resource
    private ModelMapper modelMapper;

    @Override
    public List<FieldTypeConfigRespVO> getFieldTypes() {
        // 从MetadataComponentFieldTypeDO中读取字段类型配置，替代原来的枚举方式
        return componentFieldTypeService.getFieldTypeConfigs();
    }

    @Override
    public List<EntityFieldValidationTypesRespVO> getFieldValidationTypes(@Valid EntityFieldValidationTypesReqVO reqVO) {
        List<String> fieldIds = reqVO.getFieldIdList();
        if (fieldIds == null || fieldIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 1) 批量查询字段，获取 fieldId -> fieldType 映射
    DefaultConfigStore cs = new DefaultConfigStore();
    cs.and(Compare.IN, "id", fieldIds.stream()
        .filter(s -> s != null && !s.trim().isEmpty())
        .map(s -> Long.valueOf(s.trim()))
        .collect(Collectors.toList()));
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

        // 2) 一次性联表查询：字段类型 -> 校验类型列表
        String inClause = typeCodes.stream().map(code -> "'" + code.replace("'", "''") + "'")
                .collect(Collectors.joining(","));
    // 说明：不同环境对 status 语义可能不一致（0/1 含义相反），这里不做 status 过滤，仅按 deleted=0 过滤，确保有数据即可返回
    String sql = "select mcft.field_type_code, mvt.validation_code, mvt.validation_name, mvt.validation_desc, mpo.sort_order " +
        "from metadata_permit_ref_otft mpo " +
        "join metadata_component_field_type mcft on mpo.field_type_id = mcft.id " +
        "join metadata_validation_type mvt on mpo.validation_type_id = mvt.id " +
        "where mcft.deleted = 0 and mvt.deleted = 0 and mpo.deleted = 0 " +
        "and mcft.field_type_code in (" + inClause + ") " +
        "order by mcft.field_type_code, mpo.sort_order";

        DataSet ds = anylineService.querys(sql);
        Map<String, List<EntityFieldValidationTypesRespVO.ValidationTypeItem>> typeToValidation = new HashMap<>();
        for (DataRow row : ds) {
            String ftCode = row.getString("field_type_code");
            EntityFieldValidationTypesRespVO.ValidationTypeItem item = new EntityFieldValidationTypesRespVO.ValidationTypeItem();
            item.setCode(row.getString("validation_code"));
            item.setName(row.getString("validation_name"));
            item.setDescription(row.getString("validation_desc"));
            item.setSortOrder(row.getInt("sort_order"));
            typeToValidation.computeIfAbsent(ftCode, k -> new ArrayList<>()).add(item);
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
            businessEntity = metadataBusinessEntityService.getBusinessEntity(Long.valueOf(reqVO.getEntityId()));
            log.info("获取到业务实体: {}, 表名: {}, 数据源ID: {}",
                businessEntity != null ? businessEntity.getId() : "null",
                businessEntity != null ? businessEntity.getTableName() : "null",
                businessEntity != null ? businessEntity.getDatasourceId() : "null");

            if (businessEntity != null && businessEntity.getTableName() != null &&
                !businessEntity.getTableName().trim().isEmpty()) {
                datasource = metadataDatasourceService.getDatasource(businessEntity.getDatasourceId());
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
            // 创建字段及数据库插入操作
            MetadataEntityFieldDO entityField = new MetadataEntityFieldDO();
            entityField.setEntityId(Long.valueOf(reqVO.getEntityId()));
            entityField.setFieldName(fieldItem.getFieldName());
            entityField.setDisplayName(fieldItem.getDisplayName());
            entityField.setFieldType(fieldItem.getFieldType());
            entityField.setDataLength(fieldItem.getDataLength());
            entityField.setDescription(fieldItem.getDescription());
            // 使用新的枚举值：1-是，0-否
            entityField.setIsRequired(fieldItem.getIsRequired() != null ? fieldItem.getIsRequired() : StatusEnumUtil.NO); // 默认0-不是必填
            entityField.setIsUnique(fieldItem.getIsUnique() != null ? fieldItem.getIsUnique() : StatusEnumUtil.NO); // 默认0-不是唯一
            entityField.setAllowNull(fieldItem.getAllowNull() != null ? fieldItem.getAllowNull() : StatusEnumUtil.YES); // 默认1-是允许空值
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
                        businessEntity = metadataBusinessEntityService.getBusinessEntity(firstField.getEntityId());
                        if (businessEntity != null && businessEntity.getTableName() != null &&
                            !businessEntity.getTableName().trim().isEmpty()) {
                            datasource = metadataDatasourceService.getDatasource(businessEntity.getDatasourceId());
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
            if (fieldItem.getDataLength() != null) {
                updateObj.setDataLength(fieldItem.getDataLength());
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
        MetadataBusinessEntityDO businessEntity = metadataBusinessEntityService.getBusinessEntity(Long.valueOf(reqVO.getEntityId()));
        if (businessEntity == null) {
            throw new IllegalArgumentException("业务实体不存在");
        }
        MetadataDatasourceDO datasource = null;
        if (businessEntity.getTableName() != null && !businessEntity.getTableName().trim().isEmpty()) {
            datasource = metadataDatasourceService.getDatasource(businessEntity.getDatasourceId());
        }

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

                // 实体是否允许改表结构
                validateEntityAllowModifyStructure(existing.getEntityId());

                // 先删子配置（选项、约束、自动编号）
                if (existing != null) {
                    try {
                        fieldOptionService.deleteByFieldId(existing.getId());
                        fieldConstraintService.deleteByFieldId(existing.getId());
                        autoNumberConfigService.deleteByFieldId(existing.getId());
                    } catch (Exception ignore) {}
                }

                // 先删库记录
                metadataEntityFieldRepository.deleteById(Long.valueOf(item.getId()));

                // 同步物理表
                if (datasource != null && businessEntity.getTableName() != null) {
                    dropColumnFromTable(datasource, businessEntity.getTableName(), existing.getFieldName());
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

                // 名称唯一性（若改名）
                String newName = item.getFieldName() != null ? item.getFieldName() : origin.getFieldName();
                validateEntityFieldNameUnique(item.getId(), origin.getEntityId().toString(), newName);

                validateEntityAllowModifyStructure(origin.getEntityId());

                // 组装更新对象（只覆盖非空字段）
                MetadataEntityFieldDO upd = new MetadataEntityFieldDO();
                upd.setId(origin.getId());
                upd.setEntityId(origin.getEntityId());
                if (item.getFieldName() != null) upd.setFieldName(item.getFieldName());
                if (item.getDisplayName() != null) upd.setDisplayName(item.getDisplayName());
                if (item.getFieldType() != null) upd.setFieldType(item.getFieldType());
                if (item.getDataLength() != null) upd.setDataLength(item.getDataLength());
                if (item.getDecimalPlaces() != null) upd.setDecimalPlaces(item.getDecimalPlaces());
                if (item.getDefaultValue() != null) upd.setDefaultValue(item.getDefaultValue());
                if (item.getDescription() != null) upd.setDescription(item.getDescription());
                if (item.getIsRequired() != null) upd.setIsRequired(item.getIsRequired());
                if (item.getIsUnique() != null) upd.setIsUnique(item.getIsUnique());
                if (item.getAllowNull() != null) upd.setAllowNull(item.getAllowNull());
                if (item.getSortOrder() != null) upd.setSortOrder(item.getSortOrder());
                // fieldCode字段已注释，不再处理
                // if (item.getFieldCode() != null) upd.setFieldCode(item.getFieldCode());
                // 修复：正确处理isSystemField字段的更新
                if (item.getIsSystemField() != null) upd.setIsSystemField(item.getIsSystemField());

                metadataEntityFieldRepository.update(upd);

                // 同步物理表（需要完整字段信息）
                // 若本次更新修改了字段名，先尝试对物理列执行重命名，避免后续ALTER因新列不存在而失败
                if (datasource != null && businessEntity.getTableName() != null && item.getFieldName() != null
                        && !item.getFieldName().equals(origin.getFieldName())) {
                    try {
                        renameColumnInTable(datasource, businessEntity.getTableName(), origin.getFieldName(), item.getFieldName());
                    } catch (Exception e) {
                        log.warn("重命名物理列失败，将在后续逻辑中回退为新增列处理。old={}, new={}, err={}",
                                origin.getFieldName(), item.getFieldName(), e.getMessage());
                    }
                }
                DefaultConfigStore cs2 = new DefaultConfigStore();
                cs2.and("id", origin.getId());
                MetadataEntityFieldDO full = metadataEntityFieldRepository.findOne(cs2);
                if (datasource != null && businessEntity.getTableName() != null) {
                    alterColumnInTable(datasource, businessEntity.getTableName(), full);
                }
                resp.getUpdatedIds().add(item.getId());

                // 同步选项、约束和自动编号（整体替换）
                Long fieldId = origin.getId();
                processFieldRelatedData(fieldId, full, item.getOptions(), item.getConstraints(), item.getAutoNumber());
            }
        }

        // 4. 最后新增
        for (EntityFieldUpsertItemVO item : reqVO.getItems()) {
            // 新增：当未提供ID或ID为空白时进入
            if (!Boolean.TRUE.equals(item.getIsDeleted()) && (item.getId() == null || item.getId().trim().isEmpty())) {
                // 智能处理：如果没有传ID，但fieldCode或fieldName已存在，则自动转换为更新操作
                MetadataEntityFieldDO existingField = findExistingFieldByCodeOrName(reqVO.getEntityId(), item);
                if (existingField != null) {
                    log.info("发现已存在的字段，自动转换为更新操作: fieldName={}, existingId={}",
                            item.getFieldName(), existingField.getId());

                    // 转换为更新操作
                    item.setId(existingField.getId().toString());

                    // 组装更新对象（只覆盖非空字段）
                    MetadataEntityFieldDO upd = new MetadataEntityFieldDO();
                    upd.setId(existingField.getId());
                    upd.setEntityId(existingField.getEntityId());
                    if (item.getFieldName() != null) upd.setFieldName(item.getFieldName());
                    if (item.getDisplayName() != null) upd.setDisplayName(item.getDisplayName());
                    if (item.getFieldType() != null) upd.setFieldType(item.getFieldType());
                    if (item.getDataLength() != null) upd.setDataLength(item.getDataLength());
                    if (item.getDecimalPlaces() != null) upd.setDecimalPlaces(item.getDecimalPlaces());
                    if (item.getDefaultValue() != null) upd.setDefaultValue(item.getDefaultValue());
                    if (item.getDescription() != null) upd.setDescription(item.getDescription());
                    if (item.getIsRequired() != null) upd.setIsRequired(item.getIsRequired());
                    if (item.getIsUnique() != null) upd.setIsUnique(item.getIsUnique());
                    if (item.getAllowNull() != null) upd.setAllowNull(item.getAllowNull());
                    if (item.getSortOrder() != null) upd.setSortOrder(item.getSortOrder());
                    // fieldCode字段已注释，不再处理
                    // if (item.getFieldCode() != null) upd.setFieldCode(item.getFieldCode());
                    // 关键：正确处理isSystemField字段的更新
                    if (item.getIsSystemField() != null) upd.setIsSystemField(item.getIsSystemField());

                    metadataEntityFieldRepository.update(upd);

                    // 同步物理表（需要完整字段信息）
                    // 如果修改了字段名，优先执行物理列重命名
                    if (datasource != null && businessEntity.getTableName() != null && item.getFieldName() != null
                            && !item.getFieldName().equals(existingField.getFieldName())) {
                        try {
                            renameColumnInTable(datasource, businessEntity.getTableName(), existingField.getFieldName(), item.getFieldName());
                        } catch (Exception e) {
                            log.warn("重命名物理列失败，将在后续逻辑中回退为新增列处理。old={}, new={}, err={}",
                                    existingField.getFieldName(), item.getFieldName(), e.getMessage());
                        }
                    }
                    DefaultConfigStore cs2 = new DefaultConfigStore();
                    cs2.and("id", existingField.getId());
                    MetadataEntityFieldDO full = metadataEntityFieldRepository.findOne(cs2);
                    if (datasource != null && businessEntity.getTableName() != null) {
                        alterColumnInTable(datasource, businessEntity.getTableName(), full);
                    }
                    resp.getUpdatedIds().add(existingField.getId().toString());
                    continue; // 跳过新增逻辑
                }

                // 确实是新增字段的情况
                // 名称唯一
                validateEntityFieldNameUnique(null, reqVO.getEntityId(), item.getFieldName());
                validateEntityAllowModifyStructure(Long.valueOf(reqVO.getEntityId()));

                MetadataEntityFieldDO toCreate = new MetadataEntityFieldDO();
                toCreate.setEntityId(Long.valueOf(reqVO.getEntityId()));
                toCreate.setAppId(Long.valueOf(reqVO.getAppId()));
                toCreate.setFieldName(item.getFieldName());
                toCreate.setDisplayName(item.getDisplayName());
                toCreate.setFieldType(item.getFieldType());
                toCreate.setDataLength(item.getDataLength());
                toCreate.setDecimalPlaces(item.getDecimalPlaces());
                toCreate.setDefaultValue(item.getDefaultValue());
                toCreate.setDescription(item.getDescription());
                toCreate.setIsRequired(item.getIsRequired());
                toCreate.setIsUnique(item.getIsUnique());
                toCreate.setAllowNull(item.getAllowNull());
                toCreate.setSortOrder(item.getSortOrder());
                // fieldCode字段已注释，自动生成
                toCreate.setFieldCode(generateFieldCode(item.getFieldName()));
                // 使用新的枚举值：1-是，0-否
                toCreate.setIsSystemField(item.getIsSystemField() != null ? item.getIsSystemField() : StatusEnumUtil.YES); // 默认1-是系统字段
                toCreate.setIsPrimaryKey(StatusEnumUtil.NO); // 0-不是主键
                // 设置默认运行模式，防止后续约束/自动编号处理中出现空指针
                toCreate.setRunMode(0);

                metadataEntityFieldRepository.insert(toCreate);

                if (datasource != null && businessEntity.getTableName() != null) {
                    addColumnToTable(datasource, businessEntity.getTableName(), toCreate);
                }
                resp.getCreatedIds().add(String.valueOf(toCreate.getId()));

                // 同步选项、约束和自动编号（整体替换）
                Long fieldId = toCreate.getId();
                processFieldRelatedData(fieldId, toCreate, item.getOptions(), item.getConstraints(), item.getAutoNumber());
            }
        }

        return resp;
    }

    /**
     * 根据字段编码或字段名查找已存在的字段
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
        // 校验字段名唯一性
        validateEntityFieldNameUnique(null, createReqVO.getEntityId(), createReqVO.getFieldName());

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
            MetadataBusinessEntityDO businessEntity = metadataBusinessEntityService.getBusinessEntity(Long.valueOf(createReqVO.getEntityId()));
            if (businessEntity != null && businessEntity.getTableName() != null &&
                !businessEntity.getTableName().trim().isEmpty()) {
                MetadataDatasourceDO datasource = metadataDatasourceService.getDatasource(businessEntity.getDatasourceId());
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
        // 校验字段名唯一性
        validateEntityFieldNameUnique(updateReqVO.getId(), updateReqVO.getEntityId(), updateReqVO.getFieldName());
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
            MetadataBusinessEntityDO businessEntity = metadataBusinessEntityService.getBusinessEntity(Long.valueOf(updateReqVO.getEntityId()));
            if (businessEntity != null && businessEntity.getTableName() != null &&
                !businessEntity.getTableName().trim().isEmpty()) {
                MetadataDatasourceDO datasource = metadataDatasourceService.getDatasource(businessEntity.getDatasourceId());
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
            autoNumberConfigService.deleteByFieldId(existingField.getId());
        }

        // 删除实体字段
        metadataEntityFieldRepository.deleteById(longId);

        // 从物理表删除字段
        if (existingField != null) {
            try {
                MetadataBusinessEntityDO businessEntity = metadataBusinessEntityService.getBusinessEntity(existingField.getEntityId());
                if (businessEntity != null && businessEntity.getTableName() != null &&
                    !businessEntity.getTableName().trim().isEmpty()) {
                    MetadataDatasourceDO datasource = metadataDatasourceService.getDatasource(businessEntity.getDatasourceId());
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
            throw exception(ENTITY_FIELD_CODE_DUPLICATE);
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

        // 添加排序：按照字段排序优先，然后按创建时间倒序
        configStore.order(MetadataEntityFieldDO.SORT_ORDER, Order.TYPE.ASC);
        configStore.order("create_time", Order.TYPE.DESC);

        // 分页查询
        return metadataEntityFieldRepository.findPageWithConditions(configStore, pageReqVO.getPageNo(), pageReqVO.getPageSize());
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
            businessEntity = metadataBusinessEntityService.getBusinessEntity(Long.valueOf(entityId));
                            if (businessEntity != null && businessEntity.getTableName() != null &&
                    !businessEntity.getTableName().trim().isEmpty()) {
                    datasource = metadataDatasourceService.getDatasource(businessEntity.getDatasourceId());
                }
        } catch (Exception e) {
            log.error("获取业务实体信息失败: {}", e.getMessage(), e);
        }

        for (MetadataEntityFieldDO field : fields) {
            // 删除数据库记录
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
                    throw new RuntimeException("表 " + tableName + " 不存在，请先创建表");
                }

                // 检查列是否已存在
                if (checkColumnExists(service, tableName, field.getFieldName())) {
                    log.warn("列 {} 已存在于表 {} 中，跳过添加操作", field.getFieldName(), tableName);
                    return null;
                }

                // 生成添加列 DDL
                String addColumnDDL = generateAddColumnDDL(tableName, field);

                // 执行添加列语句
                service.execute(addColumnDDL);

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
     */
    private boolean checkTableExists(AnylineService<?> service, String tableName) {
        try {
            log.info("检查表是否存在 - 表名: {}", tableName);

            // 简单尝试查询表，给表名加双引号处理PostgreSQL大小写问题
            String testSql = "SELECT 1 FROM \"" + tableName + "\" LIMIT 1";
            service.querys(testSql);

            log.info("表 {} 存在，当前连接的数据库: {}", tableName, getCurrentDatabase(service));
            return true;
        } catch (Exception e) {
            log.warn("表 {} 不存在或查询失败: {}", tableName, e.getMessage());
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
     *
     * @param service AnylineService实例
     * @param tableName 表名
     * @param columnName 列名
     * @return 如果列存在返回true，否则返回false
     */
    private boolean checkColumnExists(AnylineService<?> service, String tableName, String columnName) {
        try {
            log.info("检查列是否存在 - 表名: {}, 列名: {}", tableName, columnName);

            // 使用参数化查询 + ILIKE（PostgreSQL 不区分大小写匹配），一次查询既判断存在也可拿到详情
            String sql = "SELECT table_name, column_name, data_type FROM information_schema.columns "
                + "WHERE table_schema = ? "
                + "AND table_name ILIKE ? "
                + "AND column_name ILIKE ? "
                + "LIMIT 1";

            DataSet resultSet = service.querys(sql, "public", tableName, columnName);
            boolean exists = resultSet != null && resultSet.size() > 0;

            log.info("列 {} 在表 {} 中{}存在", columnName, tableName, exists ? "" : "不");

            if (exists) {
                try {
                    if (resultSet != null && resultSet.size() > 0) {
                        DataRow row = resultSet.getRow(0);
                        log.info("已存在的列详情: 表名={}, 列名={}, 数据类型={}",
                                row.get("table_name"), row.get("column_name"), row.get("data_type"));
                    }
                } catch (Exception detailException) {
                    log.debug("获取列详细信息失败: {}", detailException.getMessage());
                }
            }

            return exists;
        } catch (Exception e) {
            log.error("检查列 {} 在表 {} 中是否存在时发生错误: {}", columnName, tableName, e.getMessage(), e);
            // 发生异常时，使用更保守的策略：尝试直接查询表结构来确认
            try {
                log.info("使用备用方法检查列是否存在: {}.{}", tableName, columnName);
                String backupSql = "SELECT column_name FROM information_schema.columns WHERE table_name = ? AND column_name = ? AND table_schema = 'public'";
                DataSet backupResult = service.querys(backupSql, tableName, columnName);
                boolean backupExists = backupResult != null && backupResult.size() > 0;
                log.info("备用检查结果：列 {} 在表 {} 中{}存在", columnName, tableName, backupExists ? "" : "不");
                return backupExists;
            } catch (Exception backupException) {
                log.error("备用列存在性检查也失败: {}", backupException.getMessage(), backupException);
                // 最后的保险措施：假设列不存在，但在执行DDL时使用IF NOT EXISTS
                return false;
            }
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
     */
    private void alterColumnInTable(MetadataDatasourceDO datasource, String tableName, MetadataEntityFieldDO field) {
        try {
            // 使用TenantUtils.executeIgnore包装操作，忽略租户条件
            TenantUtils.executeIgnore(() -> {
                // 创建 AnylineService 实例
                AnylineService<?> service = temporaryDatasourceService.createTemporaryService(datasource);
                // 先校验表与列是否存在
                if (!checkTableExists(service, tableName)) {
                    throw new RuntimeException("表 " + tableName + " 不存在，请先创建表");
                }

                boolean exists = checkColumnExists(service, tableName, field.getFieldName());
                if (!exists) {
                    // 列不存在：容错处理，改为新增列，防止 ALTER 失败
                    String addColumnDDL = generateAddColumnDDL(tableName, field);
                    service.execute(addColumnDDL);
                    log.info("列 {} 在表 {} 中不存在，已自动改为添加列", field.getFieldName(), tableName);
                    return null;
                }

                // 生成并执行修改列 DDL
                String alterColumnDDL = generateAlterColumnDDL(tableName, field);
                service.execute(alterColumnDDL);

                log.info("成功修改表 {} 的列: {}", tableName, field.getFieldName());
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
     */
    private void dropColumnFromTable(MetadataDatasourceDO datasource, String tableName, String fieldName) {
        try {
            // 使用TenantUtils.executeIgnore包装操作，忽略租户条件
            TenantUtils.executeIgnore(() -> {
                // 创建 AnylineService 实例
                AnylineService<?> service = temporaryDatasourceService.createTemporaryService(datasource);

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
     */
    private String generateAddColumnDDL(String tableName, MetadataEntityFieldDO field) {
        StringBuilder ddl = new StringBuilder();
        // 使用 IF NOT EXISTS 语法防止重复添加列的错误
        ddl.append("ALTER TABLE \"").append(tableName).append("\" ADD COLUMN IF NOT EXISTS \"")
           .append(field.getFieldName()).append("\" ");

        // 字段类型映射
        String columnType = mapFieldType(field.getFieldType(), field.getDataLength());
        ddl.append(columnType);

        // 是否必填 - 使用新的枚举值：1-是，0-否
        if (field.getIsRequired() != null && BooleanStatusEnum.isYes(field.getIsRequired())) {
            ddl.append(" NOT NULL");
        }

        // 默认值
        if (field.getDefaultValue() != null && !field.getDefaultValue().trim().isEmpty()) {
            ddl.append(" DEFAULT ").append(field.getDefaultValue());
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
     * 生成修改字段的DDL语句
     */
    private String generateAlterColumnDDL(String tableName, MetadataEntityFieldDO field) {
        StringBuilder ddl = new StringBuilder();

        // 修改字段类型
        String columnType = mapFieldType(field.getFieldType(), field.getDataLength());
        ddl.append("ALTER TABLE \"").append(tableName).append("\" ALTER COLUMN \"")
           .append(field.getFieldName()).append("\" TYPE ").append(columnType).append(";\n");

        // 修改是否允许为空 - 使用新的枚举值：1-是，0-否
        if (field.getIsRequired() != null) {
            if (BooleanStatusEnum.isYes(field.getIsRequired())) {
                ddl.append("ALTER TABLE \"").append(tableName).append("\" ALTER COLUMN \"")
                   .append(field.getFieldName()).append("\" SET NOT NULL;\n");
            } else {
                ddl.append("ALTER TABLE \"").append(tableName).append("\" ALTER COLUMN \"")
                   .append(field.getFieldName()).append("\" DROP NOT NULL;\n");
            }
        }

        // 修改默认值
        if (field.getDefaultValue() != null && !field.getDefaultValue().trim().isEmpty()) {
            ddl.append("ALTER TABLE \"").append(tableName).append("\" ALTER COLUMN \"")
               .append(field.getFieldName()).append("\" SET DEFAULT ").append(field.getDefaultValue()).append(";\n");
        }

        // 更新字段注释
        if (field.getDescription() != null && !field.getDescription().trim().isEmpty()) {
            ddl.append("COMMENT ON COLUMN \"").append(tableName).append("\".\"")
               .append(field.getFieldName()).append("\" IS '").append(field.getDescription()).append("';");
        }

        return ddl.toString();
    }

    /**
     * 生成删除字段的DDL语句
     */
    private String generateDropColumnDDL(String tableName, String fieldName) {
        return "ALTER TABLE \"" + tableName + "\" DROP COLUMN IF EXISTS \"" + fieldName + "\";";
    }

    /**
     * 字段类型映射
     */
    private String mapFieldType(String fieldType, Integer dataLength) {
        // 使用新的字段类型服务从MetadataComponentFieldTypeDO中读取映射关系
        return componentFieldTypeService.mapFieldTypeToDatabaseType(fieldType, dataLength);
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
        MetadataBusinessEntityDO businessEntity = metadataBusinessEntityService.getBusinessEntity(Long.valueOf(entityId));
        if (businessEntity == null) {
            throw new IllegalArgumentException("业务实体不存在");
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
        
        return modelMapper.map(entityField, EntityFieldRespVO.class);
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
                .map(field -> modelMapper.map(field, EntityFieldRespVO.class))
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
        MetadataAutoNumberConfigDO config = autoNumberConfigService.getByFieldId(fieldId);
        if (config != null) {
            AutoNumberConfigRespVO autoNumberConfig = convertToAutoNumberConfigRespVO(config);
            // 获取规则项列表
            List<MetadataAutoNumberRuleItemDO> rules = autoNumberConfigService.listRules(config.getId());
            List<AutoNumberRuleItemRespVO> ruleVOs = rules.stream()
                    .map(this::convertToAutoNumberRuleItemRespVO)
                    .toList();
            autoNumberConfig.setRules(ruleVOs);
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
        // 处理选项
        if (options != null) {
            fieldOptionService.deleteByFieldId(fieldId);
            for (var opt : options) {
                com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldOptionDO d = 
                    new com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldOptionDO();
                d.setFieldId(fieldId);
                d.setOptionLabel(opt.getOptionLabel());
                d.setOptionValue(opt.getOptionValue());
                d.setOptionOrder(opt.getOptionOrder());
                d.setIsEnabled(opt.getIsEnabled());
                d.setDescription(opt.getDescription());
                d.setAppId(entityField != null ? entityField.getAppId() : null);
                fieldOptionService.create(d);
            }
        }

        // 处理约束
        if (constraints != null) {
            fieldConstraintService.deleteByFieldId(fieldId);
            processFieldConstraints(fieldId, entityField, constraints);
        }

        // 处理自动编号
        if (autoNumber != null) {
            processAutoNumberConfig(fieldId, entityField, autoNumber);
        }
    }

    /**
     * 处理字段约束
     */
    private void processFieldConstraints(Long fieldId, MetadataEntityFieldDO entityField, FieldConstraintRespVO constraints) {
        if (constraints.getMinLength() != null && constraints.getMaxLength() != null &&
                constraints.getMinLength() > constraints.getMaxLength()) {
            throw new IllegalArgumentException("最小长度不能大于最大长度");
        }

        // 长度
        if (constraints.getMinLength() != null || constraints.getMaxLength() != null ||
                constraints.getLengthEnabled() != null ||
                (constraints.getLengthPrompt() != null && !constraints.getLengthPrompt().isEmpty())) {
            FieldConstraintSaveReqVO req = new FieldConstraintSaveReqVO();
            req.setFieldId(fieldId);
            req.setConstraintType("LENGTH_RANGE");
            req.setMinLength(constraints.getMinLength());
            req.setMaxLength(constraints.getMaxLength());
            req.setPromptMessage(constraints.getLengthPrompt());
            req.setIsEnabled(constraints.getLengthEnabled());
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
            FieldConstraintSaveReqVO req = new FieldConstraintSaveReqVO();
            req.setFieldId(fieldId);
            req.setConstraintType("REQUIRED");
            req.setIsEnabled(entityField.getIsRequired());
            req.setPromptMessage(null);
            req.setRunMode(entityField.getRunMode() != null ? entityField.getRunMode() : 0);
            req.setAppId(entityField.getAppId());
            fieldConstraintService.saveFieldConstraintConfig(req);
        }

        // 唯一（与 isUnique 联动）
        if (entityField != null && entityField.getIsUnique() != null) {
            FieldConstraintSaveReqVO req = new FieldConstraintSaveReqVO();
            req.setFieldId(fieldId);
            req.setConstraintType("UNIQUE");
            req.setIsEnabled(entityField.getIsUnique());
            req.setPromptMessage(null);
            req.setRunMode(entityField.getRunMode() != null ? entityField.getRunMode() : 0);
            req.setAppId(entityField.getAppId());
            fieldConstraintService.saveFieldConstraintConfig(req);
        }
    }

    /**
     * 处理自动编号配置
     */
    private void processAutoNumberConfig(Long fieldId, MetadataEntityFieldDO entityField, AutoNumberConfigReqVO autoNumber) {
        // 先删除现有配置
        autoNumberConfigService.deleteByFieldId(fieldId);
        
        // 使用新的枚举值：1-启用，0-禁用
        if (autoNumber.getIsEnabled() != null && CommonStatusEnum.isEnabled(autoNumber.getIsEnabled())) {
            // 创建自动编号配置
            MetadataAutoNumberConfigDO config = new MetadataAutoNumberConfigDO();
            config.setFieldId(fieldId);
            config.setIsEnabled(autoNumber.getIsEnabled());
            config.setNumberMode(autoNumber.getNumberMode());
            config.setDigitWidth(autoNumber.getDigitWidth());
            config.setOverflowContinue(autoNumber.getOverflowContinue());
            config.setInitialValue(autoNumber.getInitialValue() != null ? autoNumber.getInitialValue() : 1L);
            config.setResetCycle(autoNumber.getResetCycle());
            config.setRunMode(entityField != null && entityField.getRunMode() != null ? entityField.getRunMode() : 0);
            config.setAppId(entityField != null ? entityField.getAppId() : null);
            
            Long configId = autoNumberConfigService.upsert(config);
            
            // 处理规则项
            if (autoNumber.getRules() != null && !autoNumber.getRules().isEmpty()) {
                for (AutoNumberRuleItemReqVO ruleReq : autoNumber.getRules()) {
                    MetadataAutoNumberRuleItemDO rule = new MetadataAutoNumberRuleItemDO();
                    rule.setConfigId(configId);
                    rule.setItemType(ruleReq.getItemType());
                    rule.setItemOrder(ruleReq.getItemOrder());
                    rule.setFormat(ruleReq.getFormat());
                    rule.setTextValue(ruleReq.getTextValue());
                    rule.setRefFieldId(ruleReq.getRefFieldId());
                    // 使用新的枚举值：1-启用，0-禁用
                    rule.setIsEnabled(ruleReq.getIsEnabled() != null ? ruleReq.getIsEnabled() : StatusEnumUtil.ENABLED);
                    rule.setAppId(entityField != null ? entityField.getAppId() : null);
                    
                    autoNumberRuleService.add(rule);
                }
            }
        }
    }

    /**
     * 填充字段相关数据到响应VO
     */
    private void populateFieldRelatedData(MetadataEntityFieldDO field, EntityFieldRespVO vo) {
        // 填充选项信息
        if ("SINGLE_SELECT".equalsIgnoreCase(field.getFieldType()) || 
            "MULTI_SELECT".equalsIgnoreCase(field.getFieldType()) ||
            "PICKLIST".equalsIgnoreCase(field.getFieldType())) {
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
    MetadataAutoNumberConfigDO config = autoNumberConfigService.getByFieldId(field.getId());
    if (config != null) {
        // 完整配置
        AutoNumberConfigRespVO full = convertToAutoNumberConfigRespVO(config);
        // 规则项
        List<MetadataAutoNumberRuleItemDO> rules = autoNumberConfigService.listRules(config.getId());
        List<AutoNumberRuleItemRespVO> ruleVOs = rules.stream()
            .map(this::convertToAutoNumberRuleItemRespVO)
            .toList();
        full.setRules(ruleVOs);
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
}
