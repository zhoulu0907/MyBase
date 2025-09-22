package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationLengthRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationLengthSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationLengthUpdateReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupSaveReqVO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationLengthRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationLengthDO;
import com.cmsr.onebase.module.metadata.build.service.entity.MetadataEntityFieldBuildService;
import com.cmsr.onebase.module.metadata.core.util.StatusEnumUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 长度校验 Service 实现
 *
 * @author bty418
 * @date 2025-08-27
 */
@Service
@Slf4j
public class MetadataValidationLengthBuildServiceImpl implements MetadataValidationLengthBuildService {

    @Resource private MetadataValidationLengthRepository lengthRepository; // 自身仓库
    @Resource private MetadataValidationRuleGroupBuildService ruleGroupService; // 其他服务
    @Resource private MetadataEntityFieldBuildService entityFieldService; // 其他服务
    @Resource private MetadataEntityFieldRepository entityFieldRepository; // 字段仓库，用于同步数据

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

        // 处理规则组：先查找，不存在则创建，且禁止不同字段复用同一group_id
        Long groupId;
        var existingGroup = ruleGroupService.getByName(vo.getRgName());
        boolean canReuse = false;
        if (existingGroup != null) {
            // 检查该group_id下是否已存在其他字段的长度校验
            var groupLengthList = lengthRepository.findByGroupId(existingGroup.getId());
            if (groupLengthList.isEmpty() || (groupLengthList.size() == 1 && groupLengthList.get(0).getFieldId().equals(vo.getFieldId()))) {
                canReuse = true;
            }
        }
        if (existingGroup != null && canReuse) {
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
            groupVO.setValidationType("LENGTH");
            // 修复：同步entityId到规则组
            groupVO.setEntityId(field.getEntityId());
            groupId = ruleGroupService.createValidationRuleGroup(groupVO);
        }

        // 转换VO为DO并设置必要字段
        MetadataValidationLengthDO data = BeanUtils.toBean(vo, MetadataValidationLengthDO.class);
        data.setEntityId(field.getEntityId());
        data.setAppId(field.getAppId());
        data.setGroupId(groupId);

        // 保存长度校验规则
        lengthRepository.upsert(data);
        
        // 同步到MetadataEntityFieldDO：如果设置了maxLength，则同步到dataLength字段
        syncToEntityField(vo.getFieldId(), data.getMaxLength());
        
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

        // 处理规则组：先查找，不存在则创建，且禁止不同字段复用同一group_id
        Long groupId;
        var existingGroup = ruleGroupService.getByName(reqVO.getRgName());
        boolean canReuse = false;
        if (existingGroup != null) {
            var groupLengthList = lengthRepository.findByGroupId(existingGroup.getId());
            if (groupLengthList.isEmpty() || (groupLengthList.size() == 1 && groupLengthList.get(0).getFieldId().equals(existingDO.getFieldId()))) {
                canReuse = true;
            }
        }
        if (existingGroup != null && canReuse) {
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
            groupVO.setValidationType("LENGTH");
            groupVO.setEntityId(entityFieldDO.getEntityId());
            groupId = ruleGroupService.createValidationRuleGroup(groupVO);
        }

        // 转换为DO对象并保留必要字段
        MetadataValidationLengthDO updateDO = BeanUtils.toBean(reqVO, MetadataValidationLengthDO.class);
        updateDO.setFieldId(existingDO.getFieldId());
        updateDO.setEntityId(existingDO.getEntityId());
        updateDO.setAppId(existingDO.getAppId());
        updateDO.setGroupId(groupId);

        // 执行更新
        lengthRepository.update(updateDO); // 使用update而不是upsert，避免主键冲突
        
        // 同步到MetadataEntityFieldDO：更新dataLength字段
        syncToEntityField(existingDO.getFieldId(), updateDO.getMaxLength());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(Long fieldId) {
        // 先获取要删除的记录，以便后续删除关联的校验规则分组
        List<MetadataValidationLengthDO> recordsToDelete = lengthRepository.findByFieldId(fieldId);
        
        // 删除长度校验记录
        lengthRepository.deleteByFieldId(fieldId);
        
        // 删除关联的校验规则分组（如果没有其他记录引用）
        for (MetadataValidationLengthDO record : recordsToDelete) {
            if (record.getGroupId() != null) {
                deleteRuleGroupIfNotReferenced(record.getGroupId());
            }
        }
        
        // 同步到MetadataEntityFieldDO：清空dataLength字段
        syncToEntityField(fieldId, null);
    }

    @Override
    public ValidationLengthRespVO getById(Long id) {
        MetadataValidationLengthDO lengthDO = lengthRepository.findById(id);
        if (lengthDO == null) {
            return null;
        }

        // 转换DO为VO
        ValidationLengthRespVO respVO = BeanUtils.toBean(lengthDO, ValidationLengthRespVO.class);

        // 查询并设置规则组名称
        if (lengthDO.getGroupId() != null) {
            var ruleGroup = ruleGroupService.getValidationRuleGroup(lengthDO.getGroupId());
            if (ruleGroup != null) {
                respVO.setRgName(ruleGroup.getRgName());
            }
        }

        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        // 先获取要删除的记录
        MetadataValidationLengthDO lengthDO = lengthRepository.findById(id);
        if (lengthDO == null) {
            return; // 记录不存在，直接返回
        }

        Long groupId = lengthDO.getGroupId();
        Long fieldId = lengthDO.getFieldId();

        // 删除长度校验记录
        lengthRepository.deleteById(id);

        // 删除关联的校验规则分组（如果没有其他记录引用）
        if (groupId != null) {
            deleteRuleGroupIfNotReferenced(groupId);
        }

        // 同步到MetadataEntityFieldDO：清空dataLength字段
        if (fieldId != null) {
            syncToEntityField(fieldId, null);
        }
    }
    
    /**
     * 同步长度校验到MetadataEntityFieldDO
     * 根据TODO需求：如果调用了ValidationLengthController增删改接口，也需要将信息同步到metadataEntityFieldDO中
     */
    private void syncToEntityField(Long fieldId, Integer maxLength) {
        try {
            // 获取字段信息
            MetadataEntityFieldDO field = entityFieldService.getEntityField(String.valueOf(fieldId));
            if (field != null) {
                // 创建更新对象，只更新dataLength字段
                MetadataEntityFieldDO updateField = new MetadataEntityFieldDO();
                updateField.setId(fieldId);
                updateField.setDataLength(maxLength); // 将maxLength同步到dataLength
                
                // 直接使用字段仓库进行更新
                entityFieldRepository.update(updateField);
            }
        } catch (Exception e) {
            // 记录错误但不影响主流程
            log.warn("同步长度校验到字段时发生异常，字段ID: {}, maxLength: {}, 错误: {}", fieldId, maxLength, e.getMessage(), e);
        }
    }
    
    /**
     * 删除校验规则分组（如果没有其他记录引用）
     */
    private void deleteRuleGroupIfNotReferenced(Long groupId) {
        try {
            // 检查是否还有其他校验记录引用这个分组
            boolean hasOtherReferences = false;
            
            // 检查长度校验表
            List<MetadataValidationLengthDO> lengthRecords = lengthRepository.findByGroupId(groupId);
            if (!lengthRecords.isEmpty()) {
                hasOtherReferences = true;
            }
            
            // TODO: 如果需要，可以检查其他校验类型表（Required、Unique、Range等）
            
            // 如果没有其他引用，删除规则分组
            if (!hasOtherReferences) {
                ruleGroupService.deleteValidationRuleGroup(groupId);
                log.info("删除了无引用的校验规则分组: {}", groupId);
            }
        } catch (Exception e) {
            // 记录错误但不影响主流程
            log.warn("删除校验规则分组时发生异常，分组ID: {}, 错误: {}", groupId, e.getMessage(), e);
        }
    }
}
