package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationChildNotEmptyRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationChildNotEmptySaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationChildNotEmptyUpdateReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupSaveReqVO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationChildNotEmptyRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityRelationshipRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationChildNotEmptyDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.relationship.MetadataEntityRelationshipDO;
import com.cmsr.onebase.module.metadata.build.service.entity.MetadataEntityFieldBuildService;
import com.cmsr.onebase.module.metadata.core.util.StatusEnumUtil;
import jakarta.annotation.Resource;
import org.anyline.data.param.init.DefaultConfigStore;
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
    @Resource private MetadataEntityFieldBuildService entityFieldService; // 其他服务
    @Resource private MetadataEntityRelationshipRepository entityRelationshipRepository; // 实体关系仓库

    @Override
    public MetadataValidationChildNotEmptyDO getByFieldId(Long fieldId) {
        return childNotEmptyRepository.findOneByFieldId(fieldId);
    }

    @Override
    public ValidationChildNotEmptyRespVO getByFieldIdWithRgName(Long fieldId) {
        MetadataValidationChildNotEmptyDO validationDO = childNotEmptyRepository.findOneByFieldId(fieldId);
        if (validationDO == null) {
            return null;
        }

        // 转换为 VO
        ValidationChildNotEmptyRespVO respVO = BeanUtils.toBean(validationDO, ValidationChildNotEmptyRespVO.class);

        // 获取规则组名称
        if (validationDO.getGroupId() != null) {
            MetadataValidationRuleGroupDO ruleGroup = ruleGroupService.getValidationRuleGroup(validationDO.getGroupId());
            if (ruleGroup != null) {
                respVO.setRgName(ruleGroup.getRgName());
            }
        }

        return respVO;
    }

    @Override
    public ValidationChildNotEmptyRespVO getById(Long id) {
        MetadataValidationChildNotEmptyDO validationDO = childNotEmptyRepository.findById(id);
        if (validationDO == null) {
            var group = ruleGroupService.getValidationRuleGroup(id);
            if (group != null) {
                var list = childNotEmptyRepository.findByGroupId(group.getId());
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

        // 获取规则组名称
        if (validationDO.getGroupId() != null) {
            MetadataValidationRuleGroupDO ruleGroup = ruleGroupService.getValidationRuleGroup(validationDO.getGroupId());
            if (ruleGroup != null) {
                respVO.setRgName(ruleGroup.getRgName());
            }
        }

        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        // 先校验记录是否存在
        MetadataValidationChildNotEmptyDO existing = childNotEmptyRepository.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("子表非空校验规则不存在，ID: " + id);
        }
        
        // 执行删除
        Long groupId = existing.getGroupId();
        childNotEmptyRepository.deleteById(id);
        if (groupId != null) { ruleGroupService.safeDeleteGroupDirect(groupId); }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ValidationChildNotEmptySaveReqVO vo) {
        Assert.notNull(vo, "vo不能为空");
        Assert.notNull(vo.getFieldId(), "字段ID不能为空");
        Assert.hasText(vo.getRgName(), "规则组名称不能为空");

        // 获取字段信息
        MetadataEntityFieldDO field = entityFieldService.getEntityField(String.valueOf(vo.getFieldId()));
        Assert.notNull(field, "字段不存在");

        // 检查同一字段是否已存在子表非空校验规则
        MetadataValidationChildNotEmptyDO existingRule = childNotEmptyRepository.findOneByFieldId(vo.getFieldId());
        if (existingRule != null) {
            throw new IllegalStateException("该字段已存在子表非空校验规则，同一字段只能有一条子表非空校验规则");
        }

        // 处理规则组：先查找，不存在则创建；存在但已被其他字段复用则新建
        Long groupId = null;
        var existingGroup = ruleGroupService.getByName(vo.getRgName());
        boolean needCreateGroup = false;
        if (existingGroup != null) {
            var groupList = childNotEmptyRepository.findByGroupId(existingGroup.getId());
            boolean reused = groupList.stream().anyMatch(u -> !u.getFieldId().equals(vo.getFieldId()));
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
            groupVO.setValidationType("CHILD_NOT_EMPTY");
            // 修复：同步entityId到规则组
            groupVO.setEntityId(field.getEntityId());
            groupId = ruleGroupService.createValidationRuleGroup(groupVO);
        }

        // 获取子实体ID
        Long childEntityId = vo.getChildEntityId();
        if (childEntityId == null) {
            // 如果前端没有传递子实体ID，则根据字段ID查找
            childEntityId = getChildEntityIdByFieldId(vo.getFieldId());
            if (childEntityId == null) {
                throw new IllegalArgumentException("无法找到字段对应的子实体关系，请检查字段ID是否正确或是否已建立主子关系，字段ID: " + vo.getFieldId());
            }
        }

        // 转换VO为DO并设置必要字段
        MetadataValidationChildNotEmptyDO data = BeanUtils.toBean(vo, MetadataValidationChildNotEmptyDO.class);
        data.setEntityId(field.getEntityId());
        data.setAppId(field.getAppId());
        data.setGroupId(groupId);
        data.setChildEntityId(childEntityId);
        
        // 设置默认值
        if (data.getIsEnabled() == null) {
            data.setIsEnabled(1); // 默认启用
        }
        if (data.getMinRows() == null) {
            data.setMinRows(1); // 默认最少1行
        }

        // 保存子表非空校验规则
        childNotEmptyRepository.upsert(data);
        return data.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ValidationChildNotEmptyUpdateReqVO vo) {
        Assert.notNull(vo, "vo不能为空");
        Assert.notNull(vo.getId(), "groupId不能为空");
        Long groupIdParam = vo.getId();
        var list = childNotEmptyRepository.findByGroupId(groupIdParam);
        Assert.notEmpty(list, "当前子表非空校验规则不存在(组ID=" + groupIdParam + ")");
        if (list.size() > 1) { throw new IllegalStateException("数据异常：同一组存在多条子表非空校验规则(组ID=" + groupIdParam + ")"); }
        MetadataValidationChildNotEmptyDO existing = list.get(0);
        MetadataEntityFieldDO field = entityFieldService.getEntityField(String.valueOf(existing.getFieldId()));
        Assert.notNull(field, "字段不存在");
        Long targetGroupId = groupIdParam;
        var groupDO = ruleGroupService.getValidationRuleGroup(groupIdParam);
        if (groupDO != null) {
            boolean needGroupUpdate = false;
            ValidationRuleGroupSaveReqVO updateGroupVO = new ValidationRuleGroupSaveReqVO();
            updateGroupVO.setId(groupDO.getId());
            updateGroupVO.setRgName(groupDO.getRgName());
            updateGroupVO.setRgDesc(groupDO.getRgDesc());
            updateGroupVO.setRgStatus(groupDO.getRgStatus());
            updateGroupVO.setValidationType(groupDO.getValidationType());
            updateGroupVO.setEntityId(groupDO.getEntityId());
            if (vo.getPopPrompt() != null && !vo.getPopPrompt().equals(groupDO.getPopPrompt())) { updateGroupVO.setPopPrompt(vo.getPopPrompt()); needGroupUpdate = true; }
            if (vo.getValMethod() != null && !vo.getValMethod().equals(groupDO.getValMethod())) { updateGroupVO.setValMethod(vo.getValMethod()); needGroupUpdate = true; }
            if (vo.getPopType() != null && !vo.getPopType().equals(groupDO.getPopType())) { updateGroupVO.setPopType(vo.getPopType()); needGroupUpdate = true; }
            if (needGroupUpdate) { ruleGroupService.updateValidationRuleGroup(updateGroupVO); }
        }
        MetadataValidationChildNotEmptyDO updateObj = BeanUtils.toBean(vo, MetadataValidationChildNotEmptyDO.class);
        updateObj.setId(existing.getId());
        updateObj.setFieldId(existing.getFieldId());
        updateObj.setEntityId(existing.getEntityId());
        updateObj.setAppId(existing.getAppId());
        updateObj.setGroupId(targetGroupId);
        childNotEmptyRepository.update(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(Long fieldId) {
        childNotEmptyRepository.deleteByFieldId(fieldId);
    }

    /**
     * 根据字段ID获取子实体ID
     *
     * @param fieldId 字段ID
     * @return 子实体ID，如果未找到关系则返回null
     */
    private Long getChildEntityIdByFieldId(Long fieldId) {
        // 根据sourceFieldId查找实体关系
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.and(MetadataEntityRelationshipDO.SOURCE_FIELD_ID, String.valueOf(fieldId));
        
        List<MetadataEntityRelationshipDO> relationships = entityRelationshipRepository.findAllByConfig(configStore);
        
        if (relationships.isEmpty()) {
            return null;
        }
        
        // 返回第一个关系的目标实体ID（子实体ID）
        return relationships.get(0).getTargetEntityId();
    }
}
