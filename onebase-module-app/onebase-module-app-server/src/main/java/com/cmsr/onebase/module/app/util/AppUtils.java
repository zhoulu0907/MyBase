package com.cmsr.onebase.module.app.util;

import java.util.UUID;

/**
 * @Author：huangjie
 * @Date：2025/8/12 19:12
 */
public class AppUtils {

    public static String createAppCode() {
        return "APP-" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
}
