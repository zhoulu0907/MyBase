package com.cmsr.onebase.framework.common.tools.core.util;


import com.cmsr.onebase.framework.common.tools.core.convert.Convert;
import com.cmsr.onebase.framework.common.tools.core.lang.Editor;
import com.cmsr.onebase.framework.common.tools.core.lang.Filter;
import com.cmsr.onebase.framework.common.tools.core.lang.Matcher;
import com.cmsr.onebase.framework.common.tools.core.text.StrJoiner;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

/**
 * Array工具类
 */
public class ArrayUtil {

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param <T>         被处理的集合
     * @param array       数组
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static <T> String join(T[] array, CharSequence conjunction) {
        return join(array, conjunction, null, null);
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param <T>       被处理的集合
     * @param array     数组
     * @param delimiter 分隔符
     * @param prefix    每个元素添加的前缀，null表示不添加
     * @param suffix    每个元素添加的后缀，null表示不添加
     * @return 连接后的字符串
     * @since 4.0.10
     */
    public static <T> String join(T[] array, CharSequence delimiter, String prefix, String suffix) {
        if (null == array) {
            return null;
        }

        return StrJoiner.of(delimiter, prefix, suffix)
                // 每个元素都添加前后缀
                .setWrapElement(true)
                .append(array)
                .toString();
    }

    /**
     * 将多个数组合并在一起<br>
     * 忽略null的数组
     *
     * @param <T>    数组元素类型
     * @param arrays 数组集合
     * @return 合并后的数组
     */
    @SafeVarargs
    public static <T> T[] addAll(T[]... arrays) {
        if (arrays.length == 1) {
            return arrays[0];
        }

        int length = 0;
        for (final T[] array : arrays) {
            if (isNotEmpty(array)) {
                length += array.length;
            }
        }
        final T[] result = newArray(arrays.getClass().getComponentType().getComponentType(), length);

        length = 0;
        for (final T[] array : arrays) {
            if (isNotEmpty(array)) {
                System.arraycopy(array, 0, result, length, array.length);
                length += array.length;
            }
        }
        return result;
    }

    /**
     * 判断两个数组是否相等，判断依据包括数组长度和每个元素都相等。
     *
     * @param array1 数组1
     * @param array2 数组2
     * @return 是否相等
     * @since 5.4.2
     */
    public static boolean equals(Object array1, Object array2) {
        if (array1 == array2) {
            return true;
        }
        if (hasNull(array1, array2)) {
            return false;
        }

        Assert.isTrue(isArray(array1), "First is not a Array !");
        Assert.isTrue(isArray(array2), "Second is not a Array !");

        if (array1 instanceof long[]) {
            return Arrays.equals((long[]) array1, (long[]) array2);
        } else if (array1 instanceof int[]) {
            return Arrays.equals((int[]) array1, (int[]) array2);
        } else if (array1 instanceof short[]) {
            return Arrays.equals((short[]) array1, (short[]) array2);
        } else if (array1 instanceof char[]) {
            return Arrays.equals((char[]) array1, (char[]) array2);
        } else if (array1 instanceof byte[]) {
            return Arrays.equals((byte[]) array1, (byte[]) array2);
        } else if (array1 instanceof double[]) {
            return Arrays.equals((double[]) array1, (double[]) array2);
        } else if (array1 instanceof float[]) {
            return Arrays.equals((float[]) array1, (float[]) array2);
        } else if (array1 instanceof boolean[]) {
            return Arrays.equals((boolean[]) array1, (boolean[]) array2);
        } else {
            // Not an array of primitives
            return Arrays.deepEquals((Object[]) array1, (Object[]) array2);
        }
    }

    /**
     * 是否包含{@code null}元素
     *
     * @param <T>   数组元素类型
     * @param array 被检查的数组
     * @return 是否包含{@code null}元素
     * @since 3.0.7
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean hasNull(T... array) {
        if (isNotEmpty(array)) {
            for (T element : array) {
                if (ObjUtil.isNull(element)) {
                    return true;
                }
            }
        }
        return array == null;
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array 数组，会变更
     * @return 变更后的原数组
     * @since 3.0.9
     */
    public static char[] reverse(char[] array) {
        return reverse(array, 0, array.length);
    }

    /**
     * 反转数组，会变更原数组
     *
     * @param array               数组，会变更
     * @param startIndexInclusive 起始位置（包含）
     * @param endIndexExclusive   结束位置（不包含）
     * @return 变更后的原数组
     * @since 3.0.9
     */
    public static char[] reverse(char[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (isEmpty(array)) {
            return array;
        }
        int i = Math.max(startIndexInclusive, 0);
        int j = Math.min(array.length, endIndexExclusive) - 1;
        while (j > i) {
            swap(array, i, j);
            j--;
            i++;
        }
        return array;
    }

    /**
     * 交换数组中两个位置的值
     *
     * @param array  数组
     * @param index1 位置1
     * @param index2 位置2
     * @return 交换后的数组，与传入数组为同一对象
     * @since 4.0.7
     */
    public static char[] swap(char[] array, int index1, int index2) {
        if (isEmpty(array)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        char tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
        return array;
    }

    /**
     * 包装 {@link System#arraycopy(Object, int, Object, int, int)}<br>
     * 数组复制，缘数组和目标数组都是从位置0开始复制
     *
     * @param src    源数组
     * @param dest   目标数组
     * @param length 拷贝数组长度
     * @return 目标数组
     * @since 3.0.6
     */
    public static Object copy(Object src, Object dest, int length) {
        //noinspection SuspiciousSystemArraycopy
        System.arraycopy(src, 0, dest, 0, length);
        return dest;
    }

    /**
     * 获取数组对象的元素类型
     *
     * @param array 数组对象
     * @return 元素类型
     * @since 3.2.2
     */
    public static Class<?> getComponentType(Object array) {
        return null == array ? null : array.getClass().getComponentType();
    }

    /**
     * 按照指定规则，将一种类型的数组转换为另一种类型
     *
     * @param array               被转换的数组
     * @param targetComponentType 目标的元素类型
     * @param func                转换规则函数
     * @param <T>                 原数组类型
     * @param <R>                 目标数组类型
     * @return 转换后的数组
     * @since 5.5.8
     */
    public static <T, R> R[] map(Object array, Class<R> targetComponentType, Function<? super T, ? extends R> func) {
        final int length = length(array);
        final R[] result = newArray(targetComponentType, length);
        for (int i = 0; i < length; i++) {
            result[i] = func.apply(get(array, i));
        }
        return result;
    }

    /**
     * 按照指定规则，将一种类型的数组元素提取后转换为{@link List}
     *
     * @param array 被转换的数组
     * @param func  转换规则函数
     * @param <T>   原数组类型
     * @param <R>   目标数组类型
     * @return 转换后的数组
     * @since 5.5.7
     */
    public static <T, R> List<R> map(T[] array, Function<? super T, ? extends R> func) {
        return Arrays.stream(array).map(func).collect(Collectors.toList());
    }

    /**
     * 将元素值设置为数组的某个位置，当给定的index大于数组长度，则追加<br>
     * 替换时返回原数组，追加时返回新数组
     *
     * @param array 已有数组
     * @param index 位置，大于长度追加，否则替换
     * @param value 新值
     * @return 新数组或原有数组
     * @since 4.1.2
     */
    public static Object setOrAppend(Object array, int index, Object value) {
        if (index < length(array)) {
            Array.set(array, index, value);
            return array;
        } else {
            return append(array, value);
        }
    }

    /**
     * 获取数组中指定多个下标元素值，组成新数组
     *
     * @param <T>     数组元素类型
     * @param array   数组，如果提供为{@code null}则返回{@code null}
     * @param indexes 下标列表
     * @return 结果
     */
    public static <T> T[] getAny(Object array, int... indexes) {
        if (null == array) {
            return null;
        }
        if (null == indexes) {
            return newArray(array.getClass().getComponentType(), 0);
        }

        final T[] result = newArray(array.getClass().getComponentType(), indexes.length);
        for (int i = 0; i < indexes.length; i++) {
            result[i] = ArrayUtil.get(array, indexes[i]);
        }
        return result;
    }

    /**
     * 获取子数组
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     * @since 4.2.2
     */
    public static <T> T[] sub(T[] array, int start, int end) {
        int length = length(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return newArray(array.getClass().getComponentType(), 0);
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return newArray(array.getClass().getComponentType(), 0);
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置（包括）
     * @param end   结束位置（不包括）
     * @param step  步进
     * @return 新的数组
     * @since 4.0.6
     */
    public static Object[] sub(Object array, int start, int end, int step) {
        int length = length(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new Object[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new Object[0];
            }
            end = length;
        }

        if (step <= 1) {
            step = 1;
        }

        final ArrayList<Object> list = new ArrayList<>();
        for (int i = start; i < end; i += step) {
            list.add(get(array, i));
        }

        return list.toArray();
    }

    /**
     * 获取数组对象中指定index的值，支持负数，例如-1表示倒数第一个值<br>
     * 如果数组下标越界，返回null
     *
     * @param <T>   数组元素类型
     * @param array 数组对象
     * @param index 下标，支持负数
     * @return 值
     * @since 4.0.6
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Object array, int index) {
        if (null == array) {
            return null;
        }

        if (index < 0) {
            index += Array.getLength(array);
        }
        try {
            return (T) Array.get(array, index);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * 数组中元素未找到的下标，值为-1
     */
    public static final int INDEX_NOT_FOUND = -1;
    /**
     * 对象是否为数组对象
     *
     * @param obj 对象
     * @return 是否为数组对象，如果为{@code null} 返回false
     */
    public static boolean isArray(Object obj) {
        return null != obj && obj.getClass().isArray();
    }

    /**
     * 数组是否为非空
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @return 是否为非空
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return (null != array && array.length != 0);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(byte[] array) {
        return false == isEmpty(array);
    }

    /**
     * 数组是否为空
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @return 是否为空
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 数组是否为空<br>
     * 此方法会匹配单一对象，如果此对象为{@code null}则返回true<br>
     * 如果此对象为非数组，理解为此对象为数组的第一个元素，则返回false<br>
     * 如果此对象为数组对象，数组长度大于0情况下返回false，否则返回true
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(Object array) {
        if (array != null) {
            if (isArray(array)) {
                return 0 == Array.getLength(array);
            }
            return false;
        }
        return true;
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 返回数组中指定元素所在位置，忽略大小写，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     * @since 3.1.2
     */
    public static int indexOfIgnoreCase(CharSequence[] array, CharSequence value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (StrUtil.equalsIgnoreCase(array[i], value)) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 将集合转为数组
     *
     * @param <T>           数组元素类型
     * @param collection    集合
     * @param componentType 集合元素类型
     * @return 数组
     * @since 3.0.9
     */
    public static <T> T[] toArray(Collection<T> collection, Class<T> componentType) {
        return collection.toArray(newArray(componentType, 0));
    }

    /**
     * 新建一个空数组
     *
     * @param <T>           数组元素类型
     * @param componentType 元素类型
     * @param newSize       大小
     * @return 空数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] newArray(Class<?> componentType, int newSize) {
        return (T[]) Array.newInstance(componentType, newSize);
    }

    /**
     * 返回数组中第一个匹配规则的值
     *
     * @param <T>     数组元素类型
     * @param matcher 匹配接口，实现此接口自定义匹配规则
     * @param array   数组
     * @return 匹配元素，如果不存在匹配元素或数组为空，返回 {@code null}
     * @since 3.0.7
     */
    @SuppressWarnings("unchecked")
    public static <T> T firstMatch(Matcher<T> matcher, T... array) {
        final int index = matchIndex(matcher, array);
        if (index < 0) {
            return null;
        }

        return array[index];
    }

    /**
     * 返回数组中第一个匹配规则的值的位置
     *
     * @param <T>     数组元素类型
     * @param matcher 匹配接口，实现此接口自定义匹配规则
     * @param array   数组
     * @return 匹配到元素的位置，-1表示未匹配到
     * @since 5.6.6
     */
    @SuppressWarnings("unchecked")
    public static <T> int matchIndex(Matcher<T> matcher, T... array) {
        return matchIndex(matcher, 0, array);
    }

    /**
     * 返回数组中第一个匹配规则的值的位置
     *
     * @param <T>               数组元素类型
     * @param matcher           匹配接口，实现此接口自定义匹配规则
     * @param beginIndexInclude 检索开始的位置
     * @param array             数组
     * @return 匹配到元素的位置，-1表示未匹配到
     * @since 5.7.3
     */
    @SuppressWarnings("unchecked")
    public static <T> int matchIndex(Matcher<T> matcher, int beginIndexInclude, T... array) {
        Assert.notNull(matcher, "Matcher must be not null !");
        if (isNotEmpty(array)) {
            for (int i = beginIndexInclude; i < array.length; i++) {
                if (matcher.match(array[i])) {
                    return i;
                }
            }
        }

        return INDEX_NOT_FOUND;
    }

    /**
     * 数组或集合转String
     *
     * @param obj 集合或数组对象
     * @return 数组字符串，与集合转字符串格式相同
     */
    public static String toString(Object obj) {
        if (null == obj) {
            return null;
        }

        if (obj instanceof long[]) {
            return Arrays.toString((long[]) obj);
        } else if (obj instanceof int[]) {
            return Arrays.toString((int[]) obj);
        } else if (obj instanceof short[]) {
            return Arrays.toString((short[]) obj);
        } else if (obj instanceof char[]) {
            return Arrays.toString((char[]) obj);
        } else if (obj instanceof byte[]) {
            return Arrays.toString((byte[]) obj);
        } else if (obj instanceof boolean[]) {
            return Arrays.toString((boolean[]) obj);
        } else if (obj instanceof float[]) {
            return Arrays.toString((float[]) obj);
        } else if (obj instanceof double[]) {
            return Arrays.toString((double[]) obj);
        } else if (isArray(obj)) {
            // 对象数组
            try {
                return Arrays.deepToString((Object[]) obj);
            } catch (Exception ignore) {
                //ignore
            }
        }

        return obj.toString();
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     * @since 3.0.7
     */
    public static boolean contains(char[] array, char value) {
        return indexOf(array, value) > INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     */
    public static <T> boolean contains(T[] array, T value) {
        return indexOf(array, value) > INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param <T>   数组类型
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     * @since 3.0.7
     */
    public static <T> int indexOf(T[] array, Object value) {
        return matchIndex((obj) -> ObjUtil.equal(value, obj), array);
    }

    /**
     * 返回数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置，未找到返回{@link #INDEX_NOT_FOUND}
     * @since 3.0.7
     */
    public static int indexOf(char[] array, char value) {
        if (isNotEmpty(array)) {
            for (int i = 0; i < array.length; i++) {
                if (value == array[i]) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(char[] array) {
        return false == isEmpty(array);
    }

    /**
     *
     * @param args 被检查的对象,一个或者多个
     * @return 是否都不为空
     * @since 4.5.18
     */
    public static boolean isAllNotEmpty(Object... args) {
        return false == hasEmpty(args);
    }

    /**
     * 是否存在{@code null}或空对象，通过{@link ObjectUtils#isEmpty(Object)} 判断元素
     *
     * @param args 被检查对象
     * @return 是否存在
     * @since 4.5.18
     */
    public static boolean hasEmpty(Object... args) {
        if (isNotEmpty(args)) {
            for (Object element : args) {
                if (ObjectUtils.isEmpty(element)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 按照指定规则，将一种类型的数组元素转换为另一种类型，并保存为数组
     *
     * @param array     被转换的数组
     * @param func      转换规则函数
     * @param generator 数组生成器，如返回String[]，则传入String[]::new
     * @param <T>       原数组类型
     * @param <R>       目标数组类型
     * @return 集合
     */
    public static <T, R> R[] mapToArray(final T[] array, final Function<? super T, ? extends R> func,
                                        final IntFunction<R[]> generator) {
        return Arrays.stream(array).map(func).toArray(generator);
    }

    /**
     * 如果给定数组为空，返回默认数组
     *
     * @param <T>          数组元素类型
     * @param array        数组
     * @param defaultArray 默认数组
     * @return 非空（empty）的原数组或默认数组
     * @since 4.6.9
     */
    public static <T> T[] defaultIfEmpty(final T[] array, final T[] defaultArray) {
        return isEmpty(array) ? defaultArray : array;
    }

    /**
     * 按照指定规则，将一种类型的数组元素提取后转换为{@link Set}
     *
     * @param array 被转换的数组
     * @param func  转换规则函数
     * @param <T>   原数组类型
     * @param <R>   目标数组类型
     * @return 转换后的数组
     * @since 5.8.0
     */
    public static <T, R> Set<R> mapToSet(T[] array, Function<? super T, ? extends R> func) {
        return Arrays.stream(array).map(func).collect(Collectors.toSet());
    }

    /**
     * 编辑数组<br>
     * 编辑过程通过传入的Editor实现来返回需要的元素内容，这个Editor实现可以实现以下功能：
     *
     * <pre>
     * 1、过滤出需要的对象，如果返回{@code null}表示这个元素对象抛弃
     * 2、修改元素对象，返回集合中为修改后的对象
     * </pre>
     * <p>
     *
     * @param <T>    数组元素类型
     * @param array  数组
     * @param editor 编辑器接口，{@code null}返回原集合
     * @return 编辑后的数组
     * @since 5.3.3
     */
    public static <T> T[] edit(T[] array, Editor<T> editor) {
        if (null == editor) {
            return array;
        }

        final ArrayList<T> list = new ArrayList<>(array.length);
        T modified;
        for (T t : array) {
            modified = editor.edit(t);
            if (null != modified) {
                list.add(modified);
            }
        }
        final T[] result = newArray(array.getClass().getComponentType(), list.size());
        return list.toArray(result);
    }


    /**
     * 过滤<br>
     * 过滤过程通过传入的Filter实现来过滤返回需要的元素内容，这个Filter实现可以实现以下功能：
     *
     * <pre>
     * 1、过滤出需要的对象，{@link Filter#accept(Object)}方法返回true的对象将被加入结果集合中
     * </pre>
     *
     * @param <T>    数组元素类型
     * @param array  数组
     * @param filter 过滤器接口，用于定义过滤规则，{@code null}返回原集合
     * @return 过滤后的数组
     * @since 3.2.1
     */
    public static <T> T[] filter(T[] array, Filter<T> filter) {
        if (null == array || null == filter) {
            return array;
        }
        return edit(array, t -> filter.accept(t) ? t : null);
    }

    /**
     * 根据数组元素类型，获取数组的类型<br>
     * 方法是通过创建一个空数组从而获取其类型
     *
     * @param componentType 数组元素类型
     * @return 数组类型
     * @since 3.2.2
     */
    public static Class<?> getArrayType(Class<?> componentType) {
        return Array.newInstance(componentType, 0).getClass();
    }

    /**
     * 将新元素添加到已有数组中<br>
     * 添加新元素会生成一个新的数组，不影响原数组
     *
     * @param <T>         数组元素类型
     * @param buffer      已有数组
     * @param newElements 新元素
     * @return 新数组
     */
    @SafeVarargs
    public static <T> T[] append(T[] buffer, T... newElements) {
        if (isEmpty(buffer)) {
            return newElements;
        }
        return insert(buffer, buffer.length, newElements);
    }

    /**
     * 将新元素添加到已有数组中<br>
     * 添加新元素会生成一个新的数组，不影响原数组
     *
     * @param <T>         数组元素类型
     * @param array       已有数组
     * @param newElements 新元素
     * @return 新数组
     */
    @SafeVarargs
    public static <T> Object append(Object array, T... newElements) {
        if (isEmpty(array)) {
            return newElements;
        }
        return insert(array, length(array), newElements);
    }

    /**
     * 将新元素插入到到已有数组中的某个位置<br>
     * 添加新元素会生成一个新的数组，不影响原数组<br>
     * 如果插入位置为为负数，从原数组从后向前计数，若大于原数组长度，则空白处用null填充
     *
     * @param <T>         数组元素类型
     * @param buffer      已有数组
     * @param index       插入位置，此位置为对应此位置元素之前的空档
     * @param newElements 新元素
     * @return 新数组
     * @since 4.0.8
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] insert(T[] buffer, int index, T... newElements) {
        return (T[]) insert((Object) buffer, index, newElements);
    }

    /**
     * 将新元素插入到到已有数组中的某个位置<br>
     * 添加新元素会生成一个新的数组，不影响原数组<br>
     * 如果插入位置为为负数，从原数组从后向前计数，若大于原数组长度，则空白处用null填充
     *
     * @param <T>         数组元素类型
     * @param array       已有数组
     * @param index       插入位置，此位置为对应此位置元素之前的空档
     * @param newElements 新元素
     * @return 新数组
     * @since 4.0.8
     */
    @SuppressWarnings({"unchecked", "SuspiciousSystemArraycopy"})
    public static <T> Object insert(Object array, int index, T... newElements) {
        if (isEmpty(newElements)) {
            return array;
        }
        if (isEmpty(array)) {
            return newElements;
        }

        final int len = length(array);
        if (index < 0) {
            index = (index % len) + len;
        }

        // 已有数组的元素类型
        final Class<?> originComponentType = array.getClass().getComponentType();
        Object newEleArr = newElements;
        // 如果 已有数组的元素类型是 原始类型，则需要转换 新元素数组 为该类型，避免ArrayStoreException
        if (originComponentType.isPrimitive()) {
            newEleArr = Convert.convert(array.getClass(), newElements);
        }
        final Object result = Array.newInstance(originComponentType, Math.max(len, index) + newElements.length);
        System.arraycopy(array, 0, result, 0, Math.min(len, index));
        System.arraycopy(newEleArr, 0, result, index, newElements.length);
        if (index < len) {
            System.arraycopy(array, index, result, index + newElements.length, len - index);
        }
        return result;
    }

    /**
     * 获取数组长度<br>
     * 如果参数为{@code null}，返回0
     *
     * <pre>
     * ArrayUtil.length(null)            = 0
     * ArrayUtil.length([])              = 0
     * ArrayUtil.length([null])          = 1
     * ArrayUtil.length([true, false])   = 2
     * ArrayUtil.length([1, 2, 3])       = 3
     * ArrayUtil.length(["a", "b", "c"]) = 3
     * </pre>
     *
     * @param array 数组对象
     * @return 数组长度
     * @throws IllegalArgumentException 如果参数不为数组，抛出此异常
     * @see Array#getLength(Object)
     * @since 3.0.8
     */
    public static int length(Object array) throws IllegalArgumentException {
        if (null == array) {
            return 0;
        }
        return Array.getLength(array);
    }

    /**
     * 克隆数组，如果非数组返回{@code null}
     *
     * @param <T> 数组元素类型
     * @param obj 数组对象
     * @return 克隆后的数组对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T clone(final T obj) {
        if (null == obj) {
            return null;
        }
        if (isArray(obj)) {
            final Object result;
            final Class<?> componentType = obj.getClass().getComponentType();
            if (componentType.isPrimitive()) {// 原始类型
                int length = Array.getLength(obj);
                result = Array.newInstance(componentType, length);
                while (length-- > 0) {
                    Array.set(result, length, Array.get(obj, length));
                }
            } else {
                result = ((Object[]) obj).clone();
            }
            return (T) result;
        }
        return null;
    }

}
