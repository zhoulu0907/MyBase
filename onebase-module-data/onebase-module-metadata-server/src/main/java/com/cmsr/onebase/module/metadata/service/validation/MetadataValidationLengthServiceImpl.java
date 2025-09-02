package com.cmsr.onebase.module.metadata.service.validation;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationLengthRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationLengthSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationLengthUpdateReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleGroupSaveReqVO;
import com.cmsr.onebase.module.metadata.dal.database.MetadataValidationLengthRepository;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationLengthDO;
import com.cmsr.onebase.module.metadata.service.entity.MetadataEntityFieldService;
import com.cmsr.onebase.module.metadata.util.StatusEnumUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 长度校验 Service 实现
 *
 * @author bty418
 * @date 2025-08-27
 */
@Service
public class MetadataValidationLengthServiceImpl implements MetadataValidationLengthService {

    @Resource private MetadataValidationLengthRepository lengthRepository; // 自身仓库
    @Resource private MetadataValidationRuleGroupService ruleGroupService; // 其他服务
    @Resource private MetadataEntityFieldService entityFieldService; // 其他服务

    @Override
    public MetadataValidationLengthDO getByFieldId(Long fieldId) {
        return lengthRepository.findOneByFieldId(fieldId);
    }

    @Override
    public ValidationLengthRespVO getByFieldIdWithRgName(Long fieldId) {
        MetadataValidationLengthDO lengthDO = lengthRepository.findOneByFieldId(fieldId);
        if (lengthDO == null) {
            return null;
        }
        
        // 转换DO为VO
        ValidationLengthRespVO respVO = BeanUtils.toBean(lengthDO, ValidationLengthRespVO.class);
        
        // 获取规则组名称
        var ruleGroup = ruleGroupService.getValidationRuleGroup(lengthDO.getGroupId());
        if (ruleGroup != null) {
            respVO.setRgName(ruleGroup.getRgName());
        }
        
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ValidationLengthSaveReqVO vo) {
        Assert.notNull(vo, "vo不能为空");
        Assert.notNull(vo.getFieldId(), "字段ID不能为空");
        Assert.hasText(vo.getRgName(), "规则组名称不能为空");

        // 获取字段信息
        MetadataEntityFieldDO field = entityFieldService.getEntityField(String.valueOf(vo.getFieldId()));
        Assert.notNull(field, "字段不存在");

        // 检查同一字段是否已存在长度校验规则
        MetadataValidationLengthDO existingRule = lengthRepository.findOneByFieldId(vo.getFieldId());
        if (existingRule != null) {
            throw new IllegalStateException("该字段已存在长度校验规则，同一字段只能有一条长度校验规则");
        }

        // 处理规则组：先查找，不存在则创建
        Long groupId;
        var existingGroup = ruleGroupService.getByName(vo.getRgName());
        if (existingGroup != null) {
            groupId = existingGroup.getId();
        } else {
            // 创建新的规则组
            ValidationRuleGroupSaveReqVO groupVO = new ValidationRuleGroupSaveReqVO();
            groupVO.setRgName(vo.getRgName());
            groupVO.setRgDesc("自动创建的规则组：" + vo.getRgName());
            groupVO.setRgStatus(StatusEnumUtil.ACTIVE);
            // 透传可选的组级提示配置
            groupVO.setValMethod(vo.getValMethod());
            groupVO.setPopPrompt(vo.getPopPrompt());
            groupVO.setPopType(vo.getPopType());
            groupId = ruleGroupService.createValidationRuleGroup(groupVO);
        }

        // 转换VO为DO并设置必要字段
        MetadataValidationLengthDO data = BeanUtils.toBean(vo, MetadataValidationLengthDO.class);
        data.setEntityId(field.getEntityId());
        data.setAppId(field.getAppId());
        data.setGroupId(groupId);

        // 保存长度校验规则
        lengthRepository.upsert(data);
        return data.getId();
    }

        @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ValidationLengthUpdateReqVO reqVO) {
        // 查询是否存在
        MetadataValidationLengthDO existingDO = lengthRepository.findById(reqVO.getId());
        Assert.notNull(existingDO, "当前长度校验规则不存在");

        // 查询字段信息
        MetadataEntityFieldDO entityFieldDO = entityFieldService.getEntityField(String.valueOf(existingDO.getFieldId()));
        Assert.notNull(entityFieldDO, "字段不存在");

        // 处理规则组：先查找，不存在则创建
        Long groupId;
        var existingGroup = ruleGroupService.getByName(reqVO.getRgName());
        if (existingGroup != null) {
            groupId = existingGroup.getId();
        } else {
            // 创建新的规则组
            ValidationRuleGroupSaveReqVO groupVO = new ValidationRuleGroupSaveReqVO();
            groupVO.setRgName(reqVO.getRgName());
            groupVO.setRgDesc("自动创建的规则组：" + reqVO.getRgName());
            groupVO.setRgStatus(StatusEnumUtil.ACTIVE);
            // 透传可选的组级提示配置
            groupVO.setValMethod(reqVO.getValMethod());
            groupVO.setPopPrompt(reqVO.getPopPrompt());
            groupVO.setPopType(reqVO.getPopType());
            groupId = ruleGroupService.createValidationRuleGroup(groupVO);
        }

        // 转换为DO对象并保留必要字段
        MetadataValidationLengthDO updateDO = BeanUtils.toBean(reqVO, MetadataValidationLengthDO.class);
        updateDO.setFieldId(existingDO.getFieldId());
        updateDO.setEntityId(existingDO.getEntityId());
        updateDO.setAppId(existingDO.getAppId());
        updateDO.setGroupId(groupId);
        
        // 执行更新
        lengthRepository.upsert(updateDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(Long fieldId) {
        lengthRepository.deleteByFieldId(fieldId);
    }
}
