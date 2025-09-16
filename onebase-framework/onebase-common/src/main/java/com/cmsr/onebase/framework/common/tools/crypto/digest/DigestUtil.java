package com.cmsr.onebase.framework.common.tools.crypto.digest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestUtil {

    /**
     * 使用 JDK MessageDigest 计算字符串的 SHA-256 十六进制表示
     *
     * @param data 待计算的字符串
     * @return SHA-256 的十六进制小写字符串
     */
    public static String sha256Hex(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("无法计算 SHA-256 摘要", e);
        }
    }

    public static byte[] sha256(byte[] content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(content);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("无法获取 SHA-256 算法实例", e);
        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

}
