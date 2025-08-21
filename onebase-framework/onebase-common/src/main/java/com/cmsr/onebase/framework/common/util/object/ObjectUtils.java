package com.cmsr.onebase.framework.common.util.object;

import java.util.Arrays;

/**
 * Object 工具类
 */
public class ObjectUtils {


    @SafeVarargs
    public static <T> boolean equalsAny(T obj, T... array) {
        return Arrays.asList(array).contains(obj);
    }

}
