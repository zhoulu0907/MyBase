package com.cmsr.onebase.framework.common.tools.core.text;

import com.cmsr.onebase.framework.common.tools.core.text.finder.StrFinder;
import com.cmsr.onebase.framework.common.tools.core.text.split.SplitIter;
import com.cmsr.onebase.framework.common.tools.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 字符串切分器，封装统一的字符串分割静态方法
 * @author Looly
 * @since 5.7.0
 */
public class StrSplitter {

    /**
     * 切分字符串为字符串数组
     *
     * @param str         被切分的字符串
     * @param separator   分隔符字符
     * @param limit       限制分片数
     * @param isTrim      是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty 是否忽略空串
     * @return 切分后的集合
     * @since 3.0.8
     */
    public static String[] splitToArray(CharSequence str, char separator, int limit, boolean isTrim, boolean ignoreEmpty) {
        return toArray(split(str, separator, limit, isTrim, ignoreEmpty));
    }

    /**
     * 切分字符串为字符串数组
     *
     * @param str         被切分的字符串
     * @param separator   分隔符字符
     * @param limit       限制分片数，小于等于0表示无限制
     * @param isTrim      是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty 是否忽略空串
     * @return 切分后的集合
     * @since 3.0.8
     */
    public static String[] splitToArray(CharSequence str, String separator, int limit, boolean isTrim, boolean ignoreEmpty) {
        return toArray(split(str, separator, limit, isTrim, ignoreEmpty));
    }

    /**
     * 切分字符串，不忽略大小写
     *
     * @param str         被切分的字符串
     * @param separator   分隔符字符串
     * @param limit       限制分片数，小于等于0表示无限制
     * @param isTrim      是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty 是否忽略空串
     * @return 切分后的集合
     * @since 3.0.8
     */
    public static List<String> split(CharSequence str, String separator, int limit, boolean isTrim, boolean ignoreEmpty) {
        return split(str, separator, limit, isTrim, ignoreEmpty, false);
    }

    /**
     * 切分字符串<br>
     * 如果为空字符串或者null 则返回空集合
     *
     * @param text        被切分的字符串
     * @param separator   分隔符字符串
     * @param limit       限制分片数，小于等于0表示无限制
     * @param isTrim      是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty 是否忽略空串
     * @param ignoreCase  是否忽略大小写
     * @return 切分后的集合
     * @since 3.2.1
     */
    public static List<String> split(CharSequence text, String separator, int limit, boolean isTrim, boolean ignoreEmpty, boolean ignoreCase) {
        if (null == text) {
            return new ArrayList<>(0);
        }
        final SplitIter splitIter = new SplitIter(text, new StrFinder(separator, ignoreCase), limit, ignoreEmpty);
        return splitIter.toList(isTrim);
    }

    /**
     * 切分字符串，大小写敏感
     *
     * @param str         被切分的字符串
     * @param separator   分隔符字符
     * @param limit       限制分片数，-1不限制
     * @param isTrim      是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty 是否忽略空串
     * @return 切分后的集合
     * @since 3.0.8
     */
    public static List<String> split(CharSequence str, char separator, int limit, boolean isTrim, boolean ignoreEmpty) {
        return split(str, separator, limit, isTrim, ignoreEmpty, false);
    }

    /**
     * 切分字符串
     *
     * @param text        被切分的字符串
     * @param separator   分隔符字符
     * @param limit       限制分片数，-1不限制
     * @param isTrim      是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty 是否忽略空串
     * @param ignoreCase  是否忽略大小写
     * @return 切分后的集合
     */
    public static List<String> split(CharSequence text, char separator, int limit, boolean isTrim, boolean ignoreEmpty, boolean ignoreCase) {
        return split(text, separator, limit, ignoreEmpty, ignoreCase, trimFunc(isTrim));
    }

    /**
     * 切分字符串<br>
     * 如果为空字符串或者null 则返回空集合
     *
     * @param <R>         切分后的元素类型
     * @param text        被切分的字符串
     * @param separator   分隔符字符
     * @param limit       限制分片数，-1不限制
     * @param ignoreEmpty 是否忽略空串
     * @param ignoreCase  是否忽略大小写
     * @param mapping     切分后的字符串元素的转换方法
     * @return 切分后的集合，元素类型是经过 mapping 转换后的
     * @since 5.7.14
     */
    public static <R> List<R> split(CharSequence text, char separator, int limit, boolean ignoreEmpty,
                                    boolean ignoreCase, Function<String, R> mapping) {
        if (null == text) {
            return new ArrayList<>(0);
        }
        final SplitIter splitIter = new SplitIter(text, new CharFinder(separator, ignoreCase), limit, ignoreEmpty);
        return splitIter.toList(mapping);
    }

    /**
     * List转Array
     *
     * @param list List
     * @return Array
     */
    private static String[] toArray(List<String> list) {
        return list.toArray(new String[0]);
    }

    /**
     * Trim函数
     *
     * @param isTrim 是否trim
     * @return {@link Function}
     */
    private static Function<String, String> trimFunc(boolean isTrim) {
        return (str) -> isTrim ? StrUtil.trim(str) : str;
    }
}