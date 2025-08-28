package com.cmsr.onebase.module.metadata.service.validation;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationFormatSaveReqVO;
import com.cmsr.onebase.module.metadata.controller.admin.validation.vo.ValidationRuleGroupSaveReqVO;
import com.cmsr.onebase.module.metadata.dal.database.MetadataValidationFormatRepository;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationFormatDO;
import com.cmsr.onebase.module.metadata.service.entity.MetadataEntityFieldService;
import com.cmsr.onebase.module.metadata.util.StatusEnumUtil;
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
public class MetadataValidationFormatServiceImpl implements MetadataValidationFormatService {

    @Resource private MetadataValidationFormatRepository formatRepository; // 自身仓库
    @Resource private MetadataValidationRuleGroupService ruleGroupService; // 其他服务
    @Resource private MetadataEntityFieldService entityFieldService; // 其他服务

    @Override
    public MetadataValidationFormatDO getRegexByFieldId(Long fieldId) {
        return formatRepository.findRegexByFieldId(fieldId);
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

        // 处理规则组：先查找，不存在则创建
        Long groupId;
        var existingGroup = ruleGroupService.getByName(vo.getRgName());
        if (existingGroup != null) {
            groupId = existingGroup.getId();
        } else {
            // 创建新的规则组
            ValidationRuleGroupSaveReqVO groupVO = new ValidationRuleGroupSaveReqVO();
            groupVO.setRgName(vo.getRgName());
            groupVO.setRgDesc("自动创建的规则组：" + vo.getRgName());
            groupVO.setRgStatus(StatusEnumUtil.ACTIVE);
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
    public void update(MetadataValidationFormatDO data) {
        Assert.notNull(data, "data不能为空");
        Assert.notNull(data.getId(), "id不能为空");
        formatRepository.upsert(data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(Long fieldId) {
        formatRepository.deleteByFieldId(fieldId);
    }
}
