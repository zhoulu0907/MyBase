package com.cmsr.onebase.module.infra.dal.redis;

/**
 * Infra Redis Key 常量类
 *
 * @author chengyuansen
 * @date 2025-11-18
 */
public interface RedisKeyConstants {

    /**
     * Key格式：infra:security:tenant-config#30m::{tenantId}
     * Value数据结构：json对象
     * TTL：30 分钟（固定）
     * 用途：以租户为单位缓存安全配置列表
     */
    String SECURITY_TENANT_CONFIGS = "infra:security:tenant-config#30m";
    /**
     * Key格式：infra:security:login:fail:{tenantId}:{userId}
     * Value数据结构：String（失败计数）
     * TTL：来自租户安全配置 lockDuration（分钟）
     * 用途：记录租户下某用户的连续登录失败次数
     */
    String REDIS_KEY_FAIL_COUNT = "infra:security:login:fail:";

    /**
     * Key格式：infra:security:login:fail:{tenantId}:{userId}:sharedlock
     * Value数据结构：String（锁标记）
     * TTL：5 秒（固定）
     * 用途：登录失败计数的短期分布式锁，串行化并发写入
     */
    String REDIS_KEY_FAIL_SHARED_LOCK = "infra:security:login:fail:%d:%d:sharedlock";

    /**
     * Key格式：infra:security:login:lock:{tenantId}:{userId}
     * Value数据结构：String（锁定时间戳）
     * TTL：自动解锁时=来自租户安全配置 lockDuration（分钟），手动解锁时=无
     * 用途：标记账号是否已被防暴力破解策略锁定
     */
    String REDIS_KEY_LOCK = "infra:security:login:lock:";

    /**
     * Key格式：infra:security:online:devices:{tenantId}:{userId}
     * Value数据结构：Hash（key=deviceId，value=JSON(accessToken, loginTime)）
     * TTL：未设置TTL。
     * 用途：保存用户的在线设备列表，用于多端会话管控
     */
    String ONLINE_DEVICES_KEY = "infra:security:online:devices:%d:%d";

    /**
     * Key格式：oauth2_access_token:{token}
     * Value数据结构：json对象
     * TTL：来自 OAuth2令牌有效期；
     * 用途：保存 accessToken数据。该Key并非在本包内创建而是system模块创建，本包内仅用于读取。
     */
    String OAUTH2_ACCESS_TOKEN = "oauth2_access_token:%s";

    /**
     * Key格式：infra:security:user:idle:{tenantId}:{userId}:{deviceId}
     * Value数据结构：String（最后活跃时间戳）
     * TTL：来自租户安全配置 sessionTimeout（秒）
     * 用途：会话空闲检测，强制超时登出
     */
    String USER_IDLE_KEY = "infra:security:user:idle:%d:%d:%s";
}
