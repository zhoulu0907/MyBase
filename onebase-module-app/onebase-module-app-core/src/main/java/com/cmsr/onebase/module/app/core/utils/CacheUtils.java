package com.cmsr.onebase.module.app.core.utils;

import org.redisson.codec.Kryo5Codec;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/10/28 9:23
 */
public class CacheUtils {

    public static Kryo5Codec KRYO5_CODEC = new Kryo5Codec();

    public static Duration CACHE_TIMEOUT = Duration.of(15, ChronoUnit.MINUTES);

    public static String keyForMenu(Long menuId) {
        return "app:auth:menu:" + menuId;
    }

    public static String keyForOperationPermission(Long userId, Long applicationId, Long menuId) {
        return "app:auth:operation:permission:" + userId + ":" + applicationId + ":" + menuId;
    }

    public static String keyForDataPermission(Long userId, Long applicationId, Long menuId) {
        return "app:auth:data:permission:" + userId + ":" + applicationId + ":" + menuId;
    }

    public static String keyForFieldPermission(Long userId, Long applicationId, Long menuId) {
        return "app:auth:field:permission:" + userId + ":" + applicationId + ":" + menuId;
    }

    public static String keyForPageView(Long userId, Long applicationId, Long menuId) {
        return "app:auth:page:view:" + userId + ":" + applicationId + ":" + menuId;
    }



}
