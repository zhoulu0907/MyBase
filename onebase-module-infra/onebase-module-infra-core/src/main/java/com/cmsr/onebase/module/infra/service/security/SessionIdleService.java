package com.cmsr.onebase.module.infra.service.security;

/**
 * 会话空闲超时管理服务接口
 * 
 * 负责管理用户会话空闲状态，使用Redis记录用户活跃时间
 * 当用户在配置的sessionTimeout时间内无操作时，自动登出
 *
 * @author chengyuansen
 * @date 2025-11-20
 */
public interface SessionIdleService {

    /**
     * 创建会话空闲Redis Key
     * 
     * 用户登录成功后调用，初始化会话空闲检测
     * Redis Key格式：infra:security:user:idle:{tenantId}:{userId}:{deviceId}
     * Value：当前时间戳
     * TTL：从租户配置中读取sessionTimeout值
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     */
    void createRedisIdleKey(Long userId, String deviceId);

    /**
     * 更新会话空闲Redis Key
     * 
     * 用户每次操作时调用（在拦截器或Filter中），更新会话活跃时间和TTL
     * 如果Redis Key不存在，说明会话已超时，返回false
     *
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return true-更新成功，false-会话已过期或不存在
     */
    boolean updateRedisIdleKey(Long tenantId, Long userId, String deviceId);

    /**
     * 检查会话空闲Redis Key是否存在
     * 
     * AccessToken过期使用RefreshToken刷新前调用
     * 如果Redis Key不存在，说明会话已超时，不允许刷新Token
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return true-会话有效，false-会话已过期
     */
    boolean existRedisIdleKey(Long userId, String deviceId);
}
