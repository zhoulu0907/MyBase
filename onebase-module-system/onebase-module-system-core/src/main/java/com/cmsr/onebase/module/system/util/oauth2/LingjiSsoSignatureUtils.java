package com.cmsr.onebase.module.system.util.oauth2;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.TreeMap;

/**
 * 灵畿平台 SSO 签名工具类
 *
 * 签名规则：
 * 1. 提取 head 和 data 中的非空参数（排除 sign 字段）
 * 2. 使用 TreeMap 自然排序
 * 3. 拼接参数值字符串（按排序顺序）
 * 4. 追加 sourceKey + timestamp后5位
 * 5. SM3 签名后 Base64 编码
 */
@Slf4j
public class LingjiSsoSignatureUtils {

    private LingjiSsoSignatureUtils() {
    }

    /**
     * 生成灵畿 SSO 签名
     *
     * @param headParams  head 中的参数
     * @param dataParams  data 中的参数
     * @param sourceKey   系统密钥
     * @param timestamp   时间戳
     * @return Base64 编码的签名
     */
    public static String generateSignature(Map<String, String> headParams,
                                           Map<String, String> dataParams,
                                           String sourceKey,
                                           String timestamp) {
        // 1. 合并所有参数到 TreeMap（自动排序）
        TreeMap<String, String> sortedParams = new TreeMap<>();

        // 添加 head 参数（排除 sign）
        if (headParams != null) {
            for (Map.Entry<String, String> entry : headParams.entrySet()) {
                if (!"sign".equals(entry.getKey()) && StrUtil.isNotBlank(entry.getValue())) {
                    sortedParams.put(entry.getKey(), entry.getValue());
                }
            }
        }

        // 添加 data 参数
        if (dataParams != null) {
            for (Map.Entry<String, String> entry : dataParams.entrySet()) {
                if (StrUtil.isNotBlank(entry.getValue())) {
                    sortedParams.put(entry.getKey(), entry.getValue());
                }
            }
        }

        // 2. 拼接参数值字符串
        StringBuilder paramValueBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            paramValueBuilder.append(entry.getValue());
        }

        // 3. 获取时间戳后5位
        String timestampSuffix = getTimestampSuffix(timestamp);

        // 4. 拼接最终字符串：参数值 + sourceKey + timestamp后5位
        String signStr = paramValueBuilder.toString() + sourceKey + timestampSuffix;

        log.debug("签名原文: {}", signStr);

        // 5. SM3 签名
        String sm3Hex = SmUtil.sm3(signStr);

        // 6. Base64 编码
        String signature = Base64.encode(sm3Hex);

        log.debug("签名结果: {}", signature);

        return signature;
    }

    /**
     * 获取时间戳后5位
     *
     * @param timestamp 时间戳字符串
     * @return 时间戳后5位
     */
    private static String getTimestampSuffix(String timestamp) {
        if (StrUtil.isBlank(timestamp)) {
            return "";
        }
        if (timestamp.length() <= 5) {
            return timestamp;
        }
        return timestamp.substring(timestamp.length() - 5);
    }

    /**
     * 生成时间戳 (格式: yyyyMMddHHmmssSSS)
     */
    public static String generateTimestamp() {
        return java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    /**
     * 生成 requestId
     */
    public static String generateRequestId() {
        return cn.hutool.core.util.IdUtil.fastSimpleUUID();
    }
}