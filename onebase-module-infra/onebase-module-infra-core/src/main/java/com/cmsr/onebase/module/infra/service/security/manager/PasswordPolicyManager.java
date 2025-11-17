package com.cmsr.onebase.module.infra.service.security.manager;

import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.module.infra.dal.vo.security.SecurityConfigItemRespVO;
import com.cmsr.onebase.module.infra.enums.security.SecurityConfigKey;
import com.cmsr.onebase.module.infra.service.security.SecurityConfigService;
import com.cmsr.onebase.module.infra.service.security.validator.config.PasswordPolicyConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.infra.enums.ErrorCodeConstants.WEAK_PASSWORD_TENANT_EMPTY;

/**
 * 密码策略管理器
 * 
 * 负责从数据库获取租户的密码策略配置，并转换为PasswordPolicyConfig对象
 * 会复用SecurityConfigService的Redis缓存（30分钟TTL）
 *
 * @author chengyuansen
 * @date 2025-11-07
 */
@Slf4j
@Component
public class PasswordPolicyManager {

    private static final String TRUE = "true";

    @Resource
    private SecurityConfigService securityConfigService;

    /**
     * 获取租户的密码策略配置
     *
     * @return 密码策略配置对象
     */
    public PasswordPolicyConfig getPolicyConfig() {
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null) {
            throw exception(WEAK_PASSWORD_TENANT_EMPTY);
        }
        // 从缓存中获取租户所有安全配置项
        List<SecurityConfigItemRespVO> configItems = securityConfigService.getSecurityConfigsByTenant(tenantId);

        // 转换为Map便于快速查找
        Map<String, SecurityConfigItemRespVO> configMap = configItems.stream()
                .collect(Collectors.toMap(SecurityConfigItemRespVO::getConfigKey, item -> item));

        return convertToPasswordPolicyConfig(configMap);
    }

    /**
     * 将配置项Map转换为PasswordPolicyConfig对象
     *
     * @param configMap 配置项Map，key为configKey，value为配置项
     * @return 密码策略配置对象
     */
    private PasswordPolicyConfig convertToPasswordPolicyConfig(Map<String, SecurityConfigItemRespVO> configMap) {
        PasswordPolicyConfig config = PasswordPolicyConfig.builder()
                .maxLength(32) // 固定值32
                .build();

        // 设置密码校验总开关
        String enableWeakPassword = getConfigValue(configMap, SecurityConfigKey.enableWeakPassword.getConfigKey());

        config.setEnableWeakPassword(TRUE.equalsIgnoreCase(enableWeakPassword));

        // 设置最小长度
        String minLength = getConfigValue(configMap, SecurityConfigKey.minLength.getConfigKey());
        if (minLength != null) {
            try {
                config.setMinLength(Integer.parseInt(minLength.trim()));
            } catch (NumberFormatException e) {
                log.warn("minLength配置值无效，使用默认值8");
                config.setMinLength(8);
            }
        } else {
            config.setMinLength(8); // 默认值
        }

        // 解析extraCharacter配置项
        String extraCharacter = getConfigValue(configMap, SecurityConfigKey.extraCharacter.getConfigKey());
        Set<String> extraSet = parseExtraCharacters(extraCharacter);

        // 根据extraCharacter中的值设置大写和特殊符号检查
        config.setCheckUpperCase(extraSet.contains(SecurityConfigKey.ExtraCharacterOption.uppperCase.getKey()));
        config.setCheckContainSpecialChar(extraSet.contains(SecurityConfigKey.ExtraCharacterOption.specialChar.getKey()));

        // 设置历史密码限制
        String historyLimit = getConfigValue(configMap, SecurityConfigKey.historyLimit.getConfigKey());
        if (historyLimit != null) {
            try {
                config.setHistoryLimit(Integer.parseInt(historyLimit.trim()));
            } catch (NumberFormatException e) {
                log.warn("historyLimit配置值无效，使用默认值3");
                config.setHistoryLimit(3);
            }
        } else {
            config.setHistoryLimit(3); // 默认值
        }

        // 设置密码有效期
        String expiryDays = getConfigValue(configMap, SecurityConfigKey.expiryDays.getConfigKey());
        if (expiryDays != null) {
            try {
                config.setExpiryDays(Integer.parseInt(expiryDays.trim()));
            } catch (NumberFormatException e) {
                log.warn("expiryDays配置值无效，使用默认值180");
                config.setExpiryDays(180);
            }
        } else {
            config.setExpiryDays(180); // 默认值180天
        }

        log.debug("租户密码策略配置转换完成，tenantId: {}, enableWeakPassword: {}, minLength: {}, checkUpperCase: {}, checkContainSpecialChar: {}, historyLimit: {}, expiryDays: {}",
                null, config.getEnableWeakPassword(), config.getMinLength(), config.getCheckUpperCase(), config.getCheckContainSpecialChar(), config.getHistoryLimit(), config.getExpiryDays());

        return config;
    }

    /**
     * 从配置Map中获取指定key的值
     *
     * @param configMap 配置Map
     * @param configKey 配置键
     * @return 配置值，如果不存在则返回null
     */
    private String getConfigValue(Map<String, SecurityConfigItemRespVO> configMap, String configKey) {
        SecurityConfigItemRespVO configItem = configMap.get(configKey);
        if (configItem != null) {
            return configItem.getConfigValue();
        }
        return null;
    }

    /**
     * 解析extraCharacter配置项
     * 
     * extraCharacter是逗号分隔的字符串，如："uppperCase,specialChar"
     *
     * @param extraCharacter 原始配置值
     * @return 解析后的Set集合
     */
    private Set<String> parseExtraCharacters(String extraCharacter) {
        Set<String> result = new HashSet<>();

        if (extraCharacter == null || extraCharacter.trim().isEmpty()) {
            return result;
        }

        String[] values = extraCharacter.split(",");
        for (String value : values) {
            String trimmed = value.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }

        return result;
    }

}
