package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationUniqueRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationUniqueSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationUniqueUpdateReqVO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationUniqueRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationUniqueDO;
import com.cmsr.onebase.module.metadata.build.service.entity.MetadataEntityFieldBuildService;
import com.cmsr.onebase.module.metadata.core.util.StatusEnumUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class MetadataValidationUniqueBuildServiceImpl implements MetadataValidationUniqueBuildService {

    @Resource private MetadataValidationUniqueRepository uniqueRepository; // 自身仓库
    @Resource private MetadataEntityFieldRepository entityFieldRepository; // 字段仓库
    @Resource private MetadataValidationRuleGroupBuildService ruleGroupService; // 其他服务
    @Resource private MetadataEntityFieldBuildService entityFieldService; // 其他服务

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

        // 处理规则组：先查找，不存在则创建，存在则校验是否被其他字段复用
    Long groupId = null;
        var existingGroup = ruleGroupService.getByName(vo.getRgName());
        boolean needCreateGroup = false;
        if (existingGroup != null) {
            // 检查该 groupId 是否已被其他字段的唯一性校验复用
            var groupUniqueList = uniqueRepository.findByGroupId(existingGroup.getId());
            boolean reused = groupUniqueList.stream().anyMatch(u -> !u.getFieldId().equals(vo.getFieldId()));
            if (reused) {
                needCreateGroup = true;
            } else {
                groupId = existingGroup.getId();
            }
        } else {
            needCreateGroup = true;
        }
        if (needCreateGroup) {
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
            // 修复：同步entityId到规则组
            groupVO.setEntityId(field.getEntityId());
            groupId = ruleGroupService.createValidationRuleGroup(groupVO);
        }
        Assert.notNull(groupId, "规则组ID未正确生成");

        // 转换VO为DO并设置必要字段
        MetadataValidationUniqueDO data = BeanUtils.toBean(vo, MetadataValidationUniqueDO.class);
        data.setEntityId(field.getEntityId());
        data.setAppId(field.getAppId());
        data.setGroupId(groupId);

        // 保存唯一性校验规则
        uniqueRepository.upsert(data);
        
        // 同步更新字段的唯一性状态为唯一
        syncFieldUniqueStatus(vo.getFieldId(), true);
        
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

        // 处理规则组：先查找，不存在则创建；若存在但被其他字段复用，则新建
        Long groupId = null;
        var existingGroup = ruleGroupService.getByName(vo.getRgName());
        boolean needCreateGroup = false;
        if (existingGroup != null) {
            var groupUniqueList = uniqueRepository.findByGroupId(existingGroup.getId());
            boolean reused = groupUniqueList.stream().anyMatch(u -> !u.getFieldId().equals(existing.getFieldId()));
            if (reused) {
                needCreateGroup = true;
            } else {
                groupId = existingGroup.getId();
            }
        } else {
            needCreateGroup = true;
        }
        if (needCreateGroup) {
            ValidationRuleGroupSaveReqVO groupVO = new ValidationRuleGroupSaveReqVO();
            groupVO.setRgName(vo.getRgName());
            groupVO.setRgDesc("自动创建的规则组：" + vo.getRgName());
            groupVO.setRgStatus(StatusEnumUtil.ACTIVE);
            groupVO.setValMethod(vo.getValMethod());
            groupVO.setPopPrompt(vo.getPopPrompt());
            groupVO.setPopType(vo.getPopType());
            groupVO.setValidationType("UNIQUE");
            groupVO.setEntityId(field.getEntityId());
            groupId = ruleGroupService.createValidationRuleGroup(groupVO);
        }
        Assert.notNull(groupId, "规则组ID未正确生成");

        // 将 VO 转换为 DO 并设置必要字段
        MetadataValidationUniqueDO updateObj = BeanUtils.toBean(vo, MetadataValidationUniqueDO.class);
        updateObj.setFieldId(existing.getFieldId());
        updateObj.setEntityId(existing.getEntityId());
        updateObj.setAppId(existing.getAppId());
        updateObj.setGroupId(groupId);

        // 执行更新
        uniqueRepository.update(updateObj); // 使用update而不是upsert，避免主键冲突
        
        // 同步更新字段的唯一性状态（根据校验规则的启用状态决定）
        boolean isFieldUnique = updateObj.getIsEnabled() != null && updateObj.getIsEnabled() == 1;
        syncFieldUniqueStatus(existing.getFieldId(), isFieldUnique);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(Long fieldId) {
        uniqueRepository.deleteByFieldId(fieldId);
        
        // 同步更新字段的唯一性状态为非唯一
        syncFieldUniqueStatus(fieldId, false);
    }

    @Override
    public ValidationUniqueRespVO getById(Long id) {
        MetadataValidationUniqueDO uniqueDO = uniqueRepository.findById(id);
        if (uniqueDO == null) {
            var group = ruleGroupService.getValidationRuleGroup(id);
            if (group != null) {
                var list = uniqueRepository.findByGroupId(group.getId());
                if (!list.isEmpty()) {
                    uniqueDO = list.get(0);
                }
            }
            if (uniqueDO == null) {
                return null;
            }
        }

        // 转换DO为VO
        ValidationUniqueRespVO respVO = BeanUtils.toBean(uniqueDO, ValidationUniqueRespVO.class);

        // 查询并设置规则组名称
        if (uniqueDO.getGroupId() != null) {
            var ruleGroup = ruleGroupService.getValidationRuleGroup(uniqueDO.getGroupId());
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
        MetadataValidationUniqueDO uniqueDO = uniqueRepository.findById(id);
        if (uniqueDO == null) {
            return; // 记录不存在，直接返回
        }

        Long fieldId = uniqueDO.getFieldId();

        // 删除唯一性校验记录
        uniqueRepository.deleteById(id);

        // 同步更新字段的唯一性状态为非唯一
        if (fieldId != null) {
            syncFieldUniqueStatus(fieldId, false);
        }
    }
    
    /**
     * 同步字段的唯一性状态
     * 
     * @param fieldId 字段ID
     * @param unique 是否唯一
     */
    private void syncFieldUniqueStatus(Long fieldId, boolean unique) {
        try {
            MetadataEntityFieldDO field = entityFieldRepository.findById(fieldId);
            if (field != null && field.getIsUnique() != (unique ? 1 : 0)) {
                field.setIsUnique(unique ? 1 : 0);
                // 使用直接更新而不是 upsert，避免主键冲突
                entityFieldRepository.update(field);
            }
        } catch (Exception e) {
            // 如果更新失败，记录日志但不中断流程
            // 这种情况通常发生在同一事务中有其他操作正在处理同一字段
            log.warn("同步字段唯一性状态失败，fieldId: {}, unique: {}, 错误: {}", fieldId, unique, e.getMessage());
        }
    }
}
