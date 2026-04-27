package com.cmsr.onebase.plugin.ocr.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * OCR 签名工具类 (简化版)
 */
public class OcrSignUtil {

    /**
     * 计算阿里云 RPC 签名 (HMAC-SHA1)
     */
    public static String signAliyunRpc(Map<String, String> params, String accessKeySecret) throws Exception {
        String[] sortedKeys = params.keySet().toArray(new String[]{});
        Arrays.sort(sortedKeys);

        StringBuilder canonicalizedQueryString = new StringBuilder();
        for (String key : sortedKeys) {
            canonicalizedQueryString.append("&")
                    .append(percentEncode(key)).append("=")
                    .append(percentEncode(params.get(key)));
        }

        String stringToSign = "POST" + "&" +
                percentEncode("/") + "&" +
                percentEncode(canonicalizedQueryString.toString().substring(1));

        String key = accessKeySecret + "&";
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signData);
    }

    private static String percentEncode(String value) throws Exception {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.name())
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("%7E", "~");
    }

    /**
     * 计算腾讯云 v3 签名 (TC3-HMAC-SHA256)
     * 简化实现，仅支持 POST JSON 请求
     */
    public static String signTencentV3(String secretId, String secretKey, String service, String host, String action, String version, String payload, long timestamp) throws Exception {
        // 1. 拼接规范请求串
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String date = sdf.format(new Date(timestamp * 1000));

        String canonicalRequest = "POST\n" +
                "/\n" +
                "\n" +
                "content-type:application/json\n" +
                "host:" + host + "\n" +
                "\n" +
                "content-type;host\n" +
                sha256Hex(payload);

        // 2. 拼接待签名字符串
        String credentialScope = date + "/" + service + "/" + "tc3_request";
        String stringToSign = "TC3-HMAC-SHA256\n" +
                timestamp + "\n" +
                credentialScope + "\n" +
                sha256Hex(canonicalRequest);

        // 3. 计算签名
        byte[] secretDate = hmac256(("TC3" + secretKey).getBytes(StandardCharsets.UTF_8), date);
        byte[] secretService = hmac256(secretDate, service);
        byte[] secretSigning = hmac256(secretService, "tc3_request");
        String signature = bytesToHex(hmac256(secretSigning, stringToSign));

        // 4. 拼接 Authorization
        return "TC3-HMAC-SHA256 " +
                "Credential=" + secretId + "/" + credentialScope + ", " +
                "SignedHeaders=content-type;host, " +
                "Signature=" + signature;
    }

    private static byte[] hmac256(byte[] key, String msg) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(msg.getBytes(StandardCharsets.UTF_8));
    }

    private static String sha256Hex(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(d);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
        return sb.toString();
    }
}
