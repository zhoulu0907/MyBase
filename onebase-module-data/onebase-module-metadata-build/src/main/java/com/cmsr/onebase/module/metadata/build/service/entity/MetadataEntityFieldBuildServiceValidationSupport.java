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
public abstract class MetadataEntityFieldBuildServiceValidationSupport implements MetadataEntityFieldBuildService {

    protected abstract void validateFieldNameNotSystemReserved(String fieldName);

    @Resource
    protected MetadataEntityRelationshipBuildService metadataEntityRelationshipBuildService;

    @Resource
    protected MetadataEntityFieldRepository metadataEntityFieldRepository;
    @Resource
    protected MetadataBusinessEntityRepository metadataBusinessEntityRepository;
    @Resource
    protected TemporaryDatasourceService temporaryDatasourceService;
    @Resource
    protected MetadataBusinessEntityCoreService metadataBusinessEntityCoreService;
    @Resource
    protected MetadataDatasourceBuildService metadataDatasourceBuildService;
    @Resource
    protected MetadataEntityFieldOptionBuildService fieldOptionService;
    @Resource
    protected MetadataEntityFieldConstraintBuildService fieldConstraintService;
    @Resource
    protected AutoNumberConfigBuildService autoNumberConfigBuildService;
    @Resource
    protected AutoNumberRuleBuildService autoNumberRuleBuildService;
    @Resource
    protected MetadataAutoNumberStateRepository autoNumberStateRepository;
    @Resource
    protected MetadataAutoNumberResetLogRepository autoNumberResetLogRepository;
    @Resource
    protected AutoNumberRuleEngine autoNumberRuleEngine;
    @Resource
    protected MetadataComponentFieldTypeBuildService componentFieldTypeService;
    @Resource
    protected MetadataPermitRefOtftBuildService permitRefOtftService;
    @Resource
    protected MetadataSystemFieldsRepository systemFieldsRepository;
    @Resource
    protected MetadataComponentFieldTypeRepository componentFieldTypeRepository;
    @Resource
    protected MetadataValidationTypeBuildService validationTypeService;
    @Resource
    protected MetadataValidationRequiredBuildService validationRequiredService;
    @Resource
    protected MetadataValidationUniqueBuildService validationUniqueService;
    @Resource
    protected MetadataValidationLengthBuildService validationLengthService;
    @Resource
    protected MetadataValidationRequiredRepository validationRequiredRepository;
    @Resource
    protected MetadataValidationUniqueRepository validationUniqueRepository;
    @Resource
    protected MetadataValidationLengthRepository validationLengthRepository;
    @Resource
    protected MetadataValidationFormatRepository validationFormatRepository;
    @Resource
    protected MetadataValidationRuleGroupBuildService validationRuleGroupService;

    @Resource
    protected MetadataIdUuidConverter idUuidConverter;

    @Resource
    protected ModelMapper modelMapper;
    /**
     * 构建统一规则项VO列表
     * <p>
     * 将Config表中的SEQUENCE配置和RuleItem表中的其他规则项合并为统一的AutoNumberRuleVO列表，
     * 按itemOrder排序返回
     *
     * @param config 自动编号配置DO
     * @return 统一规则项VO列表
     */
    protected List<AutoNumberRuleVO> buildUnifiedRuleVOList(MetadataAutoNumberConfigDO config) {
        List<AutoNumberRuleVO> unifiedRules = new java.util.ArrayList<>();

        // 1. 从Config构建SEQUENCE规则项
        AutoNumberRuleVO sequenceRule = new AutoNumberRuleVO();
        sequenceRule.setId(config.getId());
        sequenceRule.setUuid(config.getConfigUuid());
        sequenceRule.setItemType(AutoNumberItemTypeEnum.SEQUENCE.getCode());
        sequenceRule.setItemOrder(config.getSequenceOrder() != null ? config.getSequenceOrder() : 999);
        sequenceRule.setIsEnabled(config.getIsEnabled());
        sequenceRule.setNumberMode(config.getNumberMode());
        sequenceRule.setDigitWidth(config.getDigitWidth());
        sequenceRule.setOverflowContinue(config.getOverflowContinue());
        sequenceRule.setInitialValue(config.getInitialValue());
        sequenceRule.setResetCycle(config.getResetCycle());
        sequenceRule.setResetOnInitialChange(config.getResetOnInitialChange());
        sequenceRule.setCreateTime(config.getCreateTime());
        sequenceRule.setUpdateTime(config.getUpdateTime());
        unifiedRules.add(sequenceRule);

        // 2. 从RuleItem表获取其他类型规则项
        List<MetadataAutoNumberRuleItemDO> ruleItems = autoNumberConfigBuildService.listRules(config.getId());
        if (ruleItems != null) {
            for (MetadataAutoNumberRuleItemDO item : ruleItems) {
                AutoNumberRuleVO ruleVO = new AutoNumberRuleVO();
                ruleVO.setId(item.getId());
                ruleVO.setUuid(item.getRuleItemUuid());
                ruleVO.setItemType(item.getItemType());
                ruleVO.setItemOrder(item.getItemOrder());
                ruleVO.setIsEnabled(item.getIsEnabled());
                ruleVO.setTextValue(item.getTextValue());
                ruleVO.setFormat(item.getFormat());
                ruleVO.setRefFieldUuid(item.getRefFieldUuid());
                ruleVO.setCreateTime(item.getCreateTime());
                ruleVO.setUpdateTime(item.getUpdateTime());
                unifiedRules.add(ruleVO);
            }
        }

        // 3. 按itemOrder排序
        unifiedRules.sort(java.util.Comparator.comparingInt(r -> r.getItemOrder() != null ? r.getItemOrder() : Integer.MAX_VALUE));

        return unifiedRules;
    }

    /**
     * 手动转换MetadataEntityFieldDO为EntityFieldRespVO
     * 避免ModelMapper的复杂嵌套对象映射冲突
     */
    protected EntityFieldRespVO convertToEntityFieldRespVO(MetadataEntityFieldDO field) {
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
    protected void processLengthValidation(Long fieldId, MetadataEntityFieldDO entityField) {
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
                    }
                    var group = validationRuleGroupService.resolveRuleGroup(null, targetGroupUuid,
                            buildRuleGroupSaveReq(buildLengthRuleGroupName(fieldId),
                                    MetadataValidationRuleTypeEnum.LENGTH.getCode(), entityField,
                                    existingDO.getPromptMessage()));
                    if (group == null) {
                        log.warn("长度校验同步失败，字段ID: {}, 规则组不存在且无法重建，跳过更新", fieldId);
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
    protected void processRequiredValidation(Long fieldId, MetadataEntityFieldDO entityField) {
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
                    }
                    var group = validationRuleGroupService.resolveRuleGroup(null, targetGroupUuid,
                            buildRuleGroupSaveReq(buildRequiredRuleGroupName(fieldId),
                                    MetadataValidationRuleTypeEnum.REQUIRED.getCode(), entityField,
                                    existingDO.getPromptMessage()));
                    if (group == null) {
                        log.warn("必填校验同步失败，字段ID: {}, 规则组不存在且无法重建，跳过更新", fieldId);
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
    protected void processUniqueValidation(Long fieldId, MetadataEntityFieldDO entityField) {
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
                    }
                    var group = validationRuleGroupService.resolveRuleGroup(null, targetGroupUuid,
                            buildRuleGroupSaveReq(buildUniqueRuleGroupName(fieldId),
                                    MetadataValidationRuleTypeEnum.UNIQUE.getCode(), entityField,
                                    existingDO.getPromptMessage()));
                    if (group == null) {
                        log.warn("唯一校验同步失败，字段ID: {}, 规则组不存在且无法重建，跳过更新", fieldId);
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
    protected String buildRuleGroupName(Long fieldId, String validationType) {
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
    protected String getValidationTypeName(String validationType) {
        if (validationType == null) {
            return "未知校验";
        }

        MetadataValidationRuleTypeEnum type = MetadataValidationRuleTypeEnum.getByCode(validationType);
        return type != null ? type.getDisplayName() : validationType + "校验";
    }

    protected String buildRequiredRuleGroupName(Long fieldId) {
        return buildRuleGroupName(fieldId, MetadataValidationRuleTypeEnum.REQUIRED.getCode());
    }

    protected String buildUniqueRuleGroupName(Long fieldId) {
        return buildRuleGroupName(fieldId, MetadataValidationRuleTypeEnum.UNIQUE.getCode());
    }

    protected String buildLengthRuleGroupName(Long fieldId) {
        return buildRuleGroupName(fieldId, MetadataValidationRuleTypeEnum.LENGTH.getCode());
    }

    protected String buildRangeRuleGroupName(Long fieldId) {
        return buildRuleGroupName(fieldId, MetadataValidationRuleTypeEnum.RANGE.getCode());
    }

    protected String buildFormatRuleGroupName(Long fieldId) {
        return buildRuleGroupName(fieldId, MetadataValidationRuleTypeEnum.FORMAT.getCode());
    }

    protected String buildChildNotEmptyRuleGroupName(Long fieldId) {
        return buildRuleGroupName(fieldId, MetadataValidationRuleTypeEnum.CHILD_NOT_EMPTY.getCode());
    }

    protected String buildSelfDefinedRuleGroupName(Long fieldId) {
        return buildRuleGroupName(fieldId, MetadataValidationRuleTypeEnum.SELF_DEFINED.getCode());
    }

    protected ValidationRuleGroupSaveReqVO buildRuleGroupSaveReq(String rgName, String validationType,
            MetadataEntityFieldDO entityField, String popPrompt) {
        ValidationRuleGroupSaveReqVO groupVO = new ValidationRuleGroupSaveReqVO();
        groupVO.setRgName(rgName);
        groupVO.setRgDesc("自动重建的规则组：" + rgName);
        groupVO.setRgStatus(StatusEnumUtil.ACTIVE);
        groupVO.setValidationType(validationType);
        groupVO.setEntityUuid(entityField.getEntityUuid());
        groupVO.setApplicationId(entityField.getApplicationId());
        groupVO.setPopPrompt(popPrompt);
        return groupVO;
    }

    /**
     * 校验批量提交的字段中是否有重复的字段名
     * 
     * @param items 待保存的字段列表
     * @throws IllegalArgumentException 如果存在重复的字段名
     */
    protected void validateFieldNameDuplicationInBatch(List<EntityFieldUpsertItemVO> items) {
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
    protected void validateValidationRuleUniqueness(String fieldUuid, String entityUuid) {
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

    @Override
    public List<FieldTypeValidationTypesRespVO> getValidationTypesByFieldTypes(@Valid FieldTypeValidationTypesReqVO reqVO) {
        List<String> fieldTypeCodes = reqVO.getFieldTypeCodes();
        
        if (fieldTypeCodes == null || fieldTypeCodes.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. 查询字段类型（根据 field_type_code）
        QueryWrapper typeQueryWrapper = componentFieldTypeRepository.query()
                .where(MetadataComponentFieldTypeDO::getFieldTypeCode).in(fieldTypeCodes);
        List<MetadataComponentFieldTypeDO> fieldTypes = componentFieldTypeRepository.list(typeQueryWrapper);
        
        if (fieldTypes.isEmpty()) {
            return new ArrayList<>();
        }

        // 构建 字段类型ID -> 字段类型信息 的映射
        Map<Long, MetadataComponentFieldTypeDO> typeIdToInfo = fieldTypes.stream()
                .filter(ft -> ft.getId() != null)
                .collect(Collectors.toMap(
                        MetadataComponentFieldTypeDO::getId,
                        ft -> ft
                ));

        // 2. 查询关联表 metadata_permit_ref_otft（字段类型ID -> 校验类型ID）
        List<MetadataPermitRefOtftDO> relations = permitRefOtftService.listByFieldTypeIds(typeIdToInfo.keySet());

        // 收集所有用到的校验类型ID
        Set<Long> validationTypeIds = relations.stream()
                .map(MetadataPermitRefOtftDO::getValidationTypeId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, MetadataValidationTypeDO> validationTypeMap = new HashMap<>();
        if (!validationTypeIds.isEmpty()) {
            // 3. 查询校验类型详情
            validationTypeMap = validationTypeService.getByIds(validationTypeIds);
        }

        // 4. 按字段类型ID分组组装校验类型列表
        Map<Long, List<ValidationTypeItemRespVO>> typeIdToValidations = new HashMap<>();
        for (MetadataPermitRefOtftDO rel : relations) {
            Long ftId = rel.getFieldTypeId();
            Long vtId = rel.getValidationTypeId();
            Integer sort = rel.getSortOrder();
            
            if (ftId == null || vtId == null) {
                continue;
            }
            
            MetadataValidationTypeDO validationType = validationTypeMap.get(vtId);
            if (validationType == null) {
                continue;
            }
            
            ValidationTypeItemRespVO item = new ValidationTypeItemRespVO();
            item.setCode(validationType.getValidationCode());
            item.setName(validationType.getValidationName());
            item.setDescription(validationType.getValidationDesc());
            item.setSortOrder(sort);
            
            typeIdToValidations.computeIfAbsent(ftId, k -> new ArrayList<>()).add(item);
        }

        // 5. 确保每个列表按 sort_order 升序
        for (List<ValidationTypeItemRespVO> list : typeIdToValidations.values()) {
            list.sort((a, b) -> {
                Integer s1 = a.getSortOrder() != null ? a.getSortOrder() : 0;
                Integer s2 = b.getSortOrder() != null ? b.getSortOrder() : 0;
                return Integer.compare(s1, s2);
            });
        }

        // 6. 组装返回结果，按字段类型编码分组
        List<FieldTypeValidationTypesRespVO> result = new ArrayList<>();
        for (MetadataComponentFieldTypeDO fieldType : fieldTypes) {
            FieldTypeValidationTypesRespVO vo = new FieldTypeValidationTypesRespVO();
            vo.setFieldTypeCode(fieldType.getFieldTypeCode());
            vo.setFieldTypeName(fieldType.getFieldTypeName());
            vo.setFieldTypeDesc(fieldType.getFieldTypeDesc());
            vo.setValidationTypes(typeIdToValidations.getOrDefault(fieldType.getId(), new ArrayList<>()));
            result.add(vo);
        }
        
        return result;
    }

    @Override
    public List<MetadataEntityFieldDO> getByFieldUuids(java.util.Collection<String> fieldUuids) {
        return metadataEntityFieldRepository.getByFieldUuids(fieldUuids);
    }
}
