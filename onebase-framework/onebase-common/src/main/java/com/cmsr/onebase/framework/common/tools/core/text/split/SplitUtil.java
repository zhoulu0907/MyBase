package com.cmsr.onebase.framework.common.tools.core.text.split;

import com.cmsr.onebase.framework.common.tools.core.collection.ListUtil;
import com.cmsr.onebase.framework.common.tools.core.lang.Assert;
import com.cmsr.onebase.framework.common.tools.core.text.CharFinder;
import com.cmsr.onebase.framework.common.tools.core.text.finder.StrFinder;
import com.cmsr.onebase.framework.common.tools.core.text.finder.TextFinder;
import com.cmsr.onebase.framework.common.tools.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 字符串切分器，封装统一的字符串分割静态方法
 *
 */
public class SplitUtil {

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
    public static List<String> split(final CharSequence str, final CharSequence separator,
                                     final int limit, final boolean isTrim, final boolean ignoreEmpty) {
        return split(str, separator, limit, isTrim, ignoreEmpty, false);
    }

    /**
     * 切分字符串<br>
     * 如果提供的字符串为{@code null}，则返回一个空的{@link ArrayList}<br>
     * 如果提供的字符串为""，则当ignoreEmpty时返回空的{@link ArrayList}，否则返回只有一个""元素的{@link ArrayList}
     *
     * @param str         被切分的字符串
     * @param separator   分隔符字符串
     * @param limit       限制分片数，小于等于0表示无限制
     * @param isTrim      是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty 是否忽略空串
     * @param ignoreCase  是否忽略大小写
     * @return 切分后的集合
     */
    public static List<String> split(final CharSequence str, final CharSequence separator,
                                     final int limit, final boolean isTrim, final boolean ignoreEmpty, final boolean ignoreCase) {
        return split(str, separator, limit, ignoreEmpty, ignoreCase, trimFunc(isTrim));
    }

    /**
     * 切分字符串<br>
     * 如果提供的字符串为{@code null}，则返回一个空的{@link ArrayList}<br>
     * 如果提供的字符串为""，则当ignoreEmpty时返回空的{@link ArrayList}，否则返回只有一个""元素的{@link ArrayList}
     *
     * @param <R>         元素类型
     * @param str         被切分的字符串
     * @param separator   分隔符字符串
     * @param limit       限制分片数，小于等于0表示无限制
     * @param ignoreEmpty 是否忽略空串
     * @param ignoreCase  是否忽略大小写
     * @param mapping     切分后字段映射函数
     * @return 切分后的集合
     */
    public static <R> List<R> split(final CharSequence str, final CharSequence separator,
                                    final int limit, final boolean ignoreEmpty, final boolean ignoreCase,
                                    final Function<String, R> mapping) {
        if (null == str) {
            return ListUtil.zero();
        } else if (0 == str.length() && ignoreEmpty) {
            return ListUtil.zero();
        }
        Assert.notEmpty(separator, "Separator must be not empty!");

        // 查找分隔符的方式
        final TextFinder finder = separator.length() == 1 ?
                new CharFinder(separator.charAt(0), ignoreCase) :
                StrFinder.of(separator, ignoreCase);

        final SplitIter splitIter = new SplitIter(str, finder, limit, ignoreEmpty);
        return splitIter.toList(mapping);
    }

    /**
     * Trim函数
     *
     * @param isTrim 是否trim
     * @return {@link Function}
     */
    public static Function<String, String> trimFunc(final boolean isTrim) {
        return isTrim ? StrUtil::trim : Function.identity();
    }
}
