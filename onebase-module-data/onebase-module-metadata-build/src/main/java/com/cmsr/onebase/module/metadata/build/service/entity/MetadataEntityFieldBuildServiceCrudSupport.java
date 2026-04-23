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
public abstract class MetadataEntityFieldBuildServiceCrudSupport extends MetadataEntityFieldBuildServiceDdlSupport {
    protected String resolveFieldDisplayName(Map<String, MetadataEntityFieldDO> fieldsByUuid, String fieldUuid) {
        if (fieldsByUuid != null) {
            MetadataEntityFieldDO field = fieldsByUuid.get(fieldUuid);
            if (field != null) {
                if (field.getDisplayName() != null && !field.getDisplayName().trim().isEmpty()) {
                    return field.getDisplayName();
                }
                if (field.getFieldName() != null && !field.getFieldName().trim().isEmpty()) {
                    return field.getFieldName();
                }
            }
        }
        return fieldUuid != null ? fieldUuid : "未知字段";
    }

    protected void syncRequiredValidations(MetadataBusinessEntityDO businessEntity,
            Map<String, MetadataEntityFieldDO> fieldsByUuid,
            Set<String> fieldUuids) {
        List<com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRequiredDO> existingList = validationRequiredRepository
                .findByFieldUuids(fieldUuids);
        Map<String, com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRequiredDO> existingByFieldUuid = new HashMap<>();
        for (var r : existingList) {
            if (r != null && r.getFieldUuid() != null && !existingByFieldUuid.containsKey(r.getFieldUuid())) {
                existingByFieldUuid.put(r.getFieldUuid(), r);
            }
        }

        for (String fieldUuid : fieldUuids) {
            MetadataEntityFieldDO field = fieldsByUuid.get(fieldUuid);
            if (field == null) {
                continue;
            }
            boolean enabled = field.getIsRequired() != null && field.getIsRequired() == 1;
            var existing = existingByFieldUuid.get(fieldUuid);

            if (enabled) {
                if (existing != null) {
                    if (existing.getIsEnabled() == null || existing.getIsEnabled() != 1) {
                        existing.setIsEnabled(1);
                        validationRequiredRepository.updateById(existing);
                    }
                } else {
                    Long groupId = createValidationRuleGroup(businessEntity, field,
                            MetadataValidationRuleTypeEnum.REQUIRED.getCode(), null);
                    if (groupId == null) {
                        continue;
                    }
                    String fieldDisplayName = field.getDisplayName() != null && !field.getDisplayName().trim().isEmpty()
                            ? field.getDisplayName()
                            : (field.getFieldName() != null ? field.getFieldName() : "此字段");
                    String promptMsg = fieldDisplayName + "为必填项";

                    com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRequiredDO data = new com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRequiredDO();
                    data.setEntityUuid(field.getEntityUuid());
                    data.setApplicationId(field.getApplicationId());
                    data.setFieldUuid(fieldUuid);
                    data.setIsEnabled(1);
                    data.setPromptMessage(promptMsg);
                    var group = validationRuleGroupService.resolveRuleGroup(groupId, null, null);
                    data.setGroupUuid(group != null ? group.getGroupUuid() : null);
                    validationRequiredRepository.saveOrUpdate(data);
                }
            } else {
                if (existing != null) {
                    validationRequiredRepository.deleteByFieldUuid(fieldUuid);
                    safeDeleteRuleGroup(existing.getGroupUuid());
                }
            }
        }
    }

    protected void syncUniqueValidations(MetadataBusinessEntityDO businessEntity,
            Map<String, MetadataEntityFieldDO> fieldsByUuid,
            Set<String> fieldUuids) {
        List<com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationUniqueDO> existingList = validationUniqueRepository
                .findByFieldUuids(fieldUuids);
        Map<String, com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationUniqueDO> existingByFieldUuid = new HashMap<>();
        for (var r : existingList) {
            if (r != null && r.getFieldUuid() != null && !existingByFieldUuid.containsKey(r.getFieldUuid())) {
                existingByFieldUuid.put(r.getFieldUuid(), r);
            }
        }

        for (String fieldUuid : fieldUuids) {
            MetadataEntityFieldDO field = fieldsByUuid.get(fieldUuid);
            if (field == null) {
                continue;
            }
            boolean enabled = field.getIsUnique() != null && field.getIsUnique() == 1;
            var existing = existingByFieldUuid.get(fieldUuid);

            if (enabled) {
                if (existing != null) {
                    if (existing.getIsEnabled() == null || existing.getIsEnabled() != 1) {
                        existing.setIsEnabled(1);
                        validationUniqueRepository.updateById(existing);
                    }
                } else {
                    Long groupId = createValidationRuleGroup(businessEntity, field,
                            MetadataValidationRuleTypeEnum.UNIQUE.getCode(), null);
                    if (groupId == null) {
                        continue;
                    }
                    com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleGroupDO group = validationRuleGroupService
                            .getValidationRuleGroup(groupId);
                    if (group == null || group.getGroupUuid() == null || group.getGroupUuid().isBlank()) {
                        continue;
                    }
                    String fieldDisplayName = field.getDisplayName() != null && !field.getDisplayName().trim().isEmpty()
                            ? field.getDisplayName()
                            : (field.getFieldName() != null ? field.getFieldName() : "此字段");
                    String promptMsg = fieldDisplayName + "必须唯一";

                    com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationUniqueDO data = new com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationUniqueDO();
                    data.setEntityUuid(field.getEntityUuid());
                    data.setApplicationId(field.getApplicationId());
                    data.setFieldUuid(fieldUuid);
                    data.setIsEnabled(1);
                    data.setPromptMessage(promptMsg);
                    data.setGroupUuid(group.getGroupUuid());
                    validationUniqueRepository.saveOrUpdate(data);
                }
            } else {
                if (existing != null) {
                    String groupUuid = existing.getGroupUuid();
                    validationUniqueRepository.deleteByFieldUuid(fieldUuid);
                    if (groupUuid != null && !groupUuid.isBlank()) {
                        validationRuleGroupService.safeDeleteGroupDirect(groupUuid);
                    }
                }
            }
        }
    }

    protected void syncLengthValidations(MetadataBusinessEntityDO businessEntity,
            Map<String, MetadataEntityFieldDO> fieldsByUuid,
            Set<String> fieldUuids) {
        List<com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationLengthDO> existingList = validationLengthRepository
                .findByFieldUuids(fieldUuids);
        Map<String, com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationLengthDO> existingByFieldUuid = new HashMap<>();
        for (var r : existingList) {
            if (r != null && r.getFieldUuid() != null && !existingByFieldUuid.containsKey(r.getFieldUuid())) {
                existingByFieldUuid.put(r.getFieldUuid(), r);
            }
        }

        for (String fieldUuid : fieldUuids) {
            MetadataEntityFieldDO field = fieldsByUuid.get(fieldUuid);
            if (field == null) {
                continue;
            }
            boolean enabled = field.getDataLength() != null && field.getDataLength() > 0;
            var existing = existingByFieldUuid.get(fieldUuid);

            if (enabled) {
                if (existing != null) {
                    boolean changed = false;
                    if (existing.getIsEnabled() == null || existing.getIsEnabled() != 1) {
                        existing.setIsEnabled(1);
                        changed = true;
                    }
                    if (existing.getMaxLength() == null || !existing.getMaxLength().equals(field.getDataLength())) {
                        existing.setMaxLength(field.getDataLength());
                        changed = true;
                    }
                    if (changed) {
                        validationLengthRepository.updateById(existing);
                    }
                } else {
                    String fieldDisplayName = field.getDisplayName() != null && !field.getDisplayName().trim().isEmpty()
                            ? field.getDisplayName()
                            : (field.getFieldName() != null ? field.getFieldName() : "字段");
                    String promptMsg = String.format("%s长度不能超过%d个字符", fieldDisplayName, field.getDataLength());
                    Long groupId = createValidationRuleGroup(businessEntity, field,
                            MetadataValidationRuleTypeEnum.LENGTH.getCode(), promptMsg);
                    if (groupId == null) {
                        continue;
                    }
                    com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleGroupDO group = validationRuleGroupService
                            .getValidationRuleGroup(groupId);
                    if (group == null || group.getGroupUuid() == null || group.getGroupUuid().isBlank()) {
                        continue;
                    }

                    com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationLengthDO data = new com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationLengthDO();
                    data.setEntityUuid(field.getEntityUuid());
                    data.setApplicationId(field.getApplicationId());
                    data.setFieldUuid(fieldUuid);
                    data.setIsEnabled(1);
                    data.setMinLength(null);
                    data.setMaxLength(field.getDataLength());
                    data.setPromptMessage(promptMsg);
                    data.setGroupUuid(group.getGroupUuid());
                    validationLengthRepository.saveOrUpdate(data);
                }
            } else {
                if (existing != null) {
                    String groupUuid = existing.getGroupUuid();
                    boolean canDeleteGroup = true;
                    if (groupUuid != null && !groupUuid.isBlank()) {
                        List<com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationLengthDO> referenced = validationLengthRepository
                                .findByGroupUuid(groupUuid);
                        canDeleteGroup = referenced.size() <= 1;
                    }
                    validationLengthRepository.deleteByFieldUuid(fieldUuid);
                    if (canDeleteGroup && groupUuid != null && !groupUuid.isBlank()) {
                        validationRuleGroupService.safeDeleteGroupDirect(groupUuid);
                    }
                }
            }
        }
    }

    protected Long createValidationRuleGroup(MetadataBusinessEntityDO businessEntity, MetadataEntityFieldDO field, String validationType,
            String popPrompt) {
        String rgName = buildRuleGroupName(field, businessEntity, validationType);
        if (rgName == null || rgName.isBlank()) {
            return null;
        }
        com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupSaveReqVO groupVO = new com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupSaveReqVO();
        groupVO.setRgName(rgName);
        groupVO.setRgDesc("自动创建的规则组：" + rgName);
        groupVO.setRgStatus(StatusEnumUtil.ACTIVE);
        groupVO.setValidationType(validationType);
        groupVO.setEntityUuid(field.getEntityUuid());
        groupVO.setApplicationId(field.getApplicationId());
        if (popPrompt != null) {
            groupVO.setPopPrompt(popPrompt);
        }
        return validationRuleGroupService.createValidationRuleGroup(groupVO);
    }

    protected void safeDeleteRuleGroup(String groupUuidOrId) {
        if (groupUuidOrId == null || groupUuidOrId.isBlank()) {
            return;
        }
        try {
            validationRuleGroupService.safeDeleteGroupDirect(Long.valueOf(groupUuidOrId));
        } catch (NumberFormatException ignore) {
            validationRuleGroupService.safeDeleteGroupDirect(groupUuidOrId);
        }
    }

    protected String buildRuleGroupName(MetadataEntityFieldDO field, MetadataBusinessEntityDO businessEntity, String validationType) {
        try {
            String fieldDisplayName = field.getDisplayName() != null && !field.getDisplayName().trim().isEmpty()
                    ? field.getDisplayName()
                    : (field.getFieldName() != null ? field.getFieldName() : "未知字段");
            String entityDisplayName = businessEntity.getDisplayName() != null && !businessEntity.getDisplayName().trim().isEmpty()
                    ? businessEntity.getDisplayName()
                    : (businessEntity.getTableName() != null ? businessEntity.getTableName() : "未知实体");
            String validationTypeName = getValidationTypeName(validationType);
            return String.format("%s-%s-%s", validationTypeName, fieldDisplayName, entityDisplayName);
        } catch (Exception e) {
            log.error("构建规则组名称时发生异常，字段UUID: {}, 校验类型: {}, 错误: {}", field != null ? field.getFieldUuid() : null, validationType,
                    e.getMessage(), e);
            return getValidationTypeName(validationType) + "-未知字段-未知实体";
        }
    }

    protected void processEntityRelation(String appId, Long fieldId, MetadataEntityFieldDO full, EntityFieldUpsertItemVO item) {
        if (item.getDataSelectionConfig() == null) {
            return;
        }

        SelectionRelationRef relationRef = resolveSelectionRelationRef(item);
        if (!relationRef.isComplete()) {
            log.warn("数据选择配置不完整，跳过处理关系。sourceEntityUuid={}, fieldId={}, targetEntityId={}, targetFieldId={}, targetEntityUuid={}, targetFieldUuid={}",
                    full.getEntityUuid(), fieldId, relationRef.targetEntityId, relationRef.targetFieldId,
                    relationRef.targetEntityUuid, relationRef.targetFieldUuid);
            return;
        }

        MetadataEntityRelationshipDO existingRelation = findExistingSelectionRelation(relationRef.relationId, full);
        String targetEntityPrimaryKeyUuid = getPrimaryKeyFieldUuid(relationRef.targetEntityUuid);
        if (targetEntityPrimaryKeyUuid == null || targetEntityPrimaryKeyUuid.trim().isEmpty()) {
            log.warn("目标实体没有主键字段，无法创建数据选择关系。targetEntityUuid={}", relationRef.targetEntityUuid);
            return;
        }

        EntityRelationshipSaveReqVO request = buildSelectionRelationshipReq(
                appId, full, item, relationRef, targetEntityPrimaryKeyUuid);
        upsertSelectionRelationship(existingRelation, request, full, fieldId, relationRef);
    }

    protected SelectionRelationRef resolveSelectionRelationRef(EntityFieldUpsertItemVO item) {
        SelectionRelationRef relationRef = new SelectionRelationRef();
        relationRef.relationId = item.getDataSelectionConfig().getRelationId();
        relationRef.targetEntityId = item.getDataSelectionConfig().getTargetEntityId();
        relationRef.targetFieldId = item.getDataSelectionConfig().getTargetFieldId();
        relationRef.targetEntityUuid = item.getDataSelectionConfig().getTargetEntityUuid();
        relationRef.targetFieldUuid = item.getDataSelectionConfig().getTargetFieldUuid();

        if ((relationRef.targetEntityUuid == null || relationRef.targetEntityUuid.trim().isEmpty())
                && relationRef.targetEntityId != null) {
            relationRef.targetEntityUuid = idUuidConverter.toEntityUuid(relationRef.targetEntityId.toString());
            log.debug("通过targetEntityId={}转换得到targetEntityUuid={}",
                    relationRef.targetEntityId, relationRef.targetEntityUuid);
        }
        if ((relationRef.targetFieldUuid == null || relationRef.targetFieldUuid.trim().isEmpty())
                && relationRef.targetFieldId != null) {
            relationRef.targetFieldUuid = idUuidConverter.toFieldUuid(relationRef.targetFieldId.toString());
            log.debug("通过targetFieldId={}转换得到targetFieldUuid={}",
                    relationRef.targetFieldId, relationRef.targetFieldUuid);
        }
        return relationRef;
    }

    protected MetadataEntityRelationshipDO findExistingSelectionRelation(Long relationId, MetadataEntityFieldDO full) {
        if (relationId != null) {
            return metadataEntityRelationshipBuildService.findById(relationId);
        }
        List<MetadataEntityRelationshipDO> existingRelations =
                metadataEntityRelationshipBuildService.findBySourceEntityUuidAndTargetEntityUuid(full.getEntityUuid(), null);
        if (existingRelations == null || existingRelations.isEmpty()) {
            return null;
        }
        for (MetadataEntityRelationshipDO rel : existingRelations) {
            if (Objects.equals(rel.getSourceFieldUuid(), full.getFieldUuid())) {
                return rel;
            }
        }
        return null;
    }

    protected EntityRelationshipSaveReqVO buildSelectionRelationshipReq(String appId, MetadataEntityFieldDO full,
            EntityFieldUpsertItemVO item, SelectionRelationRef relationRef, String targetEntityPrimaryKeyUuid) {
        EntityRelationshipSaveReqVO r = new EntityRelationshipSaveReqVO();
        r.setRelationName("数据选择关系");
        r.setSourceEntityUuid(full.getEntityUuid());
        r.setTargetEntityUuid(relationRef.targetEntityUuid);
        r.setRelationshipType(resolveSelectionRelationshipType(item));
        r.setSourceFieldUuid(full.getFieldUuid());
        r.setTargetFieldUuid(targetEntityPrimaryKeyUuid);
        r.setSelectFieldUuid(relationRef.targetFieldUuid);
        r.setCascadeType(MetadataRelationshipCascadeTypeEnum.READ.getCode());
        r.setDescription("数据选择关系");
        r.setApplicationId(appId);
        return r;
    }

    protected String resolveSelectionRelationshipType(EntityFieldUpsertItemVO item) {
        if (MetadataFieldTypeCodeEnum.MULTI_DATA_SELECTION.matches(item.getFieldType())) {
            return RelationshipTypeEnum.DATA_SELECT_MULTI.getRelationshipType();
        }
        return RelationshipTypeEnum.DATA_SELECT.getRelationshipType();
    }

    protected void upsertSelectionRelationship(MetadataEntityRelationshipDO existingRelation,
            EntityRelationshipSaveReqVO request,
            MetadataEntityFieldDO full,
            Long fieldId,
            SelectionRelationRef relationRef) {
        if (existingRelation != null) {
            if (isSameSelectionRelationship(existingRelation, relationRef)) {
                log.debug("数据选择关系未变化，忽略更新。currentEntityUuid={}, fieldId={}, targetEntityUuid={}, targetFieldUuid={}",
                        full.getEntityUuid(), fieldId, relationRef.targetEntityUuid, relationRef.targetFieldUuid);
                return;
            }
            log.info("数据选择关系已变化，更新关系。currentEntityUuid={}, fieldId={}, 原targetEntityUuid={}, 原selectFieldUuid={}, 新targetEntityUuid={}, 新selectFieldUuid={}",
                    full.getEntityUuid(), fieldId, existingRelation.getTargetEntityUuid(), existingRelation.getSelectFieldUuid(),
                    relationRef.targetEntityUuid, relationRef.targetFieldUuid);
            request.setId(existingRelation.getId().toString());
            metadataEntityRelationshipBuildService.updateEntityRelationship(request);
            return;
        }

        log.info("创建新的数据选择关系。sourceEntityUuid={}, sourceFieldUuid={}, targetEntityUuid={}, targetFieldUuid={}, selectFieldUuid={}",
                full.getEntityUuid(), full.getFieldUuid(), relationRef.targetEntityUuid,
                request.getTargetFieldUuid(), relationRef.targetFieldUuid);
        metadataEntityRelationshipBuildService.createEntityRelationship(request);
    }

    protected boolean isSameSelectionRelationship(MetadataEntityRelationshipDO existingRelation,
            SelectionRelationRef relationRef) {
        return Objects.equals(existingRelation.getTargetEntityUuid(), relationRef.targetEntityUuid)
                && Objects.equals(existingRelation.getSelectFieldUuid(), relationRef.targetFieldUuid);
    }

    protected static class SelectionRelationRef {
        protected Long relationId;
        protected Long targetEntityId;
        protected Long targetFieldId;
        protected String targetEntityUuid;
        protected String targetFieldUuid;

        protected boolean isComplete() {
            return targetEntityUuid != null && !targetEntityUuid.trim().isEmpty()
                    && targetFieldUuid != null && !targetFieldUuid.trim().isEmpty();
        }
    }

    /**
     * 获取实体的主键字段UUID
     *
     * @param entityUuid 实体UUID
     * @return 主键字段UUID，如果没有主键则返回null
     */
    protected String getPrimaryKeyFieldUuid(String entityUuid) {
        try {
            // 查询该实体的所有字段
            QueryWrapper queryWrapper = QueryWrapper.create()
                    .eq(MetadataEntityFieldDO::getEntityUuid, entityUuid)
                    .eq(MetadataEntityFieldDO::getIsPrimaryKey, StatusEnumUtil.YES)  // 1表示是主键
                    .orderBy(MetadataEntityFieldDO::getSortOrder, true);

            List<MetadataEntityFieldDO> primaryKeyFields = metadataEntityFieldRepository.list(queryWrapper);
            
            if (primaryKeyFields != null && !primaryKeyFields.isEmpty()) {
                // 返回第一个主键字段的UUID
                String pkUuid = primaryKeyFields.get(0).getFieldUuid();
                log.debug("找到实体{}的主键字段UUID: {}", entityUuid, pkUuid);
                return pkUuid;
            } else {
                log.warn("实体{}没有主键字段", entityUuid);
                return null;
            }
        } catch (Exception e) {
            log.error("获取实体{}的主键字段UUID失败: {}", entityUuid, e.getMessage(), e);
            return null;
        }
    }

    protected Integer extractMaxLength(EntityFieldUpsertItemVO item) {
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
    protected MetadataEntityFieldDO findExistingFieldByCodeOrName(String entityId, EntityFieldUpsertItemVO item) {
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
        // 校验字段名不能是数据库保留关键字
        validateFieldNameNotDatabaseKeyword(createReqVO.getFieldName());
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
        // 生成字段UUID（如果为空）
        if (entityField.getFieldUuid() == null || entityField.getFieldUuid().isEmpty()) {
            entityField.setFieldUuid(UuidUtils.getUuid());
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
        // 生成字段UUID（如果为空）
        if (entityField.getFieldUuid() == null || entityField.getFieldUuid().isEmpty()) {
            entityField.setFieldUuid(UuidUtils.getUuid());
        }
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

    protected void validateEntityFieldExists(String id) {
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

    protected void validateEntityFieldNameUnique(String id, String entityId, String fieldName) {
        // 将entityId转换为entityUuid（兼容ID和UUID两种格式）
        String entityUuid = idUuidConverter.toEntityUuid(entityId);
        if (entityUuid == null || entityUuid.isEmpty()) {
            log.warn("无法解析实体标识: {}", entityId);
            return;
        }
        
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(MetadataEntityFieldDO::getEntityUuid, entityUuid)
                .eq(MetadataEntityFieldDO::getFieldName, fieldName);
        if (id != null && !id.trim().isEmpty()) {
            // id可能是数字ID或UUID，需要兼容处理
            String fieldUuid = idUuidConverter.toFieldUuid(id.trim());
            if (fieldUuid != null && !fieldUuid.isEmpty()) {
                queryWrapper.ne(MetadataEntityFieldDO::getFieldUuid, fieldUuid);
            }
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
    protected void validateFieldNameNotSystemReserved(String fieldName) {
        if (fieldName == null || fieldName.trim().isEmpty()) {
            return;
        }

        String trimmedFieldName = fieldName.trim();
        // 从数据库查询系统字段
        MetadataSystemFieldsDO systemField = systemFieldsRepository.getSystemFieldByName(trimmedFieldName);
        if (systemField != null) {
            throw exception(ENTITY_FIELD_NAME_IS_SYSTEM_RESERVED, trimmedFieldName);
        }
    }

    /**
     * 校验字段名是否为数据库保留关键字
     * <p>
     * 如if、select、case等PostgreSQL/人大金仓的保留关键字不能作为字段名，
     * 否则会导致SQL语法错误。
     * </p>
     *
     * @param fieldName 字段名
     */
    protected void validateFieldNameNotDatabaseKeyword(String fieldName) {
        if (fieldName == null || fieldName.trim().isEmpty()) {
            return;
        }
        String trimmedFieldName = fieldName.trim();
        if (DatabaseReservedKeywords.isReservedKeyword(trimmedFieldName)) {
            throw exception(ENTITY_FIELD_NAME_IS_DATABASE_KEYWORD, trimmedFieldName);
        }
    }

    protected void validateEntityFieldDisplayNameUnique(String id, String entityId, String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            return;
        }

        // 将entityId转换为entityUuid（兼容ID和UUID两种格式）
        String entityUuid = idUuidConverter.toEntityUuid(entityId);
        if (entityUuid == null || entityUuid.isEmpty()) {
            log.warn("无法解析实体标识: {}", entityId);
            return;
        }
        
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(MetadataEntityFieldDO::getEntityUuid, entityUuid)
                .eq(MetadataEntityFieldDO::getDisplayName, displayName.trim());
        if (id != null && !id.trim().isEmpty()) {
            // id可能是数字ID或UUID，需要兼容处理
            String fieldUuid = idUuidConverter.toFieldUuid(id.trim());
            if (fieldUuid != null && !fieldUuid.isEmpty()) {
                queryWrapper.ne(MetadataEntityFieldDO::getFieldUuid, fieldUuid);
            }
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

        // 添加查询条件 - 将entityId转换为entityUuid（兼容前端传入数字ID或UUID）
        if (pageReqVO.getEntityId() != null && !pageReqVO.getEntityId().trim().isEmpty()) {
            String entityUuid = idUuidConverter.toEntityUuid(pageReqVO.getEntityId().trim());
            queryWrapper.eq(MetadataEntityFieldDO::getEntityUuid, entityUuid);
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
        // 将entityId转换为entityUuid（兼容ID和UUID两种格式）
        String entityUuid = idUuidConverter.toEntityUuid(entityId);
        if (entityUuid == null || entityUuid.isEmpty()) {
            log.warn("无法解析实体标识: {}", entityId);
            return java.util.Collections.emptyList();
        }
        
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(MetadataEntityFieldDO::getEntityUuid, entityUuid.trim())
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

}
