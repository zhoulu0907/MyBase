package com.cmsr.onebase.framework.common.util.collection;

import java.util.Collection;

public class CollUtil {
    /**
     * 集合是否为空
     *
     * @param collection 集合
     * @return 是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
