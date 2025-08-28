package com.cmsr.onebase.module.metadata.service.validation;

import com.cmsr.onebase.module.metadata.dal.database.MetadataValidationRangeRepository;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRangeDO;
import com.cmsr.onebase.module.metadata.service.entity.MetadataEntityFieldService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 范围校验 Service 实现
 */
@Service
public class MetadataValidationRangeServiceImpl implements MetadataValidationRangeService {

    @Resource private MetadataValidationRangeRepository rangeRepository; // 自身仓库
    @Resource private MetadataValidationRuleGroupService ruleGroupService; // 其他服务
    @Resource private MetadataEntityFieldService entityFieldService; // 其他服务

    @Override
    public MetadataValidationRangeDO getByFieldId(Long fieldId) {
        // 当前每字段至多一条，若未来支持多条，可调整为取最新或抛错
    return rangeRepository.findByFieldId(fieldId).stream().findFirst().orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(MetadataValidationRangeDO data) {
        Assert.notNull(data, "data不能为空");
        Assert.notNull(data.getFieldId(), "fieldId不能为空");
    MetadataEntityFieldDO field = entityFieldService.getEntityField(String.valueOf(data.getFieldId()));
        Assert.notNull(field, "字段不存在");
    Long groupId = ensureFieldRuleGroup(data.getFieldId());
        data.setEntityId(field.getEntityId());
        data.setAppId(field.getAppId());
        data.setGroupId(groupId);
        rangeRepository.upsert(data);
        return data.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MetadataValidationRangeDO data) {
        Assert.notNull(data, "data不能为空");
        Assert.notNull(data.getId(), "id不能为空");
        rangeRepository.upsert(data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(Long fieldId) {
        rangeRepository.deleteByFieldId(fieldId);
    }

    private Long ensureFieldRuleGroup(Long fieldId) {
        return ruleGroupService.ensureFieldRuleGroup(fieldId);
    }
}
