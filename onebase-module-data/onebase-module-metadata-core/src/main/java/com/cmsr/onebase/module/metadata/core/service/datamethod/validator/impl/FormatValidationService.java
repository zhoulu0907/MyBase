package com.cmsr.onebase.module.metadata.core.service.datamethod.validator.impl;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationFormatDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationFormatRepository;
import com.cmsr.onebase.module.metadata.core.service.datamethod.validator.ValidationService;

import java.util.logging.Logger;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 格式校验服务
 * 
 * 校验字段格式是否符合规则（正则表达式、邮箱、手机号等）
 *
 */
public class FormatValidationService implements ValidationService {

    private static final Logger log = Logger.getLogger(FormatValidationService.class.getName());

    private final MetadataValidationFormatRepository formatRepository;

    public FormatValidationService(MetadataValidationFormatRepository formatRepository) {
        this.formatRepository = formatRepository;
    }

    @Override
    public void validate(Long entityId, Long fieldId, MetadataEntityFieldDO field, Object value, Map<String, Object> data) {
        if (value == null) {
            return; // 空值不校验格式
        }

        // 查询格式规则
        List<MetadataValidationFormatDO> rules = formatRepository.findByFieldId( fieldId);
        
        if (rules.isEmpty()) {
            return; // 没有格式规则，跳过校验
        }

        // 检查是否有启用的格式规则
        boolean hasEnabledRule = rules.stream()
                .anyMatch(rule -> rule.getIsEnabled() != null && rule.getIsEnabled() == 1);

        if (!hasEnabledRule) {
            return; // 没有启用的格式规则
        }

        String stringValue = value.toString();

        // 执行格式校验
        for (MetadataValidationFormatDO rule : rules) {
            if (rule.getIsEnabled() == null || rule.getIsEnabled() != 1) {
                continue; // 跳过未启用的规则
            }

            boolean isValid = false;
            String formatCode = rule.getFormatCode();

            if ("EMAIL".equalsIgnoreCase(formatCode)) {
                isValid = isValidEmail(stringValue);
            } else if ("MOBILE".equalsIgnoreCase(formatCode)) {
                isValid = isValidMobile(stringValue);
            } else if ("PHONE".equalsIgnoreCase(formatCode)) {
                isValid = isValidPhone(stringValue);
            } else if ("URL".equalsIgnoreCase(formatCode)) {
                isValid = isValidUrl(stringValue);
            } else if ("REGEX".equalsIgnoreCase(formatCode) && rule.getRegexPattern() != null) {
                isValid = isValidRegex(stringValue, rule.getRegexPattern(), rule.getFlags());
            }

            if (!isValid) {
                String errorMessage = rule.getPromptMessage() != null && !rule.getPromptMessage().trim().isEmpty()
                        ? rule.getPromptMessage()
                        : "字段[" + field.getDisplayName() + "]格式不正确";
                throw new IllegalArgumentException(errorMessage);
            }
        }
    }

    /**
     * 校验邮箱格式
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(emailRegex, email);
    }

    /**
     * 校验手机号格式
     */
    private boolean isValidMobile(String mobile) {
        String mobileRegex = "^1[3-9]\\d{9}$";
        return Pattern.matches(mobileRegex, mobile);
    }

    /**
     * 校验电话号码格式
     */
    private boolean isValidPhone(String phone) {
        String phoneRegex = "^0\\d{2,3}-?\\d{7,8}$";
        return Pattern.matches(phoneRegex, phone);
    }

    /**
     * 校验URL格式
     */
    private boolean isValidUrl(String url) {
        String urlRegex = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$";
        return Pattern.matches(urlRegex, url);
    }

    /**
     * 校验正则表达式
     */
    private boolean isValidRegex(String value, String pattern, String flags) {
        int regexFlags = 0;
        if (flags != null) {
            if (flags.contains("i")) regexFlags |= Pattern.CASE_INSENSITIVE;
            if (flags.contains("m")) regexFlags |= Pattern.MULTILINE;
            if (flags.contains("s")) regexFlags |= Pattern.DOTALL;
        }
        
        try {
            Pattern compiledPattern = Pattern.compile(pattern, regexFlags);
            return compiledPattern.matcher(value).matches();
        } catch (Exception e) {
            log.warning("正则表达式编译失败：" + pattern);
            return false;
        }
    }

    @Override
    public String getValidationType() {
        return "FORMAT";
    }

    @Override
    public boolean supports(String fieldType) {
        // 格式校验主要支持字符串类型
        return "VARCHAR".equalsIgnoreCase(fieldType) || 
               "TEXT".equalsIgnoreCase(fieldType) ||
               "CHAR".equalsIgnoreCase(fieldType);
    }
}
