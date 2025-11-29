package com.cmsr.onebase.module.metadata.build.service.field;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.FieldConstraintRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo.FieldConstraintSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.*;
import com.cmsr.onebase.module.metadata.build.service.validation.*;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationFormatDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationLengthDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRequiredDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationUniqueDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 字段约束 Service 实现（基于新规则表）
 */
@Service
@Slf4j
public class MetadataEntityFieldConstraintBuildServiceImpl implements MetadataEntityFieldConstraintBuildService {

    @Resource private MetadataValidationLengthBuildService lengthService;
    @Resource private MetadataValidationFormatBuildService formatService;
    @Resource private MetadataValidationRequiredBuildService requiredService;
    @Resource private MetadataValidationUniqueBuildService uniqueService;
    @Resource private MetadataValidationRangeBuildService rangeService;
    @Resource private MetadataValidationChildNotEmptyBuildService childNotEmptyService;
    @Resource private MetadataEntityFieldRepository entityFieldRepository;
    @Resource private MetadataBusinessEntityCoreService metadataBusinessEntityCoreService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(Long fieldId) {
    lengthService.deleteByFieldId(fieldId);
    formatService.deleteByFieldId(fieldId);
    requiredService.deleteByFieldId(fieldId);
    uniqueService.deleteByFieldId(fieldId);
    rangeService.deleteByFieldId(fieldId);
    childNotEmptyService.deleteByFieldId(fieldId);
    }

    @Override
    public FieldConstraintRespVO getFieldConstraintConfig(Long fieldId) {
        FieldConstraintRespVO resp = new FieldConstraintRespVO();
    // 长度
    MetadataValidationLengthDO len = lengthService.getByFieldId(fieldId);
        if (len != null) {
            resp.setLengthEnabled(len.getIsEnabled());
            resp.setMinLength(len.getMinLength());
            resp.setMaxLength(len.getMaxLength());
            resp.setLengthPrompt(len.getPromptMessage());
        }
        // 正则（格式表里 format_code=REGEX）
    MetadataValidationFormatDO regex = formatService.getRegexByFieldId(fieldId);
        if (regex != null) {
            resp.setRegexEnabled(regex.getIsEnabled());
            resp.setRegexPattern(regex.getRegexPattern());
            resp.setRegexPrompt(regex.getPromptMessage());
        }
        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveFieldConstraintConfig(FieldConstraintSaveReqVO req) {
        String type = req.getConstraintType();
        if ("LENGTH_RANGE".equalsIgnoreCase(type)) {
            Integer min = req.getMinLength();
            Integer max = req.getMaxLength();
            if (min != null && max != null && min > max) {
                throw new IllegalArgumentException("最小长度不能大于最大长度");
            }
            
            // 获取字段信息用于生成默认提示语
            MetadataEntityFieldDO field = entityFieldRepository.getById(req.getFieldId());
            if (field == null) {
                throw new IllegalStateException("字段" + req.getFieldId() + "不存在，无法同步长度校验配置");
            }
            
            // 生成默认提示语：{字段展示名称}长度不能超过X个字符
            String fieldDisplayName = field.getDisplayName() != null && !field.getDisplayName().trim().isEmpty() 
                ? field.getDisplayName() 
                : (field.getFieldName() != null ? field.getFieldName() : "字段");
            String defaultPrompt = "";
            if (max != null && min != null) {
                defaultPrompt = String.format("%s长度必须在%d-%d个字符之间", fieldDisplayName, min, max);
            } else if (max != null) {
                defaultPrompt = String.format("%s长度不能超过%d个字符", fieldDisplayName, max);
            } else if (min != null) {
                defaultPrompt = String.format("%s长度不能少于%d个字符", fieldDisplayName, min);
            } else {
                defaultPrompt = fieldDisplayName + "长度不符合要求";
            }
            String prompt = StringUtils.hasText(req.getPromptMessage()) ? req.getPromptMessage() : defaultPrompt;
            
            // upsert 长度
            MetadataValidationLengthDO exist = lengthService.getByFieldId(req.getFieldId());
            MetadataValidationLengthDO d = new MetadataValidationLengthDO();
            if (exist != null) { d.setId(exist.getId()); }
            d.setFieldId(req.getFieldId());
            d.setIsEnabled(req.getIsEnabled());
            d.setMinLength(req.getMinLength());
            d.setMaxLength(req.getMaxLength());
            d.setTrimBefore(1);
            d.setPromptMessage(prompt);
            d.setRunMode(req.getRunMode());
            if (d.getId() == null) {
                // 将DO转换为VO
                ValidationLengthSaveReqVO lengthVO = BeanUtils.toBean(d, ValidationLengthSaveReqVO.class);
                lengthVO.setRgName(buildLengthGroupName(req.getFieldId())); // 设置规则组名称
                lengthVO.setPopPrompt(prompt); // 设置popPrompt确保errorMessage字段能正确返回
                lengthService.create(lengthVO);
            } else {
                // 将DO转换为UpdateReqVO
                ValidationLengthUpdateReqVO lengthUpdateVO = BeanUtils.toBean(d, ValidationLengthUpdateReqVO.class);
                lengthUpdateVO.setRgName(buildLengthGroupName(req.getFieldId())); // 设置规则组名称
                lengthUpdateVO.setPopPrompt(prompt); // 设置popPrompt确保errorMessage字段能正确返回
                lengthService.update(lengthUpdateVO);
            }
        } else if ("REGEX".equalsIgnoreCase(type)) {
            // 获取字段信息用于生成默认提示语
            MetadataEntityFieldDO field = entityFieldRepository.getById(req.getFieldId());
            if (field == null) {
                throw new IllegalStateException("字段" + req.getFieldId() + "不存在，无法同步格式校验配置");
            }
            
            // 生成默认提示语：{字段展示名称}格式不正确
            String fieldDisplayName = field.getDisplayName() != null && !field.getDisplayName().trim().isEmpty() 
                ? field.getDisplayName() 
                : (field.getFieldName() != null ? field.getFieldName() : "字段");
            String defaultPrompt = fieldDisplayName + "格式不正确";
            String prompt = StringUtils.hasText(req.getPromptMessage()) ? req.getPromptMessage() : defaultPrompt;
            
            // upsert 正则（格式表 format_code=REGEX）
            MetadataValidationFormatDO exist = formatService.getRegexByFieldId(req.getFieldId());
            MetadataValidationFormatDO d = new MetadataValidationFormatDO();
            if (exist != null) { d.setId(exist.getId()); }
            d.setFieldId(req.getFieldId());
            d.setIsEnabled(req.getIsEnabled());
            d.setFormatCode("REGEX");
            d.setRegexPattern(req.getRegexPattern());
            d.setFlags(null);
            d.setPromptMessage(prompt);
            d.setRunMode(req.getRunMode());
            if (d.getId() == null) {
                // 将DO转换为VO
                ValidationFormatSaveReqVO formatVO = BeanUtils.toBean(d, ValidationFormatSaveReqVO.class);
                formatVO.setFormatCode("REGEX");
                formatVO.setRegexPattern(req.getRegexPattern());
                formatVO.setRgName(buildFormatGroupName(req.getFieldId())); // 设置规则组名称
                formatVO.setPopPrompt(prompt); // 设置popPrompt确保errorMessage字段能正确返回
                formatService.create(formatVO);
            } else {
                // 将DO转换为UpdateReqVO
                ValidationFormatUpdateReqVO formatUpdateVO = BeanUtils.toBean(d, ValidationFormatUpdateReqVO.class);
                formatUpdateVO.setFormatCode("REGEX");
                formatUpdateVO.setRgName(buildFormatGroupName(req.getFieldId())); // 设置规则组名称
                formatUpdateVO.setPopPrompt(prompt); // 设置popPrompt确保errorMessage字段能正确返回
                formatService.update(formatUpdateVO);
            }
        } else if ("REQUIRED".equalsIgnoreCase(type)) {
            // 同步必填到 required 表
            MetadataValidationRequiredDO exist = requiredService.getByFieldId(req.getFieldId());
            MetadataEntityFieldDO field = entityFieldRepository.getById(req.getFieldId());
            if (field == null) {
                throw new IllegalStateException("字段" + req.getFieldId() + "不存在，无法同步必填校验配置");
            }
            
            // 生成默认提示语：{字段展示名称}为必填项
            String fieldDisplayName = field.getDisplayName() != null && !field.getDisplayName().trim().isEmpty() 
                ? field.getDisplayName() 
                : (field.getFieldName() != null ? field.getFieldName() : "此字段");
            String defaultPrompt = fieldDisplayName + "为必填项";
            String prompt = StringUtils.hasText(req.getPromptMessage()) ? req.getPromptMessage() : defaultPrompt;
            
            if (exist == null) {
                // 直接创建VO对象，避免DO到VO的转换问题
                ValidationRequiredSaveReqVO requiredVO = new ValidationRequiredSaveReqVO();
                requiredVO.setFieldId(req.getFieldId());
                requiredVO.setIsEnabled(req.getIsEnabled());
                requiredVO.setPromptMessage(prompt);
                requiredVO.setPopPrompt(prompt); // 设置popPrompt确保errorMessage字段能正确返回
                requiredVO.setRunMode(req.getRunMode());
                requiredVO.setRgName(buildRequiredGroupName(req.getFieldId()));
                requiredService.create(requiredVO);
            } else {
                // 直接创建UpdateReqVO对象，避免DO到VO的转换问题
                ValidationRequiredUpdateReqVO requiredUpdateVO = new ValidationRequiredUpdateReqVO();
                requiredUpdateVO.setId(exist.getId());
                requiredUpdateVO.setIsEnabled(req.getIsEnabled());
                requiredUpdateVO.setPromptMessage(prompt);
                requiredUpdateVO.setPopPrompt(prompt); // 设置popPrompt确保errorMessage字段能正确返回
                requiredUpdateVO.setRunMode(req.getRunMode());
                requiredUpdateVO.setRgName(buildRequiredGroupName(req.getFieldId()));
                requiredService.update(requiredUpdateVO);
            }
        } else if ("UNIQUE".equalsIgnoreCase(type)) {
            MetadataValidationUniqueDO exist = uniqueService.getByFieldId(req.getFieldId());
            MetadataEntityFieldDO field = entityFieldRepository.getById(req.getFieldId());
            if (field == null) {
                throw new IllegalStateException("字段" + req.getFieldId() + "不存在，无法同步唯一性校验配置");
            }

            Integer enableFlag = req.getIsEnabled() != null ? req.getIsEnabled() : 0;
            
            // 生成默认提示语：{字段展示名称}必须唯一
            String fieldDisplayName = field.getDisplayName() != null && !field.getDisplayName().trim().isEmpty() 
                ? field.getDisplayName() 
                : (field.getFieldName() != null ? field.getFieldName() : "此字段");
            String defaultPrompt = fieldDisplayName + "必须唯一";
            String prompt = StringUtils.hasText(req.getPromptMessage()) ? req.getPromptMessage() : defaultPrompt;
            String defaultGroupName = buildUniqueGroupName(req.getFieldId());

            if (exist == null) {
                ValidationUniqueSaveReqVO uniqueVO = new ValidationUniqueSaveReqVO();
                uniqueVO.setEntityId(field.getEntityId());
                uniqueVO.setFieldId(req.getFieldId());
                uniqueVO.setIsEnabled(enableFlag);
                uniqueVO.setPromptMessage(prompt);
                uniqueVO.setRunMode(req.getRunMode());
                uniqueVO.setPopPrompt(prompt);
                uniqueVO.setRgName(defaultGroupName);
                uniqueService.create(uniqueVO);
            } else {
                ValidationUniqueRespVO respVO = uniqueService.getByFieldIdWithRgName(req.getFieldId());
                Long groupId = exist.getGroupId();
                if (groupId == null && respVO != null) {
                    groupId = respVO.getGroupId();
                }
                if (groupId == null) {
                    throw new IllegalStateException("字段" + req.getFieldId() + "缺少唯一性规则组，无法更新");
                }
                String groupName = (respVO != null && StringUtils.hasText(respVO.getRgName()))
                        ? respVO.getRgName()
                        : defaultGroupName;
                ValidationUniqueUpdateReqVO uniqueUpdateVO = new ValidationUniqueUpdateReqVO();
                uniqueUpdateVO.setId(groupId);
                uniqueUpdateVO.setIsEnabled(enableFlag);
                uniqueUpdateVO.setPromptMessage(prompt);
                uniqueUpdateVO.setRunMode(req.getRunMode());
                uniqueUpdateVO.setPopPrompt(prompt);
                uniqueUpdateVO.setRgName(groupName);
                uniqueService.update(uniqueUpdateVO);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long fieldId, String constraintType) {
        if ("LENGTH_RANGE".equalsIgnoreCase(constraintType)) {
            MetadataValidationLengthDO exist = lengthService.getByFieldId(fieldId);
            if (exist != null) { lengthService.deleteByFieldId(fieldId); }
        } else if ("REGEX".equalsIgnoreCase(constraintType)) {
            MetadataValidationFormatDO exist = formatService.getRegexByFieldId(fieldId);
            if (exist != null) { formatService.deleteByFieldId(fieldId); }
        } else if ("REQUIRED".equalsIgnoreCase(constraintType)) {
            MetadataValidationRequiredDO exist = requiredService.getByFieldId(fieldId);
            if (exist != null) { requiredService.deleteByFieldId(fieldId); }
        } else if ("UNIQUE".equalsIgnoreCase(constraintType)) {
            MetadataValidationUniqueDO exist = uniqueService.getByFieldId(fieldId);
            if (exist != null) { uniqueService.deleteByFieldId(fieldId); }
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
            MetadataEntityFieldDO field = entityFieldRepository.getById(fieldId);
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

    private String buildRequiredGroupName(Long fieldId) {
        return buildRuleGroupName(fieldId, "REQUIRED");
    }

    private String buildUniqueGroupName(Long fieldId) {
        return buildRuleGroupName(fieldId, "UNIQUE");
    }

    private String buildLengthGroupName(Long fieldId) {
        return buildRuleGroupName(fieldId, "LENGTH");
    }

    private String buildFormatGroupName(Long fieldId) {
        return buildRuleGroupName(fieldId, "FORMAT");
    }

    private String buildRangeGroupName(Long fieldId) {
        return buildRuleGroupName(fieldId, "RANGE");
    }

    private String buildChildNotEmptyGroupName(Long fieldId) {
        return buildRuleGroupName(fieldId, "CHILD_NOT_EMPTY");
    }

    private String buildSelfDefinedGroupName(Long fieldId) {
        return buildRuleGroupName(fieldId, "SELF_DEFINED");
    }

}


