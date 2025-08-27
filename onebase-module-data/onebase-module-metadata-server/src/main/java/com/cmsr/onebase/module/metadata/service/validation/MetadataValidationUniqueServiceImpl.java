package com.cmsr.onebase.module.metadata.service.validation;

import com.cmsr.onebase.module.metadata.dal.database.MetadataEntityFieldRepository;
import com.cmsr.onebase.module.metadata.dal.database.MetadataValidationRuleGroupRepository;
import com.cmsr.onebase.module.metadata.dal.database.MetadataValidationUniqueRepository;
import com.cmsr.onebase.module.metadata.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationRuleGroupDO;
import com.cmsr.onebase.module.metadata.dal.dataobject.validation.MetadataValidationUniqueDO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 唯一校验 Service 实现
 *
 * @author bty418
 * @date 2025-08-27
 */
@Service
public class MetadataValidationUniqueServiceImpl implements MetadataValidationUniqueService {

    @Resource private MetadataValidationUniqueRepository uniqueRepository;
    @Resource private MetadataValidationRuleGroupRepository ruleGroupRepository;
    @Resource private MetadataEntityFieldRepository entityFieldRepository;

    @Override
    public MetadataValidationUniqueDO getByFieldId(Long fieldId) {
        return uniqueRepository.findOneByFieldId(fieldId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(MetadataValidationUniqueDO data) {
        Assert.notNull(data, "data不能为空");
        Assert.notNull(data.getFieldId(), "fieldId不能为空");
        MetadataEntityFieldDO field = entityFieldRepository.findById(data.getFieldId());
        Assert.notNull(field, "字段不存在");
        Long groupId = ensureFieldRuleGroup(data.getFieldId());
        data.setEntityId(field.getEntityId());
        data.setAppId(field.getAppId());
        data.setGroupId(groupId);
        uniqueRepository.upsert(data);
        return data.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MetadataValidationUniqueDO data) {
        Assert.notNull(data, "data不能为空");
        Assert.notNull(data.getId(), "id不能为空");
        uniqueRepository.upsert(data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByFieldId(Long fieldId) {
        uniqueRepository.deleteByFieldId(fieldId);
    }

    private Long ensureFieldRuleGroup(Long fieldId) {
        String rgName = "RG_FIELD_" + fieldId;
        MetadataValidationRuleGroupDO group = ruleGroupRepository.selectByRgName(rgName, null);
        if (group == null) {
            group = new MetadataValidationRuleGroupDO();
            group.setRgName(rgName);
            group.setRgDesc("字段" + fieldId + "的规则组");
            group.setRgStatus(1);
            group.setValMethod("BLOCK_AND_POP");
            ruleGroupRepository.insert(group);
        }
        return group.getId();
    }
}
