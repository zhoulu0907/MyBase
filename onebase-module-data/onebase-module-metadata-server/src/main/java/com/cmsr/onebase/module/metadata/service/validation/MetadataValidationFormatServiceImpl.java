package com.cmsr.onebase.module.metadata.service.validation;

import com.cmsr.onebase.module.metadata.dal.database.MetadataValidationFormatRepository;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationFormatDO;
import com.cmsr.onebase.module.metadata.service.entity.MetadataEntityFieldService;
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
    public Long create(MetadataValidationFormatDO data) {
        Assert.notNull(data, "data不能为空");
        Assert.notNull(data.getFieldId(), "fieldId不能为空");

    MetadataEntityFieldDO field = entityFieldService.getEntityField(String.valueOf(data.getFieldId()));
        Assert.notNull(field, "字段不存在");

    Long groupId = ensureFieldRuleGroup(data.getFieldId());

        data.setEntityId(field.getEntityId());
        data.setAppId(field.getAppId());
        data.setGroupId(groupId);
        if (data.getFormatCode() == null) {
            data.setFormatCode("REGEX");
        }
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

    private Long ensureFieldRuleGroup(Long fieldId) {
        return ruleGroupService.ensureFieldRuleGroup(fieldId);
    }
}
