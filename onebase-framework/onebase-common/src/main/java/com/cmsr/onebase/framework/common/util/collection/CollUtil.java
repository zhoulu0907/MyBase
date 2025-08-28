package com.cmsr.onebase.framework.common.util.collection;

import java.util.Collection;

@Deprecated
public class CollUtil {
    /**
     * 集合是否为空
     *
     * @param collection 集合
     * @return 是否为空
     */

    @Deprecated
    public static boolean isEmpty(Collection<?> collection) {
        // 替代方法
        return org.apache.commons.collections4.CollectionUtils.isEmpty(collection);
    }
}
