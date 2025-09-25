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

        // 获取规则组名称
        var ruleGroup = ruleGroupService.getValidationRuleGroup(formatDO.getGroupId());
        if (ruleGroup != null) {
            respVO.setRgName(ruleGroup.getRgName());
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
        var ruleGroup = ruleGroupService.getValidationRuleGroup(formatDO.getGroupId());
        if (ruleGroup != null) { respVO.setRgName(ruleGroup.getRgName()); }
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        var list = formatRepository.findByGroupId(id);
        if (list.isEmpty()) { return; }
        if (list.size() > 1) { throw new IllegalStateException("数据异常：同一组存在多条格式校验规则(组ID=" + id + ")"); }
        MetadataValidationFormatDO existing = list.get(0);
        formatRepository.deleteById(existing.getId());
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
        data.setAppId(field.getAppId());
        data.setGroupId(groupId);
        if (data.getFormatCode() == null) {
            data.setFormatCode("REGEX");
        }

        // 保存格式校验规则
        formatRepository.upsert(data);
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
        MetadataValidationFormatDO updateObj = BeanUtils.toBean(vo, MetadataValidationFormatDO.class);
        updateObj.setId(existing.getId());
        updateObj.setFieldId(existing.getFieldId());
        updateObj.setEntityId(existing.getEntityId());
        updateObj.setAppId(existing.getAppId());
        updateObj.setGroupId(targetGroupId);
        formatRepository.update(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(Long fieldId) {
        formatRepository.deleteByFieldId(fieldId);
    }
}
