package com.cmsr.onebase.module.infra.service.security;

import java.util.List;

/**
 * 多设备会话管理Service接口
 * 
 * 负责管理用户在线设备数量限制和会话超时控制
 *
 * @author chengyuansen
 * @date 2025-11-18
 */
public interface MultiDeviceSessionService {

    /**
     * 检查并限制设备数
     * 
     * 登录时调用，如果超过maxOnlineDevices，踢出最早登录的设备
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param newAccessToken 新登录的AccessToken
     * @return 被踢出的Token列表（可能为空）
     */
    List<String> checkAndLimitDevices(Long userId, String deviceId, String newAccessToken);

    /**
     * 添加Token到在线设备列表（用于RefreshToken场景）
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param accessToken AccessToken
     */
    void addOnlineDevice(Long userId, String deviceId, String accessToken);

    /**
     * 移除在线设备
     * 
     * 用户登出或Token过期时调用
     *
     * @param tenantId 租户ID（可选，为null时从TenantContextHolder获取）
     * @param userId 用户ID
     * @param accessToken AccessToken
     */
    void removeOnlineDevice(Long tenantId, Long userId, String accessToken);

    /**
     * 获取用户在线设备数
     *
     * @param userId 用户ID
     * @return 在线设备数
     */
    int getOnlineDeviceCount(Long userId);

    /**
     * 通过Token反查设备ID
     * 
     * 用于RefreshToken场景，获取旧Token对应的deviceId
     *
     * @param userId 用户ID
     * @param accessToken AccessToken
     * @return 设备ID，未找到返回null
     */
    String findDeviceIdByToken(Long tenantId, Long userId, String accessToken);
}
