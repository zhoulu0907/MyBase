package com.cmsr.onebase.framework.common.tools.core.map;

import com.cmsr.onebase.framework.common.tools.core.exceptions.UtilException;
import com.cmsr.onebase.framework.common.tools.core.lang.Editor;
import com.cmsr.onebase.framework.common.tools.core.lang.Filter;
import com.cmsr.onebase.framework.common.tools.core.util.ArrayUtil;
import com.cmsr.onebase.framework.common.tools.core.util.ReflectUtil;
import com.cmsr.onebase.framework.common.tools.core.util.StrUtil;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.*;
import java.util.function.Function;

/**
 * Obj工具类
 */
public class MapUtil {

    private static final ConversionService conversionService = new DefaultConversionService();

    /**
     * 默认增长因子，当Map的size达到 容量*增长因子时，开始扩充Map
     */
    public static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * 默认初始大小
     */
    public static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * 将对应Map转换为不可修改的Map
     *
     * @param map Map
     * @param <K> 键类型
     * @param <V> 值类型
     * @return 不修改Map
     * @since 5.2.6
     */
    public static <K, V> Map<K, V> unmodifiable(Map<K, V> map) {
        return Collections.unmodifiableMap(map);
    }

    /**
     * 创建Map<br>
     * 传入抽象Map{@link AbstractMap}和{@link Map}类将默认创建{@link HashMap}
     *
     * @param <K>     map键类型
     * @param <V>     map值类型
     * @param mapType map类型
     * @return {@link Map}实例
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> createMap(Class<?> mapType) {
        if (null == mapType || mapType.isAssignableFrom(AbstractMap.class)) {
            return new HashMap<>();
        } else {
            try {
                return (Map<K, V>) ReflectUtil.newInstance(mapType);
            } catch (UtilException e) {
                // 不支持的map类型，返回默认的HashMap
                return new HashMap<>();
            }
        }
    }

    /**
     * 获取Map的部分key生成新的Map
     *
     * @param <K>  Key类型
     * @param <V>  Value类型
     * @param map  Map
     * @param keys 键列表
     * @return 新Map，只包含指定的key
     * @since 4.0.6
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> getAny(Map<K, V> map, final K... keys) {
        return filter(map, entry -> ArrayUtil.contains(keys, entry.getKey()));
    }

    /**
     * 过滤<br>
     * 过滤过程通过传入的Editor实现来返回需要的元素内容，这个Filter实现可以实现以下功能：
     *
     * <pre>
     * 1、过滤出需要的对象，如果返回null表示这个元素对象抛弃
     * </pre>
     *
     * @param <K>    Key类型
     * @param <V>    Value类型
     * @param map    Map
     * @param filter 过滤器接口，{@code null}返回原Map
     * @return 过滤后的Map
     * @since 3.1.0
     */
    public static <K, V> Map<K, V> filter(Map<K, V> map, Filter<Map.Entry<K, V>> filter) {
        if (null == map || null == filter) {
            return map;
        }
        return edit(map, t -> filter.accept(t) ? t : null);
    }

    /**
     * 编辑Map<br>
     * 编辑过程通过传入的Editor实现来返回需要的元素内容，这个Editor实现可以实现以下功能：
     *
     * <pre>
     * 1、过滤出需要的对象，如果返回{@code null}表示这个元素对象抛弃
     * 2、修改元素对象，返回集合中为修改后的对象
     * </pre>
     *
     * @param <K>    Key类型
     * @param <V>    Value类型
     * @param map    Map
     * @param editor 编辑器接口
     * @return 编辑后的Map
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> edit(Map<K, V> map, Editor<Map.Entry<K, V>> editor) {
        if (null == map || null == editor) {
            return map;
        }

        Map<K, V> map2 = ReflectUtil.newInstanceIfPossible(map.getClass());
        if (null == map2) {
            map2 = new HashMap<>(map.size(), 1f);
        }
        if (isEmpty(map)) {
            return map2;
        }

        // issue#3162@Github，在构造中put值，会导致新建map带有值内容，此处清空
        if (false == map2.isEmpty()) {
            map2.clear();
        }

        Map.Entry<K, V> modified;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            modified = editor.edit(entry);
            if (null != modified) {
                map2.put(modified.getKey(), modified.getValue());
            }
        }
        return map2;
    }

    /**
     * 获取Map指定key的值，并转换为Integer
     *
     * @param map Map
     * @param key 键
     * @return 值
     * @since 4.0.6
     */
    public static Integer getInt(Map<?, ?> map, Object key) {
        return get(map, key, Integer.class);
    }

    /**
     * 获取Map指定key的值，并转换为字符串
     *
     * @param map Map
     * @param key 键
     * @return 值
     * @since 4.0.6
     */
    public static String getStr(Map<?, ?> map, Object key) {
        return get(map, key, String.class);
    }


    /**
     * 获取Map指定key的值，并转换为Long
     *
     * @param map Map
     * @param key 键
     * @return 值
     * @since 4.0.6
     */
    public static Long getLong(Map<?, ?> map, Object key) {
        return get(map, key, Long.class);
    }


    /**
     * 获取Map指定key的值，并转换为指定类型
     *
     * @param <T>  目标值类型
     * @param map  Map
     * @param key  键
     * @param type 值类型
     * @return 值
     * @since 4.0.6
     */
    public static <T> T get(Map<?, ?> map, Object key, Class<T> type) {
        return get(map, key, type, null);
    }

    /**
     * 获取Map指定key的值，并转换为指定类型
     *
     * @param <T>          目标值类型
     * @param map          Map
     * @param key          键
     * @param type         值类型
     * @param defaultValue 默认值
     * @return 值
     * @since 5.3.11
     */
    public static <T> T get(Map<?, ?> map, Object key, Class<T> type, T defaultValue) {
        return null == map ? defaultValue : convertValue(map.get(key),type, defaultValue);
    }

    /**
     * 使用Spring ConversionService进行类型转换
     *
     * @param value 原始值
     * @param type 目标类型
     * @param defaultValue 默认值
     * @param <T> 目标类型
     * @return 转换后的值
     */
    private static <T> T convertValue(Object value, Class<T> type, T defaultValue) {
        try {
            // 如果类型已经匹配，直接返回
            if (type.isInstance(value)) {
                return type.cast(value);
            }

            // 使用Spring的ConversionService进行转换
            if (conversionService.canConvert(value.getClass(), type)) {
                return conversionService.convert(value, type);
            }

            // 特殊处理：对于JSON字符串转换为对象
            if (value instanceof String && !type.equals(String.class)) {
                try {
                    return JsonUtils.parseObject((String) value, type);
                } catch (Exception e) {
                    // JSON解析失败，继续使用其他方式
                }
            }

            // 最后尝试使用toString()方法再转换
            if (conversionService.canConvert(String.class, type)) {
                return conversionService.convert(value.toString(), type);
            }

            return defaultValue;
        } catch (Exception e) {
            // 转换失败，返回默认值
            return defaultValue;
        }
    }

    /**
     * 新建一个HashMap
     *
     * @param <K>      Key类型
     * @param <V>      Value类型
     * @param size     初始大小，由于默认负载因子0.75，传入的size会实际初始大小为size / 0.75 + 1
     * @param isLinked Map的Key是否有序，有序返回 {@link LinkedHashMap}，否则返回 {@link HashMap}
     * @return HashMap对象
     * @since 3.0.4
     */
    public static <K, V> HashMap<K, V> newHashMap(int size, boolean isLinked) {
        final int initialCapacity = (int) (size / DEFAULT_LOAD_FACTOR) + 1;
        return isLinked ? new LinkedHashMap<>(initialCapacity) : new HashMap<>(initialCapacity);
    }

    /**
     * 新建一个HashMap
     *
     * @param <K> Key类型
     * @param <V> Value类型
     * @return HashMap对象
     */
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>();
    }

    /**
     * 新建一个HashMap
     *
     * @param <K>      Key类型
     * @param <V>      Value类型
     * @param isLinked Map的Key是否有序，有序返回 {@link LinkedHashMap}，否则返回 {@link HashMap}
     * @return HashMap对象
     */
    public static <K, V> HashMap<K, V> newHashMap(boolean isLinked) {
        return newHashMap(DEFAULT_INITIAL_CAPACITY, isLinked);
    }

    /**
     * 新建一个HashMap
     *
     * @param <K>  Key类型
     * @param <V>  Value类型
     * @param size 初始大小，由于默认负载因子0.75，传入的size会实际初始大小为size / 0.75 + 1
     * @return HashMap对象
     */
    public static <K, V> HashMap<K, V> newHashMap(int size) {
        return newHashMap(size, false);
    }

    /**
     * Map是否为非空
     *
     * @param map 集合
     * @return 是否为非空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return null != map && false == map.isEmpty();
    }

    /**
     * 将 Map 按照指定的条目分隔符和 key/value 分隔符拼接为字符串
     *
     * @param map            待拼接的 Map（允许为 null 或包含 null 值）
     * @param entryDelimiter 条目之间的分隔符，如 "&"
     * @param kvDelimiter    key 与 value 之间的分隔符，如 "="
     * @return 拼接后的字符串，若 map 为 null 或为空，返回空字符串
     */
    public static String joinMap(Map<String, String> map, String entryDelimiter, String kvDelimiter) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> e : map.entrySet()) {
            String key = e.getKey() == null ? "" : e.getKey();
            String value = e.getValue() == null ? "" : e.getValue();
            if (!first) {
                sb.append(entryDelimiter);
            } else {
                first = false;
            }
            sb.append(key).append(kvDelimiter).append(value);
        }
        return sb.toString();
    }


    /**
     * 创建链接调用map
     *
     * @param <K> Key类型
     * @param <V> Value类型
     * @return map创建类
     */
    public static <K, V> MapBuilder<K, V> builder() {
        return builder(new HashMap<>());
    }

    /**
     * 创建链接调用map
     *
     * @param <K> Key类型
     * @param <V> Value类型
     * @param k   key
     * @param v   value
     * @return map创建类
     */
    public static <K, V> MapBuilder<K, V> builder(K k, V v) {
        return (builder(new HashMap<K, V>())).put(k, v);
    }

    /**
     * 创建链接调用map
     *
     * @param <K> Key类型
     * @param <V> Value类型
     * @param map 实际使用的map
     * @return map创建类
     */
    public static <K, V> MapBuilder<K, V> builder(Map<K, V> map) {
        return new MapBuilder<>(map);
    }

    /**
     * 将map转成字符串
     *
     * @param <K>               键类型
     * @param <V>               值类型
     * @param map               Map
     * @param separator         entry之间的连接符
     * @param keyValueSeparator kv之间的连接符
     * @param otherParams       其它附加参数字符串（例如密钥）
     * @return 连接字符串
     * @since 3.1.1
     */
    public static <K, V> String join(Map<K, V> map, String separator, String keyValueSeparator, String... otherParams) {
        return join(map, separator, keyValueSeparator, false, otherParams);
    }

    /**
     * 将map转成字符串
     *
     * @param <K>               键类型
     * @param <V>               值类型
     * @param map               Map，为空返回otherParams拼接
     * @param separator         entry之间的连接符
     * @param keyValueSeparator kv之间的连接符
     * @param isIgnoreNull      是否忽略null的键和值
     * @param otherParams       其它附加参数字符串（例如密钥）
     * @return 连接后的字符串，map和otherParams为空返回""
     * @since 3.1.1
     */
    public static <K, V> String join(Map<K, V> map, String separator, String keyValueSeparator, boolean isIgnoreNull, String... otherParams) {
        final StringBuilder strBuilder = StrUtil.builder();
        boolean isFirst = true;
        if (isNotEmpty(map)) {
            for (Map.Entry<K, V> entry : map.entrySet()) {
                if (false == isIgnoreNull || entry.getKey() != null && entry.getValue() != null) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        strBuilder.append(separator);
                    }
                    strBuilder.append(entry.getKey() == null ? null : entry.getKey().toString())
                            .append(keyValueSeparator)
                            .append(convertValue(entry.getValue(), String.class, ""));
                }
            }
        }
        // 补充其它字符串到末尾，默认无分隔符
        if (ArrayUtil.isNotEmpty(otherParams)) {
            for (String otherParam : otherParams) {
                strBuilder.append(otherParam);
            }
        }
        return strBuilder.toString();
    }

    /**
     * 将map转成字符串，忽略null的键和值
     *
     * @param <K>               键类型
     * @param <V>               值类型
     * @param map               Map
     * @param separator         entry之间的连接符
     * @param keyValueSeparator kv之间的连接符
     * @param otherParams       其它附加参数字符串（例如密钥）
     * @return 连接后的字符串
     * @since 3.1.1
     */
    public static <K, V> String joinIgnoreNull(Map<K, V> map, String separator, String keyValueSeparator, String... otherParams) {
        return join(map, separator, keyValueSeparator, true, otherParams);
    }

    /**
     * 如果 key 对应的 value 不存在，则使用获取 mappingFunction 重新计算后的值，并保存为该 key 的 value，否则返回 value。<br>
     * 解决使用ConcurrentHashMap.computeIfAbsent导致的死循环问题。（issues#2349）<br>
     * A temporary workaround for Java 8 specific performance issue JDK-8161372 .<br>
     * This class should be removed once we drop Java 8 support.
     *
     * <p>
     * 注意此方法只能用于JDK8
     * </p>
     *
     * @param <K>             键类型
     * @param <V>             值类型
     * @param map             Map，一般用于线程安全的Map
     * @param key             键
     * @param mappingFunction 值计算函数
     * @return 值
     * @see <a href="https://bugs.openjdk.java.net/browse/JDK-8161372">https://bugs.openjdk.java.net/browse/JDK-8161372</a>
     */
    public static <K, V> V computeIfAbsentForJdk8(final Map<K, V> map, final K key, final Function<? super K, ? extends V> mappingFunction) {
        V value = map.get(key);
        if (null == value) {
            value = mappingFunction.apply(key);
            final V res = map.putIfAbsent(key, value);
            if (null != res) {
                // issues#I6RVMY
                // 如果旧值存在，说明其他线程已经赋值成功，putIfAbsent没有执行，返回旧值
                return res;
            }
            // 如果旧值不存在，说明赋值成功，返回当前值

            // Dubbo的解决方式，判空后调用依旧无法解决死循环问题
            // 见：Issue2349Test
            //value = map.computeIfAbsent(key, mappingFunction);
        }
        return value;
    }

    /**
     * 返回一个空Map
     *
     * @param <K> 键类型
     * @param <V> 值类型
     * @return 空Map
     * @see Collections#emptyMap()
     * @since 5.3.1
     */
    public static <K, V> Map<K, V> empty() {
        return Collections.emptyMap();
    }

    /**
     * 将单一键值对转换为Map
     *
     * @param <K>     键类型
     * @param <V>     值类型
     * @param key     键
     * @param value   值
     * @param isOrder 是否有序
     * @return {@link HashMap}
     */
    public static <K, V> HashMap<K, V> of(K key, V value, boolean isOrder) {
        final HashMap<K, V> map = newHashMap(isOrder);
        map.put(key, value);
        return map;
    }

    /**
     * 将单一键值对转换为Map
     *
     * @param <K>   键类型
     * @param <V>   值类型
     * @param key   键
     * @param value 值
     * @return {@link HashMap}
     */
    public static <K, V> HashMap<K, V> of(K key, V value) {
        return of(key, value, false);
    }

    /**
     * 将键和值转换为{@link AbstractMap.SimpleImmutableEntry}<br>
     * 返回的Entry不可变
     *
     * @param key   键
     * @param value 值
     * @param <K>   键类型
     * @param <V>   值类型
     * @return {@link AbstractMap.SimpleImmutableEntry}
     * @since 5.8.0
     */
    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return entry(key, value, true);
    }

    /**
     * 将键和值转换为{@link AbstractMap.SimpleEntry} 或者 {@link AbstractMap.SimpleImmutableEntry}
     *
     * @param key         键
     * @param value       值
     * @param <K>         键类型
     * @param <V>         值类型
     * @param isImmutable 是否不可变Entry
     * @return {@link AbstractMap.SimpleEntry} 或者 {@link AbstractMap.SimpleImmutableEntry}
     * @since 5.8.0
     */
    public static <K, V> Map.Entry<K, V> entry(K key, V value, boolean isImmutable) {
        return isImmutable ?
                new AbstractMap.SimpleImmutableEntry<>(key, value) :
                new AbstractMap.SimpleEntry<>(key, value);
    }

    /**
     * Map是否为空
     *
     * @param map 集合
     * @return 是否为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return null == map || map.isEmpty();
    }

}
