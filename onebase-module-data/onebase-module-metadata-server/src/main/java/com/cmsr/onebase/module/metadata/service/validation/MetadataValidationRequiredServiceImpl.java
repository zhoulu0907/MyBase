package com.cmsr.onebase.module.metadata.service.validation;

import com.cmsr.onebase.module.metadata.dal.database.MetadataValidationRequiredRepository;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRequiredDO;
import com.cmsr.onebase.module.metadata.service.entity.MetadataEntityFieldService;
import jakarta.annotation.Resource;
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
public class MetadataValidationRequiredServiceImpl implements MetadataValidationRequiredService {

    @Resource private MetadataValidationRequiredRepository requiredRepository; // 自身仓库
    @Resource private MetadataValidationRuleGroupService ruleGroupService; // 其他服务
    @Resource private MetadataEntityFieldService entityFieldService; // 其他服务

    @Override
    public MetadataValidationRequiredDO getByFieldId(Long fieldId) {
        return requiredRepository.findOneByFieldId(fieldId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(MetadataValidationRequiredDO data) {
        Assert.notNull(data, "data不能为空");
        Assert.notNull(data.getFieldId(), "fieldId不能为空");
    MetadataEntityFieldDO field = entityFieldService.getEntityField(String.valueOf(data.getFieldId()));
        Assert.notNull(field, "字段不存在");
    Long groupId = ensureFieldRuleGroup(data.getFieldId());
        data.setEntityId(field.getEntityId());
        data.setAppId(field.getAppId());
        data.setGroupId(groupId);
        requiredRepository.upsert(data);
        return data.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MetadataValidationRequiredDO data) {
        Assert.notNull(data, "data不能为空");
        Assert.notNull(data.getId(), "id不能为空");
        requiredRepository.upsert(data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(Long fieldId) {
        requiredRepository.deleteByFieldId(fieldId);
    }

    private Long ensureFieldRuleGroup(Long fieldId) {
        return ruleGroupService.ensureFieldRuleGroup(fieldId);
    }
}
