package com.cmsr.onebase.module.app.core.utils;

import org.redisson.codec.Kryo5Codec;

import java.util.concurrent.TimeUnit;

/**
 * @Author：huangjie
 * @Date：2025/10/28 9:23
 */
public class CacheUtils {

    public static Kryo5Codec KRYO5_CODEC = new Kryo5Codec();

    public static Long CACHE_TTL = 60L;
    public static TimeUnit CACHE_TTL_UNIT = TimeUnit.MINUTES;

    public static String authHashKey(Long userId, Long applicationId) {
        return "app:auth:" + userId + ":" + applicationId;
    }

    public static String fieldForOperation(Long menuId) {
        return "opt:" + menuId;
    }

    public static String fieldForData(Long menuId) {
        return "data:" + menuId;
    }

    public static String fieldForField(Long menuId) {
        return "field:" + menuId;
    }

    public static String fieldForVisibleMenuIds() {
        return "visibleMenuIds";
    }

    public static String fieldForUserRole() {
        return "userRole";
    }

    public static String fieldForView(Long menuId) {
        return "view:" + menuId;
    }
}
