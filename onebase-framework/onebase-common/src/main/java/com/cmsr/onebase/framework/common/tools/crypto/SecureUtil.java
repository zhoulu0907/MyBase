package com.cmsr.onebase.framework.common.tools.crypto;


import com.cmsr.onebase.framework.common.tools.core.codec.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * HMAC-SHA256签名工具类
 *
 * @author yx6231
 * @date 2025-07-25
 */
public class SecureUtil {

    /**
     * 计算字符串的HMAC-SHA256签名并返回十六进制字符串
     *
     * @param key 密钥
     * @param data 待签名数据
     * @return 签名结果的十六进制字符串
     * @throws Exception 签名异常
     */
    public static String hmacSha256Hex(String key, String data) {
       try {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC-SHA256", e);
        }
    }

    /**
     * 字节数组转十六进制字符串
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    /**
     * 计算字符串的HMAC-SHA1签名并返回Base64编码字符串
     *
     * @param key 密钥
     * @param data 待签名数据
     * @return 签名结果的Base64字符串
     * @throws Exception 签名异常
     */
    public static String hmacSha1Base64(String key, String data) {
        try {
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return java.util.Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC-SHA1 signature", e);
        }
    }

    /**
     * 使用原生 Java 实现 HMAC-SHA256 并返回 Base64 编码结果
     *
     * @param data 待签名数据
     * @param key 密钥
     * @return Base64 编码的签名结果
     */
    public static String hmacSha256Base64(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.encode(rawHmac); // 使用项目中已有的 Base64 工具类
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC-SHA256 signature", e);
        }
    }

}
