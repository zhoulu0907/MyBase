package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRequiredRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRequiredSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRequiredUpdateReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupSaveReqVO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRequiredRepository;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRequiredDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import com.cmsr.onebase.module.metadata.build.service.entity.MetadataEntityFieldBuildService;
import com.cmsr.onebase.module.metadata.core.util.StatusEnumUtil;
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
    public MetadataValidationRequiredDO getByFieldId(String fieldUuid) {
        return requiredRepository.findOneByFieldUuid(fieldUuid);
    }

    @Override
    public ValidationRequiredRespVO getByFieldIdWithRgName(String fieldUuid) {
        MetadataValidationRequiredDO requiredDO = requiredRepository.findOneByFieldUuid(fieldUuid);
        if (requiredDO == null) {
            return null;
        }

        // 转换DO为VO
        ValidationRequiredRespVO respVO = BeanUtils.toBean(requiredDO, ValidationRequiredRespVO.class);

        // 获取规则组信息，包括提示语等字段
        var ruleGroup = validationRuleGroupService.getValidationRuleGroupByUuid(requiredDO.getGroupUuid());
        if (ruleGroup != null) {
            respVO.setRgName(ruleGroup.getRgName());
            respVO.setPromptMessage(ruleGroup.getPopPrompt());
        }
        
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ValidationRequiredSaveReqVO vo) {
        Assert.notNull(vo, "vo不能为空");
        Assert.notNull(vo.getFieldUuid(), "字段UUID不能为空");
        Assert.hasText(vo.getRgName(), "规则组名称不能为空");

        // 获取字段信息
        MetadataEntityFieldDO field = entityFieldService.getEntityFieldByUuid(vo.getFieldUuid());
        Assert.notNull(field, "字段不存在");

        // 检查同一字段是否已存在必填校验规则
        MetadataValidationRequiredDO existingRule = requiredRepository.findOneByFieldUuid(vo.getFieldUuid());
        if (existingRule != null) {
            throw new IllegalStateException("该字段已存在必填校验规则，同一字段只能有一条必填校验规则");
        }

        // 处理规则组：先查找，不存在则创建
        Long groupId;
        MetadataValidationRuleGroupDO existingGroup = validationRuleGroupService.getByName(vo.getRgName());
        boolean canReuse = false;
        if (existingGroup != null) {
            var groupRequiredList = requiredRepository.findByGroupId(existingGroup.getId());
            if (groupRequiredList.isEmpty() || (groupRequiredList.size() == 1 && groupRequiredList.get(0).getFieldUuid().equals(vo.getFieldUuid()))) {
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
            groupVO.setValidationType("REQUIRED");
            // 修复：同步entityId到规则组
            groupVO.setEntityId(field.getEntityUuid());
            groupId = validationRuleGroupService.createValidationRuleGroup(groupVO);
        }

        // 转换VO为DO并设置必要字段
        MetadataValidationRequiredDO data = BeanUtils.toBean(vo, MetadataValidationRequiredDO.class);
        data.setEntityUuid(field.getEntityUuid());
        data.setApplicationId(field.getApplicationId());
        data.setGroupUuid(String.valueOf(groupId));

        // 保存必填校验规则
        requiredRepository.saveOrUpdate(data);
        
        // 同步更新字段的必填状态为必填
        syncFieldRequiredStatus(vo.getFieldUuid(), true);
        
        return data.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ValidationRequiredUpdateReqVO reqVO) {
        // 约定：reqVO.id 为 groupId
        Long groupIdParam = reqVO.getId();
        Assert.notNull(groupIdParam, "规则组ID不能为空");
        var list = requiredRepository.findByGroupId(groupIdParam);
        Assert.notEmpty(list, "当前必填校验规则不存在(组ID=" + groupIdParam + ")");
        if (list.size() > 1) {
            throw new IllegalStateException("数据异常：同一组存在多条必填校验规则(组ID=" + groupIdParam + ")");
        }
        MetadataValidationRequiredDO existingDO = list.get(0);

        // 查询字段信息
        MetadataEntityFieldDO entityFieldDO = entityFieldService.getEntityFieldByUuid(existingDO.getFieldUuid());
        Assert.notNull(entityFieldDO, "字段不存在");

        // 保留原 groupId，并同步可能更新的组级配置(popPrompt/valMethod/popType)
        Long targetGroupId = groupIdParam;
        var groupDO = validationRuleGroupService.getValidationRuleGroup(groupIdParam);
        if (groupDO != null) {
            boolean needGroupUpdate = false;
            ValidationRuleGroupSaveReqVO updateGroupVO = new ValidationRuleGroupSaveReqVO();
            updateGroupVO.setId(groupDO.getId());
            updateGroupVO.setRgName(groupDO.getRgName());
            updateGroupVO.setRgDesc(groupDO.getRgDesc());
            updateGroupVO.setRgStatus(groupDO.getRgStatus());
            updateGroupVO.setValidationType(groupDO.getValidationType());
            updateGroupVO.setEntityId(groupDO.getEntityUuid());
            if (reqVO.getPopPrompt() != null && !reqVO.getPopPrompt().equals(groupDO.getPopPrompt())) {
                updateGroupVO.setPopPrompt(reqVO.getPopPrompt());
                needGroupUpdate = true;
            }
            if (reqVO.getValMethod() != null && !reqVO.getValMethod().equals(groupDO.getValMethod())) {
                updateGroupVO.setValMethod(reqVO.getValMethod());
                needGroupUpdate = true;
            }
            if (reqVO.getPopType() != null && !reqVO.getPopType().equals(groupDO.getPopType())) {
                updateGroupVO.setPopType(reqVO.getPopType());
                needGroupUpdate = true;
            }
            if (needGroupUpdate) {
                validationRuleGroupService.updateValidationRuleGroup(updateGroupVO);
            }
        }

        MetadataValidationRequiredDO updateDO = BeanUtils.toBean(reqVO, MetadataValidationRequiredDO.class);
        updateDO.setId(existingDO.getId());
        updateDO.setFieldUuid(existingDO.getFieldUuid());
        updateDO.setEntityUuid(existingDO.getEntityUuid());
        updateDO.setApplicationId(existingDO.getApplicationId());
        updateDO.setGroupUuid(String.valueOf(targetGroupId));
        requiredRepository.updateById(updateDO);

        boolean isFieldRequired = updateDO.getIsEnabled() != null && updateDO.getIsEnabled() == 1;
        syncFieldRequiredStatus(existingDO.getFieldUuid(), isFieldRequired);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(String fieldUuid) {
        // 先获取要删除的记录，以便后续删除关联的校验规则分组
        MetadataValidationRequiredDO recordToDelete = requiredRepository.findOneByFieldUuid(fieldUuid);
        
        // 删除必填校验记录
        requiredRepository.deleteByFieldUuid(fieldUuid);
        
        // 删除关联的校验规则分组
        if (recordToDelete != null && recordToDelete.getGroupUuid() != null) {
            validationRuleGroupService.safeDeleteGroupDirect(Long.valueOf(recordToDelete.getGroupUuid()));
        }
        
        // 同步更新字段的必填状态为非必填
        syncFieldRequiredStatus(fieldUuid, false);
    }

    @Override
    public ValidationRequiredRespVO getById(Long id) {
        var list = requiredRepository.findByGroupId(id);
        if (list.isEmpty()) { return null; }
        if (list.size() > 1) { throw new IllegalStateException("数据异常：同一组存在多条必填校验规则(组ID=" + id + ")"); }
        MetadataValidationRequiredDO requiredDO = list.get(0);
        ValidationRequiredRespVO respVO = BeanUtils.toBean(requiredDO, ValidationRequiredRespVO.class);
        
        // 获取规则组信息，包括提示语等字段
        var ruleGroup = validationRuleGroupService.getValidationRuleGroupByUuid(requiredDO.getGroupUuid());
        if (ruleGroup != null) {
            respVO.setRgName(ruleGroup.getRgName());
            respVO.setPromptMessage(ruleGroup.getPopPrompt());
        }
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        var list = requiredRepository.findByGroupId(id);
        
        // 删除子表记录和同步字段状态
        if (!list.isEmpty()) {
            if (list.size() > 1) {
                throw new IllegalStateException("数据异常：同一组存在多条必填校验规则(组ID=" + id + ")");
            }
            MetadataValidationRequiredDO requiredDO = list.get(0);
            String fieldUuid = requiredDO.getFieldUuid();
            requiredRepository.removeById(requiredDO.getId());
            if (fieldUuid != null) {
                syncFieldRequiredStatus(fieldUuid, false);
            }
        }
        
        // 无论子表是否存在，都要删除主表作为兜底（防止脏数据）
        validationRuleGroupService.safeDeleteGroupDirect(id);
    }
    
    /**
     * 同步字段的必填状态到字段表
     * 
     * @param fieldUuid 字段UUID
     * @param required 是否必填
     */
    private void syncFieldRequiredStatus(String fieldUuid, boolean required) {
        MetadataEntityFieldDO field = entityFieldRepository.getByUuid(fieldUuid);
        if (field != null && field.getIsRequired() != (required ? 1 : 0)) {
            field.setIsRequired(required ? 1 : 0);
            entityFieldRepository.updateById(field); // 使用updateById而不是upsert，避免主键冲突
        }
    }
}
