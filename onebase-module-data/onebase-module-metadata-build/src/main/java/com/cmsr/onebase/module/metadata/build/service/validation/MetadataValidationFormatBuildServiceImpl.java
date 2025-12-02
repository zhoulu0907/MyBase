package com.cmsr.onebase.module.metadata.build.service.validation;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationFormatRespVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationFormatSaveReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationFormatUpdateReqVO;
import com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo.ValidationRuleGroupSaveReqVO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationFormatRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationFormatDO;
import com.cmsr.onebase.module.metadata.build.service.entity.MetadataEntityFieldBuildService;
import com.cmsr.onebase.module.metadata.core.util.StatusEnumUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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

    @Override
    public MetadataValidationFormatDO getRegexByFieldId(Long fieldId) {
        return formatRepository.findRegexByFieldId(fieldId);
    }

    @Override
    public ValidationFormatRespVO getRegexByFieldIdWithRgName(Long fieldId) {
        MetadataValidationFormatDO formatDO = formatRepository.findRegexByFieldId(fieldId);
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
        var ruleGroup = ruleGroupService.getValidationRuleGroup(formatDO.getGroupId());
        if (ruleGroup != null) {
            respVO.setRgName(ruleGroup.getRgName());
            respVO.setPromptMessage(ruleGroup.getPopPrompt());
        }

        return respVO;
    }

    @Override
    public ValidationFormatRespVO getById(Long id) {
        var list = formatRepository.findByGroupId(id);
        if (list.isEmpty()) { return null; }
        if (list.size() > 1) { throw new IllegalStateException("数据异常：同一组存在多条格式校验规则(组ID=" + id + ")"); }
        MetadataValidationFormatDO formatDO = list.get(0);
        ValidationFormatRespVO respVO = BeanUtils.toBean(formatDO, ValidationFormatRespVO.class);
        
        // 手动映射字段名不匹配的属性
        respVO.setFormatType(formatDO.getFormatCode());        // formatCode -> formatType
        respVO.setFormatValue(formatDO.getRegexPattern());     // regexPattern -> formatValue
        respVO.setIgnoreCase(formatDO.getFlags() != null && formatDO.getFlags().contains("i") ? 1 : 0); // flags -> ignoreCase
        respVO.setApplicationId(formatDO.getApplicationId() != null ? String.valueOf(formatDO.getApplicationId()) : null); // Long -> String
        
        // 获取规则组信息，包括提示语等字段
        var ruleGroup = ruleGroupService.getValidationRuleGroup(formatDO.getGroupId());
        if (ruleGroup != null) {
            respVO.setRgName(ruleGroup.getRgName());
            respVO.setPromptMessage(ruleGroup.getPopPrompt());
        }
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        var list = formatRepository.findByGroupId(id);
        
        // 删除子表记录
        if (!list.isEmpty()) {
            if (list.size() > 1) {
                throw new IllegalStateException("数据异常：同一组存在多条格式校验规则(组ID=" + id + ")");
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
        Assert.notNull(vo.getFieldId(), "字段ID不能为空");
        Assert.hasText(vo.getRgName(), "规则组名称不能为空");

        // 获取字段信息
        MetadataEntityFieldDO field = entityFieldService.getEntityField(String.valueOf(vo.getFieldId()));
        Assert.notNull(field, "字段不存在");

        // 检查同一字段是否已存在格式校验规则
        MetadataValidationFormatDO existingRule = formatRepository.findRegexByFieldId(vo.getFieldId());
        if (existingRule != null) {
            throw new IllegalStateException("该字段已存在格式校验规则，同一字段只能有一条格式校验规则");
        }

        // 处理规则组：先查找，不存在则创建；存在但已被其他字段复用则新建
        Long groupId = null;
        var existingGroup = ruleGroupService.getByName(vo.getRgName());
        boolean needCreateGroup = false;
        if (existingGroup != null) {
            var groupFormatList = formatRepository.findByGroupId(existingGroup.getId());
            boolean reused = groupFormatList.stream().anyMatch(u -> !u.getFieldId().equals(vo.getFieldId()));
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
            groupVO.setValidationType("FORMAT");
            // 修复：同步entityId到规则组
            groupVO.setEntityId(field.getEntityId());
            groupId = ruleGroupService.createValidationRuleGroup(groupVO);
        }

        // 转换VO为DO并设置必要字段
        MetadataValidationFormatDO data = BeanUtils.toBean(vo, MetadataValidationFormatDO.class);
        data.setEntityId(field.getEntityId());
        data.setApplicationId(field.getApplicationId());
        data.setGroupId(groupId);
        
        // 设置默认值
        if (data.getIsEnabled() == null) {
            data.setIsEnabled(1); // 默认启用
        }
        
        // 处理格式代码和正则表达式的逻辑
        if (data.getFormatCode() == null || data.getFormatCode().trim().isEmpty()) {
            if (data.getRegexPattern() != null && !data.getRegexPattern().trim().isEmpty()) {
                // 有正则表达式，设置为REGEX类型
                data.setFormatCode("REGEX");
            } else {
                // 没有正则表达式，使用通用格式类型
                data.setFormatCode("TEXT");
            }
        } else {
            // 标准化格式代码
            String formatCode = data.getFormatCode().trim().toUpperCase();
            
            // 支持的标准格式类型
            switch (formatCode) {
                case "EMAIL":
                case "MOBILE":
                case "ID_CARD":
                case "URL":
                case "IP":
                case "TEXT":
                    data.setFormatCode(formatCode);
                    break;
                case "REGEX":
                    if (data.getRegexPattern() == null || data.getRegexPattern().trim().isEmpty()) {
                        throw new IllegalArgumentException("当格式类型为REGEX时，必须提供正则表达式");
                    }
                    data.setFormatCode("REGEX");
                    break;
                default:
                    // 不识别的格式类型，如果有正则表达式就当作REGEX，否则当作TEXT
                    if (data.getRegexPattern() != null && !data.getRegexPattern().trim().isEmpty()) {
                        data.setFormatCode("REGEX");
                    } else {
                        data.setFormatCode("TEXT");
                    }
                    break;
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
        var list = formatRepository.findByGroupId(groupIdParam);
        Assert.notEmpty(list, "当前格式校验规则不存在(组ID=" + groupIdParam + ")");
        if (list.size() > 1) { throw new IllegalStateException("数据异常：同一组存在多条格式校验规则(组ID=" + groupIdParam + ")"); }
        MetadataValidationFormatDO existing = list.get(0);
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
        MetadataValidationFormatDO updateObj = BeanUtils.toBean(vo, MetadataValidationFormatDO.class);
        updateObj.setId(existing.getId());
        updateObj.setFieldId(existing.getFieldId());
        updateObj.setEntityId(existing.getEntityId());
        updateObj.setApplicationId(existing.getApplicationId());
        updateObj.setGroupId(targetGroupId);
        
        formatRepository.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(Long fieldId) {
        // 先获取要删除的记录，以便后续删除关联的校验规则分组
        MetadataValidationFormatDO recordToDelete = formatRepository.findRegexByFieldId(fieldId);
        
        // 删除格式校验记录
        formatRepository.deleteByFieldId(fieldId);
        
        // 删除关联的校验规则分组
        if (recordToDelete != null && recordToDelete.getGroupId() != null) {
            ruleGroupService.safeDeleteGroupDirect(recordToDelete.getGroupId());
        }
    }
}
