package com.cmsr.onebase.framework.common.util.object;

import java.util.Arrays;

/**
 * Object 工具类
 */
public class ObjectUtils {

    /**
     * 判断对象是否为空
     *
     * @param obj 对象
     * @return 是否为空
     */
    public static boolean isBlank(Object obj) {
        return obj == null || obj.toString().trim().isEmpty();
    }

    @SafeVarargs
    public static <T> boolean equalsAny(T obj, T... array) {
        return Arrays.asList(array).contains(obj);
    }

}
