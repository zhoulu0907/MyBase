package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationFormatRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationFormatSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationFormatUpdateReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupSaveReqVO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationFormatRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationFormatDO;
import com.cmsr.onebase.module.metadata.core.enums.MetadataValidationFormatCodeEnum;
import com.cmsr.onebase.module.metadata.core.enums.MetadataValidationRuleTypeEnum;
import com.cmsr.onebase.module.metadata.build.service.entity.MetadataEntityFieldBuildService;
import com.cmsr.onebase.module.metadata.core.util.MetadataIdUuidConverter;
import com.cmsr.onebase.module.metadata.core.util.StatusEnumUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 格式校验 Service 实现（含REGEX）
 *
 * @author bty418
 * @date 2025-08-27
 */
@Service
public class MetadataValidationFormatBuildServiceImpl implements MetadataValidationFormatBuildService {

    @Resource private MetadataValidationFormatRepository formatRepository; // 自身仓库
    @Resource private MetadataValidationRuleGroupBuildService ruleGroupService; // 其他服务
    @Resource private MetadataEntityFieldBuildService entityFieldService; // 其他服务
    @Resource private MetadataIdUuidConverter idUuidConverter; // ID转UUID工具

    @Override
    public MetadataValidationFormatDO getRegexByFieldId(String fieldUuid) {
        return formatRepository.findRegexByFieldUuid(fieldUuid);
    }

    @Override
    public ValidationFormatRespVO getRegexByFieldIdWithRgName(String fieldUuid) {
        MetadataValidationFormatDO formatDO = formatRepository.findRegexByFieldUuid(fieldUuid);
        if (formatDO == null) {
            return null;
        }

        // 转换DO为VO
        ValidationFormatRespVO respVO = BeanUtils.toBean(formatDO, ValidationFormatRespVO.class);
        
        // 手动映射字段名不匹配的属性
        respVO.setFormatType(formatDO.getFormatCode());        // formatCode -> formatType
        respVO.setFormatValue(formatDO.getRegexPattern());     // regexPattern -> formatValue
        respVO.setIgnoreCase(formatDO.getFlags() != null && formatDO.getFlags().contains("i") ? 1 : 0); // flags -> ignoreCase
        respVO.setApplicationId(formatDO.getApplicationId() != null ? String.valueOf(formatDO.getApplicationId()) : null); // Long -> String

        // 获取规则组信息，包括提示语等字段
        var ruleGroup = ruleGroupService.getValidationRuleGroupByUuid(formatDO.getGroupUuid());
        if (ruleGroup != null) {
            respVO.setRgName(ruleGroup.getRgName());
            respVO.setPromptMessage(ruleGroup.getPopPrompt());
        }

        return respVO;
    }

    @Override
    public ValidationFormatRespVO getById(Long id) {
        var group = ruleGroupService.resolveRuleGroup(id, null, null);
        if (group == null) { return null; }
        var list = findByRuleGroup(group);
        if (list.isEmpty()) {
            ValidationFormatRespVO fallbackVO = new ValidationFormatRespVO();
            fallbackVO.setRgName(group.getRgName());
            fallbackVO.setEntityUuid(group.getEntityUuid());
            fallbackVO.setGroupUuid(group.getGroupUuid());
            fallbackVO.setApplicationId(group.getApplicationId() == null ? null : String.valueOf(group.getApplicationId()));
            fallbackVO.setPromptMessage(group.getPopPrompt());
            fallbackVO.setIsEnabled(1);
            return fallbackVO;
        }
        if (list.size() > 1) { throw new IllegalStateException("数据异常：同一组存在多条格式校验规则(组UUID=" + group.getGroupUuid() + ")"); }
        MetadataValidationFormatDO formatDO = list.get(0);
        ValidationFormatRespVO respVO = BeanUtils.toBean(formatDO, ValidationFormatRespVO.class);
        
        // 手动映射字段名不匹配的属性
        respVO.setFormatType(formatDO.getFormatCode());        // formatCode -> formatType
        respVO.setFormatValue(formatDO.getRegexPattern());     // regexPattern -> formatValue
        respVO.setIgnoreCase(formatDO.getFlags() != null && formatDO.getFlags().contains("i") ? 1 : 0); // flags -> ignoreCase
        respVO.setApplicationId(formatDO.getApplicationId() != null ? String.valueOf(formatDO.getApplicationId()) : null); // Long -> String
        
        // 获取规则组信息，包括提示语等字段
        respVO.setRgName(group.getRgName());
        respVO.setPromptMessage(group.getPopPrompt());
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        var group = ruleGroupService.resolveRuleGroup(id, null, null);
        if (group == null) { return; }
        var list = findByRuleGroup(group);
        
        // 删除子表记录
        if (!list.isEmpty()) {
            if (list.size() > 1) {
                throw new IllegalStateException("数据异常：同一组存在多条格式校验规则(组UUID=" + group.getGroupUuid() + ")");
            }
            MetadataValidationFormatDO existing = list.get(0);
            formatRepository.removeById(existing.getId());
        }
        
        // 无论子表是否存在，都要删除主表作为兖底（防止脏数据）
        ruleGroupService.safeDeleteGroupDirect(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ValidationFormatSaveReqVO vo) {
        Assert.notNull(vo, "vo不能为空");
        // ID与UUID兼容处理：优先使用UUID，若为空则通过ID转换
        vo.setFieldUuid(idUuidConverter.resolveFieldUuid(vo.getFieldUuid(), vo.getFieldId()));
        Assert.hasText(vo.getRgName(), "规则组名称不能为空");

        // 获取字段信息
        MetadataEntityFieldDO field = entityFieldService.getEntityFieldByUuid(vo.getFieldUuid());
        Assert.notNull(field, "字段不存在");

        // 检查同一字段是否已存在格式校验规则
        MetadataValidationFormatDO existingRule = formatRepository.findRegexByFieldUuid(vo.getFieldUuid());
        if (existingRule != null) {
            throw new IllegalStateException("该字段已存在格式校验规则，同一字段只能有一条格式校验规则");
        }

        // 处理规则组：先查找，不存在则创建；存在但已被其他字段复用则新建
        Long groupId = null;
        String groupUuid = null;
        var existingGroup = ruleGroupService.getByName(vo.getRgName());
        boolean needCreateGroup = false;
        if (existingGroup != null) {
            existingGroup = ruleGroupService.resolveRuleGroup(existingGroup.getId(), existingGroup.getGroupUuid(), null);
            var groupFormatList = formatRepository.findByGroupUuid(existingGroup.getGroupUuid());
            boolean reused = groupFormatList.stream().anyMatch(u -> !u.getFieldUuid().equals(vo.getFieldUuid()));
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
            groupVO.setValidationType(MetadataValidationRuleTypeEnum.FORMAT.getCode());
            // 修复：同步entityUuid到规则组
            groupVO.setEntityUuid(field.getEntityUuid());
            groupId = ruleGroupService.createValidationRuleGroup(groupVO);
            // 获取新建规则组的UUID
            var newGroup = ruleGroupService.getValidationRuleGroup(groupId);
            if (newGroup != null) {
                groupUuid = newGroup.getGroupUuid();
            }
        }

        // 转换VO为DO并设置必要字段
        MetadataValidationFormatDO data = BeanUtils.toBean(vo, MetadataValidationFormatDO.class);
        data.setEntityUuid(field.getEntityUuid());
        data.setApplicationId(field.getApplicationId() != null ? Long.valueOf(field.getApplicationId()) : null);
        data.setGroupUuid(groupUuid);
        data.setPromptMessage(resolvePrompt(vo.getPopPrompt(), vo.getPromptMessage(), null, null));
        
        // 设置默认值
        if (data.getIsEnabled() == null) {
            data.setIsEnabled(1); // 默认启用
        }
        
        // 处理格式代码和正则表达式的逻辑
        if (data.getFormatCode() == null || data.getFormatCode().trim().isEmpty()) {
            if (data.getRegexPattern() != null && !data.getRegexPattern().trim().isEmpty()) {
                // 有正则表达式，设置为REGEX类型
                data.setFormatCode(MetadataValidationFormatCodeEnum.REGEX.getCode());
            } else {
                // 没有正则表达式，使用通用格式类型
                data.setFormatCode(MetadataValidationFormatCodeEnum.TEXT.getCode());
            }
        } else {
            // 标准化格式代码
            String formatCode = data.getFormatCode().trim().toUpperCase();

            MetadataValidationFormatCodeEnum formatCodeEnum = MetadataValidationFormatCodeEnum.getByCode(formatCode);
            if (formatCodeEnum == null) {
                // 不识别的格式类型，如果有正则表达式就当作REGEX，否则当作TEXT
                if (data.getRegexPattern() != null && !data.getRegexPattern().trim().isEmpty()) {
                    data.setFormatCode(MetadataValidationFormatCodeEnum.REGEX.getCode());
                } else {
                    data.setFormatCode(MetadataValidationFormatCodeEnum.TEXT.getCode());
                }
            } else if (MetadataValidationFormatCodeEnum.REGEX == formatCodeEnum) {
                if (data.getRegexPattern() == null || data.getRegexPattern().trim().isEmpty()) {
                    throw new IllegalArgumentException("当格式类型为REGEX时，必须提供正则表达式");
                }
                data.setFormatCode(MetadataValidationFormatCodeEnum.REGEX.getCode());
            } else {
                data.setFormatCode(formatCodeEnum.getCode());
            }
        }

        // 保存格式校验规则
        formatRepository.saveOrUpdate(data);
        return data.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ValidationFormatUpdateReqVO vo) {
        Assert.notNull(vo, "vo不能为空");
        Assert.notNull(vo.getId(), "groupId不能为空");
        Long groupIdParam = vo.getId();
        var group = ruleGroupService.resolveRuleGroup(groupIdParam, null, null);
        if (group == null) {
            throw new IllegalArgumentException("规则组不存在(组ID=" + groupIdParam + ")");
        }
        String groupUuidParam = group.getGroupUuid();
        var list = findByRuleGroup(group);
        if (list.isEmpty()) {
            Assert.hasText(vo.getFieldUuid(), "当前格式校验规则缺失，请传入fieldUuid进行补建");
            MetadataEntityFieldDO fallbackFieldDO = entityFieldService.getEntityFieldByUuid(vo.getFieldUuid());
            Assert.notNull(fallbackFieldDO, "字段不存在");
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
            if (needGroupUpdate) {
                ruleGroupService.updateValidationRuleGroup(updateGroupVO);
            }
            MetadataValidationFormatDO rebuildDO = BeanUtils.toBean(vo, MetadataValidationFormatDO.class);
            rebuildDO.setId(null);
            rebuildDO.setFieldUuid(fallbackFieldDO.getFieldUuid());
            rebuildDO.setEntityUuid(fallbackFieldDO.getEntityUuid());
            rebuildDO.setApplicationId(fallbackFieldDO.getApplicationId());
            rebuildDO.setGroupUuid(groupUuidParam);
            rebuildDO.setPromptMessage(mergedPrompt);
            rebuildDO.setIsEnabled(resolveIsEnabledForRebuild(vo.getIsEnabled()));
            formatRepository.saveOrUpdate(rebuildDO);
            return;
        }
        if (list.size() > 1) { throw new IllegalStateException("数据异常：同一组存在多条格式校验规则(组UUID=" + groupUuidParam + ")"); }
        MetadataValidationFormatDO existing = list.get(0);
        MetadataEntityFieldDO field = entityFieldService.getEntityFieldByUuid(existing.getFieldUuid());
        Assert.notNull(field, "字段不存在");
        String targetGroupUuid = groupUuidParam;
        var groupDO = group;
        String mergedPrompt = resolvePrompt(vo.getPopPrompt(), vo.getPromptMessage(), groupDO.getPopPrompt(), existing.getPromptMessage());
        if (groupDO != null) {
            boolean needGroupUpdate = false;
            ValidationRuleGroupSaveReqVO updateGroupVO = new ValidationRuleGroupSaveReqVO();
            updateGroupVO.setId(groupDO.getId());
            String targetRgName = StringUtils.hasText(vo.getRgName()) ? vo.getRgName() : groupDO.getRgName();
            updateGroupVO.setRgName(targetRgName);
            updateGroupVO.setRgDesc(groupDO.getRgDesc());
            updateGroupVO.setRgStatus(groupDO.getRgStatus());
            updateGroupVO.setValidationType(groupDO.getValidationType());
            updateGroupVO.setEntityUuid(groupDO.getEntityUuid());
            if (!targetRgName.equals(groupDO.getRgName())) { needGroupUpdate = true; }
            if (mergedPrompt != null && !mergedPrompt.equals(groupDO.getPopPrompt())) { updateGroupVO.setPopPrompt(mergedPrompt); needGroupUpdate = true; }
            if (vo.getValMethod() != null && !vo.getValMethod().equals(groupDO.getValMethod())) { updateGroupVO.setValMethod(vo.getValMethod()); needGroupUpdate = true; }
            if (vo.getPopType() != null && !vo.getPopType().equals(groupDO.getPopType())) { updateGroupVO.setPopType(vo.getPopType()); needGroupUpdate = true; }
            if (needGroupUpdate) { ruleGroupService.updateValidationRuleGroup(updateGroupVO); }
        }
        MetadataValidationFormatDO updateObj = BeanUtils.toBean(vo, MetadataValidationFormatDO.class);
        updateObj.setId(existing.getId());
        updateObj.setFieldUuid(existing.getFieldUuid());
        updateObj.setEntityUuid(existing.getEntityUuid());
        updateObj.setApplicationId(existing.getApplicationId());
        updateObj.setGroupUuid(targetGroupUuid);
        updateObj.setPromptMessage(mergedPrompt);
        updateObj.setIsEnabled(resolveIsEnabledForUpdate(vo.getIsEnabled(), existing.getIsEnabled()));
        
        formatRepository.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(String fieldUuid) {
        // 先获取要删除的记录，以便后续删除关联的校验规则分组
        MetadataValidationFormatDO recordToDelete = formatRepository.findRegexByFieldUuid(fieldUuid);
        
        // 删除格式校验记录
        formatRepository.deleteByFieldUuid(fieldUuid);
        
        // 删除关联的校验规则分组
        if (recordToDelete != null && recordToDelete.getGroupUuid() != null) {
            ruleGroupService.safeDeleteGroupDirect(recordToDelete.getGroupUuid());
        }
    }

    private java.util.List<MetadataValidationFormatDO> findByRuleGroup(
            com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRuleGroupDO group) {
        if (group == null || !StringUtils.hasText(group.getGroupUuid())) {
            return java.util.Collections.emptyList();
        }
        java.util.List<MetadataValidationFormatDO> list = formatRepository.findByGroupUuid(group.getGroupUuid());
        if (!list.isEmpty()) {
            return list;
        }
        String legacyGroupUuid = String.valueOf(group.getId());
        if (legacyGroupUuid.equals(group.getGroupUuid())) {
            return list;
        }
        list = formatRepository.findByGroupUuid(legacyGroupUuid);
        migrateGroupUuid(list, group.getGroupUuid());
        return list;
    }

    private void migrateGroupUuid(java.util.List<MetadataValidationFormatDO> records, String targetGroupUuid) {
        if (!StringUtils.hasText(targetGroupUuid) || records == null || records.isEmpty()) {
            return;
        }
        for (MetadataValidationFormatDO record : records) {
            if (targetGroupUuid.equals(record.getGroupUuid())) {
                continue;
            }
            record.setGroupUuid(targetGroupUuid);
            formatRepository.updateById(record);
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
