package com.cmsr.onebase.module.app.core.utils;

import org.redisson.codec.Kryo5Codec;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * @Author：huangjie
 * @Date：2025/10/28 9:23
 */
public class CacheUtils {

    public static Kryo5Codec KRYO5_CODEC = new Kryo5Codec();

    //TODO 调试阶段把时间设置都很短
    public static Duration CACHE_EXPIRE_TIME = Duration.of(5, ChronoUnit.SECONDS);

    public static String keyForOperationPermission(Long userId, Long applicationId, Long menuId) {
        return "app:auth:operation:permission:" + userId + ":" + applicationId + ":" + menuId;
    }

    public static String keyForDataPermission(Long userId, Long applicationId, Long menuId) {
        return "app:auth:data:permission:" + userId + ":" + applicationId + ":" + menuId;
    }

    public static String keyForFieldPermission(Long userId, Long applicationId, Long menuId) {
        return "app:auth:field:permission:" + userId + ":" + applicationId + ":" + menuId;
    }

    public static String keyForPagePermission(Long userId, Long applicationId, Long menuId) {
        return "app:auth:page:permission:" + userId + ":" + applicationId + ":" + menuId;
    }

}
