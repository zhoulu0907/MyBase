package com.cmsr.onebase.module.app.util;

import java.util.UUID;

/**
 * @Author：huangjie
 * @Date：2025/7/30 16:16
 */
public class MenuUtils {

    public static final String ROOT_MENU_CODE = "NAV-ROOT";

    public static final Integer MENU_SORT_MAX_VALUE = 9999;

    public static String generateMenuCode() {
        return "NAV-" + UUID.randomUUID().toString().replace("-", "");
    }
}
