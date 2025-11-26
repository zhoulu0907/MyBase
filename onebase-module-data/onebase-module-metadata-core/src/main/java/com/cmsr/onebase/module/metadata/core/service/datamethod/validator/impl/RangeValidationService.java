package com.cmsr.onebase.module.metadata.core.service.datamethod.validator.impl;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationRangeDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationRangeRepository;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataDataMethodSubEntityContext;
import com.cmsr.onebase.module.metadata.core.service.datamethod.validator.ValidationService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

/**
 * 范围校验服务
 * 校验字段值是否在指定范围内（数值范围、日期范围）
 *
 */
@Component
public class RangeValidationService implements ValidationService {

    private final MetadataValidationRangeRepository rangeRepository;

    public RangeValidationService(MetadataValidationRangeRepository rangeRepository) {
        this.rangeRepository = rangeRepository;
    }

    @Override
    public boolean supports(String fieldType) {
        // 支持数值和日期类型的范围校验
        return "NUMBER".equalsIgnoreCase(fieldType) || 
               "DECIMAL".equalsIgnoreCase(fieldType) ||
               "DATE".equalsIgnoreCase(fieldType) ||
               "DATETIME".equalsIgnoreCase(fieldType);
    }

    @Override
    public void validate(Long entityId, Long fieldId, MetadataEntityFieldDO field, Object value, Map<String, Object> data, List<MetadataDataMethodSubEntityContext> subEntities) {
        if (value == null) {
            return; // 空值不校验范围
        }

        // 查询范围规则
        List<MetadataValidationRangeDO> rules = rangeRepository.findByFieldId(fieldId);
        
        if (rules.isEmpty()) {
            return; // 没有范围规则，跳过校验
        }

        // 检查是否有启用的范围规则
        boolean hasEnabledRule = rules.stream()
                .anyMatch(rule -> rule.getIsEnabled() != null && rule.getIsEnabled() == 1);

        if (!hasEnabledRule) {
            return; // 没有启用的范围规则
        }

        // 执行范围校验
        for (MetadataValidationRangeDO rule : rules) {
            if (rule.getIsEnabled() == null || rule.getIsEnabled() != 1) {
                continue; // 跳过未启用的规则
            }

            String rangeType = rule.getRangeType();
            if ("NUMBER".equalsIgnoreCase(rangeType) || "DECIMAL".equalsIgnoreCase(rangeType)) {
                validateNumberRange(value, rule, field);
            } else if ("DATE".equalsIgnoreCase(rangeType) || "DATETIME".equalsIgnoreCase(rangeType)) {
                validateDateRange(value, rule, field);
            }
        }
    }

    /**
     * 校验数值范围
     */
    private void validateNumberRange(Object value, MetadataValidationRangeDO rule, MetadataEntityFieldDO field) {
        BigDecimal numValue;
        try {
            if (value instanceof BigDecimal) {
                numValue = (BigDecimal) value;
            } else {
                numValue = new BigDecimal(value.toString());
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("字段[" + field.getDisplayName() + "]不是有效的数值");
        }

        // 校验最小值
        if (rule.getMinValue() != null) {
            boolean includeMin = rule.getIncludeMin() != null && rule.getIncludeMin() == 1;
            if (includeMin && numValue.compareTo(rule.getMinValue()) < 0) {
                String errorMessage = rule.getPromptMessage() != null && !rule.getPromptMessage().trim().isEmpty()
                        ? rule.getPromptMessage()
                        : "字段[" + field.getDisplayName() + "]值不能小于" + rule.getMinValue();
                throw new IllegalArgumentException(errorMessage);
            } else if (!includeMin && numValue.compareTo(rule.getMinValue()) <= 0) {
                String errorMessage = rule.getPromptMessage() != null && !rule.getPromptMessage().trim().isEmpty()
                        ? rule.getPromptMessage()
                        : "字段[" + field.getDisplayName() + "]值必须大于" + rule.getMinValue();
                throw new IllegalArgumentException(errorMessage);
            }
        }

        // 校验最大值
        if (rule.getMaxValue() != null) {
            boolean includeMax = rule.getIncludeMax() != null && rule.getIncludeMax() == 1;
            if (includeMax && numValue.compareTo(rule.getMaxValue()) > 0) {
                String errorMessage = rule.getPromptMessage() != null && !rule.getPromptMessage().trim().isEmpty()
                        ? rule.getPromptMessage()
                        : "字段[" + field.getDisplayName() + "]值不能大于" + rule.getMaxValue();
                throw new IllegalArgumentException(errorMessage);
            } else if (!includeMax && numValue.compareTo(rule.getMaxValue()) >= 0) {
                String errorMessage = rule.getPromptMessage() != null && !rule.getPromptMessage().trim().isEmpty()
                        ? rule.getPromptMessage()
                        : "字段[" + field.getDisplayName() + "]值必须小于" + rule.getMaxValue();
                throw new IllegalArgumentException(errorMessage);
            }
        }
    }

    /**
     * 校验日期范围
     */
    private void validateDateRange(Object value, MetadataValidationRangeDO rule, MetadataEntityFieldDO field) {
        LocalDateTime dateValue;
        try {
            if (value instanceof LocalDateTime) {
                dateValue = (LocalDateTime) value;
            } else {
                // 尝试解析字符串格式的日期
                String dateStr = value.toString();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                dateValue = LocalDateTime.parse(dateStr, formatter);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("字段[" + field.getDisplayName() + "]不是有效的日期格式");
        }

        // 校验最小日期
        if (rule.getMinDate() != null) {
            boolean includeMin = rule.getIncludeMin() != null && rule.getIncludeMin() == 1;
            if (includeMin && dateValue.isBefore(rule.getMinDate())) {
                String errorMessage = rule.getPromptMessage() != null && !rule.getPromptMessage().trim().isEmpty()
                        ? rule.getPromptMessage()
                        : "字段[" + field.getDisplayName() + "]日期不能早于" + rule.getMinDate();
                throw new IllegalArgumentException(errorMessage);
            } else if (!includeMin && !dateValue.isAfter(rule.getMinDate())) {
                String errorMessage = rule.getPromptMessage() != null && !rule.getPromptMessage().trim().isEmpty()
                        ? rule.getPromptMessage()
                        : "字段[" + field.getDisplayName() + "]日期必须晚于" + rule.getMinDate();
                throw new IllegalArgumentException(errorMessage);
            }
        }

        // 校验最大日期
        if (rule.getMaxDate() != null) {
            boolean includeMax = rule.getIncludeMax() != null && rule.getIncludeMax() == 1;
            if (includeMax && dateValue.isAfter(rule.getMaxDate())) {
                String errorMessage = rule.getPromptMessage() != null && !rule.getPromptMessage().trim().isEmpty()
                        ? rule.getPromptMessage()
                        : "字段[" + field.getDisplayName() + "]日期不能晚于" + rule.getMaxDate();
                throw new IllegalArgumentException(errorMessage);
            } else if (!includeMax && !dateValue.isBefore(rule.getMaxDate())) {
                String errorMessage = rule.getPromptMessage() != null && !rule.getPromptMessage().trim().isEmpty()
                        ? rule.getPromptMessage()
                        : "字段[" + field.getDisplayName() + "]日期必须早于" + rule.getMaxDate();
                throw new IllegalArgumentException(errorMessage);
            }
        }
    }

    @Override
    public String getValidationType() {
        return "RANGE";
    }
}