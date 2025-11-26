package com.cmsr.onebase.module.infra.service.security;

import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.module.infra.dal.redis.RedisKeyConstants;
import com.cmsr.onebase.module.infra.enums.security.SecurityConfigKey;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 会话空闲超时管理服务实现类
 * 
 * 使用Redis String结构存储用户最后活跃时间：
 * Key: infra:security:user:idle:{tenantId}:{userId}:{deviceId}
 * Value: 最后活跃时间戳
 * TTL: 从租户配置中读取sessionTimeout值（秒）
 *
 * @author chengyuansen
 * @date 2025-11-20
 */
@Service
@Slf4j
public class SessionIdleServiceImpl implements SessionIdleService {

    /**
     * 默认会话超时时间（秒）
     * 当租户未配置或配置无效时使用此默认值
     */
    private static final int DEFAULT_SESSION_TIMEOUT = 1800;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private SecurityConfigService securityConfigService;

    @Override
    public void createRedisIdleKey(Long userId, String deviceId) {
        if (userId == null || StrUtil.isBlank(deviceId)) {
            log.warn("创建会话空闲Key失败，参数不完整: userId={}, deviceId={}", userId, deviceId);
            return;
        }

        // 获取当前租户ID
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null) {
            log.warn("创建会话空闲Key失败，无法获取租户ID: userId={}, deviceId={}", userId, deviceId);
            return;
        }

        // 获取租户的会话超时配置
        Integer sessionTimeout = getSessionTimeout(tenantId);
        
        // 构建Redis Key
        String redisKey = String.format(RedisKeyConstants.USER_IDLE_KEY, tenantId, userId, deviceId);
        
        // 记录当前时间戳
        String currentTime = String.valueOf(System.currentTimeMillis());
        
        // 设置Key和TTL
        stringRedisTemplate.opsForValue().set(redisKey, currentTime, sessionTimeout, TimeUnit.SECONDS);
        
        log.debug("创建会话空闲Key成功: userId={}, deviceId={}, timeout={}秒", userId, deviceId, sessionTimeout);
    }

    @Override
    public boolean updateRedisIdleKey(Long tenantId, Long userId, String deviceId) {
        if (tenantId == null || userId == null || StrUtil.isBlank(deviceId)) {
            log.error("更新会话空闲Key失败，参数不完整: tenantId={}, userId={}, deviceId={}", tenantId, userId, deviceId);
            return false;
        }

        // 构建 Redis Key
        String redisKey = String.format(RedisKeyConstants.USER_IDLE_KEY, tenantId, userId, deviceId);
        
        // 检查Key是否存在
        Boolean exists = stringRedisTemplate.hasKey(redisKey);
        if (!exists) {
            log.info("会话空闲Key不存在，会话已超时: userId={}, deviceId={}", userId, deviceId);
            return false;
        }
        
        // 获取租户的会话超时配置
        Integer sessionTimeout = getSessionTimeout(tenantId);
        
        // 更新时间戳和TTL
        String currentTime = String.valueOf(System.currentTimeMillis());
        stringRedisTemplate.opsForValue().set(redisKey, currentTime, sessionTimeout, TimeUnit.SECONDS);
        
        log.trace("更新会话空闲Key成功: userId={}, deviceId={}", userId, deviceId);
        return true;
    }

    @Override
    public boolean existRedisIdleKey(Long userId, String deviceId) {
        if (userId == null || StrUtil.isBlank(deviceId)) {
            log.warn("检查会话空闲Key失败，参数不完整: userId={}, deviceId={}", userId, deviceId);
            return false;
        }

        // 获取当前租户ID
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null) {
            log.warn("检查会话空闲Key失败，无法获取租户ID: userId={}, deviceId={}", userId, deviceId);
            return false;
        }

        // 构建Redis Key
        String redisKey = String.format(RedisKeyConstants.USER_IDLE_KEY, tenantId, userId, deviceId);
        
        // 检查Key是否存在
        Boolean exists = stringRedisTemplate.hasKey(redisKey);
        
        log.debug("检查会话空闲Key: userId={}, deviceId={}, exists={}", userId, deviceId, exists);
        return exists;
    }

    /**
     * 获取租户的会话超时配置（秒）
     * 
     * 如果租户未配置或配置无效，返回默认值
     *
     * @param tenantId 租户ID
     * @return 会话超时时间（秒）
     */
    private Integer getSessionTimeout(Long tenantId) {
        Integer sessionTimeout = securityConfigService.getIntConfig(tenantId, SecurityConfigKey.sessionTimeout.name());
        
        // 校验配置值有效性
        if (sessionTimeout == null || sessionTimeout <= 0) {
            log.debug("租户[{}]未配置会话超时时间或配置无效，使用默认值{}秒", tenantId, DEFAULT_SESSION_TIMEOUT);
            return DEFAULT_SESSION_TIMEOUT;
        }
        
        return sessionTimeout;
    }
}
