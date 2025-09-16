package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRequiredRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRequiredSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRequiredUpdateReqVO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRequiredRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRequiredDO;
import com.cmsr.onebase.module.metadata.build.service.entity.MetadataEntityFieldBuildService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 必填校验 Service 实现
 *
 * @author bty418
 * @date 2025-08-27
 */
@Service
@Slf4j
public class MetadataValidationRequiredBuildServiceImpl implements MetadataValidationRequiredBuildService {

    @Resource
    private MetadataValidationRequiredRepository requiredRepository;

    @Resource
    private MetadataEntityFieldRepository entityFieldRepository;

    @Resource
    private MetadataEntityFieldBuildService entityFieldService;

    @Resource
    private MetadataValidationRuleGroupBuildService validationRuleGroupService;

    @Override
    public MetadataValidationRequiredDO getByFieldId(Long fieldId) {
        return requiredRepository.findOneByFieldId(fieldId);
    }

    @Override
    public ValidationRequiredRespVO getByFieldIdWithRgName(Long fieldId) {
        MetadataValidationRequiredDO requiredDO = requiredRepository.findOneByFieldId(fieldId);
        if (requiredDO == null) {
            return null;
        }

        // 转换DO为VO
        ValidationRequiredRespVO respVO = BeanUtils.toBean(requiredDO, ValidationRequiredRespVO.class);

        // 简化实现：暂时不设置规则组名称，专注于字段同步功能
        // TODO: 如需要规则组名称，可以从requiredDO.getGroupId()查询获取
        
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ValidationRequiredSaveReqVO vo) {
        Assert.notNull(vo, "vo不能为空");
        Assert.notNull(vo.getFieldId(), "字段ID不能为空");
        Assert.hasText(vo.getRgName(), "规则组名称不能为空");

        // 获取字段信息
        MetadataEntityFieldDO field = entityFieldService.getEntityField(String.valueOf(vo.getFieldId()));
        Assert.notNull(field, "字段不存在");

        // 检查同一字段是否已存在必填校验规则
        MetadataValidationRequiredDO existingRule = requiredRepository.findOneByFieldId(vo.getFieldId());
        if (existingRule != null) {
            throw new IllegalStateException("该字段已存在必填校验规则，同一字段只能有一条必填校验规则");
        }

        // 确保字段规则组存在，如果不存在则自动创建
        Long groupId = validationRuleGroupService.ensureFieldRuleGroup(vo.getFieldId());

        // 转换VO为DO并设置必要字段
        MetadataValidationRequiredDO data = BeanUtils.toBean(vo, MetadataValidationRequiredDO.class);
        data.setEntityId(field.getEntityId());
        data.setAppId(field.getAppId());
        data.setGroupId(groupId);

        // 保存必填校验规则
        requiredRepository.upsert(data);
        
        // 同步更新字段的必填状态为必填
        syncFieldRequiredStatus(vo.getFieldId(), true);
        
        return data.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ValidationRequiredUpdateReqVO reqVO) {
        // 查询是否存在
        MetadataValidationRequiredDO existingDO = requiredRepository.findById(reqVO.getId());
        Assert.notNull(existingDO, "当前必填校验规则不存在");

        // 查询字段信息
        MetadataEntityFieldDO entityFieldDO = entityFieldService.getEntityField(String.valueOf(existingDO.getFieldId()));
        Assert.notNull(entityFieldDO, "字段不存在");

        // 确保字段规则组存在，如果不存在则自动创建
        Long groupId = validationRuleGroupService.ensureFieldRuleGroup(existingDO.getFieldId());

        // 转换为DO对象并保留必要字段
        MetadataValidationRequiredDO updateDO = BeanUtils.toBean(reqVO, MetadataValidationRequiredDO.class);
        updateDO.setFieldId(existingDO.getFieldId());
        updateDO.setEntityId(existingDO.getEntityId());
        updateDO.setAppId(existingDO.getAppId());
        updateDO.setGroupId(groupId);

        // 执行更新
        requiredRepository.update(updateDO); // 使用update而不是upsert，避免主键冲突
        
        // 同步更新字段的必填状态（根据校验规则的启用状态决定）
        boolean isFieldRequired = updateDO.getIsEnabled() != null && updateDO.getIsEnabled() == 1;
        syncFieldRequiredStatus(existingDO.getFieldId(), isFieldRequired);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(Long fieldId) {
        requiredRepository.deleteByFieldId(fieldId);
        
        // 同步更新字段的必填状态为非必填
        syncFieldRequiredStatus(fieldId, false);
    }
    
    /**
     * 同步字段的必填状态到字段表
     * 
     * @param fieldId 字段ID
     * @param required 是否必填
     */
    private void syncFieldRequiredStatus(Long fieldId, boolean required) {
        MetadataEntityFieldDO field = entityFieldRepository.findById(fieldId);
        if (field != null && field.getIsRequired() != (required ? 1 : 0)) {
            field.setIsRequired(required ? 1 : 0);
            entityFieldRepository.update(field); // 使用update而不是upsert，避免主键冲突
        }
    }
}
