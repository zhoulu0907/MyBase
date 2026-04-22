package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationChildNotEmptyRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationChildNotEmptySaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationChildNotEmptyUpdateReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupSaveReqVO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationChildNotEmptyRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationChildNotEmptyDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import com.cmsr.onebase.module.metadata.core.util.MetadataIdUuidConverter;
import com.cmsr.onebase.module.metadata.core.util.StatusEnumUtil;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 子表非空校验 Service 实现
 */
@Service
public class MetadataValidationChildNotEmptyBuildServiceImpl implements MetadataValidationChildNotEmptyBuildService {

    @Resource private MetadataValidationChildNotEmptyRepository childNotEmptyRepository; // 自身仓库
    @Resource private MetadataValidationRuleGroupBuildService ruleGroupService; // 其他服务
    @Resource private MetadataIdUuidConverter idUuidConverter; // ID转UUID工具

    @Override
    public MetadataValidationChildNotEmptyDO getByFieldId(String fieldUuid) {
        return childNotEmptyRepository.findOneByFieldUuid(fieldUuid);
    }

    @Override
    public ValidationChildNotEmptyRespVO getByFieldIdWithRgName(String fieldUuid) {
        MetadataValidationChildNotEmptyDO validationDO = childNotEmptyRepository.findOneByFieldUuid(fieldUuid);
        if (validationDO == null) {
            return null;
        }

        // 转换为 VO
        ValidationChildNotEmptyRespVO respVO = BeanUtils.toBean(validationDO, ValidationChildNotEmptyRespVO.class);
        respVO.setChildEntityId(respVO.getChildEntityUuid());

        // 获取规则组信息，包括提示语等字段
        if (validationDO.getGroupUuid() != null) {
            MetadataValidationRuleGroupDO ruleGroup = ruleGroupService.getValidationRuleGroupByUuid(validationDO.getGroupUuid());
            if (ruleGroup != null) {
                respVO.setRgName(ruleGroup.getRgName());
                respVO.setPromptMessage(ruleGroup.getPopPrompt());
            }
        }

        return respVO;
    }

    @Override
    public ValidationChildNotEmptyRespVO getById(Long id) {
        MetadataValidationRuleGroupDO group = ruleGroupService.resolveRuleGroup(id, null, null);
        if (group == null) {
            return null;
        }
        var list = findByRuleGroup(group);
        if (list.isEmpty()) {
            ValidationChildNotEmptyRespVO fallbackVO = new ValidationChildNotEmptyRespVO();
            fallbackVO.setRgName(group.getRgName());
            fallbackVO.setEntityUuid(group.getEntityUuid());
            fallbackVO.setGroupUuid(group.getGroupUuid());
            fallbackVO.setApplicationId(group.getApplicationId() == null ? null : String.valueOf(group.getApplicationId()));
            fallbackVO.setPromptMessage(group.getPopPrompt());
            fallbackVO.setIsEnabled(1);
            return fallbackVO;
        }
        if (list.size() > 1) {
            throw new IllegalStateException("数据异常：同一组存在多条子表非空校验规则(组UUID=" + group.getGroupUuid() + ")");
        }
        MetadataValidationChildNotEmptyDO validationDO = list.get(0);

        // 转换为 VO
        ValidationChildNotEmptyRespVO respVO = BeanUtils.toBean(validationDO, ValidationChildNotEmptyRespVO.class);
        respVO.setChildEntityId(respVO.getChildEntityUuid());

        // 获取规则组信息，包括提示语等字段
        if (validationDO.getGroupUuid() != null) {
            MetadataValidationRuleGroupDO ruleGroup = ruleGroupService.getValidationRuleGroupByUuid(validationDO.getGroupUuid());
            if (ruleGroup != null) {
                respVO.setRgName(ruleGroup.getRgName());
                respVO.setPromptMessage(ruleGroup.getPopPrompt());
            }
        }

        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        MetadataValidationRuleGroupDO group = ruleGroupService.resolveRuleGroup(id, null, null);
        if (group == null) {
            return;
        }
        var list = findByRuleGroup(group);
        if (!list.isEmpty()) {
            if (list.size() > 1) {
                throw new IllegalStateException("数据异常：同一组存在多条子表非空校验规则(组UUID=" + group.getGroupUuid() + ")");
            }
            childNotEmptyRepository.removeById(list.get(0).getId());
        }
        
        // 无论子表是否存在，都要删除主表作为兜底（防止脏数据）
        ruleGroupService.safeDeleteGroupDirect(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ValidationChildNotEmptySaveReqVO vo) {
        Assert.notNull(vo, "vo不能为空");
        // ID与UUID兼容处理：优先使用UUID，若为空则通过ID转换
        vo.setEntityUuid(idUuidConverter.resolveEntityUuid(vo.getEntityUuid(), vo.getEntityId()));
        vo.setChildEntityUuid(idUuidConverter.resolveEntityUuid(vo.getChildEntityUuid(), vo.getChildEntityId()));
        Assert.hasText(vo.getRgName(), "规则组名称不能为空");

        // 检查同一父实体和子实体是否已存在子表非空校验规则
        QueryWrapper queryWrapper = childNotEmptyRepository.query()
                .eq(MetadataValidationChildNotEmptyDO::getEntityUuid, vo.getEntityUuid())
                .eq(MetadataValidationChildNotEmptyDO::getChildEntityUuid, vo.getChildEntityUuid());
        List<MetadataValidationChildNotEmptyDO> existingRules = childNotEmptyRepository.list(queryWrapper);
        if (!existingRules.isEmpty()) {
            throw new IllegalStateException("该父子实体关系已存在子表非空校验规则，同一关系只能有一条子表非空校验规则");
        }

        // 处理规则组：先查找，不存在则创建；存在但已被其他实体复用则新建
        String groupUuid = null;
        var existingGroup = ruleGroupService.getByName(vo.getRgName());
        boolean needCreateGroup = false;
        if (existingGroup != null) {
            existingGroup = ruleGroupService.resolveRuleGroup(existingGroup.getId(), existingGroup.getGroupUuid(), null);
            var groupList = childNotEmptyRepository.findByGroupUuid(existingGroup.getGroupUuid());
            boolean reused = groupList.stream().anyMatch(u -> !u.getEntityUuid().equals(vo.getEntityUuid()) 
                    || !u.getChildEntityUuid().equals(vo.getChildEntityUuid()));
            if (reused) {
                needCreateGroup = true;
            } else {
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
            groupVO.setValidationType("CHILD_NOT_EMPTY");
            // 同步entityUuid到规则组
            groupVO.setEntityUuid(vo.getEntityUuid());
            Long groupId = ruleGroupService.createValidationRuleGroup(groupVO);
            // 获取创建后的规则组UUID
            MetadataValidationRuleGroupDO createdGroup = ruleGroupService.getValidationRuleGroup(groupId);
            groupUuid = createdGroup != null ? createdGroup.getGroupUuid() : null;
        }

        // 转换VO为DO并设置必要字段
        MetadataValidationChildNotEmptyDO data = BeanUtils.toBean(vo, MetadataValidationChildNotEmptyDO.class);
        data.setEntityUuid(vo.getEntityUuid());
        data.setChildEntityUuid(vo.getChildEntityUuid());
        data.setGroupUuid(groupUuid);
        // fieldUuid设置为null，不再使用
        data.setFieldUuid(null);
        // applicationId暂时设置为null，如果需要可以从其他地方获取
        data.setApplicationId(null);
        
        // 设置默认值
        if (data.getIsEnabled() == null) {
            data.setIsEnabled(1); // 默认启用
        }
        if (data.getMinRows() == null) {
            data.setMinRows(1); // 默认最少1行
        }
        // 提示信息
        data.setPromptMessage(vo.getPopPrompt());
        // 保存子表非空校验规则
        childNotEmptyRepository.saveOrUpdate(data);
        return data.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ValidationChildNotEmptyUpdateReqVO vo) {
        Assert.notNull(vo, "vo不能为空");
        Assert.notNull(vo.getId(), "规则组ID不能为空");
        vo.setEntityUuid(idUuidConverter.resolveEntityUuid(vo.getEntityUuid(), vo.getEntityId()));
        vo.setChildEntityUuid(idUuidConverter.resolveEntityUuid(vo.getChildEntityUuid(), vo.getChildEntityId()));
        Assert.hasText(vo.getEntityUuid(), "父实体UUID不能为空");
        Assert.hasText(vo.getChildEntityUuid(), "子实体UUID不能为空");
        
        Long groupIdParam = vo.getId();
        var group = ruleGroupService.resolveRuleGroup(groupIdParam, null, null);
        Assert.notNull(group, "当前子表非空校验规则不存在(组ID=" + groupIdParam + ")");
        var list = findByRuleGroup(group);
        if (list.isEmpty()) {
            String mergedPrompt = resolvePrompt(vo.getPopPrompt(), vo.getPromptMessage(), group.getPopPrompt(), null);
            boolean needGroupUpdate = false;
            ValidationRuleGroupSaveReqVO updateGroupVO = new ValidationRuleGroupSaveReqVO();
            updateGroupVO.setId(group.getId());
            String targetRgName = StringUtils.hasText(vo.getRgName()) ? vo.getRgName() : group.getRgName();
            updateGroupVO.setRgName(targetRgName);
            updateGroupVO.setRgDesc(group.getRgDesc());
            updateGroupVO.setRgStatus(group.getRgStatus());
            updateGroupVO.setValidationType(group.getValidationType());
            updateGroupVO.setEntityUuid(group.getEntityUuid());
            if (!targetRgName.equals(group.getRgName())) { needGroupUpdate = true; }
            if (mergedPrompt != null && !mergedPrompt.equals(group.getPopPrompt())) { updateGroupVO.setPopPrompt(mergedPrompt); needGroupUpdate = true; }
            if (vo.getValMethod() != null && !vo.getValMethod().equals(group.getValMethod())) { updateGroupVO.setValMethod(vo.getValMethod()); needGroupUpdate = true; }
            if (vo.getPopType() != null && !vo.getPopType().equals(group.getPopType())) { updateGroupVO.setPopType(vo.getPopType()); needGroupUpdate = true; }
            if (needGroupUpdate) { ruleGroupService.updateValidationRuleGroup(updateGroupVO); }

            MetadataValidationChildNotEmptyDO rebuildDO = BeanUtils.toBean(vo, MetadataValidationChildNotEmptyDO.class);
            rebuildDO.setId(null);
            rebuildDO.setEntityUuid(vo.getEntityUuid());
            rebuildDO.setChildEntityUuid(vo.getChildEntityUuid());
            rebuildDO.setApplicationId(group.getApplicationId());
            rebuildDO.setGroupUuid(group.getGroupUuid());
            rebuildDO.setFieldUuid(null);
            rebuildDO.setPromptMessage(mergedPrompt);
            rebuildDO.setIsEnabled(resolveIsEnabledForRebuild(vo.getIsEnabled()));
            if (rebuildDO.getMinRows() == null) {
                rebuildDO.setMinRows(1);
            }
            childNotEmptyRepository.saveOrUpdate(rebuildDO);
            return;
        }
        if (list.size() > 1) { 
            throw new IllegalStateException("数据异常：同一组存在多条子表非空校验规则(组UUID=" + group.getGroupUuid() + ")"); 
        }
        MetadataValidationChildNotEmptyDO existing = list.get(0);
        String targetGroupUuid = group.getGroupUuid();
        String mergedPrompt = resolvePrompt(vo.getPopPrompt(), vo.getPromptMessage(), group.getPopPrompt(), existing.getPromptMessage());
        if (group != null) {
            boolean needGroupUpdate = false;
            ValidationRuleGroupSaveReqVO updateGroupVO = new ValidationRuleGroupSaveReqVO();
            updateGroupVO.setId(group.getId());
            String targetRgName = StringUtils.hasText(vo.getRgName()) ? vo.getRgName() : group.getRgName();
            updateGroupVO.setRgName(targetRgName);
            updateGroupVO.setRgDesc(group.getRgDesc());
            updateGroupVO.setRgStatus(group.getRgStatus());
            updateGroupVO.setValidationType(group.getValidationType());
            updateGroupVO.setEntityUuid(group.getEntityUuid());
            if (!targetRgName.equals(group.getRgName())) { needGroupUpdate = true; }
            if (mergedPrompt != null && !mergedPrompt.equals(group.getPopPrompt())) { updateGroupVO.setPopPrompt(mergedPrompt); needGroupUpdate = true; }
            if (vo.getValMethod() != null && !vo.getValMethod().equals(group.getValMethod())) { updateGroupVO.setValMethod(vo.getValMethod()); needGroupUpdate = true; }
            if (vo.getPopType() != null && !vo.getPopType().equals(group.getPopType())) { updateGroupVO.setPopType(vo.getPopType()); needGroupUpdate = true; }
            if (needGroupUpdate) { ruleGroupService.updateValidationRuleGroup(updateGroupVO); }
        }
        MetadataValidationChildNotEmptyDO updateObj = BeanUtils.toBean(vo, MetadataValidationChildNotEmptyDO.class);
        updateObj.setId(existing.getId());
        updateObj.setEntityUuid(vo.getEntityUuid());
        updateObj.setChildEntityUuid(vo.getChildEntityUuid());
        updateObj.setApplicationId(existing.getApplicationId());
        updateObj.setGroupUuid(targetGroupUuid);
        // fieldUuid保持不变，设置为null或保留原值
        updateObj.setFieldUuid(null);
        // 提示信息
        updateObj.setPromptMessage(mergedPrompt);
        updateObj.setIsEnabled(resolveIsEnabledForUpdate(vo.getIsEnabled(), existing.getIsEnabled()));
        childNotEmptyRepository.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(String fieldUuid) {
        // 先获取要删除的记录，以便后续删除关联的校验规则分组
        MetadataValidationChildNotEmptyDO recordToDelete = childNotEmptyRepository.findOneByFieldUuid(fieldUuid);
        
        // 删除子表非空校验记录
        childNotEmptyRepository.deleteByFieldUuid(fieldUuid);
        
        // 删除关联的校验规则分组
        if (recordToDelete != null && recordToDelete.getGroupUuid() != null) {
            ruleGroupService.safeDeleteGroupDirect(recordToDelete.getGroupUuid());
        }
    }

    private java.util.List<MetadataValidationChildNotEmptyDO> findByRuleGroup(MetadataValidationRuleGroupDO group) {
        if (group == null || !StringUtils.hasText(group.getGroupUuid())) {
            return java.util.Collections.emptyList();
        }
        java.util.List<MetadataValidationChildNotEmptyDO> list = childNotEmptyRepository.findByGroupUuid(group.getGroupUuid());
        if (!list.isEmpty()) {
            return list;
        }
        String legacyGroupUuid = String.valueOf(group.getId());
        if (legacyGroupUuid.equals(group.getGroupUuid())) {
            return list;
        }
        list = childNotEmptyRepository.findByGroupUuid(legacyGroupUuid);
        migrateGroupUuid(list, group.getGroupUuid());
        return list;
    }

    private void migrateGroupUuid(java.util.List<MetadataValidationChildNotEmptyDO> records, String targetGroupUuid) {
        if (!StringUtils.hasText(targetGroupUuid) || records == null || records.isEmpty()) {
            return;
        }
        for (MetadataValidationChildNotEmptyDO record : records) {
            if (targetGroupUuid.equals(record.getGroupUuid())) {
                continue;
            }
            record.setGroupUuid(targetGroupUuid);
            childNotEmptyRepository.updateById(record);
        }
    }

    private String resolvePrompt(String popPrompt, String promptMessage, String fallbackGroupPrompt, String fallbackRulePrompt) {
        if (StringUtils.hasText(popPrompt)) {
            return popPrompt;
        }
        if (StringUtils.hasText(promptMessage)) {
            return promptMessage;
        }
        if (StringUtils.hasText(fallbackGroupPrompt)) {
            return fallbackGroupPrompt;
        }
        return fallbackRulePrompt;
    }

    private Integer resolveIsEnabledForUpdate(Integer requested, Integer existing) {
        if (requested == null) {
            return existing == null ? 1 : existing;
        }
        // 兼容旧前端编辑接口误传 isEnabled=0，统一按启用处理，避免规则被误关
        if (requested == 0) {
            return 1;
        }
        return requested;
    }

    private Integer resolveIsEnabledForRebuild(Integer requested) {
        // 缺失记录补建场景默认启用，避免被旧前端误传 0 导致规则失效
        if (requested == null || requested == 0) {
            return 1;
        }
        return requested;
    }
}
