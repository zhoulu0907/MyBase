package com.cmsr.onebase.module.infra.service.security;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.cmsr.onebase.framework.tenant.core.context.TenantContextHolder;
import com.cmsr.onebase.module.infra.dal.vo.security.SecurityConfigItemRespVO;
import com.cmsr.onebase.module.infra.enums.security.SecurityConfigKey;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.infra.enums.ErrorCodeConstants.CAPTCHA_REFRESH_TOO_FAST;

/**
 * 验证码安全配置服务实现类
 *
 * @author chengyuansen
 * @date 2025-11-17
 */
@Slf4j
@Service
public class CaptchaSecurityServiceImpl implements CaptchaSecurityService {

    /**
     * Redis Key前缀 - 验证码刷新时间记录
     */
    private static final String REDIS_KEY_CAPTCHA_REFRESH = "infra:security:captcha:refresh:";

    /**
     * 默认验证码有效期（秒）
     */
    private static final int DEFAULT_EXPIRY_SECONDS = 600;

    /**
     * 默认刷新间隔（秒）
     */
    private static final int DEFAULT_REFRESH_INTERVAL = 120;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private SecurityConfigService securityConfigService;

    @Override
    public Integer getCaptchaExpirySeconds() {
        Long tenantId = TenantContextHolder.getTenantId();
        
        // 获取租户配置
        Map<String, String> configMap = getTenantConfigMap(tenantId);
        
        // 解析expirySeconds配置
        String expirySecondsStr = configMap.get(SecurityConfigKey.expirySeconds.getConfigKey());
        Integer expirySeconds = parseIntConfig(expirySecondsStr, DEFAULT_EXPIRY_SECONDS);
        
        log.debug("获取验证码有效期配置，tenantId: {}, expirySeconds: {}", tenantId, expirySeconds);
        return expirySeconds;
    }

    @Override
    public void checkCanRefreshCaptcha(String sessionKey) {
        if (StrUtil.isBlank(sessionKey)) {
            log.warn("sessionKey为空，跳过刷新间隔检查");
            return;
        }

        Long tenantId = TenantContextHolder.getTenantId();
        Integer refreshInterval = getCaptchaRefreshInterval();
        String redisKey = buildRefreshKey(tenantId, sessionKey);
        
        // 使用SETNX实现原子性检查和记录，避免竞态条件
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(redisKey, String.valueOf(System.currentTimeMillis()), 
                             refreshInterval, TimeUnit.SECONDS);
        
        if (Boolean.FALSE.equals(success)) {
            // 设置失败说明key已存在，刷新过快
            Long ttl = stringRedisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
            long remainingSeconds = (ttl != null && ttl > 0) ? ttl : refreshInterval;
            log.warn("验证码刷新过快，tenantId: {}, 剩余等待时间: {}秒", tenantId, remainingSeconds);
            throw exception(CAPTCHA_REFRESH_TOO_FAST, remainingSeconds + "秒");
        }
        
        log.debug("验证码刷新间隔检查通过，tenantId: {}", tenantId);
    }

    @Override
    public Boolean isCaptchaEnabledForScenario(SecurityConfigKey.EnableScenariosOption scenario) {
        if (scenario == null) {
            log.warn("scenario为空，默认启用验证码");
            return true;
        }

        Long tenantId = TenantContextHolder.getTenantId();
        
        // 获取租户配置
        Map<String, String> configMap = getTenantConfigMap(tenantId);
        
        // 解析enableScenarios配置（格式：login,pwdreset,register）
        String enableScenariosStr = configMap.get(SecurityConfigKey.enableScenarios.getConfigKey());
        if (StrUtil.isBlank(enableScenariosStr)) {
            log.debug("enableScenarios配置为空，默认所有场景启用验证码，tenantId: {}", tenantId);
            return true;
        }
        
        // 解析场景列表
        List<String> enabledScenarios = Arrays.stream(enableScenariosStr.split(","))
                .map(String::trim)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
        
        // 使用枚举的key进行匹配
        boolean enabled = enabledScenarios.contains(scenario.getKey());
        
        log.debug("检查场景是否启用验证码，tenantId: {}, scenario: {}, enabled: {}", 
                tenantId, scenario.getKey(), enabled);
        
        return enabled;
    }


    /**
     * 获取验证码刷新间隔配置（秒）
     *
     * 从租户安全配置中读取refreshInterval配置项
     * 如果租户未配置，返回模板默认值
     *
     * @return 刷新间隔（秒），默认120秒
     */
    private Integer getCaptchaRefreshInterval() {
        Long tenantId = TenantContextHolder.getTenantId();

        // 获取租户配置
        Map<String, String> configMap = getTenantConfigMap(tenantId);

        // 解析refreshInterval配置
        String refreshIntervalStr = configMap.get(SecurityConfigKey.refreshInterval.getConfigKey());
        Integer refreshInterval = parseIntConfig(refreshIntervalStr, DEFAULT_REFRESH_INTERVAL);

        log.debug("获取验证码刷新间隔配置，tenantId: {}, refreshInterval: {}", tenantId, refreshInterval);
        return refreshInterval;
    }

    /**
     * 获取租户安全配置Map
     *
     * @param tenantId 租户ID
     * @return 配置Map（configKey -> configValue）
     */
    private Map<String, String> getTenantConfigMap(Long tenantId) {
        // 从SecurityConfigService获取租户配置（带缓存）
        List<SecurityConfigItemRespVO> configItems = securityConfigService.getSecurityConfigsByTenant(tenantId);
        
        // 转换为Map便于查找，过滤掉configValue为null的项
        return configItems.stream()
                .filter(item -> item.getConfigKey() != null && item.getConfigValue() != null)
                .collect(Collectors.toMap(
                        SecurityConfigItemRespVO::getConfigKey, 
                        SecurityConfigItemRespVO::getConfigValue
                ));
    }

    /**
     * 解析整型配置
     *
     * @param value        配置值
     * @param defaultValue 默认值
     * @return 解析结果
     */
    private int parseIntConfig(String value, int defaultValue) {
        if (StrUtil.isBlank(value)) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            log.warn("配置值解析失败，使用默认值，value: {}, defaultValue: {}", value, defaultValue);
            return defaultValue;
        }
    }

    /**
     * 构建验证码刷新Redis Key
     * 
     * 使用MD5处理sessionKey，避免Redis Key过长
     * sessionKey通常是IP+UserAgent，可能很长，使用MD5缩短
     *
     * @param tenantId   租户ID
     * @param sessionKey 会话标识（IP+UserAgent）
     * @return Redis Key
     */
    private String buildRefreshKey(Long tenantId, String sessionKey) {
        // 使用MD5处理sessionKey，避免Redis Key过长
        String sessionKeyMd5 = DigestUtil.md5Hex(sessionKey);
        return REDIS_KEY_CAPTCHA_REFRESH + tenantId + ":" + sessionKeyMd5;
    }

}
