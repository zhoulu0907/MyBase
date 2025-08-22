package com.cmsr.onebase.framework.common.util.collection;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.util.ArrayUtil;

import java.util.Collection;
import java.util.function.Function;

import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.convertList;

/**
 * Array 工具类
 */
public class ArrayUtils {

    public static <T, V> V[] toArray(Collection<T> from, Function<T, V> mapper) {
        return toArray(convertList(from, mapper));
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] toArray(Collection<T> from) {
        if (CollectionUtil.isEmpty(from)) {
            return (T[]) (new Object[0]);
        }
        return ArrayUtil.toArray(from, (Class<T>) IterUtil.getElementType(from.iterator()));
    }


}
