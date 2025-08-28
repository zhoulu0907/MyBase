package com.cmsr.onebase.module.metadata.service.field;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.FieldConstraintRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.entity.vo.FieldConstraintSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.*;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationFormatDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationLengthDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRequiredDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationUniqueDO;
import com.cmsr.onebase.module.metadata.service.validation.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 字段约束 Service 实现（基于新规则表）
 */
@Service
public class MetadataEntityFieldConstraintServiceImpl implements MetadataEntityFieldConstraintService {

    @Resource private MetadataValidationLengthService lengthService;
    @Resource private MetadataValidationFormatService formatService;
    @Resource private MetadataValidationRequiredService requiredService;
    @Resource private MetadataValidationUniqueService uniqueService;
    @Resource private MetadataValidationRangeService rangeService;
    @Resource private MetadataValidationChildNotEmptyService childNotEmptyService;

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
            // upsert 长度
            MetadataValidationLengthDO exist = lengthService.getByFieldId(req.getFieldId());
            MetadataValidationLengthDO d = new MetadataValidationLengthDO();
            if (exist != null) { d.setId(exist.getId()); }
            d.setFieldId(req.getFieldId());
            d.setIsEnabled(req.getIsEnabled());
            d.setMinLength(req.getMinLength());
            d.setMaxLength(req.getMaxLength());
            d.setTrimBefore(1);
            d.setPromptMessage(req.getPromptMessage());
            d.setRunMode(req.getRunMode());
            if (d.getId() == null) {
                // 将DO转换为VO
                ValidationLengthSaveReqVO lengthVO = BeanUtils.toBean(d, ValidationLengthSaveReqVO.class);
                lengthVO.setRgName("字段约束-" + req.getFieldId()); // 设置规则组名称
                lengthService.create(lengthVO);
            } else {
                // 将DO转换为UpdateReqVO
                ValidationLengthUpdateReqVO lengthUpdateVO = BeanUtils.toBean(d, ValidationLengthUpdateReqVO.class);
                lengthUpdateVO.setRgName("字段约束-" + req.getFieldId()); // 设置规则组名称
                lengthService.update(lengthUpdateVO);
            }
        } else if ("REGEX".equalsIgnoreCase(type)) {
            // upsert 正则（格式表 format_code=REGEX）
            MetadataValidationFormatDO exist = formatService.getRegexByFieldId(req.getFieldId());
            MetadataValidationFormatDO d = new MetadataValidationFormatDO();
            if (exist != null) { d.setId(exist.getId()); }
            d.setFieldId(req.getFieldId());
            d.setIsEnabled(req.getIsEnabled());
            d.setFormatCode("REGEX");
            d.setRegexPattern(req.getRegexPattern());
            d.setFlags(null);
            d.setPromptMessage(req.getPromptMessage());
            d.setRunMode(req.getRunMode());
            if (d.getId() == null) {
                // 将DO转换为VO
                ValidationFormatSaveReqVO formatVO = BeanUtils.toBean(d, ValidationFormatSaveReqVO.class);
                formatVO.setRgName("字段约束-" + req.getFieldId()); // 设置规则组名称
                formatService.create(formatVO);
            } else {
                // 将DO转换为UpdateReqVO
                ValidationFormatUpdateReqVO formatUpdateVO = BeanUtils.toBean(d, ValidationFormatUpdateReqVO.class);
                formatUpdateVO.setRgName("字段约束-" + req.getFieldId()); // 设置规则组名称
                formatService.update(formatUpdateVO);
            }
        } else if ("REQUIRED".equalsIgnoreCase(type)) {
            // 同步必填到 required 表
            MetadataValidationRequiredDO exist = requiredService.getByFieldId(req.getFieldId());
            MetadataValidationRequiredDO d = new MetadataValidationRequiredDO();
            if (exist != null) { d.setId(exist.getId()); }
            d.setFieldId(req.getFieldId());
            d.setIsEnabled(req.getIsEnabled());
            d.setPromptMessage(req.getPromptMessage());
            d.setRunMode(req.getRunMode());
            if (d.getId() == null) {
                // 将DO转换为VO
                ValidationRequiredSaveReqVO requiredVO = BeanUtils.toBean(d, ValidationRequiredSaveReqVO.class);
                requiredVO.setRgName("字段约束-" + req.getFieldId()); // 设置规则组名称
                requiredService.create(requiredVO);
            } else {
                // 将DO转换为UpdateReqVO
                ValidationRequiredUpdateReqVO requiredUpdateVO = BeanUtils.toBean(d, ValidationRequiredUpdateReqVO.class);
                requiredUpdateVO.setRgName("字段约束-" + req.getFieldId()); // 设置规则组名称
                requiredService.update(requiredUpdateVO);
            }
        } else if ("UNIQUE".equalsIgnoreCase(type)) {
            // 同步唯一性到 unique 表
            MetadataValidationUniqueDO exist = uniqueService.getByFieldId(req.getFieldId());
            MetadataValidationUniqueDO d = new MetadataValidationUniqueDO();
            if (exist != null) { d.setId(exist.getId()); }
            d.setFieldId(req.getFieldId());
            d.setIsEnabled(req.getIsEnabled());
            d.setPromptMessage(req.getPromptMessage());
            d.setRunMode(req.getRunMode());
            if (d.getId() == null) {
                // 将DO转换为VO
                ValidationUniqueSaveReqVO uniqueVO = BeanUtils.toBean(d, ValidationUniqueSaveReqVO.class);
                uniqueVO.setRgName("字段约束-" + req.getFieldId()); // 设置规则组名称
                uniqueService.create(uniqueVO);
            } else {
                // 将DO转换为UpdateReqVO
                ValidationUniqueUpdateReqVO uniqueUpdateVO = BeanUtils.toBean(d, ValidationUniqueUpdateReqVO.class);
                uniqueUpdateVO.setRgName("字段约束-" + req.getFieldId()); // 设置规则组名称
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
}


