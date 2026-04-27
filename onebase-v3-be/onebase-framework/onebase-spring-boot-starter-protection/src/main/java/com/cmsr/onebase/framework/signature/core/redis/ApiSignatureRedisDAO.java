package com.cmsr.onebase.framework.signature.core.redis;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * HTTP API 签名 Redis DAO
 *
 * @author Zhougang
 */
@AllArgsConstructor
public class ApiSignatureRedisDAO {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 验签随机数
     * <p>
     * KEY 格式：signature_nonce:%s // 参数为 随机数
     * VALUE 格式：String
     * 过期时间：不固定
     */
    private static final String SIGNATURE_NONCE = "api_signature_nonce:%s:%s";

    /**
     * 签名密钥
     * <p>
     * HASH 结构
     * KEY 格式：%s // 参数为 appid
     * VALUE 格式：String
     * 过期时间：永不过期（预加载到 Redis）
     */
    private static final String SIGNATURE_APPID = "api_signature_app";

    private static final String SIGNATURE_ENABLE = "system:security:api-sign-enable";

    private static final String SIGNATURE_IP_WHITE_LIST = "system:security:api-sign-ip-whitelist";

    public boolean isApiSignEnabled() {
        // 从redis中读取配置
        // stringRedisTemplate.opsForValue().set(SIGNATURE_ENABLE, "true");
        String enable = stringRedisTemplate.opsForValue().get(SIGNATURE_ENABLE);
        return !"false".equalsIgnoreCase(enable);
    }


    /**
     * 获取IP白名单,英文逗号分割
     * @return
     */
    public String getIpWhiteList() {
        return stringRedisTemplate.opsForValue().get(SIGNATURE_IP_WHITE_LIST);
    }

    /**
     * 设置IP白名单
     * @param ipWhiteList 英文逗号分割
     * @return
     */
    public Boolean setIpWhiteList(String ipWhiteList) {
        return stringRedisTemplate.opsForValue().setIfAbsent(SIGNATURE_IP_WHITE_LIST, ipWhiteList);
    }

    // ========== 验签随机数 ==========

    public String getNonce(String appId, String nonce) {
        return stringRedisTemplate.opsForValue().get(formatNonceKey(appId, nonce));
    }

    public Boolean setNonce(String appId, String nonce, int time, TimeUnit timeUnit) {
        return stringRedisTemplate.opsForValue().setIfAbsent(formatNonceKey(appId, nonce), "onebase", time, timeUnit);
    }

    private static String formatNonceKey(String appId, String nonce) {
        return String.format(SIGNATURE_NONCE, appId, nonce);
    }

    // ========== 签名密钥 ==========

    public String getAppSecret(String appId) {
        return (String) stringRedisTemplate.opsForHash().get(SIGNATURE_APPID, appId);
    }

}
