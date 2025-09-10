package com.cmsr.onebase.module.build.util;

import java.util.UUID;

/**
 * @Author：huangjie
 * @Date：2025/7/30 16:16
 */
public class MenuUtils {

    public static final Long ROOT_MENU_ID = 0L;

    public static final Integer MENU_SORT_MAX_VALUE = 9999;

    public static String generateMenuCode() {
        return "NAV-" + UUID.randomUUID().toString().replace("-", "");
    }
}
