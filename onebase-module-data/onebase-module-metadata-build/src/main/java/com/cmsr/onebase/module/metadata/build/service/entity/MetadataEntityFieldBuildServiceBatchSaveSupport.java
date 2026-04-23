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
public abstract class MetadataEntityFieldBuildServiceBatchSaveSupport extends MetadataEntityFieldBuildServiceCrudSupport {
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EntityFieldBatchSaveRespVO batchSaveEntityFields(@Valid EntityFieldBatchSaveReqVO reqVO) {
        String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        long totalStartNs = System.nanoTime();
        int itemCount = reqVO.getItems() != null ? reqVO.getItems().size() : 0;
        log.info("metadata.entityField.batchSave start traceId={}, entityId={}, appId={}, items={}",
                traceId, reqVO.getEntityId(), reqVO.getApplicationId(), itemCount);

        long stageStartNs = System.nanoTime();
        Long resolvedEntityId = idUuidConverter.resolveEntityId(reqVO.getEntityId());
        BatchSaveTimings timings = new BatchSaveTimings();
        timings.resolveEntityIdMs = elapsedMs(stageStartNs);

        EntityFieldBatchSaveRespVO resp = new EntityFieldBatchSaveRespVO();

        stageStartNs = System.nanoTime();
        BatchSaveContext context = prepareBatchSaveContext(reqVO, resolvedEntityId);
        timings.loadEntityAndDatasourceMs = elapsedMs(stageStartNs);

        stageStartNs = System.nanoTime();
        validateBatchSaveInput(reqVO, context);
        timings.validateBatchDuplicationMs = elapsedMs(stageStartNs);

        stageStartNs = System.nanoTime();
        BatchSaveCounts counts = new BatchSaveCounts();
        counts.deleted = processDeletedFields(reqVO, context, resp);
        timings.deletePhaseMs = elapsedMs(stageStartNs);

        stageStartNs = System.nanoTime();
        counts.updated = processUpdatedFields(reqVO, context, resp);
        timings.updatePhaseMs = elapsedMs(stageStartNs);

        stageStartNs = System.nanoTime();
        counts.created = processCreatedFields(reqVO, context, resp);
        timings.createPhaseMs = elapsedMs(stageStartNs);

        stageStartNs = System.nanoTime();
        syncBatchValidationRules(context);
        timings.validationSyncMs = elapsedMs(stageStartNs);
        validateValidationRuleUniquenessBatch(context.touchedFieldsByUuid, context.validationUniquenessCheckFieldUuids);

        stageStartNs = System.nanoTime();
        executeBatchPhysicalTableOperations(context);
        timings.physicalOpsMs = elapsedMs(stageStartNs);

        logBatchSaveSummary(traceId, totalStartNs, timings, counts, context.physicalTableOps.size());

        return resp;
    }

    protected BatchSaveContext prepareBatchSaveContext(EntityFieldBatchSaveReqVO reqVO, Long resolvedEntityId) {
        BatchSaveContext context = new BatchSaveContext();
        context.businessEntity = loadMutableBusinessEntity(resolvedEntityId);
        context.entityUuidForField = resolveEntityUuidForField(context.businessEntity, resolvedEntityId);
        reqVO.setEntityId(context.entityUuidForField);
        context.datasource = loadDatasourceForBatchSave(context.businessEntity);
        return context;
    }

    protected MetadataBusinessEntityDO loadMutableBusinessEntity(Long resolvedEntityId) {
        MetadataBusinessEntityDO businessEntity = metadataBusinessEntityRepository.getById(resolvedEntityId);
        if (businessEntity == null) {
            throw new IllegalArgumentException("业务实体不存在");
        }
        if (!BusinessEntityTypeEnum.allowModifyTableStructure(businessEntity.getEntityType())) {
            BusinessEntityTypeEnum entityType = BusinessEntityTypeEnum.getByCode(businessEntity.getEntityType());
            String typeName = entityType != null ? entityType.getName() : "未知类型";
            throw new IllegalArgumentException(
                    String.format("实体类型为 %s (%s)，不允许修改表结构", typeName, businessEntity.getEntityType()));
        }
        return businessEntity;
    }

    protected String resolveEntityUuidForField(MetadataBusinessEntityDO businessEntity, Long resolvedEntityId) {
        String entityUuidForField = businessEntity.getEntityUuid();
        if (entityUuidForField == null || entityUuidForField.trim().isEmpty()) {
            return String.valueOf(resolvedEntityId);
        }
        return entityUuidForField;
    }

    protected MetadataDatasourceDO loadDatasourceForBatchSave(MetadataBusinessEntityDO businessEntity) {
        if (businessEntity.getTableName() == null || businessEntity.getTableName().trim().isEmpty()) {
            return null;
        }
        return metadataDatasourceBuildService.getDatasourceByUuid(businessEntity.getDatasourceUuid());
    }

    protected void validateBatchSaveInput(EntityFieldBatchSaveReqVO reqVO, BatchSaveContext context) {
        validateFieldNameDuplicationInBatch(reqVO.getItems());
        List<MetadataEntityFieldDO> existingFields = metadataEntityFieldRepository
                .getEntityFieldListByEntityUuid(context.entityUuidForField);
        context.fieldsById = loadFieldsByIds(reqVO.getItems());
        validateBatchFieldUniqueness(context.entityUuidForField, reqVO.getApplicationId(), reqVO.getItems(),
                existingFields, context.fieldsById);
    }

    protected int processDeletedFields(EntityFieldBatchSaveReqVO reqVO, BatchSaveContext context,
            EntityFieldBatchSaveRespVO resp) {
        int deletedCount = 0;
        for (EntityFieldUpsertItemVO item : reqVO.getItems()) {
            if (Boolean.TRUE.equals(item.getIsDeleted())) {
                deleteBatchField(reqVO, context, resp, item);
                deletedCount++;
            }
        }
        return deletedCount;
    }

    protected void deleteBatchField(EntityFieldBatchSaveReqVO reqVO, BatchSaveContext context,
            EntityFieldBatchSaveRespVO resp, EntityFieldUpsertItemVO item) {
        Long fieldId = parseDeleteFieldId(item);
        MetadataEntityFieldDO existing = context.fieldsById.get(fieldId);
        validateBatchFieldOwnership(reqVO, item, existing, "删除");
        deleteFieldRelatedData(existing.getFieldUuid());
        metadataEntityFieldRepository.removeById(Long.valueOf(item.getId()));
        appendDropOperationIfNeeded(context, existing);
        resp.getDeletedIds().add(item.getId());
    }

    protected Long parseDeleteFieldId(EntityFieldUpsertItemVO item) {
        if (item.getId() == null || item.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("删除操作必须提供字段ID");
        }
        try {
            return Long.valueOf(item.getId().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("删除操作必须提供字段ID");
        }
    }

    protected Long parseUpdateFieldId(EntityFieldUpsertItemVO item) {
        try {
            return Long.valueOf(item.getId().trim());
        } catch (NumberFormatException e) {
            throw exception(ENTITY_FIELD_NOT_EXISTS);
        }
    }

    protected void validateBatchFieldOwnership(EntityFieldBatchSaveReqVO reqVO, EntityFieldUpsertItemVO item,
            MetadataEntityFieldDO field, String actionName) {
        if (field == null) {
            throw new IllegalArgumentException("字段不存在: " + item.getId());
        }
        if (!field.getEntityUuid().equals(reqVO.getEntityId())) {
            log.error("安全校验失败：尝试跨实体{}字段。fieldId={}, 字段归属entityUuid={}, 请求entityId={}",
                    actionName, item.getId(), field.getEntityUuid(), reqVO.getEntityId());
            throw new IllegalArgumentException("字段不属于当前实体，禁止跨实体" + actionName);
        }
        if (!String.valueOf(field.getApplicationId()).equals(reqVO.getApplicationId())) {
            log.error("安全校验失败：尝试跨应用{}字段。fieldId={}, 字段归属appId={}, 请求appId={}",
                    actionName, item.getId(), field.getApplicationId(), reqVO.getApplicationId());
            throw new IllegalArgumentException("字段不属于当前应用，禁止跨应用" + actionName);
        }
    }

    protected void deleteFieldRelatedData(String fieldUuid) {
        fieldOptionService.deleteByFieldId(fieldUuid);
        fieldConstraintService.deleteByFieldId(fieldUuid);
        autoNumberConfigBuildService.deleteByFieldId(fieldUuid);
        validationRequiredService.deleteByFieldId(fieldUuid);
        validationUniqueService.deleteByFieldId(fieldUuid);
        validationLengthService.deleteByFieldId(fieldUuid);
        metadataEntityRelationshipBuildService.deleteRelationShipByFieldId(fieldUuid);
    }

    protected void appendDropOperationIfNeeded(BatchSaveContext context, MetadataEntityFieldDO existing) {
        if (shouldCollectPhysicalOperation(context)) {
            PhysicalTableOperation op = new PhysicalTableOperation();
            op.setOperationType(MetadataPhysicalTableOperationTypeEnum.DROP);
            op.setFieldName(existing.getFieldName());
            context.physicalTableOps.add(op);
        }
    }

    protected int processUpdatedFields(EntityFieldBatchSaveReqVO reqVO, BatchSaveContext context,
            EntityFieldBatchSaveRespVO resp) {
        int updatedCount = 0;
        for (EntityFieldUpsertItemVO item : reqVO.getItems()) {
            if (!Boolean.TRUE.equals(item.getIsDeleted()) && item.getId() != null && !item.getId().trim().isEmpty()) {
                updateBatchField(reqVO, context, resp, item);
                updatedCount++;
            }
        }
        return updatedCount;
    }

    protected void updateBatchField(EntityFieldBatchSaveReqVO reqVO, BatchSaveContext context,
            EntityFieldBatchSaveRespVO resp, EntityFieldUpsertItemVO item) {
        Long fieldId = parseUpdateFieldId(item);
        MetadataEntityFieldDO origin = context.fieldsById.get(fieldId);
        validateBatchFieldOwnership(reqVO, item, origin, "操作");
        Integer maxLength = extractMaxLength(item);
        metadataEntityFieldRepository.updateById(buildBatchUpdateField(origin, item, maxLength));
        MetadataEntityFieldDO updatedSnapshot = buildUpdatedSnapshot(origin, item, maxLength);
        touchBatchField(context, updatedSnapshot);
        collectPhysicalOperationsForUpdate(context, item, origin, updatedSnapshot, maxLength);
        resp.getUpdatedIds().add(item.getId());
        processFieldRelatedData(origin.getFieldUuid(), updatedSnapshot, item.getOptions(), item.getConstraints(),
                item.getAutoNumber());
        registerValidationChangesForUpdate(context, item, origin, updatedSnapshot, maxLength);
        processEntityRelationIfNeeded(reqVO.getApplicationId(), fieldId, updatedSnapshot, item);
    }

    protected MetadataEntityFieldDO buildBatchUpdateField(MetadataEntityFieldDO origin, EntityFieldUpsertItemVO item,
            Integer maxLength) {
        MetadataEntityFieldDO upd = new MetadataEntityFieldDO();
        upd.setId(origin.getId());
        upd.setEntityUuid(origin.getEntityUuid());
        if (item.getFieldName() != null)
            upd.setFieldName(item.getFieldName());
        if (item.getDisplayName() != null)
            upd.setDisplayName(item.getDisplayName());
        if (item.getFieldType() != null)
            upd.setFieldType(item.getFieldType());
        if (maxLength != null)
            upd.setDataLength(maxLength);
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
        if (item.getDictTypeId() != null)
            upd.setDictTypeId(item.getDictTypeId());
        return upd;
    }

    protected void touchBatchField(BatchSaveContext context, MetadataEntityFieldDO field) {
        context.touchedFieldsByUuid.put(field.getFieldUuid(), field);
        context.validationUniquenessCheckFieldUuids.add(field.getFieldUuid());
    }

    protected void collectPhysicalOperationsForUpdate(BatchSaveContext context, EntityFieldUpsertItemVO item,
            MetadataEntityFieldDO origin, MetadataEntityFieldDO updatedSnapshot, Integer maxLength) {
        if (!shouldCollectPhysicalOperation(context)) {
            return;
        }
        if (item.getFieldName() != null && !item.getFieldName().equals(origin.getFieldName())) {
            PhysicalTableOperation renameOp = new PhysicalTableOperation();
            renameOp.setOperationType(MetadataPhysicalTableOperationTypeEnum.RENAME);
            renameOp.setOldFieldName(origin.getFieldName());
            renameOp.setFieldName(item.getFieldName());
            context.physicalTableOps.add(renameOp);
        }
        if (needsAlterPhysicalColumn(item, origin, maxLength)) {
            PhysicalTableOperation alterOp = new PhysicalTableOperation();
            alterOp.setOperationType(MetadataPhysicalTableOperationTypeEnum.ALTER);
            alterOp.setFieldInfo(updatedSnapshot);
            context.physicalTableOps.add(alterOp);
            log.info("字段 {} 需要修改物理表结构", origin.getFieldName());
        } else {
            log.debug("字段 {} 无物理表结构变化，跳过 ALTER 操作", origin.getFieldName());
        }
    }

    protected boolean needsAlterPhysicalColumn(EntityFieldUpsertItemVO item, MetadataEntityFieldDO origin,
            Integer maxLength) {
        boolean needAlter = false;
        if (item.getFieldType() != null && !item.getFieldType().equals(origin.getFieldType())) {
            needAlter = true;
            log.debug("字段 {} 类型发生变化: {} -> {}", origin.getFieldName(), origin.getFieldType(), item.getFieldType());
        }
        if (maxLength != null && !maxLength.equals(origin.getDataLength())) {
            needAlter = true;
            log.debug("字段 {} 长度发生变化: {} -> {}", origin.getFieldName(), origin.getDataLength(), maxLength);
        }
        if (item.getDecimalPlaces() != null && !item.getDecimalPlaces().equals(origin.getDecimalPlaces())) {
            needAlter = true;
            log.debug("字段 {} 小数位数发生变化: {} -> {}", origin.getFieldName(), origin.getDecimalPlaces(), item.getDecimalPlaces());
        }
        if (item.getDefaultValue() != null && !item.getDefaultValue().equals(origin.getDefaultValue())) {
            needAlter = true;
            log.debug("字段 {} 默认值发生变化: {} -> {}", origin.getFieldName(), origin.getDefaultValue(), item.getDefaultValue());
        }
        return needAlter;
    }

    protected void registerValidationChangesForUpdate(BatchSaveContext context, EntityFieldUpsertItemVO item,
            MetadataEntityFieldDO origin, MetadataEntityFieldDO updatedSnapshot, Integer maxLength) {
        if (item.getIsRequired() != null && !item.getIsRequired().equals(origin.getIsRequired())) {
            context.requiredValidationSyncFieldUuids.add(updatedSnapshot.getFieldUuid());
            context.validationChangedFieldsByUuid.put(updatedSnapshot.getFieldUuid(), updatedSnapshot);
        }
        if (item.getIsUnique() != null && !item.getIsUnique().equals(origin.getIsUnique())) {
            context.uniqueValidationSyncFieldUuids.add(updatedSnapshot.getFieldUuid());
            context.validationChangedFieldsByUuid.put(updatedSnapshot.getFieldUuid(), updatedSnapshot);
        }
        if (maxLength != null && !maxLength.equals(origin.getDataLength()) && !hasConstraintsLengthConfig(item)) {
            context.lengthValidationSyncFieldUuids.add(updatedSnapshot.getFieldUuid());
            context.validationChangedFieldsByUuid.put(updatedSnapshot.getFieldUuid(), updatedSnapshot);
        }
    }

    protected int processCreatedFields(EntityFieldBatchSaveReqVO reqVO, BatchSaveContext context,
            EntityFieldBatchSaveRespVO resp) {
        int createdCount = 0;
        for (EntityFieldUpsertItemVO item : reqVO.getItems()) {
            if (!Boolean.TRUE.equals(item.getIsDeleted()) && (item.getId() == null || item.getId().trim().isEmpty())) {
                createBatchField(reqVO, context, resp, item);
                createdCount++;
            }
        }
        return createdCount;
    }

    protected void createBatchField(EntityFieldBatchSaveReqVO reqVO, BatchSaveContext context,
            EntityFieldBatchSaveRespVO resp, EntityFieldUpsertItemVO item) {
        log.info("未提供字段ID，将作为新增字段处理: fieldName={}, entityId={}, appId={}",
                item.getFieldName(), reqVO.getEntityId(), reqVO.getApplicationId());
        validateFieldNameNotDatabaseKeyword(item.getFieldName());
        Integer maxLength = extractMaxLength(item);
        MetadataEntityFieldDO toCreate = buildBatchCreateField(reqVO, item, maxLength);
        metadataEntityFieldRepository.save(toCreate);
        touchBatchField(context, toCreate);
        appendAddOperationIfNeeded(context, toCreate);
        resp.getCreatedIds().add(String.valueOf(toCreate.getId()));
        processFieldRelatedData(toCreate.getFieldUuid(), toCreate, item.getOptions(), item.getConstraints(),
                item.getAutoNumber());
        registerValidationChangesForCreate(context, item, toCreate, maxLength);
        processEntityRelationIfNeeded(reqVO.getApplicationId(), toCreate.getId(), toCreate, item);
    }

    protected MetadataEntityFieldDO buildBatchCreateField(EntityFieldBatchSaveReqVO reqVO,
            EntityFieldUpsertItemVO item, Integer maxLength) {
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
        toCreate.setIsSystemField(item.getIsSystemField() != null ? item.getIsSystemField() : StatusEnumUtil.YES);
        toCreate.setIsPrimaryKey(StatusEnumUtil.NO);
        toCreate.setVersionTag(0L);
        toCreate.setDictTypeId(item.getDictTypeId());
        if (toCreate.getFieldUuid() == null || toCreate.getFieldUuid().isEmpty()) {
            toCreate.setFieldUuid(UuidUtils.getUuid());
        }
        return toCreate;
    }

    protected void appendAddOperationIfNeeded(BatchSaveContext context, MetadataEntityFieldDO toCreate) {
        if (shouldCollectPhysicalOperation(context)) {
            PhysicalTableOperation addOp = new PhysicalTableOperation();
            addOp.setOperationType(MetadataPhysicalTableOperationTypeEnum.ADD);
            addOp.setFieldInfo(toCreate);
            context.physicalTableOps.add(addOp);
        }
    }

    protected void registerValidationChangesForCreate(BatchSaveContext context, EntityFieldUpsertItemVO item,
            MetadataEntityFieldDO toCreate, Integer maxLength) {
        if (item.getIsRequired() != null && item.getIsRequired() == 1) {
            context.requiredValidationSyncFieldUuids.add(toCreate.getFieldUuid());
            context.validationChangedFieldsByUuid.put(toCreate.getFieldUuid(), toCreate);
        }
        if (item.getIsUnique() != null && item.getIsUnique() == 1) {
            context.uniqueValidationSyncFieldUuids.add(toCreate.getFieldUuid());
            context.validationChangedFieldsByUuid.put(toCreate.getFieldUuid(), toCreate);
        }
        if (maxLength != null && maxLength > 0 && !hasConstraintsLengthConfig(item)) {
            context.lengthValidationSyncFieldUuids.add(toCreate.getFieldUuid());
            context.validationChangedFieldsByUuid.put(toCreate.getFieldUuid(), toCreate);
        }
    }

    protected boolean hasConstraintsLengthConfig(EntityFieldUpsertItemVO item) {
        return item.getConstraints() != null &&
                (item.getConstraints().getLengthEnabled() != null ||
                        item.getConstraints().getMinLength() != null ||
                        item.getConstraints().getMaxLength() != null ||
                        StringUtils.hasText(item.getConstraints().getLengthPrompt()));
    }

    protected void processEntityRelationIfNeeded(String applicationId, Long fieldId, MetadataEntityFieldDO field,
            EntityFieldUpsertItemVO item) {
        if (MetadataFieldTypeCodeEnum.isDataSelection(item.getFieldType())) {
            processEntityRelation(applicationId, fieldId, field, item);
        }
    }

    protected void syncBatchValidationRules(BatchSaveContext context) {
        if (!context.validationChangedFieldsByUuid.isEmpty()) {
            syncValidationRulesForFields(context.businessEntity, context.validationChangedFieldsByUuid,
                    context.requiredValidationSyncFieldUuids, context.uniqueValidationSyncFieldUuids,
                    context.lengthValidationSyncFieldUuids);
        }
    }

    protected void executeBatchPhysicalTableOperations(BatchSaveContext context) {
        if (shouldCollectPhysicalOperation(context) && !context.physicalTableOps.isEmpty()) {
            executePhysicalTableOperations(context.datasource, context.businessEntity.getTableName(), context.physicalTableOps);
        }
    }

    protected boolean shouldCollectPhysicalOperation(BatchSaveContext context) {
        return context.datasource != null && context.businessEntity.getTableName() != null;
    }

    protected void logBatchSaveSummary(String traceId, long totalStartNs, BatchSaveTimings timings,
            BatchSaveCounts counts, int physicalOpsCount) {
        long totalMs = elapsedMs(totalStartNs);
        log.info("metadata.entityField.batchSave done traceId={}, totalMs={}, resolveEntityIdMs={}, loadEntityAndDatasourceMs={}, validateBatchDuplicationMs={}, deleteMs={}, updateMs={}, createMs={}, validationSyncMs={}, physicalOpsMs={}, deleted={}, updated={}, created={}, physicalOps={}",
                traceId, totalMs, timings.resolveEntityIdMs, timings.loadEntityAndDatasourceMs,
                timings.validateBatchDuplicationMs, timings.deletePhaseMs, timings.updatePhaseMs,
                timings.createPhaseMs, timings.validationSyncMs, timings.physicalOpsMs,
                counts.deleted, counts.updated, counts.created, physicalOpsCount);
    }

    protected long elapsedMs(long startNs) {
        return (System.nanoTime() - startNs) / 1_000_000;
    }

    protected static class BatchSaveContext {
        protected MetadataBusinessEntityDO businessEntity;
        protected MetadataDatasourceDO datasource;
        protected String entityUuidForField;
        protected Map<Long, MetadataEntityFieldDO> fieldsById = new HashMap<>();
        protected List<PhysicalTableOperation> physicalTableOps = new java.util.ArrayList<>();
        protected Map<String, MetadataEntityFieldDO> touchedFieldsByUuid = new HashMap<>();
        protected Map<String, MetadataEntityFieldDO> validationChangedFieldsByUuid = new HashMap<>();
        protected Set<String> requiredValidationSyncFieldUuids = new TreeSet<>();
        protected Set<String> uniqueValidationSyncFieldUuids = new TreeSet<>();
        protected Set<String> lengthValidationSyncFieldUuids = new TreeSet<>();
        protected Set<String> validationUniquenessCheckFieldUuids = new TreeSet<>();
    }

    protected static class BatchSaveTimings {
        protected long resolveEntityIdMs;
        protected long loadEntityAndDatasourceMs;
        protected long validateBatchDuplicationMs;
        protected long deletePhaseMs;
        protected long updatePhaseMs;
        protected long createPhaseMs;
        protected long validationSyncMs;
        protected long physicalOpsMs;
    }

    protected static class BatchSaveCounts {
        protected int deleted;
        protected int updated;
        protected int created;
    }

    void syncValidationRulesForFields(MetadataBusinessEntityDO businessEntity,
            Map<String, MetadataEntityFieldDO> fieldsByUuid,
            Set<String> requiredFieldUuids,
            Set<String> uniqueFieldUuids,
            Set<String> lengthFieldUuids) {
        if (fieldsByUuid == null || fieldsByUuid.isEmpty()) {
            return;
        }
        if (businessEntity == null) {
            return;
        }
        if (requiredFieldUuids != null && !requiredFieldUuids.isEmpty()) {
            syncRequiredValidations(businessEntity, fieldsByUuid, requiredFieldUuids);
        }
        if (uniqueFieldUuids != null && !uniqueFieldUuids.isEmpty()) {
            syncUniqueValidations(businessEntity, fieldsByUuid, uniqueFieldUuids);
        }
        if (lengthFieldUuids != null && !lengthFieldUuids.isEmpty()) {
            syncLengthValidations(businessEntity, fieldsByUuid, lengthFieldUuids);
        }
    }

    void validateValidationRuleUniquenessBatch(Map<String, MetadataEntityFieldDO> fieldsByUuid, Set<String> fieldUuids) {
        if (fieldUuids == null || fieldUuids.isEmpty()) {
            return;
        }
        Map<String, Integer> requiredEnabledCount = new HashMap<>();
        Map<String, Integer> uniqueEnabledCount = new HashMap<>();
        Map<String, Integer> lengthEnabledCount = new HashMap<>();
        Map<String, Integer> formatEnabledCount = new HashMap<>();

        List<com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRequiredDO> requiredList = validationRequiredRepository
                .findByFieldUuids(fieldUuids);
        for (var r : requiredList) {
            if (r == null || r.getFieldUuid() == null) {
                continue;
            }
            if (r.getIsEnabled() != null && r.getIsEnabled() == 1) {
                requiredEnabledCount.put(r.getFieldUuid(), requiredEnabledCount.getOrDefault(r.getFieldUuid(), 0) + 1);
            }
        }

        List<com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationUniqueDO> uniqueList = validationUniqueRepository
                .findByFieldUuids(fieldUuids);
        for (var u : uniqueList) {
            if (u == null || u.getFieldUuid() == null) {
                continue;
            }
            if (u.getIsEnabled() != null && u.getIsEnabled() == 1) {
                uniqueEnabledCount.put(u.getFieldUuid(), uniqueEnabledCount.getOrDefault(u.getFieldUuid(), 0) + 1);
            }
        }

        List<com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationLengthDO> lengthList = validationLengthRepository
                .findByFieldUuids(fieldUuids);
        for (var l : lengthList) {
            if (l == null || l.getFieldUuid() == null) {
                continue;
            }
            if (l.getIsEnabled() != null && l.getIsEnabled() == 1) {
                lengthEnabledCount.put(l.getFieldUuid(), lengthEnabledCount.getOrDefault(l.getFieldUuid(), 0) + 1);
            }
        }

        List<com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationFormatDO> formatList = validationFormatRepository
                .findByFieldUuids(fieldUuids);
        for (var f : formatList) {
            if (f == null || f.getFieldUuid() == null) {
                continue;
            }
            if (f.getIsEnabled() != null && f.getIsEnabled() == 1) {
                formatEnabledCount.put(f.getFieldUuid(), formatEnabledCount.getOrDefault(f.getFieldUuid(), 0) + 1);
            }
        }

        for (String fieldUuid : fieldUuids) {
            int reqCnt = requiredEnabledCount.getOrDefault(fieldUuid, 0);
            if (reqCnt > 1) {
                throw new IllegalArgumentException(String.format(
                        "字段【%s】存在多条生效的必填校验规则，同一字段的同一种校验类型只能有一条生效规则",
                        resolveFieldDisplayName(fieldsByUuid, fieldUuid)));
            }
            int uniqCnt = uniqueEnabledCount.getOrDefault(fieldUuid, 0);
            if (uniqCnt > 1) {
                throw new IllegalArgumentException(String.format(
                        "字段【%s】存在多条生效的唯一性校验规则，同一字段的同一种校验类型只能有一条生效规则",
                        resolveFieldDisplayName(fieldsByUuid, fieldUuid)));
            }
            int lenCnt = lengthEnabledCount.getOrDefault(fieldUuid, 0);
            if (lenCnt > 1) {
                throw new IllegalArgumentException(String.format(
                        "字段【%s】存在多条生效的长度校验规则，同一字段的同一种校验类型只能有一条生效规则",
                        resolveFieldDisplayName(fieldsByUuid, fieldUuid)));
            }
            int fmtCnt = formatEnabledCount.getOrDefault(fieldUuid, 0);
            if (fmtCnt > 1) {
                throw new IllegalArgumentException(String.format(
                        "字段【%s】存在多条生效的格式校验规则，同一字段的同一种校验类型只能有一条生效规则",
                        resolveFieldDisplayName(fieldsByUuid, fieldUuid)));
            }
        }
    }

}
