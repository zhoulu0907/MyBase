package com.cmsr.onebase.module.metadata.service.validation;

import com.cmsr.onebase.module.metadata.dal.database.MetadataValidationChildNotEmptyRepository;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationChildNotEmptyDO;
import com.cmsr.onebase.module.metadata.service.entity.MetadataEntityFieldService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 子表非空校验 Service 实现
 */
@Service
public class MetadataValidationChildNotEmptyServiceImpl implements MetadataValidationChildNotEmptyService {

    @Resource private MetadataValidationChildNotEmptyRepository childNotEmptyRepository; // 自身仓库
    @Resource private MetadataValidationRuleGroupService ruleGroupService; // 其他服务
    @Resource private MetadataEntityFieldService entityFieldService; // 其他服务

    @Override
    public MetadataValidationChildNotEmptyDO getByFieldId(Long fieldId) {
        return childNotEmptyRepository.findOneByFieldId(fieldId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(MetadataValidationChildNotEmptyDO data) {
        Assert.notNull(data, "data不能为空");
        Assert.notNull(data.getFieldId(), "fieldId不能为空");
    MetadataEntityFieldDO field = entityFieldService.getEntityField(String.valueOf(data.getFieldId()));
        Assert.notNull(field, "字段不存在");
    Long groupId = ensureFieldRuleGroup(data.getFieldId());
        data.setEntityId(field.getEntityId());
        data.setAppId(field.getAppId());
        data.setGroupId(groupId);
        childNotEmptyRepository.upsert(data);
        return data.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MetadataValidationChildNotEmptyDO data) {
        Assert.notNull(data, "data不能为空");
        Assert.notNull(data.getId(), "id不能为空");
        childNotEmptyRepository.upsert(data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(Long fieldId) {
        childNotEmptyRepository.deleteByFieldId(fieldId);
    }

    private Long ensureFieldRuleGroup(Long fieldId) {
        return ruleGroupService.ensureFieldRuleGroup(fieldId);
    }
}
