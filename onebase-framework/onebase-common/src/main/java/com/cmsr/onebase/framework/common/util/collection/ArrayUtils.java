package com.cmsr.onebase.framework.common.util.collection;

import com.cmsr.onebase.framework.common.tools.core.collection.CollUtil;
import com.cmsr.onebase.framework.common.tools.core.collection.IterUtil;
import com.cmsr.onebase.framework.common.tools.core.util.ArrayUtil;

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
        if (CollUtil.isEmpty(from)) {
            return (T[]) (new Object[0]);
        }
        return ArrayUtil.toArray(from, (Class<T>) IterUtil.getElementType(from.iterator()));
    }


}
