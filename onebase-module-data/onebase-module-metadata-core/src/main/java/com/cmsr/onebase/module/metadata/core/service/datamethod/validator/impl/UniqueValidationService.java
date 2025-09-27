package com.cmsr.onebase.module.metadata.core.service.datamethod.validator.impl;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationUniqueDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationUniqueRepository;
import com.cmsr.onebase.module.metadata.core.service.datamethod.validator.ValidationService;

import java.util.logging.Logger;

import java.util.List;
import java.util.Map;

/**
 * 唯一性校验服务
 * 
 * 校验字段值是否唯一
 *
 */
public class UniqueValidationService implements ValidationService {

    private static final Logger log = Logger.getLogger(UniqueValidationService.class.getName());

    private final MetadataValidationUniqueRepository uniqueRepository;

    public UniqueValidationService(MetadataValidationUniqueRepository uniqueRepository) {
        this.uniqueRepository = uniqueRepository;
    }

    @Override
    public void validate(Long entityId, Long fieldId, MetadataEntityFieldDO field, Object value, Map<String, Object> data) {
        if (value == null) {
            return; // 空值不校验唯一性
        }

        // 查询唯一性规则
        List<MetadataValidationUniqueDO> rules = uniqueRepository.findByFieldId(fieldId);
        
        if (rules.isEmpty()) {
            return; // 没有唯一性规则，跳过校验
        }

        // 检查是否有启用的唯一性规则
        boolean hasEnabledRule = rules.stream()
                .anyMatch(rule -> rule.getIsEnabled() != null && rule.getIsEnabled() == 1);

        if (!hasEnabledRule) {
            return; // 没有启用的唯一性规则
        }

        // 执行唯一性校验
        for (MetadataValidationUniqueDO rule : rules) {
            if (rule.getIsEnabled() == null || rule.getIsEnabled() != 1) {
                continue; // 跳过未启用的规则
            }

            // TODO: 实现数据库唯一性检查
            // 这里需要根据 rule.getUniqueScope() 来确定检查范围
            // 可能需要调用数据库查询来检查是否已存在相同的值
            
            log.fine("执行唯一性校验：entityId=" + entityId + ", fieldId=" + fieldId + ", value=" + value);
            
            // 临时实现：这里应该查询数据库检查唯一性
            // 如果发现重复值，抛出异常
            // throw new IllegalArgumentException("字段[" + field.getDisplayName() + "]值已存在");
        }
    }

    @Override
    public String getValidationType() {
        return "UNIQUE";
    }

    @Override
    public boolean supports(String fieldType) {
        // 唯一性校验支持所有字段类型
        return true;
    }
}
