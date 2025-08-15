package com.cmsr.onebase.module.app.util;

import org.apache.commons.lang3.RandomUtils;

import java.util.UUID;

/**
 * @Author：huangjie
 * @Date：2025/8/12 19:12
 */
public class AppUtils {

    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * 返回一个4位的随机数，有小写字母和数字组成，其中第一位必须是小写字母
     *
     * @return
     */
    public static String createAppUid() {
        // 生成一个4位的随机字符串，第一位是小写字母，其余位可以是小写字母或数字
        StringBuilder sb = new StringBuilder();

        // 第一位必须是小写字母
        int firstIndex = RandomUtils.secure().randomInt(0, LETTERS.length());
        sb.append(LETTERS.charAt(firstIndex));

        // 剩余3位可以是小写字母或数字
        for (int i = 0; i < 3; i++) {
            int index = RandomUtils.secure().randomInt(0, CHARS.length());
            sb.append(CHARS.charAt(index));
        }
        return sb.toString();
    }

    public static String createAppCode() {
        return "APP-" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
}
