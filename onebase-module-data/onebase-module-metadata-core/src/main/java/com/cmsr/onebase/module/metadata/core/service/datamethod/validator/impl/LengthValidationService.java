package com.cmsr.onebase.module.metadata.core.service.datamethod.validator.impl;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationLengthDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationLengthRepository;
import com.cmsr.onebase.module.metadata.core.service.datamethod.validator.ValidationService;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Map;

/**
 * 长度校验服务
 * 
 * 校验字段长度是否符合规则
 *
 */
@Component
public class LengthValidationService implements ValidationService {


    private final MetadataValidationLengthRepository lengthRepository;

    public LengthValidationService(MetadataValidationLengthRepository lengthRepository) {
        this.lengthRepository = lengthRepository;
    }

    @Override
    public void validate(Long entityId, Long fieldId, MetadataEntityFieldDO field, Object value, Map<String, Object> data) {
        if (value == null) {
            return; // 空值不校验长度
        }

        // 查询长度规则
        List<MetadataValidationLengthDO> rules = lengthRepository.findByFieldId( fieldId);

        if (rules.isEmpty()) {
            return; // 没有长度规则，跳过校验
        }

        // 检查是否有启用的长度规则
        boolean hasEnabledRule = rules.stream()
                .anyMatch(rule -> rule.getIsEnabled() != null && rule.getIsEnabled() == 1);

        if (!hasEnabledRule) {
            return; // 没有启用的长度规则
        }

        // 执行长度校验
        String stringValue = value.toString();
        
        for (MetadataValidationLengthDO rule : rules) {
            if (rule.getIsEnabled() == null || rule.getIsEnabled() != 1) {
                continue; // 跳过未启用的规则
            }

            // 是否在校验前去除空格
            if (rule.getTrimBefore() != null && rule.getTrimBefore() == 1) {
                stringValue = stringValue.trim();
            }

            // 校验最小长度
            if (rule.getMinLength() != null && stringValue.length() < rule.getMinLength()) {
                String errorMessage = rule.getPromptMessage() != null && !rule.getPromptMessage().trim().isEmpty()
                        ? rule.getPromptMessage()
                        : "字段[" + field.getDisplayName() + "]长度不能小于" + rule.getMinLength() + "个字符";
                throw new IllegalArgumentException(errorMessage);
            }

            // 校验最大长度
            if (rule.getMaxLength() != null && stringValue.length() > rule.getMaxLength()) {
                String errorMessage = rule.getPromptMessage() != null && !rule.getPromptMessage().trim().isEmpty()
                        ? rule.getPromptMessage()
                        : "字段[" + field.getDisplayName() + "]长度不能大于" + rule.getMaxLength() + "个字符";
                throw new IllegalArgumentException(errorMessage);
            }
        }
    }

    @Override
    public String getValidationType() {
        return "LENGTH";
    }

    @Override
    public boolean supports(String fieldType) {
        // 长度校验主要支持字符串类型
        return "VARCHAR".equalsIgnoreCase(fieldType) || 
               "TEXT".equalsIgnoreCase(fieldType) ||
               "CHAR".equalsIgnoreCase(fieldType);
    }
}
