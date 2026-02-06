package com.cmsr.onebase.module.system.util.oauth2;

import org.apache.commons.lang3.RandomUtils;
import java.util.UUID;

/**
 * OAuth2 客户端凭证生成工具类
 *
 * @author lingma
 * @date 2026-02-04
 */
public class OAuth2ClientUtils {

    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String SECRET_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+-=[]{}|;:,.<>?";

    /**
     * 生成8位客户端ID
     * 规则：
     * - 8位长度
     * - 首位必须是字母（大小写均可）
     * - 其余7位可以是字母或数字
     *
     * @return 8位客户端ID
     */
    public static String generateClientId() {
        StringBuilder sb = new StringBuilder();

        // 第一位必须是字母
        int firstIndex = RandomUtils.secure().randomInt(0, LETTERS.length());
        sb.append(LETTERS.charAt(firstIndex));

        // 剩余7位可以是字母或数字
        for (int i = 0; i < 7; i++) {
            int index = RandomUtils.secure().randomInt(0, CHARS.length());
            sb.append(CHARS.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 生成客户端密钥
     * 规则：
     * - 使用UUID生成32位不带横杠的字符串
     * - 确保唯一性和安全性
     *
     * @return 32位客户端密钥（UUID去除横杠）
     */
    public static String generateClientSecret() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 打乱字符串字符顺序
     *
     * @param input 输入字符串
     * @return 打乱后的字符串
     */
    private static String shuffleString(String input) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = RandomUtils.secure().randomInt(0, i + 1);
            // 交换字符
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
}