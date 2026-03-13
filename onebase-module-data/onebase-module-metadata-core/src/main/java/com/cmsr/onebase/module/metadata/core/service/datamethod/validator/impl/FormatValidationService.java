package com.cmsr.onebase.module.metadata.core.service.datamethod.validator.impl;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationFormatDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationFormatRepository;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataDataMethodSubEntityContext;
import com.cmsr.onebase.module.metadata.core.service.datamethod.validator.PrefetchableValidationService;
import org.springframework.stereotype.Component;

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
@Component
public class FormatValidationService implements PrefetchableValidationService {

    private static final Logger log = Logger.getLogger(FormatValidationService.class.getName());

    private final MetadataValidationFormatRepository formatRepository;
    private transient Map<String, java.util.List<MetadataValidationFormatDO>> prefetched;

    public FormatValidationService(MetadataValidationFormatRepository formatRepository) {
        this.formatRepository = formatRepository;
    }

    @Override
    public void validate(String entityUuid, String fieldUuid, MetadataEntityFieldDO field, Object value, Map<String, Object> data, List<MetadataDataMethodSubEntityContext> subEntities) {
        if (value == null) {
            return; // 空值不校验格式
        }

        // 查询格式规则
        List<MetadataValidationFormatDO> rules = null;
        if (prefetched != null) {
            rules = prefetched.get(fieldUuid);
        }
        if (rules == null) {
            rules = formatRepository.findByFieldUuid(fieldUuid);
        }

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

            // 使用正则表达式进行验证
            String regexPattern = rule.getRegexPattern();
            if (regexPattern == null || regexPattern.trim().isEmpty()) {
                continue; // 跳过没有正则表达式的规则
            }

            boolean isValid = isValidRegex(stringValue, regexPattern, rule.getFlags());

            if (!isValid) {
                String errorMessage = rule.getPromptMessage() != null && !rule.getPromptMessage().trim().isEmpty()
                        ? rule.getPromptMessage()
                        : "字段[" + field.getDisplayName() + "]格式不正确";
                throw new IllegalArgumentException(errorMessage);
            }
        }
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
        // 格式校验支持所有字段类型
        return true;
    }

    @Override
    public void preloadBatchRules(Map<String, Map<String, ? extends java.util.List<?>>> rulesByType) {
        Map<String, ? extends java.util.List<?>> m = rulesByType != null ? rulesByType.get("FORMAT") : null;
        if (m != null) {
            this.prefetched = new java.util.HashMap<>();
            for (Map.Entry<String, ? extends java.util.List<?>> e : m.entrySet()) {
                @SuppressWarnings("unchecked")
                java.util.List<MetadataValidationFormatDO> list = (java.util.List<MetadataValidationFormatDO>) (java.util.List<?>) e.getValue();
                this.prefetched.put(e.getKey(), list);
            }
        }
    }

    @Override
    public void clearPrefetchedRules() {
        this.prefetched = null;
    }
}
