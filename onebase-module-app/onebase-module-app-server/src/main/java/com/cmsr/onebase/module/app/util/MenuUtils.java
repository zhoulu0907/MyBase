package com.cmsr.onebase.module.app.util;

import java.util.UUID;

/**
 * @Author：huangjie
 * @Date：2025/7/30 16:16
 */
public class MenuUtils {

    public static final String ROOT_MENU_UUID = "NAV-PARENT-UUID";

    public static String createMenuUuid() {
        return "NAV-" + UUID.randomUUID().toString().replace("-", "");
    }
}
