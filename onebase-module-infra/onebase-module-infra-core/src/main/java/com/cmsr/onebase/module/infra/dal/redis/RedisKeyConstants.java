package com.cmsr.onebase.module.infra.dal.redis;

/**
 * Infra Redis Key 常量类
 *
 * @author chengyuansen
 * @date 2025-11-18
 */
public interface RedisKeyConstants {

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
     * OAuth2 访问令牌 Key 的前缀，用于Lua脚本传参（不包含token）
     */
    String OAUTH2_ACCESS_TOKEN_PREFIX = "oauth2_access_token:";
}
