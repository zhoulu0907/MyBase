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
public abstract class MetadataEntityFieldBuildServiceRelatedSupport extends MetadataEntityFieldBuildServiceValidationSupport {
    /**
     * 校验实体是否允许修改表结构
     *
     * @param entityUuid 实体UUID
     */
    protected void validateEntityAllowModifyStructure(String entityUuid) {
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

        // 补充完整的自动编号配置信息（使用统一规则项列表）
        MetadataEntityFieldDO field = metadataEntityFieldRepository.getById(Long.valueOf(id));
        if (field != null) {
            MetadataAutoNumberConfigDO config = autoNumberConfigBuildService.getByFieldId(field.getFieldUuid());
            if (config != null) {
                AutoNumberConfigRespVO autoNumberConfig = convertToAutoNumberConfigRespVO(config);
                // 构建统一规则项列表（包含SEQUENCE）
                List<AutoNumberRuleVO> unifiedRules = buildUnifiedRuleVOList(config);
                autoNumberConfig.setRuleItems(unifiedRules);
                result.setAutoNumberConfig(autoNumberConfig);
            }
        }

        return result;
    }

    /**
     * 处理字段相关数据（选项、约束、自动编号）
     */
    protected void processFieldRelatedData(String fieldUuid, MetadataEntityFieldDO entityField,
            List<FieldOptionRespVO> options,
            FieldConstraintRespVO constraints,
            AutoNumberConfigReqVO autoNumber) {
        syncFieldOptions(fieldUuid, entityField, options);
        syncFieldConstraints(fieldUuid, entityField, constraints);
        if (autoNumber != null) {
            processAutoNumberConfig(fieldUuid, entityField, autoNumber);
        }
    }

    protected void syncFieldOptions(String fieldUuid, MetadataEntityFieldDO entityField, List<FieldOptionRespVO> options) {
        if (entityField != null && entityField.getDictTypeId() == null && options != null) {
            upsertCustomOptions(fieldUuid, entityField, options);
            return;
        }
        if (entityField != null && entityField.getDictTypeId() != null) {
            fieldOptionService.deleteByFieldId(fieldUuid);
        }
    }

    protected void upsertCustomOptions(String fieldUuid, MetadataEntityFieldDO entityField, List<FieldOptionRespVO> options) {
        List<MetadataEntityFieldOptionDO> existingOptions = fieldOptionService.listByFieldId(fieldUuid);
        Map<Long, MetadataEntityFieldOptionDO> existingOptionsMap = existingOptions.stream()
                .collect(java.util.stream.Collectors.toMap(MetadataEntityFieldOptionDO::getId, o -> o, (a, b) -> a));
        Map<String, MetadataEntityFieldOptionDO> existingOptionsByValue = existingOptions.stream()
                .collect(java.util.stream.Collectors.toMap(
                        MetadataEntityFieldOptionDO::getOptionValue,
                        o -> o,
                        (a, b) -> a));
        Set<Long> processedOptionIds = new java.util.HashSet<>();

        for (FieldOptionRespVO option : options) {
            upsertSingleOption(fieldUuid, entityField, option, existingOptionsMap, existingOptionsByValue, processedOptionIds);
        }
        deleteMissingOptions(existingOptions, processedOptionIds);
    }

    protected void upsertSingleOption(String fieldUuid, MetadataEntityFieldDO entityField, FieldOptionRespVO option,
            Map<Long, MetadataEntityFieldOptionDO> existingOptionsMap,
            Map<String, MetadataEntityFieldOptionDO> existingOptionsByValue,
            Set<Long> processedOptionIds) {
        Long optionId = parseOptionId(option);
        if (optionId != null && existingOptionsMap.containsKey(optionId)) {
            updateOption(fieldUuid, entityField, option, optionId);
            processedOptionIds.add(optionId);
            return;
        }

        MetadataEntityFieldOptionDO existingByValue = existingOptionsByValue.get(option.getOptionValue());
        if (existingByValue != null) {
            log.info("发现已存在相同值的选项，自动转换为更新操作: fieldUuid={}, optionValue={}, existingId={}",
                    fieldUuid, option.getOptionValue(), existingByValue.getId());
            updateOption(fieldUuid, entityField, option, existingByValue.getId());
            processedOptionIds.add(existingByValue.getId());
            return;
        }
        createOption(fieldUuid, entityField, option);
    }

    protected Long parseOptionId(FieldOptionRespVO option) {
        if (option.getId() == null || option.getId().trim().isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(option.getId());
        } catch (NumberFormatException e) {
            log.warn("选项ID格式错误，将作为新增处理: {}", option.getId());
            return null;
        }
    }

    protected void updateOption(String fieldUuid, MetadataEntityFieldDO entityField, FieldOptionRespVO option, Long optionId) {
        MetadataEntityFieldOptionDO updateObj = buildOption(fieldUuid, entityField, option);
        updateObj.setId(optionId);
        fieldOptionService.update(updateObj);
    }

    protected void createOption(String fieldUuid, MetadataEntityFieldDO entityField, FieldOptionRespVO option) {
        MetadataEntityFieldOptionDO optionDO = buildOption(fieldUuid, entityField, option);
        optionDO.setOptionUuid(UuidUtils.getUuid());
        fieldOptionService.create(optionDO);
        log.debug("新增字段选项: fieldUuid={}, optionValue={}", fieldUuid, option.getOptionValue());
    }

    protected MetadataEntityFieldOptionDO buildOption(String fieldUuid, MetadataEntityFieldDO entityField, FieldOptionRespVO option) {
        MetadataEntityFieldOptionDO optionDO = new MetadataEntityFieldOptionDO();
        optionDO.setFieldUuid(fieldUuid);
        optionDO.setOptionLabel(option.getOptionLabel());
        optionDO.setOptionValue(option.getOptionValue());
        optionDO.setOptionOrder(option.getOptionOrder());
        optionDO.setIsEnabled(option.getIsEnabled());
        optionDO.setDescription(option.getDescription());
        optionDO.setApplicationId(entityField.getApplicationId());
        return optionDO;
    }

    protected void deleteMissingOptions(List<MetadataEntityFieldOptionDO> existingOptions, Set<Long> processedOptionIds) {
        for (MetadataEntityFieldOptionDO existingOption : existingOptions) {
            if (!processedOptionIds.contains(existingOption.getId())) {
                fieldOptionService.deleteById(existingOption.getId());
                log.info("删除未在请求中出现的字段选项: {}", existingOption.getId());
            }
        }
    }

    protected void syncFieldConstraints(String fieldUuid, MetadataEntityFieldDO entityField, FieldConstraintRespVO constraints) {
        if (constraints != null && !isConstraintsEmpty(constraints)) {
            processFieldConstraints(fieldUuid, entityField, constraints);
        }
    }

    /**
     * 判断 FieldConstraintRespVO 是否为空对象
     * 
     * @param constraints 约束对象
     * @return true-空对象（所有字段都为null），false-有实际内容
     */
    protected boolean isConstraintsEmpty(FieldConstraintRespVO constraints) {
        if (constraints == null) {
            return true;
        }
        boolean lengthEnabledEmpty = constraints.getLengthEnabled() == null || constraints.getLengthEnabled() == 0;
        boolean minLengthEmpty = constraints.getMinLength() == null || constraints.getMinLength() == 0;
        boolean maxLengthEmpty = constraints.getMaxLength() == null || constraints.getMaxLength() == 0;
        boolean lengthPromptEmpty = !StringUtils.hasText(constraints.getLengthPrompt());

        boolean regexEnabledEmpty = constraints.getRegexEnabled() == null || constraints.getRegexEnabled() == 0;
        boolean regexPatternEmpty = !StringUtils.hasText(constraints.getRegexPattern());
        boolean regexPromptEmpty = !StringUtils.hasText(constraints.getRegexPrompt());

        return lengthEnabledEmpty
                && minLengthEmpty
                && maxLengthEmpty
                && lengthPromptEmpty
                && regexEnabledEmpty
                && regexPatternEmpty
                && regexPromptEmpty;
    }

    /**
     * 处理字段约束
     * 
     * 注意：本方法仅处理字段约束(constraint)配置，不处理validation规则。
     * validation规则的同步由batchSaveEntityFields中的字段变更检测逻辑负责，避免重复处理。
     */
    protected void processFieldConstraints(String fieldUuid, MetadataEntityFieldDO entityField,
            FieldConstraintRespVO constraints) {
        if (constraints.getMinLength() != null && constraints.getMaxLength() != null &&
                constraints.getMinLength() > constraints.getMaxLength()) {
            throw new IllegalArgumentException("最小长度不能大于最大长度");
        }

        // 长度
        boolean lengthEnabled = constraints.getLengthEnabled() != null
                && CommonStatusEnum.isEnabled(constraints.getLengthEnabled());
        boolean lengthExplicitDisabled = constraints.getLengthEnabled() != null
                && !CommonStatusEnum.isEnabled(constraints.getLengthEnabled());
        boolean hasLengthRange = (constraints.getMinLength() != null && constraints.getMinLength() > 0)
                || (constraints.getMaxLength() != null && constraints.getMaxLength() > 0);
        boolean hasLengthPrompt = StringUtils.hasText(constraints.getLengthPrompt());
        boolean hasExplicitEnableFlag = constraints.getLengthEnabled() != null;
        
        // 当明确启用，或者没有明确启停标志但有配置数据时，保存约束配置
        if (lengthEnabled || (!hasExplicitEnableFlag && (hasLengthRange || hasLengthPrompt))) {
            FieldConstraintSaveReqVO req = new FieldConstraintSaveReqVO();
            req.setFieldUuid(fieldUuid);
            req.setConstraintType(MetadataFieldConstraintTypeEnum.LENGTH_RANGE.getCode());
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
        } else if (lengthExplicitDisabled && (hasLengthRange || hasLengthPrompt)) {
            // 当明确禁用但有配置数据时，保存约束配置并设置isEnabled=0（而不是删除）
            FieldConstraintSaveReqVO req = new FieldConstraintSaveReqVO();
            req.setFieldUuid(fieldUuid);
            req.setConstraintType(MetadataFieldConstraintTypeEnum.LENGTH_RANGE.getCode());
            req.setMinLength(constraints.getMinLength());
            req.setMaxLength(constraints.getMaxLength());
            req.setPromptMessage(constraints.getLengthPrompt());
            req.setIsEnabled(CommonStatusEnum.DISABLED.getStatus()); // 设置为禁用
            req.setVersionTag(entityField != null && entityField.getVersionTag() != null ? entityField.getVersionTag() : 0L);
            req.setApplicationId(entityField != null ? entityField.getApplicationId() : null);
            fieldConstraintService.saveFieldConstraintConfig(req);
        }

        // 正则 - 只有当正则表达式不为空且启用时才创建REGEX约束
        boolean regexEnabled = constraints.getRegexEnabled() != null && constraints.getRegexEnabled() == 1;
        boolean regexExplicitDisabled = constraints.getRegexEnabled() != null && constraints.getRegexEnabled() == 0;
        boolean hasRegexPattern = StringUtils.hasText(constraints.getRegexPattern());
        boolean hasRegexPrompt = StringUtils.hasText(constraints.getRegexPrompt());
        if (regexEnabled && hasRegexPattern) {
            FieldConstraintSaveReqVO req = new FieldConstraintSaveReqVO();
            req.setFieldUuid(fieldUuid);
            req.setConstraintType(MetadataFieldConstraintTypeEnum.REGEX.getCode());
            req.setRegexPattern(constraints.getRegexPattern());
            req.setPromptMessage(constraints.getRegexPrompt());
            req.setIsEnabled(constraints.getRegexEnabled());
            req.setVersionTag(entityField != null && entityField.getVersionTag() != null ? entityField.getVersionTag() : 0L);
            req.setApplicationId(entityField != null ? entityField.getApplicationId() : null);
            fieldConstraintService.saveFieldConstraintConfig(req);
        } else if (regexExplicitDisabled && (hasRegexPattern || hasRegexPrompt)) {
            // 当明确禁用但有配置数据时，保存约束配置并设置isEnabled=0（而不是删除）
            FieldConstraintSaveReqVO req = new FieldConstraintSaveReqVO();
            req.setFieldUuid(fieldUuid);
            req.setConstraintType(MetadataFieldConstraintTypeEnum.REGEX.getCode());
            req.setRegexPattern(constraints.getRegexPattern());
            req.setPromptMessage(constraints.getRegexPrompt());
            req.setIsEnabled(CommonStatusEnum.DISABLED.getStatus()); // 设置为禁用
            req.setVersionTag(entityField != null && entityField.getVersionTag() != null ? entityField.getVersionTag() : 0L);
            req.setApplicationId(entityField != null ? entityField.getApplicationId() : null);
            fieldConstraintService.saveFieldConstraintConfig(req);
        }
    }

    /**
     * 处理自动编号配置（智能更新版本）
     * <p>
     * 支持统一规则项列表，从中提取SEQUENCE配置到Config表，其他类型存入RuleItem表
     *
     * @param fieldUuid   字段UUID
     * @param entityField 字段实体
     * @param autoNumber  自动编号配置（使用统一的AutoNumberRuleVO列表）
     */
    protected void processAutoNumberConfig(String fieldUuid, MetadataEntityFieldDO entityField,
            AutoNumberConfigReqVO autoNumber) {
        MetadataAutoNumberConfigDO existingConfig = autoNumberConfigBuildService.getByFieldId(fieldUuid);
        if (!shouldEnableAutoNumber(fieldUuid, autoNumber)) {
            deleteAutoNumberConfigIfExists(fieldUuid, existingConfig);
            return;
        }

        AutoNumberRuleParts ruleParts = splitAutoNumberRules(autoNumber.getRuleItems());
        MetadataAutoNumberConfigDO config = buildAutoNumberConfig(
                fieldUuid, entityField, autoNumber, existingConfig, ruleParts.sequenceRule);
        Long configId = autoNumberConfigBuildService.upsert(config);
        MetadataAutoNumberConfigDO savedConfig = autoNumberConfigBuildService.getByFieldId(fieldUuid);
        resetAutoNumberStateIfInitialChanged(existingConfig, savedConfig);
        syncAutoNumberRuleItems(configId, savedConfig, ruleParts.otherRules);
    }

    protected boolean shouldEnableAutoNumber(String fieldUuid, AutoNumberConfigReqVO autoNumber) {
        boolean hasRules = autoNumber.getRuleItems() != null && !autoNumber.getRuleItems().isEmpty();
        if (autoNumber.getIsEnabled() == null && hasRules) {
            autoNumber.setIsEnabled(CommonStatusEnum.ENABLED.getStatus());
            log.info("自动编号配置未传入isEnabled，但有规则项，自动设为启用状态: fieldUuid={}", fieldUuid);
        }
        return (autoNumber.getIsEnabled() != null && CommonStatusEnum.isEnabled(autoNumber.getIsEnabled()))
                || (autoNumber.getIsEnabled() == null && hasRules);
    }

    protected void deleteAutoNumberConfigIfExists(String fieldUuid, MetadataAutoNumberConfigDO existingConfig) {
        if (existingConfig != null) {
            autoNumberConfigBuildService.deleteByFieldId(fieldUuid);
            log.info("删除字段 {} 的自动编号配置", fieldUuid);
        }
    }

    protected AutoNumberRuleParts splitAutoNumberRules(List<AutoNumberRuleVO> ruleItems) {
        AutoNumberRuleParts parts = new AutoNumberRuleParts();
        if (ruleItems == null) {
            return parts;
        }
        for (AutoNumberRuleVO rule : ruleItems) {
            if (AutoNumberItemTypeEnum.SEQUENCE.getCode().equalsIgnoreCase(rule.getItemType())) {
                parts.sequenceRule = rule;
            } else {
                parts.otherRules.add(rule);
            }
        }
        return parts;
    }

    protected MetadataAutoNumberConfigDO buildAutoNumberConfig(String fieldUuid, MetadataEntityFieldDO entityField,
            AutoNumberConfigReqVO autoNumber, MetadataAutoNumberConfigDO existingConfig, AutoNumberRuleVO sequenceRule) {
        MetadataAutoNumberConfigDO config = new MetadataAutoNumberConfigDO();
        if (existingConfig != null) {
            config.setId(existingConfig.getId());
            config.setConfigUuid(existingConfig.getConfigUuid());
        } else {
            config.setConfigUuid(UuidUtils.getUuid());
        }
        config.setFieldUuid(fieldUuid);
        config.setIsEnabled(autoNumber.getIsEnabled());
        applySequenceRule(config, sequenceRule);
        config.setVersionTag(entityField != null && entityField.getVersionTag() != null ? entityField.getVersionTag() : 0L);
        config.setApplicationId(entityField != null ? entityField.getApplicationId() : null);
        return config;
    }

    protected void applySequenceRule(MetadataAutoNumberConfigDO config, AutoNumberRuleVO sequenceRule) {
        if (sequenceRule == null) {
            config.setNumberMode(NumberModeEnum.NATURAL.getCode());
            config.setDigitWidth(null);
            config.setOverflowContinue(1);
            config.setInitialValue(1L);
            config.setResetCycle(ResetCycleEnum.NEVER.getCode());
            config.setResetOnInitialChange(0);
            config.setSequenceOrder(999);
            return;
        }
        config.setNumberMode(sequenceRule.getNumberMode());
        config.setDigitWidth(sequenceRule.getDigitWidth());
        config.setOverflowContinue(sequenceRule.getOverflowContinue());
        config.setInitialValue(sequenceRule.getInitialValue() != null ? sequenceRule.getInitialValue() : 1L);
        config.setResetCycle(sequenceRule.getResetCycle());
        config.setResetOnInitialChange(
                sequenceRule.getResetOnInitialChange() != null ? sequenceRule.getResetOnInitialChange() : 0);
        config.setSequenceOrder(sequenceRule.getItemOrder() != null ? sequenceRule.getItemOrder() : 999);
    }

    protected void resetAutoNumberStateIfInitialChanged(MetadataAutoNumberConfigDO existingConfig,
            MetadataAutoNumberConfigDO savedConfig) {
        if (existingConfig != null && savedConfig != null
                && existingConfig.getInitialValue() != null
                && savedConfig.getInitialValue() != null
                && !existingConfig.getInitialValue().equals(savedConfig.getInitialValue())
                && savedConfig.getResetOnInitialChange() != null
                && savedConfig.getResetOnInitialChange() == 1) {
            resetAutoNumberStateOnInitialChange(savedConfig);
        }
    }

    protected void syncAutoNumberRuleItems(Long configId, MetadataAutoNumberConfigDO savedConfig,
            List<AutoNumberRuleVO> otherRules) {
        if (savedConfig == null) {
            return;
        }
        List<MetadataAutoNumberRuleItemDO> existingRules = autoNumberRuleBuildService.listByConfigId(savedConfig.getId());
        if (otherRules.isEmpty()) {
            deleteAllAutoNumberRuleItems(existingRules);
            return;
        }

        Map<Long, MetadataAutoNumberRuleItemDO> existingRulesMap = existingRules.stream()
                .collect(java.util.stream.Collectors.toMap(MetadataAutoNumberRuleItemDO::getId, r -> r, (a, b) -> a));
        Set<Long> processedRuleIds = new java.util.HashSet<>();
        String configUuid = savedConfig.getConfigUuid();
        for (AutoNumberRuleVO ruleReq : otherRules) {
            normalizeAutoNumberRuleRef(ruleReq);
            if (updateExistingAutoNumberRuleItem(configId, configUuid, ruleReq, existingRulesMap, processedRuleIds)) {
                continue;
            }
            createAutoNumberRuleItem(configId, configUuid, ruleReq);
        }
        deleteObsoleteAutoNumberRuleItems(existingRules, processedRuleIds);
    }

    protected void normalizeAutoNumberRuleRef(AutoNumberRuleVO ruleReq) {
        if (!AutoNumberItemTypeEnum.FIELD_REF.getCode().equalsIgnoreCase(ruleReq.getItemType())) {
            return;
        }
        String resolvedRefFieldUuid = resolveFieldRefUuid(ruleReq);
        if (resolvedRefFieldUuid == null || resolvedRefFieldUuid.trim().isEmpty()) {
            log.error("FIELD_REF类型规则项缺少引用字段UUID，跳过保存。itemOrder={}, format={}, refFieldUuid={}",
                    ruleReq.getItemOrder(), ruleReq.getFormat(), ruleReq.getRefFieldUuid());
            throw new IllegalArgumentException("FIELD_REF类型规则项必须指定引用字段UUID");
        }
        ruleReq.setRefFieldUuid(resolvedRefFieldUuid);
        ruleReq.setFormat(null);
    }

    protected boolean updateExistingAutoNumberRuleItem(Long configId, String configUuid, AutoNumberRuleVO ruleReq,
            Map<Long, MetadataAutoNumberRuleItemDO> existingRulesMap, Set<Long> processedRuleIds) {
        if (ruleReq.getId() == null) {
            return false;
        }
        MetadataAutoNumberRuleItemDO existingRule = existingRulesMap.get(ruleReq.getId());
        if (existingRule == null) {
            log.warn("请求中的规则项ID不存在，将作为新增处理，id={}", ruleReq.getId());
            return false;
        }
        applyAutoNumberRuleItem(existingRule, configUuid, ruleReq);
        autoNumberRuleBuildService.update(existingRule);
        processedRuleIds.add(ruleReq.getId());
        log.info("更新自动编号规则项，id={}, configId={}, itemOrder={}",
                ruleReq.getId(), configId, ruleReq.getItemOrder());
        return true;
    }

    protected void createAutoNumberRuleItem(Long configId, String configUuid, AutoNumberRuleVO ruleReq) {
        MetadataAutoNumberRuleItemDO rule = new MetadataAutoNumberRuleItemDO();
        applyAutoNumberRuleItem(rule, configUuid, ruleReq);
        Long newRuleId = autoNumberRuleBuildService.add(rule);
        log.info("新增自动编号规则项，id={}, configId={}, itemOrder={}",
                newRuleId, configId, ruleReq.getItemOrder());
    }

    protected void applyAutoNumberRuleItem(MetadataAutoNumberRuleItemDO rule, String configUuid, AutoNumberRuleVO ruleReq) {
        rule.setConfigUuid(configUuid);
        rule.setItemType(ruleReq.getItemType());
        rule.setItemOrder(ruleReq.getItemOrder());
        rule.setFormat(ruleReq.getFormat());
        rule.setTextValue(ruleReq.getTextValue());
        rule.setRefFieldUuid(ruleReq.getRefFieldUuid());
        rule.setIsEnabled(ruleReq.getIsEnabled() != null ? ruleReq.getIsEnabled() : StatusEnumUtil.ENABLED);
        rule.setApplicationId(null);
    }

    protected void deleteObsoleteAutoNumberRuleItems(List<MetadataAutoNumberRuleItemDO> existingRules,
            Set<Long> processedRuleIds) {
        for (MetadataAutoNumberRuleItemDO existingRule : existingRules) {
            if (!processedRuleIds.contains(existingRule.getId())) {
                autoNumberRuleBuildService.deleteById(existingRule.getId());
                log.info("删除未在请求中出现的自动编号规则项，id={}, itemOrder={}",
                        existingRule.getId(), existingRule.getItemOrder());
            }
        }
    }

    protected void deleteAllAutoNumberRuleItems(List<MetadataAutoNumberRuleItemDO> existingRules) {
        for (MetadataAutoNumberRuleItemDO existingRule : existingRules) {
            autoNumberRuleBuildService.deleteById(existingRule.getId());
            log.info("删除自动编号规则项，id={}", existingRule.getId());
        }
    }

    protected static class AutoNumberRuleParts {
        protected AutoNumberRuleVO sequenceRule;
        protected List<AutoNumberRuleVO> otherRules = new java.util.ArrayList<>();
    }

    /**
     * 解析FIELD_REF类型规则项的引用字段UUID
     * <p>
     * 兼容处理逻辑：
     * 1. 如果 refFieldUuid 不为空，直接返回
     * 2. 如果 refFieldUuid 为空但 format 有值，尝试将 format 作为字段标识符（可能是ID或UUID）转换为UUID
     * 3. 否则返回 null
     *
     * @param ruleReq 规则项请求VO
     * @return 解析后的字段UUID，如果无法解析则返回null
     */
    protected String resolveFieldRefUuid(AutoNumberRuleVO ruleReq) {
        // 1. 优先使用 refFieldUuid
        if (ruleReq.getRefFieldUuid() != null && !ruleReq.getRefFieldUuid().trim().isEmpty()) {
            return ruleReq.getRefFieldUuid();
        }

        // 2. 尝试从 format 字段获取并转换（兼容前端将字段ID放在format的情况）
        if (ruleReq.getFormat() != null && !ruleReq.getFormat().trim().isEmpty()) {
            try {
                String resolvedUuid = idUuidConverter.toFieldUuid(ruleReq.getFormat());
                log.info("FIELD_REF规则项兼容处理：从 format={} 转换得到 refFieldUuid={}",
                        ruleReq.getFormat(), resolvedUuid);
                return resolvedUuid;
            } catch (Exception e) {
                log.warn("FIELD_REF规则项转换失败：无法将 format={} 转换为字段UUID，错误: {}",
                        ruleReq.getFormat(), e.getMessage());
            }
        }

        // 3. 无法解析
        return null;
    }

    /**
     * 初始值变更后重置序号状态，使下一条记录从新初始值开始
     *
     * @param config 最新自动编号配置
     */
    protected void resetAutoNumberStateOnInitialChange(MetadataAutoNumberConfigDO config) {
        String periodKey = autoNumberRuleEngine.generatePeriodKey(
                config.getResetCycle(), LocalDateTime.now());

        MetadataAutoNumberStateDO state = autoNumberStateRepository.findOneByPeriod(config.getConfigUuid(), periodKey);
        Long prevValue = state != null ? state.getCurrentValue() : null;
        Long nextValue = config.getInitialValue() - 1;

        if (state == null) {
            state = new MetadataAutoNumberStateDO();
            state.setConfigUuid(config.getConfigUuid());
            state.setPeriodKey(periodKey);
            state.setCurrentValue(nextValue);
            state.setApplicationId(config.getApplicationId());
            autoNumberStateRepository.save(state);
        } else {
            MetadataAutoNumberStateDO update = new MetadataAutoNumberStateDO();
            update.setId(state.getId());
            update.setCurrentValue(nextValue);
            autoNumberStateRepository.updateById(update);
        }

        MetadataAutoNumberResetLogDO resetLog = new MetadataAutoNumberResetLogDO();
        resetLog.setConfigId(config.getId());
        resetLog.setPeriodKey(periodKey);
        resetLog.setPrevValue(prevValue);
        resetLog.setNextValue(nextValue + 1);
        resetLog.setResetReason("修改初始值后生效");
        resetLog.setApplicationId(config.getApplicationId());
        resetLog.setResetTime(LocalDateTime.now());
        autoNumberResetLogRepository.save(resetLog);
    }

    /**
     * 填充字段相关数据到响应VO
     */
    protected void populateFieldRelatedData(MetadataEntityFieldDO field, EntityFieldRespVO vo) {
        // 填充选项信息
        if (MetadataFieldTypeCodeEnum.isOptionLike(field.getFieldType())) {
            var options = fieldOptionService.listByFieldId(field.getFieldUuid());
            if (options != null && !options.isEmpty()) {
                List<FieldOptionRespVO> optionVOs = options.stream().map(o -> {
                    FieldOptionRespVO item = new FieldOptionRespVO();
                    item.setId(o.getId() != null ? String.valueOf(o.getId()) : null);
                    item.setOptionUuid(o.getOptionUuid());
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

        // 填充自动编号完整配置（统一规则项列表，包含SEQUENCE）
        MetadataAutoNumberConfigDO config = autoNumberConfigBuildService.getByFieldId(field.getFieldUuid());
        if (config != null) {
            log.debug("找到字段 {} 的自动编号配置，configId: {}", field.getFieldUuid(), config.getId());
            // 基本配置
            AutoNumberConfigRespVO full = convertToAutoNumberConfigRespVO(config);
            // 构建统一规则项列表
            List<AutoNumberRuleVO> unifiedRules = buildUnifiedRuleVOList(config);
            full.setRuleItems(unifiedRules);
            vo.setAutoNumberConfig(full);
        } else {
            log.debug("字段 {} 没有配置自动编号", field.getFieldUuid());
        }

        // 填充数据选择配置（单选和多选都需要）
        if (MetadataFieldTypeCodeEnum.isDataSelection(field.getFieldType())) {
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
    protected DataSelectionConfig buildDataSelectionConfig(MetadataEntityFieldDO field) {
        if (field == null || field.getId() == null || field.getEntityUuid() == null) {
            return null;
        }

        // 使用 findBySourceEntityUuidAndTargetEntityUuid 方法查询关系
        // 当前实体是source，查询以当前实体为source的关系
        List<MetadataEntityRelationshipDO> relationships = metadataEntityRelationshipBuildService
                .findBySourceEntityUuidAndTargetEntityUuid(field.getEntityUuid(), null);
        
        // 过滤出 sourceFieldUuid 匹配当前字段的关系
        MetadataEntityRelationshipDO relationship = null;
        if (relationships != null) {
            for (MetadataEntityRelationshipDO rel : relationships) {
                if (rel.getSourceFieldUuid() != null && rel.getSourceFieldUuid().equals(field.getFieldUuid())) {
                    relationship = rel;
                    break;
                }
            }
        }
        
        if (relationship == null || relationship.getTargetEntityUuid() == null || relationship.getSelectFieldUuid() == null) {
            return null;
        }

        // 构建返回数据
        // 关系存储：source=当前实体, target=被选择实体, selectField=展示字段
        // 前端需要：targetEntityUuid=被选择实体, targetFieldUuid=展示字段
        DataSelectionConfig dataSelectionConfig = new DataSelectionConfig();
        dataSelectionConfig.setRelationId(relationship.getId());
        dataSelectionConfig.setTargetEntityUuid(relationship.getTargetEntityUuid());
        dataSelectionConfig.setTargetFieldUuid(relationship.getSelectFieldUuid());
        
        // 同时提供ID格式，兼容前端
        // 通过UUID查询对应的实体和字段,获取其ID和名称
        try {
            MetadataBusinessEntityDO targetEntity = metadataBusinessEntityRepository.getByEntityUuid(relationship.getTargetEntityUuid());
            if (targetEntity != null) {
                dataSelectionConfig.setTargetEntityId(targetEntity.getId());
                dataSelectionConfig.setTargetTableName(targetEntity.getTableName());
            }
            
            MetadataEntityFieldDO selectField = metadataEntityFieldRepository.getByFieldUuid(relationship.getSelectFieldUuid());
            if (selectField != null) {
                dataSelectionConfig.setTargetFieldId(selectField.getId());
                dataSelectionConfig.setTargetFieldName(selectField.getFieldName());
            }
        } catch (Exception e) {
            log.warn("查询UUID对应的ID失败: {}", e.getMessage());
        }
        
        return dataSelectionConfig;
    }


    /**
     * 转换自动编号配置DO为响应VO（基本信息）
     */
    protected AutoNumberConfigRespVO convertToAutoNumberConfigRespVO(MetadataAutoNumberConfigDO config) {
        AutoNumberConfigRespVO vo = new AutoNumberConfigRespVO();
        vo.setId(config.getId());
        vo.setConfigUuid(config.getConfigUuid());
        vo.setFieldUuid(config.getFieldUuid());
        vo.setIsEnabled(config.getIsEnabled());
        vo.setVersionTag(config.getVersionTag());
        vo.setApplicationId(config.getApplicationId());
        vo.setCreateTime(config.getCreateTime());
        vo.setUpdateTime(config.getUpdateTime());
        return vo;
    }

    /**
     * 转换自动编号规则项DO为响应VO
     */
    protected AutoNumberRuleItemRespVO convertToAutoNumberRuleItemRespVO(MetadataAutoNumberRuleItemDO rule) {
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

}
