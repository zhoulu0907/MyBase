package com.cmsr.onebase.framework.common.util.collection;

import cn.hutool.core.collection.CollUtil;

import java.util.Set;

/**
 * Set 工具类
 *
 */
@Deprecated
public class SetUtils {

    /**
     * 使用 java.util.Set#of(E...) 替代
     * @param objs
     * @return
     * @param <T>
     */
    @Deprecated
    @SafeVarargs
    public static <T> Set<T> asSet(T... objs) {
        return CollUtil.newHashSet(objs);
    }

}
