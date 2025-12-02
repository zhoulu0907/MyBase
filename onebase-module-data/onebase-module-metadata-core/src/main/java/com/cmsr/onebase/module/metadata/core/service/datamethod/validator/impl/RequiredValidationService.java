package com.cmsr.onebase.module.metadata.core.service.datamethod.validator.impl;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRequiredDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRequiredRepository;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataDataMethodSubEntityContext;
import com.cmsr.onebase.module.metadata.core.service.datamethod.validator.ValidationService;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Map;

/**
 * 必填校验服务
 * 
 * 校验字段是否必填
 *
 */
@Component
public class RequiredValidationService implements ValidationService {


    private final MetadataValidationRequiredRepository requiredRepository;

    public RequiredValidationService(MetadataValidationRequiredRepository requiredRepository) {
        this.requiredRepository = requiredRepository;
    }

    @Override
    public void validate(Long entityId, Long fieldId, MetadataEntityFieldDO field, Object value, Map<String, Object> data, List<MetadataDataMethodSubEntityContext> subEntities) {
        // 查询必填规则
        List<MetadataValidationRequiredDO> rules = requiredRepository.findByFieldId(fieldId);
        
        if (rules.isEmpty()) {
            return; // 没有必填规则，跳过校验
        }

        // 检查是否有启用的必填规则
        boolean hasEnabledRule = rules.stream()
                .anyMatch(rule -> rule.getIsEnabled() != null && rule.getIsEnabled() == 1);

        if (!hasEnabledRule) {
            return; // 没有启用的必填规则
        }

        // 执行必填校验
        if (value == null || String.valueOf(value).trim().isEmpty()) {
            String errorMessage = rules.stream()
                    .filter(rule -> rule.getIsEnabled() != null && rule.getIsEnabled() == 1)
                    .map(rule -> rule.getPromptMessage())
                    .filter(msg -> msg != null && !msg.trim().isEmpty())
                    .findFirst()
                    .orElse("字段[" + field.getDisplayName() + "]为必填字段");
            
            throw new IllegalArgumentException(errorMessage);
        }
    }

    @Override
    public String getValidationType() {
        return "REQUIRED";
    }

    @Override
    public boolean supports(String fieldType) {
        // 必填校验支持所有字段类型
        return true;
    }
}
