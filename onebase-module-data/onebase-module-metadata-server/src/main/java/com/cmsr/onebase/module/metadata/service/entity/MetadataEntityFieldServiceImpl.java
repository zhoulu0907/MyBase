package com.cmsr.onebase.module.metadata.service.entity;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.*;
import com.cmsr.onebase.module.metadata.service.entity.vo.EntityFieldQueryVO;
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
import com.cmsr.onebase.module.metadata.enums.FieldTypeEnum;
import com.cmsr.onebase.module.metadata.enums.BusinessEntityTypeEnum;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private com.cmsr.onebase.module.metadata.service.entity.MetadataBusinessEntityService metadataBusinessEntityService;
    @Resource
    private MetadataDatasourceService metadataDatasourceService;
    @Resource
    private MetadataEntityFieldOptionService fieldOptionService;
    @Resource
    private MetadataEntityFieldConstraintService fieldConstraintService;
    @Resource
    private AutoNumberConfigService autoNumberConfigService;

    @Override
    public List<FieldTypeConfigRespVO> getFieldTypes() {
        return Arrays.stream(FieldTypeEnum.values())
                .map(this::convertToFieldTypeConfigRespVO)
                .toList();
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
            entityField.setIsRequired(fieldItem.getIsRequired() != null ? fieldItem.getIsRequired() : 1); // 默认1-不是必填
            entityField.setIsUnique(fieldItem.getIsUnique() != null ? fieldItem.getIsUnique() : 1); // 默认1-不是唯一
            entityField.setAllowNull(fieldItem.getAllowNull() != null ? fieldItem.getAllowNull() : 0); // 默认0-是允许空值
            entityField.setDefaultValue(fieldItem.getDefaultValue());
            entityField.setSortOrder(fieldItem.getSortOrder() != null ? fieldItem.getSortOrder() : 0);
            entityField.setIsSystemField(1); // 1-不是系统字段
            entityField.setIsPrimaryKey(1); // 1-不是主键
            entityField.setAppId(Long.valueOf(reqVO.getAppId()));

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
            configStore.and(MetadataEntityFieldDO.ENTITY_ID, Long.valueOf(queryVO.getEntityId()));
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

        configStore.order(MetadataEntityFieldDO.SORT_ORDER, Order.TYPE.ASC);
        configStore.order("create_time", Order.TYPE.DESC);

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
        var constraints = fieldConstraintService.listByFieldId(entityField.getId());
        if (constraints != null && !constraints.isEmpty()) {
            FieldConstraintRespVO cr = new FieldConstraintRespVO();
            for (var c : constraints) {
                if ("LENGTH_RANGE".equalsIgnoreCase(c.getConstraintType())) {
                    cr.setLengthEnabled(c.getIsEnabled());
                    cr.setMinLength(c.getMinLength());
                    cr.setMaxLength(c.getMaxLength());
                    cr.setLengthPrompt(c.getPromptMessage());
                } else if ("REGEX".equalsIgnoreCase(c.getConstraintType())) {
                    cr.setRegexEnabled(c.getIsEnabled());
                    cr.setRegexPattern(c.getRegexPattern());
                    cr.setRegexPrompt(c.getPromptMessage());
                }
            }
            result.setConstraints(cr);
        }

        // 自动编号摘要
        MetadataAutoNumberConfigDO cfg = autoNumberConfigService.getByFieldId(entityField.getId());
        if (cfg != null) {
            EntityFieldAutoNumberBriefRespVO brief = new EntityFieldAutoNumberBriefRespVO();
            brief.setEnabled(cfg.getIsEnabled());
            brief.setMode(cfg.getNumberMode());
            brief.setDigitWidth(cfg.getDigitWidth() != null ? (int) cfg.getDigitWidth() : null);
            brief.setResetCycle(cfg.getResetCycle());
            result.setAutoNumber(brief);
        }

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
                DefaultConfigStore configStore = new DefaultConfigStore();
                configStore.and("id", Long.valueOf(firstFieldId));
                MetadataEntityFieldDO firstField = metadataEntityFieldRepository.findOne(configStore);
                if (firstField != null) {
                    businessEntity = metadataBusinessEntityService.getBusinessEntity(firstField.getEntityId());
                    if (businessEntity != null && businessEntity.getTableName() != null &&
                        !businessEntity.getTableName().trim().isEmpty()) {
                        datasource = metadataDatasourceService.getDatasource(businessEntity.getDatasourceId());
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
                if (item.getId() == null) {
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
            if (!Boolean.TRUE.equals(item.getIsDeleted()) && item.getId() != null) {
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
                DefaultConfigStore cs2 = new DefaultConfigStore();
                cs2.and("id", origin.getId());
                MetadataEntityFieldDO full = metadataEntityFieldRepository.findOne(cs2);
                if (datasource != null && businessEntity.getTableName() != null) {
                    alterColumnInTable(datasource, businessEntity.getTableName(), full);
                }
                resp.getUpdatedIds().add(item.getId());

                // 同步选项与约束（整体替换）
                Long fieldId = origin.getId();
                if (item.getOptions() != null) {
                    fieldOptionService.deleteByFieldId(fieldId);
                    for (var opt : item.getOptions()) {
                        com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldOptionDO d = new com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldOptionDO();
                        d.setFieldId(fieldId);
                        d.setOptionLabel(opt.getOptionLabel());
                        d.setOptionValue(opt.getOptionValue());
                        d.setOptionOrder(opt.getOptionOrder());
                        d.setIsEnabled(opt.getIsEnabled());
                        d.setDescription(opt.getDescription());
                        // 批量接口无 appId，沿用字段的 appId
                        d.setAppId(full != null ? full.getAppId() : null);
                        fieldOptionService.create(d);
                    }
                }
                if (item.getConstraints() != null) {
                    fieldConstraintService.deleteByFieldId(fieldId);
                    var c = item.getConstraints();
                    if (c.getMinLength() != null && c.getMaxLength() != null && c.getMinLength() > c.getMaxLength()) {
                        throw new IllegalArgumentException("最小长度不能大于最大长度");
                    }
                    if (c.getMinLength() != null || c.getMaxLength() != null || c.getLengthEnabled() != null || (c.getLengthPrompt() != null && !c.getLengthPrompt().isEmpty())) {
                        var d1 = new com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldConstraintDO();
                        d1.setFieldId(fieldId);
                        d1.setConstraintType("LENGTH_RANGE");
                        d1.setMinLength(c.getMinLength());
                        d1.setMaxLength(c.getMaxLength());
                        d1.setPromptMessage(c.getLengthPrompt());
                        d1.setIsEnabled(c.getLengthEnabled());
                        d1.setRunMode(full != null ? full.getRunMode() : 0);
                        d1.setAppId(full != null ? full.getAppId() : null);
                        fieldConstraintService.upsert(d1);
                    }
                    if (c.getRegexPattern() != null || c.getRegexEnabled() != null || (c.getRegexPrompt() != null && !c.getRegexPrompt().isEmpty())) {
                        var d2 = new com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldConstraintDO();
                        d2.setFieldId(fieldId);
                        d2.setConstraintType("REGEX");
                        d2.setRegexPattern(c.getRegexPattern());
                        d2.setPromptMessage(c.getRegexPrompt());
                        d2.setIsEnabled(c.getRegexEnabled());
                        d2.setRunMode(full != null ? full.getRunMode() : 0);
                        d2.setAppId(full != null ? full.getAppId() : null);
                        fieldConstraintService.upsert(d2);
                    }
                }
            }
        }

        // 4. 最后新增
        for (EntityFieldUpsertItemVO item : reqVO.getItems()) {
            if (!Boolean.TRUE.equals(item.getIsDeleted()) && item.getId() == null) {
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
                // 修复：正确设置isSystemField，如果前端传入则使用传入值，否则默认为0（系统字段）
                toCreate.setIsSystemField(item.getIsSystemField() != null ? item.getIsSystemField() : 0);
                toCreate.setIsPrimaryKey(1); // 1表示不是主键

                metadataEntityFieldRepository.insert(toCreate);

                if (datasource != null && businessEntity.getTableName() != null) {
                    addColumnToTable(datasource, businessEntity.getTableName(), toCreate);
                }
                resp.getCreatedIds().add(String.valueOf(toCreate.getId()));

                // 同步选项与约束（整体替换）
                Long fieldId = toCreate.getId();
                if (item.getOptions() != null) {
                    fieldOptionService.deleteByFieldId(fieldId);
                    for (var opt : item.getOptions()) {
                        com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldOptionDO d = new com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldOptionDO();
                        d.setFieldId(fieldId);
                        d.setOptionLabel(opt.getOptionLabel());
                        d.setOptionValue(opt.getOptionValue());
                        d.setOptionOrder(opt.getOptionOrder());
                        d.setIsEnabled(opt.getIsEnabled());
                        d.setDescription(opt.getDescription());
                        d.setAppId(toCreate.getAppId());
                        fieldOptionService.create(d);
                    }
                }
                if (item.getConstraints() != null) {
                    fieldConstraintService.deleteByFieldId(fieldId);
                    var c = item.getConstraints();
                    if (c.getMinLength() != null && c.getMaxLength() != null && c.getMinLength() > c.getMaxLength()) {
                        throw new IllegalArgumentException("最小长度不能大于最大长度");
                    }
                    if (c.getMinLength() != null || c.getMaxLength() != null || c.getLengthEnabled() != null || (c.getLengthPrompt() != null && !c.getLengthPrompt().isEmpty())) {
                        var d1 = new com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldConstraintDO();
                        d1.setFieldId(fieldId);
                        d1.setConstraintType("LENGTH_RANGE");
                        d1.setMinLength(c.getMinLength());
                        d1.setMaxLength(c.getMaxLength());
                        d1.setPromptMessage(c.getLengthPrompt());
                        d1.setIsEnabled(c.getLengthEnabled());
                        d1.setRunMode(toCreate.getRunMode());
                        d1.setAppId(toCreate.getAppId());
                        fieldConstraintService.upsert(d1);
                    }
                    if (c.getRegexPattern() != null || c.getRegexEnabled() != null || (c.getRegexPrompt() != null && !c.getRegexPrompt().isEmpty())) {
                        var d2 = new com.cmsr.onebase.module.metadata.dal.dataobject.field.MetadataEntityFieldConstraintDO();
                        d2.setFieldId(fieldId);
                        d2.setConstraintType("REGEX");
                        d2.setRegexPattern(c.getRegexPattern());
                        d2.setPromptMessage(c.getRegexPrompt());
                        d2.setIsEnabled(c.getRegexEnabled());
                        d2.setRunMode(toCreate.getRunMode());
                        d2.setAppId(toCreate.getAppId());
                        fieldConstraintService.upsert(d2);
                    }
                }
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
        configStore.and(MetadataEntityFieldDO.ENTITY_ID, Long.valueOf(entityId));

        // fieldCode字段已注释，跳过根据fieldCode查找逻辑
        // 直接根据fieldName查找

        // 其次根据fieldName查找
        if (item.getFieldName() != null && !item.getFieldName().trim().isEmpty()) {
            DefaultConfigStore nameConfigStore = new DefaultConfigStore();
            nameConfigStore.and(MetadataEntityFieldDO.ENTITY_ID, Long.valueOf(entityId));
            nameConfigStore.and(MetadataEntityFieldDO.FIELD_NAME, item.getFieldName());
            MetadataEntityFieldDO existingField = metadataEntityFieldRepository.findOne(nameConfigStore);
            if (existingField != null) {
                return existingField;
            }
        }

        return null;
    }

    /**
     * 将字段类型枚举转换为响应VO
     *
     * @param fieldTypeEnum 字段类型枚举
     * @return 字段类型配置响应VO
     */
    private FieldTypeConfigRespVO convertToFieldTypeConfigRespVO(FieldTypeEnum fieldTypeEnum) {
        FieldTypeConfigRespVO respVO = new FieldTypeConfigRespVO();
        respVO.setFieldType(fieldTypeEnum.getFieldType());
        respVO.setDisplayName(fieldTypeEnum.getDisplayName());
        respVO.setCategory(fieldTypeEnum.getCategory());
        respVO.setSupportLength(fieldTypeEnum.getSupportLength());
        respVO.setSupportDecimal(fieldTypeEnum.getSupportDecimal());
        respVO.setDefaultLength(fieldTypeEnum.getDefaultLength());
        respVO.setMaxLength(fieldTypeEnum.getMaxLength());
        // 对于支持小数位的类型，设置默认小数位数
        if (fieldTypeEnum.getSupportDecimal()) {
            respVO.setDefaultDecimal(2);
        }
        return respVO;
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
        // 校验存在
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
        Long longId = Long.valueOf(id);
        if (metadataEntityFieldRepository.findById(longId) == null) {
            throw exception(ENTITY_FIELD_NOT_EXISTS);
        }
    }

    private void validateEntityFieldNameUnique(String id, String entityId, String fieldName) {
        Long longEntityId = Long.valueOf(entityId);
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataEntityFieldDO.ENTITY_ID, longEntityId);
        configStore.and(MetadataEntityFieldDO.FIELD_NAME, fieldName);
        if (id != null) {
            Long longId = Long.valueOf(id);
            configStore.and(Compare.NOT_EQUAL, "id", longId);
        }

        long count = metadataEntityFieldRepository.countByConfig(configStore);
        if (count > 0) {
            throw exception(ENTITY_FIELD_CODE_DUPLICATE);
        }
    }

    @Override
    public MetadataEntityFieldDO getEntityField(String id) {
        Long longId = Long.valueOf(id);
        return metadataEntityFieldRepository.findById(longId);
    }

    @Override
    public PageResult<MetadataEntityFieldDO> getEntityFieldPage(EntityFieldPageReqVO pageReqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        // 添加查询条件
        if (pageReqVO.getEntityId() != null && !pageReqVO.getEntityId().trim().isEmpty()) {
            configStore.and(MetadataEntityFieldDO.ENTITY_ID, Long.valueOf(pageReqVO.getEntityId()));
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
            configStore.and(MetadataEntityFieldDO.APP_ID, Long.valueOf(pageReqVO.getAppId()));
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
        Long longEntityId = Long.valueOf(entityId);
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataEntityFieldDO.ENTITY_ID, longEntityId);
        configStore.order(MetadataEntityFieldDO.SORT_ORDER, Order.TYPE.ASC);
        configStore.order("create_time", Order.TYPE.DESC);
        return metadataEntityFieldRepository.findAllByConfig(configStore);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEntityFieldsByEntityId(String entityId) {
        Long longEntityId = Long.valueOf(entityId);
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

            // 简单尝试查询表，如果表不存在会抛出异常
            String testSql = "SELECT 1 FROM " + tableName + " LIMIT 1";
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
     * 修改表中的列
     */
    private void alterColumnInTable(MetadataDatasourceDO datasource, String tableName, MetadataEntityFieldDO field) {
        try {
            // 使用TenantUtils.executeIgnore包装操作，忽略租户条件
            TenantUtils.executeIgnore(() -> {
                // 创建 AnylineService 实例
                AnylineService<?> service = temporaryDatasourceService.createTemporaryService(datasource);

                // 生成修改列 DDL
                String alterColumnDDL = generateAlterColumnDDL(tableName, field);

                // 执行修改列语句
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
        ddl.append("ALTER TABLE \"").append(tableName).append("\" ADD COLUMN \"")
           .append(field.getFieldName()).append("\" ");

        // 字段类型映射
        String columnType = mapFieldType(field.getFieldType(), field.getDataLength());
        ddl.append(columnType);

        // 是否必填
        if (field.getIsRequired() != null && Boolean.TRUE.equals(field.getIsRequired() == 0)) {
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

        // 修改是否允许为空
        if (field.getIsRequired() != null) {
            if (Boolean.TRUE.equals(field.getIsRequired() == 0)) {
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
        switch (fieldType.toUpperCase()) {
            case "BIGINT":
                return "BIGINT";
            case "VARCHAR":
                return "VARCHAR(" + (dataLength != null && dataLength > 0 ? dataLength : 255) + ")";
            case "TEXT":
                return "TEXT";
            case "TIMESTAMP":
                return "TIMESTAMP";
            case "BOOLEAN":
                return "BOOLEAN";
            case "INTEGER":
                return "INTEGER";
            case "DECIMAL":
                return "DECIMAL(18,2)";
            case "DATE":
                return "DATE";
            case "TIME":
                return "TIME";
            case "JSON":
                return "JSONB";
            default:
                return "VARCHAR(" + (dataLength != null && dataLength > 0 ? dataLength : 255) + ")"; // 默认类型
        }
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
}
