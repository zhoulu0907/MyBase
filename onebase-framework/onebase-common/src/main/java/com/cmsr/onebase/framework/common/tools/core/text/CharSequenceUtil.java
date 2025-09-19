package com.cmsr.onebase.framework.common.tools.core.text;

import com.cmsr.onebase.framework.common.tools.core.util.CharUtil;

/**
 * {@link CharSequence} 相关工具类封装
 *
 * @author looly
 * @since 5.5.3
 */
public class CharSequenceUtil {

    /**
     * <p>字符串是否为非空白，非空白的定义如下： </p>
     * <ol>
     *     <li>不为 {@code null}</li>
     *     <li>不为空字符串：{@code ""}</li>
     * </ol>
     *
     * <p>例：</p>
     * <ul>
     *     <li>{@code CharSequenceUtil.isNotEmpty(null)     // false}</li>
     *     <li>{@code CharSequenceUtil.isNotEmpty("")       // false}</li>
     *     <li>{@code CharSequenceUtil.isNotEmpty(" \t\n")  // true}</li>
     *     <li>{@code CharSequenceUtil.isNotEmpty("abc")    // true}</li>
     * </ul>
     *
     * <p>建议：该方法建议用于工具类或任何可以预期的方法参数的校验中。</p>
     *
     * @param str 被检测的字符串
     * @return 是否为非空
     * @see #isEmpty(CharSequence)
     */
    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    /**
     * <p>字符串是否为空，空的定义如下：</p>
     * <ol>
     *     <li>{@code null}</li>
     *     <li>空字符串：{@code ""}</li>
     * </ol>
     *
     * <p>例：</p>
     * <ul>
     *     <li>{@code CharSequenceUtil.isEmpty(null)     // true}</li>
     *     <li>{@code CharSequenceUtil.isEmpty("")       // true}</li>
     *     <li>{@code CharSequenceUtil.isEmpty(" \t\n")  // false}</li>
     *     <li>{@code CharSequenceUtil.isEmpty("abc")    // false}</li>
     * </ul>
     *
     * <p>建议：</p>
     * <ul>
     *     <li>该方法建议用于工具类或任何可以预期的方法参数的校验中。</li>
     * </ul>
     *
     * @param str 被检测的字符串
     * @return 是否为空
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * 截取两个字符串的不同部分（长度一致），判断截取的子串是否相同<br>
     * 任意一个字符串为null返回false
     *
     * @param str1       第一个字符串
     * @param offset1    第一个字符串开始的位置
     * @param str2       第二个字符串
     * @param offset2    第二个字符串开始的位置
     * @param length     截取长度
     * @param ignoreCase 是否忽略大小写
     * @return 子串是否相同
     * @see String#regionMatches(boolean, int, String, int, int)
     * @since 3.2.1
     */
    public static boolean isSubEquals(final CharSequence str1, final int offset1,
                                      final CharSequence str2, final int offset2, final int length,
                                      final boolean ignoreCase) {
        if (null == str1 || null == str2) {
            return false;
        }

        return str1.toString().regionMatches(ignoreCase, offset1, str2.toString(), offset2, length);
    }

    /**
     * <p>字符串是否为空白，空白的定义如下：</p>
     * <ol>
     *     <li>{@code null}</li>
     *     <li>空字符串：{@code ""}</li>
     *     <li>空格、全角空格、制表符、换行符，等不可见字符</li>
     * </ol>
     *
     * <p>例：</p>
     * <ul>
     *     <li>{@code CharSequenceUtil.isBlank(null)     // true}</li>
     *     <li>{@code CharSequenceUtil.isBlank("")       // true}</li>
     *     <li>{@code CharSequenceUtil.isBlank(" \t\n")  // true}</li>
     *     <li>{@code CharSequenceUtil.isBlank("abc")    // false}</li>
     * </ul>
     *
     * <p>注意：该方法与 {@link #isEmpty(CharSequence)} 的区别是：
     * 该方法会校验空白字符，且性能相对于 {@link #isEmpty(CharSequence)} 略慢。</p>
     * <br>
     *
     * <p>建议：</p>
     * <ul>
     *     <li>该方法建议仅对于客户端（或第三方接口）传入的参数使用该方法。</li>
     * </ul>
     *
     * @param str 被检测的字符串
     * @return 若为空白，则返回 true
     * @see #isEmpty(CharSequence)
     */
    public static boolean isBlank(CharSequence str) {
        final int length;
        if ((str == null) || ((length = str.length()) == 0)) {
            return true;
        }

        for (int i = 0; i < length; i++) {
            // 只要有一个非空字符即为非空字符串
            if (false == CharUtil.isBlankChar(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

}