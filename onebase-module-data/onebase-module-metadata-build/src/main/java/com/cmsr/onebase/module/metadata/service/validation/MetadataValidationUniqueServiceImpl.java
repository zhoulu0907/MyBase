package com.cmsr.onebase.module.metadata.service.validation;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleGroupSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationUniqueRespVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationUniqueSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationUniqueUpdateReqVO;
import com.cmsr.onebase.module.metadata.dal.database.MetadataValidationUniqueRepository;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationUniqueDO;
import com.cmsr.onebase.module.metadata.service.entity.MetadataEntityFieldService;
import com.cmsr.onebase.module.metadata.util.StatusEnumUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 唯一校验 Service 实现
 *
 * @author bty418
 * @date 2025-08-27
 */
@Service
public class MetadataValidationUniqueServiceImpl implements MetadataValidationUniqueService {

    @Resource private MetadataValidationUniqueRepository uniqueRepository; // 自身仓库
    @Resource private MetadataValidationRuleGroupService ruleGroupService; // 其他服务
    @Resource private MetadataEntityFieldService entityFieldService; // 其他服务

    @Override
    public MetadataValidationUniqueDO getByFieldId(Long fieldId) {
        return uniqueRepository.findOneByFieldId(fieldId);
    }

    @Override
    public ValidationUniqueRespVO getByFieldIdWithRgName(Long fieldId) {
        MetadataValidationUniqueDO uniqueDO = uniqueRepository.findOneByFieldId(fieldId);
        if (uniqueDO == null) {
            return null;
        }
        
        // 转换DO为VO
        ValidationUniqueRespVO respVO = BeanUtils.toBean(uniqueDO, ValidationUniqueRespVO.class);
        
        // 获取规则组名称
        var ruleGroup = ruleGroupService.getValidationRuleGroup(uniqueDO.getGroupId());
        if (ruleGroup != null) {
            respVO.setRgName(ruleGroup.getRgName());
        }
        
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ValidationUniqueSaveReqVO vo) {
        Assert.notNull(vo, "vo不能为空");
        Assert.notNull(vo.getFieldId(), "字段ID不能为空");
        Assert.hasText(vo.getRgName(), "规则组名称不能为空");

        // 获取字段信息
        MetadataEntityFieldDO field = entityFieldService.getEntityField(String.valueOf(vo.getFieldId()));
        Assert.notNull(field, "字段不存在");

        // 检查同一字段是否已存在唯一性校验规则
        MetadataValidationUniqueDO existingRule = uniqueRepository.findOneByFieldId(vo.getFieldId());
        if (existingRule != null) {
            throw new IllegalStateException("该字段已存在唯一性校验规则，同一字段只能有一条唯一性校验规则");
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
            groupVO.setValidationType("UNIQUE");
            groupId = ruleGroupService.createValidationRuleGroup(groupVO);
        }

        // 转换VO为DO并设置必要字段
        MetadataValidationUniqueDO data = BeanUtils.toBean(vo, MetadataValidationUniqueDO.class);
        data.setEntityId(field.getEntityId());
        data.setAppId(field.getAppId());
        data.setGroupId(groupId);

        // 保存唯一性校验规则
        uniqueRepository.upsert(data);
        return data.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ValidationUniqueUpdateReqVO vo) {
        Assert.notNull(vo, "vo不能为空");
        Assert.notNull(vo.getId(), "id不能为空");
        Assert.hasText(vo.getRgName(), "规则组名称不能为空");
        
        // 查询现有记录获取完整信息
        MetadataValidationUniqueDO existing = uniqueRepository.findById(vo.getId());
        Assert.notNull(existing, "记录不存在");
        
        // 获取字段信息
        MetadataEntityFieldDO field = entityFieldService.getEntityField(String.valueOf(existing.getFieldId()));
        Assert.notNull(field, "字段不存在");
        
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
            groupVO.setValidationType("UNIQUE");
            groupId = ruleGroupService.createValidationRuleGroup(groupVO);
        }
        
        // 将 VO 转换为 DO 并设置必要字段
        MetadataValidationUniqueDO updateObj = BeanUtils.toBean(vo, MetadataValidationUniqueDO.class);
        updateObj.setFieldId(existing.getFieldId());
        updateObj.setEntityId(existing.getEntityId());
        updateObj.setAppId(existing.getAppId());
        updateObj.setGroupId(groupId);
        
        // 执行更新
        uniqueRepository.upsert(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(Long fieldId) {
        uniqueRepository.deleteByFieldId(fieldId);
    }
}
