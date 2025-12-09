package com.cmsr.onebase.plugin.service;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 缓存服务
 * <p>
 * 提供Redis缓存操作能力。
 * 缓存键会自动添加插件ID前缀，避免不同插件间的键冲突。
 * </p>
 *
 * @author matianyu
 * @date 2025-11-29
 */
public interface CacheService {

    // ==================== 基础操作 ====================

    /**
     * 获取缓存值
     *
     * @param key 缓存键
     * @param <T> 值类型
     * @return 缓存值，不存在返回null
     */
    <T> T get(String key);

    /**
     * 获取缓存值，不存在时使用默认值
     *
     * @param key          缓存键
     * @param defaultValue 默认值
     * @param <T>          值类型
     * @return 缓存值或默认值
     */
    <T> T get(String key, T defaultValue);

    /**
     * 设置缓存值
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    void set(String key, Object value);

    /**
     * 设置缓存值（带过期时间）
     *
     * @param key      缓存键
     * @param value    缓存值
     * @param duration 过期时间
     */
    void set(String key, Object value, Duration duration);

    /**
     * 设置缓存值（带过期时间，秒）
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param seconds 过期时间（秒）
     */
    void set(String key, Object value, long seconds);

    /**
     * 如果不存在则设置
     *
     * @param key   缓存键
     * @param value 缓存值
     * @return true表示设置成功
     */
    boolean setIfAbsent(String key, Object value);

    /**
     * 如果不存在则设置（带过期时间）
     *
     * @param key      缓存键
     * @param value    缓存值
     * @param duration 过期时间
     * @return true表示设置成功
     */
    boolean setIfAbsent(String key, Object value, Duration duration);

    /**
     * 删除缓存
     *
     * @param key 缓存键
     * @return true表示删除成功
     */
    boolean delete(String key);

    /**
     * 批量删除缓存
     *
     * @param keys 缓存键集合
     * @return 删除的数量
     */
    long delete(Collection<String> keys);

    /**
     * 检查缓存是否存在
     *
     * @param key 缓存键
     * @return true表示存在
     */
    boolean exists(String key);

    /**
     * 设置过期时间
     *
     * @param key      缓存键
     * @param duration 过期时间
     * @return true表示设置成功
     */
    boolean expire(String key, Duration duration);

    /**
     * 获取剩余过期时间
     *
     * @param key 缓存键
     * @return 剩余时间（秒），-1表示永不过期，-2表示键不存在
     */
    long getExpire(String key);

    // ==================== 计数器 ====================

    /**
     * 递增
     *
     * @param key 缓存键
     * @return 递增后的值
     */
    long increment(String key);

    /**
     * 递增指定值
     *
     * @param key   缓存键
     * @param delta 增量
     * @return 递增后的值
     */
    long increment(String key, long delta);

    /**
     * 递减
     *
     * @param key 缓存键
     * @return 递减后的值
     */
    long decrement(String key);

    /**
     * 递减指定值
     *
     * @param key   缓存键
     * @param delta 减量
     * @return 递减后的值
     */
    long decrement(String key, long delta);

    // ==================== Hash操作 ====================

    /**
     * 获取Hash字段值
     *
     * @param key   缓存键
     * @param field 字段名
     * @param <T>   值类型
     * @return 字段值
     */
    <T> T hashGet(String key, String field);

    /**
     * 获取多个Hash字段值
     *
     * @param key    缓存键
     * @param fields 字段名列表
     * @return 字段值列表
     */
    List<Object> hashMultiGet(String key, Collection<String> fields);

    /**
     * 获取所有Hash字段
     *
     * @param key 缓存键
     * @return 字段Map
     */
    Map<String, Object> hashGetAll(String key);

    /**
     * 设置Hash字段值
     *
     * @param key   缓存键
     * @param field 字段名
     * @param value 字段值
     */
    void hashSet(String key, String field, Object value);

    /**
     * 批量设置Hash字段值
     *
     * @param key 缓存键
     * @param map 字段Map
     */
    void hashSetAll(String key, Map<String, Object> map);

    /**
     * 删除Hash字段
     *
     * @param key    缓存键
     * @param fields 字段名
     * @return 删除的数量
     */
    long hashDelete(String key, String... fields);

    /**
     * 检查Hash字段是否存在
     *
     * @param key   缓存键
     * @param field 字段名
     * @return true表示存在
     */
    boolean hashExists(String key, String field);

    // ==================== List操作 ====================

    /**
     * 从左侧推入元素
     *
     * @param key   缓存键
     * @param value 元素值
     * @return 列表长度
     */
    long listLeftPush(String key, Object value);

    /**
     * 从右侧推入元素
     *
     * @param key   缓存键
     * @param value 元素值
     * @return 列表长度
     */
    long listRightPush(String key, Object value);

    /**
     * 从左侧弹出元素
     *
     * @param key 缓存键
     * @param <T> 元素类型
     * @return 元素值
     */
    <T> T listLeftPop(String key);

    /**
     * 从右侧弹出元素
     *
     * @param key 缓存键
     * @param <T> 元素类型
     * @return 元素值
     */
    <T> T listRightPop(String key);

    /**
     * 获取列表范围
     *
     * @param key   缓存键
     * @param start 起始索引
     * @param end   结束索引
     * @return 元素列表
     */
    List<Object> listRange(String key, long start, long end);

    /**
     * 获取列表长度
     *
     * @param key 缓存键
     * @return 列表长度
     */
    long listSize(String key);

    // ==================== Set操作 ====================

    /**
     * 添加Set元素
     *
     * @param key    缓存键
     * @param values 元素值
     * @return 添加的数量
     */
    long setAdd(String key, Object... values);

    /**
     * 移除Set元素
     *
     * @param key    缓存键
     * @param values 元素值
     * @return 移除的数量
     */
    long setRemove(String key, Object... values);

    /**
     * 检查Set是否包含元素
     *
     * @param key   缓存键
     * @param value 元素值
     * @return true表示包含
     */
    boolean setIsMember(String key, Object value);

    /**
     * 获取Set所有元素
     *
     * @param key 缓存键
     * @return 元素集合
     */
    Set<Object> setMembers(String key);

    /**
     * 获取Set大小
     *
     * @param key 缓存键
     * @return Set大小
     */
    long setSize(String key);
}
