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
import com.cmsr.onebase.module.metadata.core.util.MetadataIdUuidConverter;
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
    @Resource private MetadataIdUuidConverter idUuidConverter; // ID转UUID工具

    @Override
    public MetadataValidationUniqueDO getByFieldId(String fieldUuid) {
        return uniqueRepository.findOneByFieldUuid(fieldUuid);
    }

    @Override
    public ValidationUniqueRespVO getByFieldIdWithRgName(String fieldUuid) {
        MetadataValidationUniqueDO uniqueDO = uniqueRepository.findOneByFieldUuid(fieldUuid);
        if (uniqueDO == null) {
            return null;
        }

        // 转换DO为VO
        ValidationUniqueRespVO respVO = BeanUtils.toBean(uniqueDO, ValidationUniqueRespVO.class);

        // 获取规则组信息，包括提示语等字段
        var ruleGroup = ruleGroupService.getValidationRuleGroupByUuid(uniqueDO.getGroupUuid());
        if (ruleGroup != null) {
            respVO.setRgName(ruleGroup.getRgName());
            respVO.setPromptMessage(ruleGroup.getPopPrompt());
        }

        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ValidationUniqueSaveReqVO vo) {
        Assert.notNull(vo, "vo不能为空");
        // ID与UUID兼容处理：优先使用UUID，若为空则通过ID转换
        vo.setFieldUuid(idUuidConverter.resolveFieldUuid(vo.getFieldUuid(), vo.getFieldId()));
        Assert.hasText(vo.getRgName(), "规则组名称不能为空");

        // 获取字段信息
        MetadataEntityFieldDO field = entityFieldService.getEntityFieldByUuid(vo.getFieldUuid());
        Assert.notNull(field, "字段不存在");

        // 检查同一字段是否已存在唯一性校验规则
        MetadataValidationUniqueDO existingRule = uniqueRepository.findOneByFieldUuid(vo.getFieldUuid());
        if (existingRule != null) {
            throw new IllegalStateException("该字段已存在唯一性校验规则，同一字段只能有一条唯一性校验规则");
        }

        // 处理规则组：先查找，不存在则创建，存在则校验是否被其他字段复用
        Long groupId = null;
        String groupUuid = null;
        var existingGroup = ruleGroupService.getByName(vo.getRgName());
        boolean needCreateGroup = false;
        if (existingGroup != null) {
            // 检查该 groupUuid 是否已被其他字段的唯一性校验复用
            var groupUniqueList = uniqueRepository.findByGroupUuid(existingGroup.getGroupUuid());
            boolean reused = groupUniqueList.stream().anyMatch(u -> !u.getFieldUuid().equals(vo.getFieldUuid()));
            if (reused) {
                needCreateGroup = true;
            } else {
                groupId = existingGroup.getId();
                groupUuid = existingGroup.getGroupUuid();
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
            // 修复：同步entityUuid到规则组
            groupVO.setEntityUuid(field.getEntityUuid());
            groupId = ruleGroupService.createValidationRuleGroup(groupVO);
            // 获取新建规则组的UUID
            var newGroup = ruleGroupService.getValidationRuleGroup(groupId);
            if (newGroup != null) {
                groupUuid = newGroup.getGroupUuid();
            }
        }
        Assert.notNull(groupId, "规则组ID未正确生成");

        // 转换VO为DO并设置必要字段
        MetadataValidationUniqueDO data = BeanUtils.toBean(vo, MetadataValidationUniqueDO.class);
        data.setEntityUuid(field.getEntityUuid());
        data.setApplicationId(field.getApplicationId() != null ? Long.valueOf(field.getApplicationId()) : null);
        data.setGroupUuid(groupUuid);

        // 保存唯一性校验规则
        uniqueRepository.saveOrUpdate(data);
        
        // 同步更新字段的唯一性状态为唯一
        syncFieldUniqueStatus(vo.getFieldUuid(), true);
        
        return data.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ValidationUniqueUpdateReqVO vo) {
        Assert.notNull(vo, "vo不能为空");
        Assert.notNull(vo.getId(), "groupUuid不能为空");
        String groupUuidParam = String.valueOf(vo.getId());
        var list = uniqueRepository.findByGroupUuid(groupUuidParam);
        Assert.notEmpty(list, "当前唯一性校验规则不存在(组UUID=" + groupUuidParam + ")");
        if (list.size() > 1) {
            throw new IllegalStateException("数据异常：同一组存在多条唯一性校验规则(组UUID=" + groupUuidParam + ")");
        }
        MetadataValidationUniqueDO existing = list.get(0);
        MetadataEntityFieldDO field = entityFieldService.getEntityFieldByUuid(existing.getFieldUuid());
        Assert.notNull(field, "字段不存在");

        // 不变更组，但需要同步组配置(popPrompt/valMethod/popType)
        String targetGroupUuid = groupUuidParam;
        var groupDO = ruleGroupService.getValidationRuleGroupByUuid(groupUuidParam);
        if (groupDO != null) {
            boolean needGroupUpdate = false;
            ValidationRuleGroupSaveReqVO updateGroupVO = new ValidationRuleGroupSaveReqVO();
            updateGroupVO.setId(groupDO.getId());
            updateGroupVO.setRgName(groupDO.getRgName());
            updateGroupVO.setRgDesc(groupDO.getRgDesc());
            updateGroupVO.setRgStatus(groupDO.getRgStatus());
            updateGroupVO.setValidationType(groupDO.getValidationType());
            updateGroupVO.setEntityUuid(groupDO.getEntityUuid());
            if (vo.getPopPrompt() != null && !vo.getPopPrompt().equals(groupDO.getPopPrompt())) {
                updateGroupVO.setPopPrompt(vo.getPopPrompt());
                needGroupUpdate = true;
            }
            if (vo.getValMethod() != null && !vo.getValMethod().equals(groupDO.getValMethod())) {
                updateGroupVO.setValMethod(vo.getValMethod());
                needGroupUpdate = true;
            }
            if (vo.getPopType() != null && !vo.getPopType().equals(groupDO.getPopType())) {
                updateGroupVO.setPopType(vo.getPopType());
                needGroupUpdate = true;
            }
            if (needGroupUpdate) { ruleGroupService.updateValidationRuleGroup(updateGroupVO); }
        }
        MetadataValidationUniqueDO updateObj = BeanUtils.toBean(vo, MetadataValidationUniqueDO.class);
        updateObj.setId(existing.getId());
        updateObj.setFieldUuid(existing.getFieldUuid());
        updateObj.setEntityUuid(existing.getEntityUuid());
        updateObj.setApplicationId(existing.getApplicationId());
        updateObj.setGroupUuid(targetGroupUuid);
        uniqueRepository.updateById(updateObj);
        boolean isFieldUnique = updateObj.getIsEnabled() != null && updateObj.getIsEnabled() == 1;
        syncFieldUniqueStatus(existing.getFieldUuid(), isFieldUnique);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(String fieldUuid) {
        // 先获取要删除的记录，以便后续删除关联的校验规则分组
        MetadataValidationUniqueDO recordToDelete = uniqueRepository.findOneByFieldUuid(fieldUuid);
        
        // 删除唯一性校验记录
        uniqueRepository.deleteByFieldUuid(fieldUuid);
        
        // 删除关联的校验规则分组
        if (recordToDelete != null && recordToDelete.getGroupUuid() != null) {
            ruleGroupService.safeDeleteGroupDirect(recordToDelete.getGroupUuid());
        }
        
        // 同步更新字段的唯一性状态为非唯一
        syncFieldUniqueStatus(fieldUuid, false);
    }

    @Override
    public ValidationUniqueRespVO getById(Long id) {
        var group = ruleGroupService.getValidationRuleGroup(id);
        if (group == null) { return null; }
        var list = uniqueRepository.findByGroupUuid(group.getGroupUuid());
        if (list.isEmpty()) { return null; }
        if (list.size() > 1) { throw new IllegalStateException("数据异常：同一组存在多条唯一性校验规则(组UUID=" + group.getGroupUuid() + ")"); }
        MetadataValidationUniqueDO uniqueDO = list.get(0);
        ValidationUniqueRespVO respVO = BeanUtils.toBean(uniqueDO, ValidationUniqueRespVO.class);
        
        // 获取规则组信息，包括提示语等字段
        respVO.setRgName(group.getRgName());
        respVO.setPromptMessage(group.getPopPrompt());
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        var group = ruleGroupService.getValidationRuleGroup(id);
        if (group == null) { return; }
        var list = uniqueRepository.findByGroupUuid(group.getGroupUuid());
        
        // 删除子表记录和同步字段状态
        if (!list.isEmpty()) {
            if (list.size() > 1) {
                throw new IllegalStateException("数据异常：同一组存在多条唯一性校验规则(组UUID=" + group.getGroupUuid() + ")");
            }
            MetadataValidationUniqueDO uniqueDO = list.get(0);
            String fieldUuid = uniqueDO.getFieldUuid();
            uniqueRepository.removeById(uniqueDO.getId());
            if (fieldUuid != null) {
                syncFieldUniqueStatus(fieldUuid, false);
            }
        }
        
        // 无论子表是否存在，都要删除主表作为兜底（防止脏数据）
        ruleGroupService.safeDeleteGroupDirect(id);
    }
    
    /**
     * 同步字段的唯一性状态
     * 
     * @param fieldUuid 字段UUID
     * @param unique 是否唯一
     */
    private void syncFieldUniqueStatus(String fieldUuid, boolean unique) {
        try {
            MetadataEntityFieldDO field = entityFieldRepository.getByUuid(fieldUuid);
            if (field != null && field.getIsUnique() != (unique ? 1 : 0)) {
                field.setIsUnique(unique ? 1 : 0);
                // 使用直接更新而不是 upsert，避免主键冲突
                entityFieldRepository.updateById(field);
            }
        } catch (Exception e) {
            // 如果更新失败，记录日志但不中断流程
            // 这种情况通常发生在同一事务中有其他操作正在处理同一字段
            log.warn("同步字段唯一性状态失败，fieldUuid: {}, unique: {}, 错误: {}", fieldUuid, unique, e.getMessage());
        }
    }
}
