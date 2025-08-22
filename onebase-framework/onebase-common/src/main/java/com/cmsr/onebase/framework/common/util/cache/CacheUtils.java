package com.cmsr.onebase.framework.common.util.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.time.Duration;
import java.util.concurrent.Executors;

/**
 * Cache 工具类
 *
 */
@Deprecated
public class CacheUtils {

    /**
     * 废弃
     * 这种包装没有实际意义！刷新的策略很多，需要根据不同情况调整！
     *
     * 构建异步刷新的 LoadingCache 对象
     *
     * 注意：如果你的缓存和 ThreadLocal 有关系，要么自己处理 ThreadLocal 的传递，要么使用 {@link #buildCache(Duration, CacheLoader)} 方法
     *
     * 或者简单理解：
     * 1、和“人”相关的，使用 {@link #buildCache(Duration, CacheLoader)} 方法
     * 2、和“全局”、“系统”相关的，使用当前缓存方法
     *
     * @param duration 过期时间
     * @param loader  CacheLoader 对象
     * @return LoadingCache 对象
     */
    @Deprecated
    public static <K, V> LoadingCache<K, V> buildAsyncReloadingCache(Duration duration, CacheLoader<K, V> loader) {
        return CacheBuilder.newBuilder()
                // 只阻塞当前数据加载线程，其他线程返回旧值
                .refreshAfterWrite(duration)
                // 通过 asyncReloading 实现全异步加载，包括 refreshAfterWrite 被阻塞的加载线程
                .build(CacheLoader.asyncReloading(loader, Executors.newCachedThreadPool())); // TODO 开发者：可能要思考下，未来要不要做成可配置
    }

    /**
     * 废弃
     * 这种包装没有实际意义！刷新的策略很多，需要根据不同情况调整！
     *
     * 构建同步刷新的 LoadingCache 对象
     *
     * @param duration 过期时间
     * @param loader  CacheLoader 对象
     * @return LoadingCache 对象
     */
    @Deprecated
    public static <K, V> LoadingCache<K, V> buildCache(Duration duration, CacheLoader<K, V> loader) {
        return CacheBuilder.newBuilder().refreshAfterWrite(duration).build(loader);
    }

}
