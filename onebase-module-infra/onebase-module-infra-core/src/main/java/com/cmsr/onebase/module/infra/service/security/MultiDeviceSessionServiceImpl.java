package com.cmsr.onebase.module.infra.service.security;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.module.infra.dal.redis.RedisKeyConstants;
import com.cmsr.onebase.module.infra.enums.security.SecurityConfigKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 多设备会话管理Service实现类
 * 
 * 使用Redis Hash结构存储设备信息：
 * Key: infra:security:online:devices:{tenantId}:{userId}
 * Hash Field: deviceId
 * Hash Value: {"accessToken":"xxx","loginTime":123456}
 *
 * @author chengyuansen
 * @date 2025-11-18
 */
@Service
@Slf4j
public class MultiDeviceSessionServiceImpl implements MultiDeviceSessionService {

    private static final String ACCESS_TOKEN_KEY = "accessToken";

    private static final String LOGIN_TIME_KEY = "loginTime";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private SecurityConfigServiceImpl securityConfigService;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public List<String> checkAndLimitDevices(Long userId, String deviceId, String newAccessToken) {
        List<String> removedTokens = new ArrayList<>();
        Long tenantId = TenantContextHolder.getTenantId();

        Integer maxOnlineDevices = getMaxOnlineDevices(tenantId);
        if (maxOnlineDevices == null || maxOnlineDevices <= 0) {
            log.debug("租户[{}]未配置最大设备数限制，跳过检查", tenantId);
            return new ArrayList<>();
        }

        String redisKey = String.format(RedisKeyConstants.ONLINE_DEVICES_KEY, tenantId, userId);
        
        // 1. 检查同设备是否已存在，记录旧token
        String existingDeviceInfo = (String) stringRedisTemplate.opsForHash().get(redisKey, deviceId);
        String oldTokenFromSameDevice = null;
        if (StrUtil.isNotBlank(existingDeviceInfo)) {
            String oldToken = getTokenFromDeviceInfo(existingDeviceInfo);
            if (StrUtil.isNotBlank(oldToken) && !oldToken.equals(newAccessToken)) {
                oldTokenFromSameDevice = oldToken;
            }
        }
        
        // 2. 清理过期设备
        int cleanedCount = cleanExpiredDevices(redisKey);
        if (cleanedCount > 0) {
            log.debug("用户[{}]清理了{}个过期设备（回退）", userId, cleanedCount);
        }

        // 3. 检查设备数限制（排除当前设备）
        Long currentCount = stringRedisTemplate.opsForHash().size(redisKey);

        // 如果当前设备已存在，实际其他设备数 = currentCount - 1
        boolean deviceExists = stringRedisTemplate.opsForHash().hasKey(redisKey, deviceId);
        long otherDeviceCount = deviceExists ? currentCount - 1 : currentCount;

        // 4. 踢出最早的设备直到满足限制（需要为新设备留出位置）
        while (otherDeviceCount >= maxOnlineDevices) {
            String oldestDeviceId = findOldestDevice(redisKey);
            if (StrUtil.isBlank(oldestDeviceId) || oldestDeviceId.equals(deviceId)) {
                break;
            }
            String deviceInfo = (String) stringRedisTemplate.opsForHash().get(redisKey, oldestDeviceId);
            if (StrUtil.isBlank(deviceInfo)) {
                break;
            }
            String oldestToken = getTokenFromDeviceInfo(deviceInfo);
            stringRedisTemplate.opsForHash().delete(redisKey, oldestDeviceId);
            if (StrUtil.isNotBlank(oldestToken)) {
                removedTokens.add(oldestToken);
            }
            otherDeviceCount--;
        }

        // 5. 如果同设备有旧token且不同，加入移除列表
        // 移除同设备互踢，同一用户在同时登录空间（编辑态）和应用（运行态）时，会导致互踢
        // if (StrUtil.isNotBlank(oldTokenFromSameDevice)) {
        //     removedTokens.add(oldTokenFromSameDevice);
        // }

        // 6. 添加新设备
        stringRedisTemplate.opsForHash().put(redisKey, deviceId, buildDeviceInfo(newAccessToken, System.currentTimeMillis()));
        
        return removedTokens;
    }

    @Override
    public void addOnlineDevice(Long userId, String deviceId, String accessToken) {
        Long tenantId = TenantContextHolder.getTenantId();
        String redisKey = String.format(RedisKeyConstants.ONLINE_DEVICES_KEY, tenantId, userId);
        String deviceInfo = buildDeviceInfo(accessToken, System.currentTimeMillis());
        stringRedisTemplate.opsForHash().put(redisKey, deviceId, deviceInfo);
        log.debug("用户[{}]添加在线设备[{}]", userId, deviceId);
    }

    @Override
    public void removeOnlineDevice(Long tenantId, Long userId, String accessToken) {
        // 优先使用TenantContextHolder中的租户ID，为null时使用传入的参数
        Long actualTenantId = TenantContextHolder.getTenantId();
        if (actualTenantId == null) {
            actualTenantId = tenantId;
        }
        String redisKey = String.format(RedisKeyConstants.ONLINE_DEVICES_KEY, actualTenantId, userId);

        // 反查deviceId
        String deviceId = findDeviceIdByTokenInternal(redisKey, accessToken);
        
        if (StrUtil.isNotBlank(deviceId)) {
            stringRedisTemplate.opsForHash().delete(redisKey, deviceId);
            log.debug("清理用户[{}]的在线设备[{}]", userId, deviceId);

            // 如果Hash为空，删除Key
            Long count = stringRedisTemplate.opsForHash().size(redisKey);
            if (count == 0) {
                stringRedisTemplate.delete(redisKey);
                log.debug("用户[{}]的在线设备列表已清空，删除Key", userId);
            }
        } else {
            log.warn("未找到用户[{}]的Token对应的设备ID，无法删除", userId);
        }
    }

    @Override
    public int getOnlineDeviceCount(Long userId) {
        Long tenantId = TenantContextHolder.getTenantId();
        String redisKey = String.format(RedisKeyConstants.ONLINE_DEVICES_KEY, tenantId, userId);

        // 清理过期设备
        cleanExpiredDevices(redisKey);

        // 返回准确的设备数
        Long count = stringRedisTemplate.opsForHash().size(redisKey);
        return count.intValue();
    }

    /**
     * 构建设备信息JSON
     *
     * @param accessToken AccessToken
     * @param loginTime 登录时间戳
     * @return JSON字符串
     */
    private String buildDeviceInfo(String accessToken, long loginTime) {
        Map<String, Object> deviceInfo = new HashMap<>();
        deviceInfo.put(ACCESS_TOKEN_KEY, accessToken);
        deviceInfo.put(LOGIN_TIME_KEY, loginTime);
        try {
            return objectMapper.writeValueAsString(deviceInfo);
        } catch (JsonProcessingException e) {
            log.error("序列化设备信息失败", e);
            return String.format("{\"%s\":\"%s\",\"%s\":%d}", ACCESS_TOKEN_KEY, accessToken, LOGIN_TIME_KEY, loginTime);
        }
    }

    /**
     * 从设备信息JSON中提取Token
     *
     * @param deviceInfo 设备信息JSON字符串
     * @return AccessToken
     */
    private String getTokenFromDeviceInfo(String deviceInfo) {
        if (StrUtil.isBlank(deviceInfo)) {
            return null;
        }
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = objectMapper.readValue(deviceInfo, Map.class);
            return (String) map.get(ACCESS_TOKEN_KEY);
        } catch (JsonProcessingException e) {
            log.error("解析设备信息失败: {}", deviceInfo, e);
            return null;
        }
    }

    /**
     * 从设备信息JSON中提取登录时间
     *
     * @param deviceInfo 设备信息JSON字符串
     * @return 登录时间戳
     */
    private Long getLoginTimeFromDeviceInfo(String deviceInfo) {
        if (StrUtil.isBlank(deviceInfo)) {
            return null;
        }
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = objectMapper.readValue(deviceInfo, Map.class);
            String loginTime = (String)map.get(LOGIN_TIME_KEY);
            return Long.parseLong(loginTime);
        } catch (JsonProcessingException e) {
            log.error("解析设备信息失败: {}", deviceInfo, e);
            return null;
        }
    }

    /**
     * 找到最早登录的设备ID
     *
     * @param redisKey Redis Hash Key
     * @return 最早登录的设备ID
     */
    private String findOldestDevice(String redisKey) {
        Map<Object, Object> allDevices = stringRedisTemplate.opsForHash().entries(redisKey);
        
        if (CollUtil.isEmpty(allDevices)) {
            return null;
        }

        String oldestDeviceId = null;
        Long oldestLoginTime = Long.MAX_VALUE;

        for (Map.Entry<Object, Object> entry : allDevices.entrySet()) {
            String deviceId = (String) entry.getKey();
            String deviceInfo = (String) entry.getValue();
            
            Long loginTime = getLoginTimeFromDeviceInfo(deviceInfo);
            if (loginTime != null && loginTime < oldestLoginTime) {
                oldestLoginTime = loginTime;
                oldestDeviceId = deviceId;
            }
        }

        return oldestDeviceId;
    }

    @Override
    public String findDeviceIdByToken(Long tenantId, Long userId, String accessToken) {
        // 优先使用TenantContextHolder中的租户ID，如果为null则使用传递的参数
        Long actualTenantId = TenantContextHolder.getTenantId();
        if (actualTenantId == null) {
            actualTenantId = tenantId;
        }
        String redisKey = String.format(RedisKeyConstants.ONLINE_DEVICES_KEY, actualTenantId, userId);
        return findDeviceIdByTokenInternal(redisKey, accessToken);
    }

    /**
     * 通过Token反查设备ID（内部方法）
     *
     * @param redisKey Redis Hash Key
     * @param accessToken AccessToken
     * @return 设备ID，未找到返回null
     */
    private String findDeviceIdByTokenInternal(String redisKey, String accessToken) {
        if (StrUtil.isBlank(accessToken)) {
            return null;
        }

        Map<Object, Object> allDevices = stringRedisTemplate.opsForHash().entries(redisKey);
        
        if (CollUtil.isEmpty(allDevices)) {
            return null;
        }

        for (Map.Entry<Object, Object> entry : allDevices.entrySet()) {
            String deviceId = (String) entry.getKey();
            String deviceInfo = (String) entry.getValue();
            
            String token = getTokenFromDeviceInfo(deviceInfo);
            if (accessToken.equals(token)) {
                return deviceId;
            }
        }

        return null;
    }

    /**
     * 清理Redis Hash中的过期设备
     *
     * @param redisKey Redis Hash Key
     * @return 清理的设备数量
     */
    private int cleanExpiredDevices(String redisKey) {
        Map<Object, Object> allDevices = stringRedisTemplate.opsForHash().entries(redisKey);

        if (CollUtil.isEmpty(allDevices)) {
            return 0;
        }

        // 1. 提取所有Token
        List<String> allTokens = new ArrayList<>();
        for (Object deviceInfo : allDevices.values()) {
            String token = getTokenFromDeviceInfo((String) deviceInfo);
            if (StrUtil.isNotBlank(token)) {
                allTokens.add(token);
            }
        }

        if (CollUtil.isEmpty(allTokens)) {
            return 0;
        }

        // 2. 使用Pipeline批量查询Token是否存在
        List<Object> results = stringRedisTemplate.executePipelined(
                new RedisCallback<Object>() {
                    @Override
                    public Object doInRedis(RedisConnection connection) {
                        for (String token : allTokens) {
                            String tokenKey = String.format(RedisKeyConstants.OAUTH2_ACCESS_TOKEN, token);
                            connection.keyCommands().exists(tokenKey.getBytes());
                        }
                        return null;
                    }
                }
        );

        // 3. 找出过期的设备并删除
        List<String> expiredDeviceIds = new ArrayList<>();
        int index = 0;
        for (Map.Entry<Object, Object> entry : allDevices.entrySet()) {
            String deviceId = (String) entry.getKey();
            String deviceInfo = (String) entry.getValue();
            String token = getTokenFromDeviceInfo(deviceInfo);
            if (StrUtil.isNotBlank(token) && index < results.size()) {
                if (Boolean.FALSE.equals(results.get(index))) {
                    expiredDeviceIds.add(deviceId);
                }
                index++;
            }
        }

        if (CollUtil.isNotEmpty(expiredDeviceIds)) {
            stringRedisTemplate.opsForHash().delete(redisKey, expiredDeviceIds.toArray());
        }

        // 4. 如果Hash为空，删除Key
        Long count = stringRedisTemplate.opsForHash().size(redisKey);
        if (count == 0) {
            stringRedisTemplate.delete(redisKey);
        }

        return expiredDeviceIds.size();
    }

    /**
     * 获取租户的最大在线设备数配置
     *
     * @param tenantId 租户ID
     * @return 最大设备数，null表示未配置
     */
    private Integer getMaxOnlineDevices(Long tenantId) {
        return securityConfigService.getIntConfig(tenantId, SecurityConfigKey.maxOnlineDevices.name());
    }
}
