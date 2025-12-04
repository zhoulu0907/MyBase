package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationChildNotEmptyRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationChildNotEmptySaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationChildNotEmptyUpdateReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupSaveReqVO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationChildNotEmptyRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationChildNotEmptyDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import com.cmsr.onebase.module.metadata.core.util.StatusEnumUtil;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 子表非空校验 Service 实现
 */
@Service
public class MetadataValidationChildNotEmptyBuildServiceImpl implements MetadataValidationChildNotEmptyBuildService {

    @Resource private MetadataValidationChildNotEmptyRepository childNotEmptyRepository; // 自身仓库
    @Resource private MetadataValidationRuleGroupBuildService ruleGroupService; // 其他服务

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
        MetadataValidationChildNotEmptyDO validationDO = childNotEmptyRepository.getById(id);
        if (validationDO == null) {
            var group = ruleGroupService.getValidationRuleGroup(id);
            if (group != null) {
                var list = childNotEmptyRepository.findByGroupUuid(group.getGroupUuid());
                if (!list.isEmpty()) {
                    validationDO = list.get(0);
                }
            }
            if (validationDO == null) {
                return null;
            }
        }

        // 转换为 VO
        ValidationChildNotEmptyRespVO respVO = BeanUtils.toBean(validationDO, ValidationChildNotEmptyRespVO.class);

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
        // 先尝试按主键ID查找记录
        MetadataValidationChildNotEmptyDO existing = childNotEmptyRepository.getById(id);
        String groupUuidToDelete = null;
        Long groupIdToDelete = null;
        
        if (existing != null) {
            // 按主键ID找到了记录
            groupUuidToDelete = existing.getGroupUuid();
            childNotEmptyRepository.removeById(id);
        } else {
            // 按主键ID未找到，尝试通过groupId查询groupUuid再查找
            var group = ruleGroupService.getValidationRuleGroup(id);
            if (group != null) {
                var list = childNotEmptyRepository.findByGroupUuid(group.getGroupUuid());
                if (!list.isEmpty()) {
                    if (list.size() > 1) {
                        throw new IllegalStateException("数据异常：同一组存在多条子表非空校验规则(组UUID=" + group.getGroupUuid() + ")");
                    }
                    MetadataValidationChildNotEmptyDO validationDO = list.get(0);
                    childNotEmptyRepository.removeById(validationDO.getId());
                }
                groupIdToDelete = id;
            }
        }
        
        // 无论子表是否存在，都要删除主表作为兜底（防止脏数据）
        if (groupUuidToDelete != null) {
            ruleGroupService.safeDeleteGroupDirect(groupUuidToDelete);
        } else if (groupIdToDelete != null) {
            ruleGroupService.safeDeleteGroupDirect(groupIdToDelete);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ValidationChildNotEmptySaveReqVO vo) {
        Assert.notNull(vo, "vo不能为空");
        Assert.notNull(vo.getEntityUuid(), "父实体UUID不能为空");
        Assert.notNull(vo.getChildEntityUuid(), "子实体UUID不能为空");
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
        Assert.notNull(vo.getEntityUuid(), "父实体UUID不能为空");
        Assert.notNull(vo.getChildEntityUuid(), "子实体UUID不能为空");
        
        Long groupIdParam = vo.getId();
        var group = ruleGroupService.getValidationRuleGroup(groupIdParam);
        Assert.notNull(group, "当前子表非空校验规则不存在(组ID=" + groupIdParam + ")");
        var list = childNotEmptyRepository.findByGroupUuid(group.getGroupUuid());
        Assert.notEmpty(list, "当前子表非空校验规则不存在(组UUID=" + group.getGroupUuid() + ")");
        if (list.size() > 1) { 
            throw new IllegalStateException("数据异常：同一组存在多条子表非空校验规则(组UUID=" + group.getGroupUuid() + ")"); 
        }
        MetadataValidationChildNotEmptyDO existing = list.get(0);
        String targetGroupUuid = group.getGroupUuid();
        if (group != null) {
            boolean needGroupUpdate = false;
            ValidationRuleGroupSaveReqVO updateGroupVO = new ValidationRuleGroupSaveReqVO();
            updateGroupVO.setId(group.getId());
            updateGroupVO.setRgName(group.getRgName());
            updateGroupVO.setRgDesc(group.getRgDesc());
            updateGroupVO.setRgStatus(group.getRgStatus());
            updateGroupVO.setValidationType(group.getValidationType());
            updateGroupVO.setEntityUuid(group.getEntityUuid());
            if (vo.getPopPrompt() != null && !vo.getPopPrompt().equals(group.getPopPrompt())) { updateGroupVO.setPopPrompt(vo.getPopPrompt()); needGroupUpdate = true; }
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
        updateObj.setPromptMessage(vo.getPopPrompt());
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
}
