package com.cmsr.onebase.module.metadata.core.service.datamethod.validator.impl;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationFormatDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationFormatRepository;
import com.cmsr.onebase.module.metadata.core.service.datamethod.validator.ValidationService;
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


    /**
     * 校验地址格式
     */
    private boolean isValidAddress(String addr){
        return false;
    }

    /**
     * 校验数字格式
     */
    private boolean isValidNumber(String number){
        return false;
    }


    /**
     * 校验日期格式
     */
    private boolean isValidDate(String date){
        return false;
    }

    /**
     * 校验日期时间格式
     */
    private boolean isValidDatetime(String dateTime){
        return false;
    }

    /**
     * 校验布尔值格式
     */
    private boolean isValidBool(String bool){
        return false;
    }
    /**
     * 校验文件格式
     */
    private boolean isValidFile(String file){
        return false;
    }

    /**
     * 校验图片格式
     */
    private boolean isValidImage(String image){
        return false;
    }

    /**
     * 校验地理位置格式
     */
    private boolean isValidGeography(String geography){
        return false;
    }

    /**
     * 校验密码格式
     */
    private boolean isValidPasswrod(String password){
        return false;
    }

    /**
     * 校验加密字段格式
     */
    private boolean isValidEncryped(String encryption){
        return false;
    }

    /**
     * 校验聚合统计格式
     */
    private boolean isValidAggregate(String aggregate){
        return false;
    }

    /**
     * 校验唯一标识格式
     */
    private boolean isValidId(String id){
        return false;
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
