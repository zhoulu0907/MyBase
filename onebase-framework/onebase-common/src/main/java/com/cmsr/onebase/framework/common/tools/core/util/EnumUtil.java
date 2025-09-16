package com.cmsr.onebase.framework.common.tools.core.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 枚举工具类
 *
 */
public class EnumUtil {

    /**
     * 枚举类中所有枚举对象的name列表
     *
     * @param clazz 枚举类
     * @return name列表
     */
    public static List<String> getNames(Class<? extends Enum<?>> clazz) {
        if (null == clazz) {
            return null;
        }
        final Enum<?>[] enums = clazz.getEnumConstants();
        if (null == enums) {
            return null;
        }
        final List<String> list = new ArrayList<>(enums.length);
        for (Enum<?> e : enums) {
            list.add(e.name());
        }
        return list;
    }

    /**
     * 字符串转枚举，调用{@link Enum#valueOf(Class, String)}
     *
     * @param <E>       枚举类型泛型
     * @param enumClass 枚举类
     * @param value     值
     * @return 枚举值
     * @since 4.1.13
     */
    public static <E extends Enum<E>> E fromString(Class<E> enumClass, String value) {
        if (null == enumClass || StrUtil.isBlank(value)) {
            return null;
        }
        return Enum.valueOf(enumClass, value);
    }

    /**
     * 获取给定位置的枚举值
     *
     * @param <E>       枚举类型泛型
     * @param enumClass 枚举类
     * @param index     枚举索引
     * @return 枚举值，null表示无此对应枚举
     * @since 5.1.6
     */
    public static <E extends Enum<E>> E getEnumAt(Class<E> enumClass, int index) {
        if (null == enumClass) {
            return null;
        }
        final E[] enumConstants = enumClass.getEnumConstants();
        if (index < 0) {
            index = enumConstants.length + index;
        }

        return index >= 0 && index < enumConstants.length ? enumConstants[index] : null;
    }

}
