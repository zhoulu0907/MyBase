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

    public static Duration CACHE_TIMEOUT = Duration.of(15, ChronoUnit.MINUTES);

    /**
     * 用户角色缓存key  applicationId, userId
     */
    public static String REDIS_USER_ROLE_KEY = "app:auth:user:role:%s:%s";


    /**
     * 菜单缓存key  menuId
     */
    public static String REDIS_MENU_KEY = "app:menu:%s";


}
