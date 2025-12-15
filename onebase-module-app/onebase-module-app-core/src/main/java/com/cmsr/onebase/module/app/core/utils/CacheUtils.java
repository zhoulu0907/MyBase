package com.cmsr.onebase.module.app.core.utils;

import org.redisson.codec.Kryo5Codec;

import java.util.concurrent.TimeUnit;

/**
 * @Author：huangjie
 * @Date：2025/10/28 9:23
 */
public class CacheUtils {

    public static Kryo5Codec KRYO5_CODEC = new Kryo5Codec();

    //TODO 调试阶段把时间设置都很短
    public static Long CACHE_TTL = 60L;
    public static TimeUnit CACHE_TTL_UNIT =TimeUnit.MINUTES;

    public static String keyForAuth(Long userId, Long applicationId) {
        return "app:auth:" + userId + ":" + applicationId;
    }

    public static String keyForHasPerm( ) {
        return "hasPermission";
    }


    public static String keyForOperation(Long menuId) {
        return "opt:" + menuId;
    }

    public static String keyForData(Long menuId) {
        return  "data:" + menuId;
    }

    public static String keyForField(Long menuId) {
        return "field:" + menuId;
    }

    public static String keyForVisibleMenuIds() {
            return "visibleMenuIds";
    }
}
