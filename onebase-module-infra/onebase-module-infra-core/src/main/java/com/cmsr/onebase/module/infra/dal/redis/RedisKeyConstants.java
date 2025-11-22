package com.cmsr.onebase.module.infra.dal.redis;

/**
 * Infra Redis Key 常量类
 *
 * @author chengyuansen
 * @date 2025-11-18
 */
public interface RedisKeyConstants {

    String SECURITY_TENANT_CONFIGS = "infra:security:tenant-config#30m";
    /**
     * Redis Key前缀 - 失败次数记录
     */
    String REDIS_KEY_FAIL_COUNT = "infra:security:login:fail:";

    /**
     * Redis Key前缀 - 锁定状态记录
     * 设置过期时间，过期自动删除(自动解锁）
     */
    String REDIS_KEY_LOCK = "infra:security:login:lock:";

    /**
     * 在线设备列表的缓存
     * <p>
     * KEY 格式：infra:security:online:devices:{tenantId}:{userId}
     * VALUE 数据类型：Hash
     * <ul>
     *   <li>Field: deviceId（设备ID）</li>
     *   <li>Value: JSON字符串，包含 accessToken 和 loginTime</li>
     *   <li>示例：{"accessToken":"xxx","loginTime":1234567890}</li>
     * </ul>
     * <p>
     * 用于限制用户最大同时在线设备数，支持同设备重新登录覆盖旧Token
     */
    String ONLINE_DEVICES_KEY = "infra:security:online:devices:%d:%d";

    /**
     * OAuth2访问令牌的缓存Key格式（引用system模块）
     * <p>
     * KEY 格式：oauth2_access_token:{token}
     * VALUE 数据类型：String 访问令牌信息
     * <p>
     * 注意：此常量与system模块的RedisKeyConstants.OAUTH2_ACCESS_TOKEN保持一致
     */
    String OAUTH2_ACCESS_TOKEN = "oauth2_access_token:%s";

    /**
     * 用户会话空闲检测的缓存
     * <p>
     * KEY 格式：infra:security:user:idle:{tenantId}:{userId}:{deviceId}
     * VALUE 数据类型：String 最后活跃时间戳
     * TTL：从租户配置中读取sessionTimeout值（秒）
     * <p>
     * 用于实现会话超时自动登出功能：
     * <ul>
     *   <li>用户登录时创建此Key，TTL为配置的超时时间</li>
     *   <li>用户每次操作时更新此Key的TTL和值</li>
     *   <li>如果Key过期，说明用户超过设定时间未操作，需要重新登录</li>
     * </ul>
     */
    String USER_IDLE_KEY = "infra:security:user:idle:%d:%d:%s";
}
