package com.cmsr.onebase.framework.common.tools.core.collection;

import com.cmsr.onebase.framework.common.tools.core.map.MapUtil;
import com.cmsr.onebase.framework.common.tools.core.stream.StreamUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 集合的stream操作封装
 *
 * @author 528910437@QQ.COM, VampireAchao&lt;achao1441470436@gmail.com&gt;Lion Li&gt;
 * @since 5.5.2
 */
public class CollStreamUtil {

    /**
     * 将Collection转化为map(value类型与collection的泛型不同)<br>
     * <B>{@code Collection<E> -----> Map<K,V>  }</B>
     *
     * @param collection 需要转化的集合
     * @param key        E类型转化为K类型的lambda方法
     * @param value      E类型转化为V类型的lambda方法
     * @param <E>        collection中的泛型
     * @param <K>        map中的key类型
     * @param <V>        map中的value类型
     * @return 转化后的map
     */
    public static <E, K, V> Map<K, V> toMap(Collection<E> collection, Function<E, K> key, Function<E, V> value) {
        return toMap(collection, key, value, false);
    }

    /**
     * @param collection 需要转化的集合
     * @param key        E类型转化为K类型的lambda方法
     * @param value      E类型转化为V类型的lambda方法
     * @param isParallel 是否并行流
     * @param <E>        collection中的泛型
     * @param <K>        map中的key类型
     * @param <V>        map中的value类型
     * @return 转化后的map
     */
    public static <E, K, V> Map<K, V> toMap(Collection<E> collection, Function<E, K> key, Function<E, V> value, boolean isParallel) {
        if (CollUtil.isEmpty(collection)) {
            return MapUtil.newHashMap(0);
        }
        return StreamUtil.of(collection, isParallel)
                .collect(HashMap::new, (m, v) -> m.put(key.apply(v), value.apply(v)), HashMap::putAll);
    }
}