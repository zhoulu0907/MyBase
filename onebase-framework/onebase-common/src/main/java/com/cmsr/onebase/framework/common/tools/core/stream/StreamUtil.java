package com.cmsr.onebase.framework.common.tools.core.stream;


import com.cmsr.onebase.framework.common.tools.core.lang.Assert;

import java.util.Collection;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * {@link Stream} 工具类
 *
 * @author looly
 * @since 5.6.7
 */
public class StreamUtil {

    /**
     * {@link Iterable}转换为{@link Stream}
     *
     * @param iterable 集合
     * @param parallel 是否并行
     * @param <T>      集合元素类型
     * @return {@link Stream}
     */
    public static <T> Stream<T> of(Iterable<T> iterable, boolean parallel) {
        Assert.notNull(iterable, "Iterable must be not null!");

        return iterable instanceof Collection ?
                parallel ? ((Collection<T>) iterable).parallelStream() : ((Collection<T>) iterable).stream() :
                StreamSupport.stream(iterable.spliterator(), parallel);
    }
}