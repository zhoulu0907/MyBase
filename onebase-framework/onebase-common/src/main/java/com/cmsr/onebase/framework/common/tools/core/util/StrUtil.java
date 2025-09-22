package com.cmsr.onebase.framework.common.tools.core.util;


import com.cmsr.onebase.framework.common.tools.core.collection.CollUtil;
import com.cmsr.onebase.framework.common.tools.core.lang.Filter;
import com.cmsr.onebase.framework.common.tools.core.lang.Matcher;
import com.cmsr.onebase.framework.common.tools.core.text.*;
import com.cmsr.onebase.framework.common.tools.core.text.finder.StrFinder;
import com.cmsr.onebase.framework.common.tools.core.text.finder.TextFinder;
import com.cmsr.onebase.framework.common.tools.core.text.split.SplitIter;
import org.springframework.util.Assert;

import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 字符串工具类
 */
public class StrUtil implements StrPool {

    public static final Charset CHARSET_UTF_8 = StandardCharsets.UTF_8;

    public static final int INDEX_NOT_FOUND = -1;

    /**
     * 字符串常量：{@code "null"} <br>
     * 注意：{@code "null" != null}
     */
    public static final String NULL = "null";
    /**
     * 字符串常量：空字符串 {@code ""}
     */
    public static final String EMPTY = "";

    /**
     * 字符串常量：空格符 {@code " "}
     */
    public static final String SPACE = " ";
    /**
     * 无限制切分个数
     */
    public static final int UNLIMITED = -1;

    /**
     * 以 conjunction 为分隔符将多个对象转换为字符串
     *
     * @param objs        数组
     * @return 连接后的字符串
     */
    public static String join(CharSequence conjunction, Object... objs) {
        return ArrayUtil.join(objs, conjunction);
    }

    /**
     * 给定字符串是否与提供的中任一字符串相同，相同则返回{@code true}，没有相同的返回{@code false}<br>
     * 如果参与比对的字符串列表为空，返回{@code false}
     *
     * @param str1 给定需要检查的字符串
     * @param strs 需要参与比对的字符串列表
     * @return 是否相同
     * @since 4.3.2
     */
    public static boolean equalsAny(CharSequence str1, CharSequence... strs) {
        return equalsAny(str1, false, strs);
    }

    /**
     * 给定字符串是否与提供的中任一字符串相同，相同则返回{@code true}，没有相同的返回{@code false}<br>
     * 如果参与比对的字符串列表为空，返回{@code false}
     *
     * @param str1       给定需要检查的字符串
     * @param ignoreCase 是否忽略大小写
     * @param strs       需要参与比对的字符串列表
     * @return 是否相同
     * @since 4.3.2
     */
    public static boolean equalsAny(CharSequence str1, boolean ignoreCase, CharSequence... strs) {
        if (ArrayUtil.isEmpty(strs)) {
            return false;
        }

        for (CharSequence str : strs) {
            if (equals(str1, str, ignoreCase)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 替换字符串中的指定字符串
     *
     * @param str         字符串
     * @param searchStr   被查找的字符串
     * @param replacement 被替换的字符串
     * @return 替换后的字符串
     * @since 4.0.3
     */
    public static String replace(CharSequence str, CharSequence searchStr, CharSequence replacement) {
        return replace(str, 0, searchStr, replacement, false);
    }

    /**
     * 替换字符串中的指定字符串
     *
     * @param str         字符串
     * @param fromIndex   开始位置（包括）
     * @param searchStr   被查找的字符串
     * @param replacement 被替换的字符串
     * @param ignoreCase  是否忽略大小写
     * @return 替换后的字符串
     * @since 4.0.3
     */
    public static String replace(CharSequence str, int fromIndex, CharSequence searchStr, CharSequence replacement, boolean ignoreCase) {
        if (isEmpty(str) || isEmpty(searchStr)) {
            return str(str);
        }
        if (null == replacement) {
            replacement = EMPTY;
        }

        final int strLength = str.length();
        final int searchStrLength = searchStr.length();
        if (strLength < searchStrLength) {
            // issue#I4M16G@Gitee
            return str(str);
        }

        if (fromIndex > strLength) {
            return str(str);
        } else if (fromIndex < 0) {
            fromIndex = 0;
        }

        final StringBuilder result = new StringBuilder(strLength - searchStrLength + replacement.length());
        if (0 != fromIndex) {
            result.append(str.subSequence(0, fromIndex));
        }

        int preIndex = fromIndex;
        int index;
        while ((index = indexOf(str, searchStr, preIndex, ignoreCase)) > -1) {
            result.append(str.subSequence(preIndex, index));
            result.append(replacement);
            preIndex = index + searchStrLength;
        }

        if (preIndex < strLength) {
            // 结尾部分
            result.append(str.subSequence(preIndex, strLength));
        }
        return result.toString();
    }

    /**
     * 如果给定字符串不是以suffix结尾的，在尾部补充 suffix
     *
     * @param str    字符串
     * @param suffix 后缀
     * @return 补充后的字符串
     * @see #appendIfMissing(CharSequence, CharSequence, CharSequence...)
     */
    public static String addSuffixIfNot(CharSequence str, CharSequence suffix) {
        return appendIfMissing(str, suffix, suffix);
    }

    /**
     * 如果给定字符串不是以给定的一个或多个字符串为结尾，则在尾部添加结尾字符串<br>
     * 不忽略大小写
     *
     * @param str      被检查的字符串
     * @param suffix   需要添加到结尾的字符串
     * @param suffixes 需要额外检查的结尾字符串，如果以这些中的一个为结尾，则不再添加
     * @return 如果已经结尾，返回原字符串，否则返回添加结尾的字符串
     * @since 3.0.7
     */
    public static String appendIfMissing(CharSequence str, CharSequence suffix, CharSequence... suffixes) {
        return appendIfMissing(str, suffix, false, suffixes);
    }

    /**
     * 如果给定字符串不是以给定的一个或多个字符串为结尾，则在尾部添加结尾字符串
     *
     * @param str          被检查的字符串
     * @param suffix       需要添加到结尾的字符串，不参与检查匹配
     * @param ignoreCase   检查结尾时是否忽略大小写
     * @param testSuffixes 需要额外检查的结尾字符串，如果以这些中的一个为结尾，则不再添加
     * @return 如果已经结尾，返回原字符串，否则返回添加结尾的字符串
     * @since 3.0.7
     */
    public static String appendIfMissing(CharSequence str, CharSequence suffix, boolean ignoreCase, CharSequence... testSuffixes) {
        if (str == null || isEmpty(suffix) || endWith(str, suffix, ignoreCase)) {
            return str(str);
        }
        if (ArrayUtil.isNotEmpty(testSuffixes)) {
            for (final CharSequence testSuffix : testSuffixes) {
                if (endWith(str, testSuffix, ignoreCase)) {
                    return str.toString();
                }
            }
        }
        return str.toString().concat(suffix.toString());
    }

    /**
     * 给定字符串是否以任何一个字符串开始（忽略大小写）<br>
     * 给定字符串和数组为空都返回false
     *
     * @param str      给定字符串
     * @param prefixes 需要检测的开始字符串
     * @return 给定字符串是否以任何一个字符串开始
     * @since 5.8.1
     */
    public static boolean startWithAnyIgnoreCase(final CharSequence str, final CharSequence... prefixes) {
        if (isEmpty(str) || ArrayUtil.isEmpty(prefixes)) {
            return false;
        }

        for (final CharSequence prefix : prefixes) {
            if (startWith(str, prefix, true)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 字符串的每一个字符是否都与定义的匹配器匹配
     *
     * @param value   字符串
     * @param matcher 匹配器
     * @return 是否全部匹配
     * @since 3.2.3
     */
    public static boolean isAllCharMatch(CharSequence value, Matcher<Character> matcher) {
        if (CharSequenceUtil.isBlank(value)) {
            return false;
        }
        for (int i = value.length(); --i >= 0; ) {
            if (false == matcher.match(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 替换指定字符串的指定区间内字符为"*"
     * 俗称：脱敏功能，后面其他功能，可以见：DesensitizedUtil(脱敏工具类)
     *
     * <pre>
     * CharSequenceUtil.hide(null,*,*)=null
     * CharSequenceUtil.hide("",0,*)=""
     * CharSequenceUtil.hide("jackduan@163.com",-1,4)   ****duan@163.com
     * CharSequenceUtil.hide("jackduan@163.com",2,3)    ja*kduan@163.com
     * CharSequenceUtil.hide("jackduan@163.com",3,2)    jackduan@163.com
     * CharSequenceUtil.hide("jackduan@163.com",16,16)  jackduan@163.com
     * CharSequenceUtil.hide("jackduan@163.com",16,17)  jackduan@163.com
     * </pre>
     *
     * @param str          字符串
     * @param startInclude 开始位置（包含）
     * @param endExclude   结束位置（不包含）
     * @return 替换后的字符串
     * @since 4.1.14
     */
    public static String hide(CharSequence str, int startInclude, int endExclude) {
        return replaceByCodePoint(str, startInclude, endExclude, '*');
    }

    /**
     * 替换指定字符串的指定区间内字符为固定字符<br>
     * 此方法使用{@link String#codePoints()}完成拆分替换
     *
     * @param str          字符串
     * @param startInclude 开始位置（包含）
     * @param endExclude   结束位置（不包含）
     * @param replacedChar 被替换的字符
     * @return 替换后的字符串
     * @since 5.8.27
     */
    public static String replaceByCodePoint(CharSequence str, int startInclude, int endExclude, char replacedChar) {
        if (isEmpty(str)) {
            return str(str);
        }
        final String originalStr = str(str);
        int[] strCodePoints = originalStr.codePoints().toArray();
        final int strLength = strCodePoints.length;
        if (startInclude > strLength) {
            return originalStr;
        }
        if (endExclude > strLength) {
            endExclude = strLength;
        }
        if (startInclude > endExclude) {
            // 如果起始位置大于结束位置，不替换
            return originalStr;
        }

        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strLength; i++) {
            if (i >= startInclude && i < endExclude) {
                stringBuilder.append(replacedChar);
            } else {
                stringBuilder.append(new String(strCodePoints, i, 1));
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 包装指定字符串<br>
     * 当前缀和后缀一致时使用此方法
     *
     * @param str             被包装的字符串
     * @param prefixAndSuffix 前缀和后缀
     * @return 包装后的字符串
     * @since 3.1.0
     */
    public static String wrap(CharSequence str, CharSequence prefixAndSuffix) {
        return wrap(str, prefixAndSuffix, prefixAndSuffix);
    }

    /**
     * 包装指定字符串
     *
     * @param str    被包装的字符串
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 包装后的字符串
     */
    public static String wrap(CharSequence str, CharSequence prefix, CharSequence suffix) {
        return nullToEmpty(prefix).concat(nullToEmpty(str)).concat(nullToEmpty(suffix));
    }

    /**
     * 检查字符串是否为null、“null”、“undefined”
     *
     * @param str 被检查的字符串
     * @return 是否为null、“null”、“undefined”
     * @since 4.0.10
     */
    public static boolean isNullOrUndefined(CharSequence str) {
        if (null == str) {
            return true;
        }
        return isNullOrUndefinedStr(str);
    }

    /**
     * 是否为“null”、“undefined”，不做空指针检查
     *
     * @param str 字符串
     * @return 是否为“null”、“undefined”
     */
    private static boolean isNullOrUndefinedStr(CharSequence str) {
        String strString = str.toString().trim();
        return NULL.equals(strString) || "undefined".equals(strString);
    }

    /**
     * 反转字符串<br>
     * 例如：abcd =》dcba
     *
     * @param str 被反转的字符串
     * @return 反转后的字符串
     * @since 3.0.9
     */
    public static String reverse(String str) {
        return new String(ArrayUtil.reverse(str.toCharArray()));
    }

    /**
     * <p>指定字符串数组中，是否包含空字符串。</p>
     * <p>如果指定的字符串数组的长度为 0，或者其中的任意一个元素是空字符串，则返回 true。</p>
     * <br>
     *
     * <p>例：</p>
     * <ul>
     *     <li>{@code CharSequenceUtil.hasBlank()                  // true}</li>
     *     <li>{@code CharSequenceUtil.hasBlank("", null, " ")     // true}</li>
     *     <li>{@code CharSequenceUtil.hasBlank("123", " ")        // true}</li>
     *     <li>{@code CharSequenceUtil.hasBlank("123", "abc")      // false}</li>
     * </ul>
     *
     * <ul>
     *     <li>hasBlank(CharSequence...)            等价于 {@code isBlank(...) || isBlank(...) || ...}</li>
     * </ul>
     *
     * @param strs 字符串列表
     * @return 是否包含空字符串
     */
    public static boolean hasBlank(CharSequence... strs) {
        if (ArrayUtil.isEmpty(strs)) {
            return true;
        }

        for (CharSequence str : strs) {
            if (isBlank(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 调用对象的toString方法，null会返回“null”
     *
     * @param obj 对象
     * @return 字符串
     * @see String#valueOf(Object)
     * @since 4.1.3
     */
    public static String toString(Object obj) {
        return String.valueOf(obj);
    }

    /**
     * 获得StringWriter
     *
     * @return StringWriter
     */
    public static StringWriter getWriter() {
        return new StringWriter();
    }

    /**
     * 编码字符串
     *
     * @param str     字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 编码后的字节码
     */
    public static byte[] bytes(CharSequence str, Charset charset) {
        if (str == null) {
            return null;
        }

        if (null == charset) {
            return str.toString().getBytes();
        }
        return str.toString().getBytes(charset);
    }

    /**
     * 编码字符串
     *
     * @param str     字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 编码后的字节码
     */
    public static byte[] bytes(CharSequence str, String charset) {
        return bytes(str, isBlank(charset) ? Charset.defaultCharset() : Charset.forName(charset));
    }

    /**
     * 去掉首部指定长度的字符串并将剩余字符串首字母小写<br>
     * 例如：str=setName, preLength=3 =》 return name
     *
     * @param str       被处理的字符串
     * @param preLength 去掉的长度
     * @return 处理后的字符串，不符合规范返回null
     */
    public static String removePreAndLowerFirst(CharSequence str, int preLength) {
        if (str == null) {
            return null;
        }
        if (str.length() > preLength) {
            char first = Character.toLowerCase(str.charAt(preLength));
            if (str.length() > preLength + 1) {
                return first + str.toString().substring(preLength + 1);
            }
            return String.valueOf(first);
        } else {
            return str.toString();
        }
    }

    /**
     * 去掉字符包装，如果未被包装则返回原字符串
     *
     * @param str             字符串
     * @param prefixAndSuffix 前置和后置字符
     * @return 去掉包装字符的字符串
     * @since 4.0.1
     */
    public static String unWrap(CharSequence str, char prefixAndSuffix) {
        return unWrap(str, prefixAndSuffix, prefixAndSuffix);
    }

    /**
     * 去掉字符包装，如果未被包装则返回原字符串
     *
     * @param str    字符串
     * @param prefix 前置字符
     * @param suffix 后置字符
     * @return 去掉包装字符的字符串
     * @since 4.0.1
     */
    public static String unWrap(CharSequence str, char prefix, char suffix) {
        if (isEmpty(str)) {
            return str(str);
        }
        if (str.charAt(0) == prefix && str.charAt(str.length() - 1) == suffix) {
            return sub(str, 1, str.length() - 1);
        }
        return str.toString();
    }

    /**
     * 编码字符串，编码为UTF-8
     *
     * @param str 字符串
     * @return 编码后的字节码
     */
    public static byte[] utf8Bytes(CharSequence str) {
        return bytes(str, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 将驼峰式命名的字符串转换为下划线方式。如果转换前的驼峰式命名的字符串为空，则返回空字符串。<br>
     * 例如：
     *
     * <pre>
     * HelloWorld=》hello_world
     * Hello_World=》hello_world
     * HelloWorld_test=》hello_world_test
     * </pre>
     *
     * @param str 转换前的驼峰式命名的字符串，也可以为下划线形式
     * @return 转换后下划线方式命名的字符串
     * @see NamingCase#toUnderlineCase(CharSequence)
     */
    public static String toUnderlineCase(CharSequence str) {
        return NamingCase.toUnderlineCase(str);
    }

    /**
     * 重复某个字符
     *
     * <pre>
     * CharSequenceUtil.repeat('e', 0)  = ""
     * CharSequenceUtil.repeat('e', 3)  = "eee"
     * CharSequenceUtil.repeat('e', -2) = ""
     * </pre>
     *
     * @param c     被重复的字符
     * @param count 重复的数目，如果小于等于0则返回""
     * @return 重复字符字符串
     */
    public static String repeat(char c, int count) {
        if (count <= 0) {
            return EMPTY;
        }

        char[] result = new char[count];
        Arrays.fill(result, c);
        return new String(result);
    }

    /**
     * 当给定字符串为null时，转换为Empty
     *
     * @param str 被转换的字符串
     * @return 转换后的字符串
     */
    public static String nullToEmpty(CharSequence str) {
        return nullToDefault(str, EMPTY);
    }

    /**
     * 去除字符串中指定的多个字符，如有多个则全部去除
     *
     * @param str   字符串
     * @param chars 字符列表
     * @return 去除后的字符
     * @since 4.2.2
     */
    public static String removeAll(CharSequence str, char... chars) {
        if (null == str || ArrayUtil.isEmpty(chars)) {
            return str(str);
        }
        final int len = str.length();
        if (0 == len) {
            return str(str);
        }
        final StringBuilder builder = new StringBuilder(len);
        char c;
        for (int i = 0; i < len; i++) {
            c = str.charAt(i);
            if (false == ArrayUtil.contains(chars, c)) {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    /**
     * <p>如果对象是字符串是否为空白，空白的定义如下：</p>
     * <ol>
     *     <li>{@code null}</li>
     *     <li>空字符串：{@code ""}</li>
     *     <li>空格、全角空格、制表符、换行符，等不可见字符</li>
     * </ol>
     *
     * <p>例：</p>
     * <ul>
     *     <li>{@code StrUtil.isBlankIfStr(null)     // true}</li>
     *     <li>{@code StrUtil.isBlankIfStr("")       // true}</li>
     *     <li>{@code StrUtil.isBlankIfStr(" \t\n")  // true}</li>
     *     <li>{@code StrUtil.isBlankIfStr("abc")    // false}</li>
     * </ul>
     *
     * <p>注意：该方法与 {@link #isEmptyIfStr(Object)} 的区别是：
     * 该方法会校验空白字符，且性能相对于 {@link #isEmptyIfStr(Object)} 略慢。</p>
     *
     * @param obj 对象
     * @return 如果为字符串是否为空串
     * @see StrUtil#isBlank(CharSequence)
     * @since 3.3.0
     */
    public static boolean isBlankIfStr(Object obj) {
        if (null == obj) {
            return true;
        } else if (obj instanceof CharSequence) {
            return isBlank((CharSequence) obj);
        }
        return false;
    }

    /**
     * 替换字符字符数组中所有的字符为replacedStr<br>
     * 提供的chars为所有需要被替换的字符，例如："\r\n"，则"\r"和"\n"都会被替换，哪怕他们单独存在
     *
     * @param str         被检查的字符串
     * @param chars       需要替换的字符列表，用一个字符串表示这个字符列表
     * @param replacedStr 替换成的字符串
     * @return 新字符串
     * @since 3.2.2
     */
    public static String replaceChars(CharSequence str, String chars, CharSequence replacedStr) {
        if (isEmpty(str) || isEmpty(chars)) {
            return str(str);
        }
        return replaceChars(str, chars.toCharArray(), replacedStr);
    }

    /**
     * 替换字符字符数组中所有的字符为replacedStr
     *
     * @param str         被检查的字符串
     * @param chars       需要替换的字符列表
     * @param replacedStr 替换成的字符串
     * @return 新字符串
     * @since 3.2.2
     */
    public static String replaceChars(CharSequence str, char[] chars, CharSequence replacedStr) {
        if (isEmpty(str) || ArrayUtil.isEmpty(chars)) {
            return str(str);
        }

        final Set<Character> set = new HashSet<>(chars.length);
        for (char c : chars) {
            set.add(c);
        }
        int strLen = str.length();
        final StringBuilder builder = new StringBuilder();
        char c;
        for (int i = 0; i < strLen; i++) {
            c = str.charAt(i);
            builder.append(set.contains(c) ? replacedStr : c);
        }
        return builder.toString();
    }

    /**
     * 统计指定内容中包含指定字符的数量
     *
     * @param content       内容
     * @param charForSearch 被统计的字符
     * @return 包含数量
     */
    public static int count(CharSequence content, char charForSearch) {
        int count = 0;
        if (isEmpty(content)) {
            return 0;
        }
        int contentLength = content.length();
        for (int i = 0; i < contentLength; i++) {
            if (charForSearch == content.charAt(i)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 查找指定字符串是否包含指定字符串列表中的任意一个字符串<br>
     * 忽略大小写
     *
     * @param str      指定字符串
     * @param testStrs 需要检查的字符串数组
     * @return 是否包含任意一个字符串
     * @since 3.2.0
     */
    public static boolean containsAnyIgnoreCase(CharSequence str, CharSequence... testStrs) {
        return null != getContainsStrIgnoreCase(str, testStrs);
    }

    /**
     * 查找指定字符串是否包含指定字符串列表中的任意一个字符串，如果包含返回找到的第一个字符串<br>
     * 忽略大小写
     *
     * @param str      指定字符串
     * @param testStrs 需要检查的字符串数组
     * @return 被包含的第一个字符串
     * @since 3.2.0
     */
    public static String getContainsStrIgnoreCase(CharSequence str, CharSequence... testStrs) {
        if (isEmpty(str) || ArrayUtil.isEmpty(testStrs)) {
            return null;
        }
        for (CharSequence testStr : testStrs) {
            if (containsIgnoreCase(str, testStr)) {
                return testStr.toString();
            }
        }
        return null;
    }

    /**
     * 截取分隔字符串之前的字符串，不包括分隔字符串<br>
     * 如果给定的字符串为空串（null或""）或者分隔字符串为null，返回原字符串<br>
     * 如果分隔字符串未找到，返回原字符串，举例如下：
     *
     * <pre>
     * CharSequenceUtil.subBefore(null, *, false)      = null
     * CharSequenceUtil.subBefore("", *, false)        = ""
     * CharSequenceUtil.subBefore("abc", 'a', false)   = ""
     * CharSequenceUtil.subBefore("abcba", 'b', false) = "a"
     * CharSequenceUtil.subBefore("abc", 'c', false)   = "ab"
     * CharSequenceUtil.subBefore("abc", 'd', false)   = "abc"
     * </pre>
     *
     * @param string          被查找的字符串
     * @param separator       分隔字符串（不包括）
     * @param isLastSeparator 是否查找最后一个分隔字符串（多次出现分隔字符串时选取最后一个），true为选取最后一个
     * @return 切割后的字符串
     * @since 4.1.15
     */
    public static String subBefore(CharSequence string, char separator, boolean isLastSeparator) {
        if (isEmpty(string)) {
            return null == string ? null : EMPTY;
        }

        final String str = string.toString();
        final int pos = isLastSeparator ? str.lastIndexOf(separator) : str.indexOf(separator);
        if (INDEX_NOT_FOUND == pos) {
            return str;
        }
        if (0 == pos) {
            return EMPTY;
        }
        return str.substring(0, pos);
    }

    /**
     * 截取分隔字符串之后的字符串，不包括分隔字符串<br>
     * 如果给定的字符串为空串（null或""），返回原字符串<br>
     * 如果分隔字符串为空串（null或""），则返回空串，如果分隔字符串未找到，返回空串，举例如下：
     *
     * <pre>
     * CharSequenceUtil.subAfter(null, *, false)      = null
     * CharSequenceUtil.subAfter("", *, false)        = ""
     * CharSequenceUtil.subAfter("abc", 'a', false)   = "bc"
     * CharSequenceUtil.subAfter("abcba", 'b', false) = "cba"
     * CharSequenceUtil.subAfter("abc", 'c', false)   = ""
     * CharSequenceUtil.subAfter("abc", 'd', false)   = ""
     * </pre>
     *
     * @param string          被查找的字符串
     * @param separator       分隔字符串（不包括）
     * @param isLastSeparator 是否查找最后一个分隔字符串（多次出现分隔字符串时选取最后一个），true为选取最后一个
     * @return 切割后的字符串
     * @since 4.1.15
     */
    public static String subAfter(CharSequence string, char separator, boolean isLastSeparator) {
        if (isEmpty(string)) {
            return null == string ? null : EMPTY;
        }
        final String str = string.toString();
        final int pos = isLastSeparator ? str.lastIndexOf(separator) : str.indexOf(separator);
        if (INDEX_NOT_FOUND == pos) {
            return EMPTY;
        }
        return str.substring(pos + 1);
    }

    /**
     * 是否以指定字符串开头，忽略相等字符串的情况
     *
     * @param str    被监测字符串
     * @param prefix 开头字符串
     * @return 是否以指定字符串开头并且两个字符串不相等
     */
    public static boolean startWithIgnoreEquals(CharSequence str, CharSequence prefix) {
        return startWith(str, prefix, false, true);
    }

    /**
     * 格式化文本，使用 {varName} 占位<br>
     * map = {a: "aValue", b: "bValue"} format("{a} and {b}", map) ---=》 aValue and bValue
     *
     * @param template 文本模板，被替换的部分用 {key} 表示
     * @param map      参数值对
     * @return 格式化后的文本
     */
    public static String format(CharSequence template, Map<?, ?> map) {
        return StrFormatter.format(template, map, true);
    }

    /**
     * 格式化文本, {} 表示占位符<br>
     * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
     * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "a", "b") =》 this is a for b<br>
     * 转义{}： format("this is \\{} for {}", "a", "b") =》 this is {} for a<br>
     * 转义\： format("this is \\\\{} for {}", "a", "b") =》 this is \a for b<br>
     *
     * @param template 文本模板，被替换的部分用 {} 表示，如果模板为null，返回"null"
     * @param params   参数值
     * @return 格式化后的文本，如果模板为null，返回"null"
     */
    public static String format(CharSequence template, Object... params) {
        if (null == template) {
            return null;
        }
        if (ArrayUtil.isEmpty(params) || isBlank(template)) {
            return template.toString();
        }
        return StrFormatter.format(template.toString(), params);
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
     * <br>
     *
     * <p>建议：</p>
     * <ul>
     *     <li>该方法建议仅对于客户端（或第三方接口）传入的参数使用该方法。</li>
     * </ul>
     *
     * @param str 被检测的字符串
     * @return 若为空白，则返回 true
     */
    public static boolean isBlank(CharSequence str) {
        final int length;
        if ((str == null) || ((length = str.length()) == 0)) {
            return true;
        }

        for (int i = 0; i < length; i++) {
            // 只要有一个非空字符即为非空字符串
            if (!CharUtil.isBlankChar(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 给定字符串是否被字符包围
     *
     * @param str    字符串
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 是否包围，空串不包围
     */
    public static boolean isSurround(CharSequence str, char prefix, char suffix) {
        if (CharSequenceUtil.isBlank(str)) {
            return false;
        }
        if (str.length() < 2) {
            return false;
        }

        return str.charAt(0) == prefix && str.charAt(str.length() - 1) == suffix;
    }

    /**
     * 将对象转为字符串<br>
     *
     * <pre>
     * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组
     * 2、对象数组会调用Arrays.toString方法
     * </pre>
     *
     * @param obj 对象
     * @return 字符串
     */
    public static String utf8Str(Object obj) {
        return str(obj, CHARSET_UTF_8);
    }

    /**
     * 将对象转为字符串
     * <pre>
     * 	 1、Byte数组和ByteBuffer会被转换为对应字符串的数组
     * 	 2、对象数组会调用Arrays.toString方法
     * </pre>
     *
     * @param obj     对象
     * @param charset 字符集
     * @return 字符串
     */
    public static String str(Object obj, Charset charset) {
        if (null == obj) {
            return null;
        }

        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof byte[]) {
            return str((byte[]) obj, charset);
        } else if (obj instanceof Byte[]) {
            return str((Byte[]) obj, charset);
        } else if (obj instanceof ByteBuffer) {
            return str((ByteBuffer) obj, charset);
        } else if (ArrayUtil.isArray(obj)) {
            return ArrayUtil.toString(obj);
        }

        return obj.toString();
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
     * <p>注意：该方法与 {@link #isBlank(CharSequence)} 的区别是：该方法不校验空白字符。</p>
     * <p>建议：</p>
     * <ul>
     *     <li>该方法建议用于工具类或任何可以预期的方法参数的校验中。</li>
     * </ul>
     *
     * @param str 被检测的字符串
     * @return 是否为空
     * @see #isBlank(CharSequence)
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * {@link CharSequence} 转为字符串，null安全
     *
     * @param cs {@link CharSequence}
     * @return 字符串
     */
    public static String str(CharSequence cs) {
        return null == cs ? null : cs.toString();
    }

    /**
     * 调用对象的toString方法，null会返回{@code null}
     *
     * @param obj 对象
     * @return 字符串 or {@code null}
     * @since 5.7.17
     */
    public static String toStringOrNull(Object obj) {
        return null == obj ? null : obj.toString();
    }

    /**
     * 如果字符串是{@code null}或者&quot;&quot;或者空白，则返回指定默认字符串，否则返回字符串本身。
     *
     * <pre>
     * blankToDefault(null, &quot;default&quot;)  = &quot;default&quot;
     * blankToDefault(&quot;&quot;, &quot;default&quot;)    = &quot;default&quot;
     * blankToDefault(&quot;  &quot;, &quot;default&quot;)  = &quot;default&quot;
     * blankToDefault(&quot;bat&quot;, &quot;default&quot;) = &quot;bat&quot;
     * </pre>
     *
     * @param str        要转换的字符串
     * @param defaultStr 默认字符串
     * @return 字符串本身或指定的默认字符串
     * @since 4.1.0
     */
    public static String blankToDefault(CharSequence str, String defaultStr) {
        return isBlank(str) ? defaultStr : str.toString();
    }

    /**
     * <p>字符串是否为非空白，非空白的定义如下： </p>
     * <ol>
     *     <li>不为 {@code null}</li>
     *     <li>不为空字符串：{@code ""}</li>
     *     <li>不为空格、全角空格、制表符、换行符，等不可见字符</li>
     * </ol>
     *
     * <p>例：</p>
     * <ul>
     *     <li>{@code CharSequenceUtil.isNotBlank(null)     // false}</li>
     *     <li>{@code CharSequenceUtil.isNotBlank("")       // false}</li>
     *     <li>{@code CharSequenceUtil.isNotBlank(" \t\n")  // false}</li>
     *     <li>{@code CharSequenceUtil.isNotBlank("abc")    // true}</li>
     * </ul>
     *
     * <p>建议：仅对于客户端（或第三方接口）传入的参数使用该方法。</p>
     *
     * @param str 被检测的字符串
     * @return 是否为非空
     * @see #isBlank(CharSequence)
     */
    public static boolean isNotBlank(CharSequence str) {
        return !isBlank(str);
    }

    /**
     * 替换 Hutool StrUtil.splitTrim 方法
     * 按分隔符分割字符串并去除每个元素的首尾空白
     *
     * @param str 待分割的字符串
     * @param separator 分隔符
     * @return 分割后的字符串列表
     */
    public static List<String> splitTrim(String str, char separator) {
        List<String> result = new ArrayList<>();
        if (str == null || str.isEmpty()) {
            return result;
        }

        String[] parts = str.split(String.valueOf(separator));
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    /**
     * 切分字符串
     *
     * @param text      被切分的字符串
     * @param separator 分隔符字符
     * @param limit     限制分片数
     * @return 切分后的数组
     */
    public static String[] splitToArray(CharSequence text, char separator, int limit) {
        Assert.notNull(text, "Text must be not null!");
        return StrSplitter.splitToArray(text.toString(), separator, limit, false, false);
    }

    /**
     * 切分字符串，去除切分后每个元素两边的空白符，去除空白项
     *
     * @param str       被切分的字符串
     * @param separator 分隔符字符
     * @return 切分后的集合
     * @since 3.2.0
     */
    public static List<String> splitTrim(final CharSequence str, final CharSequence separator) {
        return split(str, separator, true, true);
    }

    /**
     * 切分字符串，去除切分后每个元素两边的空白符，去除空白项
     *
     * @param str       被切分的字符串
     * @param separator 分隔符字符
     * @return 切分后的集合
     * @since 3.1.2
     */
    public static List<String> splitTrim(CharSequence str, char separator) {
        return splitTrim(str, separator, -1);
    }

    /**
     * 切分字符串，去除切分后每个元素两边的空白符，去除空白项
     *
     * @param str       被切分的字符串
     * @param separator 分隔符字符
     * @param limit     限制分片数，-1不限制
     * @return 切分后的集合
     * @since 3.1.0
     */
    public static List<String> splitTrim(CharSequence str, char separator, int limit) {
        return split(str, separator, limit, true, true);
    }

    /**
     * 切分字符串<br>
     * a#b#c =》 [a,b,c] <br>
     * a##b#c =》 [a,"",b,c]
     *
     * @param str       被切分的字符串
     * @param separator 分隔符字符
     * @return 切分后的集合
     */
    public static List<String> split(CharSequence str, char separator) {
        return split(str, separator, 0);
    }

    /**
     * 切分字符串，不忽略大小写
     *os
     * @param str         被切分的字符串
     * @param separator   分隔符字符串
     * @param isTrim      是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty 是否忽略空串
     * @return 切分后的集合
     * @since 3.0.8
     */
    public static List<String> split(final CharSequence str, final CharSequence separator,
                                     final boolean isTrim, final boolean ignoreEmpty) {
        return split(str, separator, UNLIMITED, isTrim, ignoreEmpty, false);
    }

    /**
     * 切分字符串，不限制分片数量
     *
     * @param str         被切分的字符串
     * @param separator   分隔符字符
     * @param isTrim      是否去除切分字符串后每个元素两边的空格
     * @param ignoreEmpty 是否忽略空串
     * @return 切分后的集合
     * @since 3.0.8
     */
    public static List<String> split(CharSequence str, char separator, boolean isTrim, boolean ignoreEmpty) {
        return split(str, separator, 0, isTrim, ignoreEmpty);
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
     * Trim函数
     *
     * @param isTrim 是否trim
     * @return {@link Function}
     */
    public static Function<String, String> trimFunc(final boolean isTrim) {
        return isTrim ? StrUtil::trim : Function.identity();
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
            return CollUtil.zero();
        } else if (0 == str.length() && ignoreEmpty) {
            return CollUtil.zero();
        }
        if (StrUtil.isEmpty(separator)) {
            throw new IllegalArgumentException("Separator must be not empty!");
        }
        // 查找分隔符的方式
        final TextFinder finder = separator.length() == 1 ?
                new CharFinder(separator.charAt(0), ignoreCase) :
                StrFinder.of(separator, ignoreCase);

        final SplitIter splitIter = new SplitIter(str, finder, limit, ignoreEmpty);
        return splitIter.toList(mapping);
    }

    /**
     * 指定字符是否在字符串中出现过
     *
     * @param str        字符串
     * @param searchChar 被查找的字符
     * @return 是否包含
     * @since 3.1.2
     */
    public static boolean contains(final CharSequence str, final char searchChar) {
        return indexOf(str, searchChar) > -1;
    }

    /**
     * 指定范围内查找指定字符
     *
     * @param str        字符串
     * @param searchChar 被查找的字符
     * @return 位置
     */
    public static int indexOf(final CharSequence str, final char searchChar) {
        return indexOf(str, searchChar, 0);
    }

    /**
     * 指定范围内查找指定字符
     *
     * @param str        字符串
     * @param searchChar 被查找的字符
     * @param start      起始位置，如果小于0，从0开始查找
     * @return 位置
     */
    public static int indexOf(final CharSequence str, final char searchChar, final int start) {
        if (str instanceof String) {
            return ((String) str).indexOf(searchChar, start);
        } else {
            return indexOf(str, searchChar, start, -1);
        }
    }

    /**
     * 指定范围内查找指定字符
     *
     * @param text       字符串
     * @param searchChar 被查找的字符
     * @param start      起始位置，如果小于0，从0开始查找
     * @param end        终止位置，如果超过str.length()则默认查找到字符串末尾
     * @return 位置
     */
    public static int indexOf(final CharSequence text, final char searchChar, final int start, final int end) {
        if (isEmpty(text)) {
            return INDEX_NOT_FOUND;
        }
        return new CharFinder(searchChar).setText(text).setEndIndex(end).start(start);
    }

    /**
     * 是否以指定字符串开头
     *
     * @param str    被监测字符串
     * @param prefix 开头字符串
     * @return 是否以指定字符串开头
     */
    public static boolean startWith(final CharSequence str, final CharSequence prefix) {
        return startWith(str, prefix, false);
    }

    /**
     * 调用对象的toString方法，{@code null}会返回空字符串 ""<br>
     *
     * @param obj 对象
     * @return {@link String }
     * @author Junwei Xu
     */
    public static String toStringOrEmpty(final Object obj) {
        // obj为空时, 返回 null 或 "null" 都不适用部分场景, 此处返回 "" 空字符串
        return null == obj ? EMPTY : obj.toString();
    }

    /**
     * 指定字符串是否被包装
     *
     * @param str        字符串
     * @param prefixChar 前缀
     * @param suffixChar 后缀
     * @return 是否被包装
     */
    public static boolean isWrap(CharSequence str, char prefixChar, char suffixChar) {
        if (null == str || str.length() < 2) {
            return false;
        }

        return str.charAt(0) == prefixChar && str.charAt(str.length() - 1) == suffixChar;
    }

    /**
     * 替代 Hutool StrUtil.subAfter 方法
     *
     * @param string 字符串
     * @param separator 分隔符
     * @param isLastIndexOf 是否查找最后一个分隔符
     * @return 分隔符后的子串
     */
    public static String subAfter(String string, String separator, boolean isLastIndexOf) {
        if (string == null || separator == null) {
            return null;
        }
        if (separator.isEmpty()) {
            return "";
        }
        int index = isLastIndexOf ? string.lastIndexOf(separator) : string.indexOf(separator);
        if (index == -1) {
            return "";
        }
        return string.substring(index + separator.length());
    }

    /**
     * 替代 Hutool StrUtil.subBefore 方法
     *
     * @param string 字符串
     * @param separator 分隔符
     * @param isLastIndexOf 是否查找最后一个分隔符
     * @return 分隔符前的子串
     */
    public static String subBefore(String string, String separator, boolean isLastIndexOf) {
        if (string == null || separator == null) {
            return null;
        }
        if (separator.isEmpty()) {
            return "";
        }
        int index = isLastIndexOf ? string.lastIndexOf(separator) : string.indexOf(separator);
        if (index == -1) {
            return string;
        }
        return string.substring(0, index);
    }

    /**
     * 替代 Hutool StrUtil.isNotEmpty 方法
     *
     * @param str 字符串
     * @return 是否非空
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !isEmpty(str);
    }

    /**
     * 是否以指定字符串开头，忽略大小写
     *
     * @param str    被监测字符串
     * @param prefix 开头字符串
     * @return 是否以指定字符串开头
     */
    public static boolean startWithIgnoreCase(CharSequence str, CharSequence prefix) {
        return startWith(str, prefix, true);
    }

    /**
     * 是否以指定字符串开头<br>
     * 如果给定的字符串和开头字符串都为null则返回true，否则任意一个值为null返回false
     *
     * @param str        被监测字符串
     * @param prefix     开头字符串
     * @param ignoreCase 是否忽略大小写
     * @return 是否以指定字符串开头
     * @since 5.4.3
     */
    public static boolean startWith(CharSequence str, CharSequence prefix, boolean ignoreCase) {
        return startWith(str, prefix, ignoreCase, false);
    }

    /**
     * 是否以指定字符串开头<br>
     * 如果给定的字符串和开头字符串都为null则返回true，否则任意一个值为null返回false<br>
     * <pre>
     *     CharSequenceUtil.startWith("123", "123", false, true);   -- false
     *     CharSequenceUtil.startWith("ABCDEF", "abc", true, true); -- true
     *     CharSequenceUtil.startWith("abc", "abc", true, true);    -- false
     * </pre>
     *
     * @param str          被监测字符串
     * @param prefix       开头字符串
     * @param ignoreCase   是否忽略大小写
     * @param ignoreEquals 是否忽略字符串相等的情况
     * @return 是否以指定字符串开头
     * @since 5.4.3
     */
    public static boolean startWith(CharSequence str, CharSequence prefix, boolean ignoreCase, boolean ignoreEquals) {
        if (null == str || null == prefix) {
            if (ignoreEquals) {
                return false;
            }
            return null == str && null == prefix;
        }

        boolean isStartWith = str.toString()
                .regionMatches(ignoreCase, 0, prefix.toString(), 0, prefix.length());

        if (isStartWith) {
            return (false == ignoreEquals) || (false == equals(str, prefix, ignoreCase));
        }
        return false;
    }

    /**
     * 比较两个字符串是否相等，规则如下
     * <ul>
     *     <li>str1和str2都为{@code null}</li>
     *     <li>忽略大小写使用{@link String#equalsIgnoreCase(String)}判断相等</li>
     *     <li>不忽略大小写使用{@link String#contentEquals(CharSequence)}判断相等</li>
     * </ul>
     *
     * @param str1       要比较的字符串1
     * @param str2       要比较的字符串2
     * @param ignoreCase 是否忽略大小写
     * @return 如果两个字符串相同，或者都是{@code null}，则返回{@code true}
     * @since 3.2.0
     */
    public static boolean equals(CharSequence str1, CharSequence str2, boolean ignoreCase) {
        if (null == str1) {
            // 只有两个都为null才判断相等
            return str2 == null;
        }
        if (null == str2) {
            // 字符串2空，字符串1非空，直接false
            return false;
        }

        if (ignoreCase) {
            return str1.toString().equalsIgnoreCase(str2.toString());
        } else {
            return str1.toString().contentEquals(str2);
        }
    }

    /**
     * 创建StringBuilder对象
     *
     * @return StringBuilder对象
     */
    public static StringBuilder builder() {
        return new StringBuilder();
    }

    /**
     * 限制字符串长度，如果超过指定长度，截取指定长度并在末尾加"..."
     *
     * @param string 字符串
     * @param length 最大长度
     * @return 切割后的剩余的前半部分字符串+"..."
     * @since 4.0.10
     */
    public static String maxLength(CharSequence string, int length) {
        Assert.isTrue(length > 0, "length must be greater than 0");

        if (null == string) {
            return null;
        }
        if (string.length() <= length) {
            return string.toString();
        }
        return sub(string, 0, length) + "...";
    }

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
     * <p>注意：该方法与 {@link #isNotBlank(CharSequence)} 的区别是：该方法不校验空白字符。</p>
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
     * 改进JDK subString<br>
     * index从0开始计算，最后一个字符为-1<br>
     * 如果from和to位置一样，返回 "" <br>
     * 如果from或to为负数，则按照length从后向前数位置，如果绝对值大于字符串长度，则from归到0，to归到length<br>
     * 如果经过修正的index中from大于to，则互换from和to example: <br>
     * abcdefgh 2 3 =》 c <br>
     * abcdefgh 2 -3 =》 cde <br>
     *
     * @param str              String
     * @param fromIndexInclude 开始的index（包括）
     * @param toIndexExclude   结束的index（不包括）
     * @return 字串
     */
    public static String sub(CharSequence str, int fromIndexInclude, int toIndexExclude) {
        if (isEmpty(str)) {
            return str(str);
        }
        int len = str.length();

        if (fromIndexInclude < 0) {
            fromIndexInclude = len + fromIndexInclude;
            if (fromIndexInclude < 0) {
                fromIndexInclude = 0;
            }
        } else if (fromIndexInclude > len) {
            fromIndexInclude = len;
        }

        if (toIndexExclude < 0) {
            toIndexExclude = len + toIndexExclude;
            if (toIndexExclude < 0) {
                toIndexExclude = len;
            }
        } else if (toIndexExclude > len) {
            toIndexExclude = len;
        }

        if (toIndexExclude < fromIndexInclude) {
            int tmp = fromIndexInclude;
            fromIndexInclude = toIndexExclude;
            toIndexExclude = tmp;
        }

        if (fromIndexInclude == toIndexExclude) {
            return EMPTY;
        }

        return str.toString().substring(fromIndexInclude, toIndexExclude);
    }

    /**
     * 字符串是否以给定字符开始
     *
     * @param str 字符串
     * @param c   字符
     * @return 是否开始
     */
    public static boolean startWith(CharSequence str, char c) {
        if (isEmpty(str)) {
            return false;
        }
        return c == str.charAt(0);
    }

    /**
     * 切割指定位置之后部分的字符串
     *
     * @param string    字符串
     * @param fromIndex 切割开始的位置（包括）
     * @return 切割后后剩余的后半部分字符串
     */
    public static String subSuf(CharSequence string, int fromIndex) {
        if (isEmpty(string)) {
            return null;
        }
        return sub(string, fromIndex, string.length());
    }

    /**
     * 截取分隔字符串之前的字符串，不包括分隔字符串<br>
     * 如果给定的字符串为空串（null或""）或者分隔字符串为null，返回原字符串<br>
     * 如果分隔字符串为空串""，则返回空串，如果分隔字符串未找到，返回原字符串，举例如下：
     *
     * <pre>
     * CharSequenceUtil.subBefore(null, *, false)      = null
     * CharSequenceUtil.subBefore("", *, false)        = ""
     * CharSequenceUtil.subBefore("abc", "a", false)   = ""
     * CharSequenceUtil.subBefore("abcba", "b", false) = "a"
     * CharSequenceUtil.subBefore("abc", "c", false)   = "ab"
     * CharSequenceUtil.subBefore("abc", "d", false)   = "abc"
     * CharSequenceUtil.subBefore("abc", "", false)    = ""
     * CharSequenceUtil.subBefore("abc", null, false)  = "abc"
     * </pre>
     *
     * @param string          被查找的字符串
     * @param separator       分隔字符串（不包括）
     * @param isLastSeparator 是否查找最后一个分隔字符串（多次出现分隔字符串时选取最后一个），true为选取最后一个
     * @return 切割后的字符串
     * @since 3.1.1
     */
    public static String subBefore(CharSequence string, CharSequence separator, boolean isLastSeparator) {
        if (isEmpty(string) || separator == null) {
            return null == string ? null : string.toString();
        }

        final String str = string.toString();
        final String sep = separator.toString();
        if (sep.isEmpty()) {
            return EMPTY;
        }
        final int pos = isLastSeparator ? str.lastIndexOf(sep) : str.indexOf(sep);
        if (INDEX_NOT_FOUND == pos) {
            return str;
        }
        if (0 == pos) {
            return EMPTY;
        }
        return str.substring(0, pos);
    }

    /**
     * 截取分隔字符串之后的字符串，不包括分隔字符串<br>
     * 如果给定的字符串为空串（null或""），返回原字符串<br>
     * 如果分隔字符串为空串（null或""），则返回空串，如果分隔字符串未找到，返回空串，举例如下：
     *
     * <pre>
     * CharSequenceUtil.subAfter(null, *, false)      = null
     * CharSequenceUtil.subAfter("", *, false)        = ""
     * CharSequenceUtil.subAfter(*, null, false)      = ""
     * CharSequenceUtil.subAfter("abc", "a", false)   = "bc"
     * CharSequenceUtil.subAfter("abcba", "b", false) = "cba"
     * CharSequenceUtil.subAfter("abc", "c", false)   = ""
     * CharSequenceUtil.subAfter("abc", "d", false)   = ""
     * CharSequenceUtil.subAfter("abc", "", false)    = "abc"
     * </pre>
     *
     * @param string          被查找的字符串
     * @param separator       分隔字符串（不包括）
     * @param isLastSeparator 是否查找最后一个分隔字符串（多次出现分隔字符串时选取最后一个），true为选取最后一个
     * @return 切割后的字符串
     * @since 3.1.1
     */
    public static String subAfter(CharSequence string, CharSequence separator, boolean isLastSeparator) {
        if (isEmpty(string)) {
            return null == string ? null : EMPTY;
        }
        if (separator == null) {
            return EMPTY;
        }
        final String str = string.toString();
        final String sep = separator.toString();
        final int pos = isLastSeparator ? str.lastIndexOf(sep) : str.indexOf(sep);
        if (INDEX_NOT_FOUND == pos || (string.length() - 1) == pos) {
            return EMPTY;
        }
        return str.substring(pos + separator.length());
    }

    /**
     * 给定字符串是否以任何一个字符串开始<br>
     * 给定字符串和数组为空都返回false
     *
     * @param str      给定字符串
     * @param prefixes 需要检测的开始字符串
     * @return 给定字符串是否以任何一个字符串开始
     * @since 3.0.6
     */
    public static boolean startWithAny(CharSequence str, CharSequence... prefixes) {
        if (isEmpty(str) || ArrayUtil.isEmpty(prefixes)) {
            return false;
        }

        for (CharSequence prefix : prefixes) {
            if (startWith(str, prefix, false)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取字符串的长度，如果为null返回0
     *
     * @param cs a 字符串
     * @return 字符串的长度，如果为null返回0
     * @since 4.3.2
     */
    public static int length(CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    /**
     * 如果字符串是 {@code null}，则返回指定默认字符串，否则返回字符串本身。
     *
     * <pre>
     * nullToDefault(null, &quot;default&quot;)  = &quot;default&quot;
     * nullToDefault(&quot;&quot;, &quot;default&quot;)    = &quot;&quot;
     * nullToDefault(&quot;  &quot;, &quot;default&quot;)  = &quot;  &quot;
     * nullToDefault(&quot;bat&quot;, &quot;default&quot;) = &quot;bat&quot;
     * </pre>
     *
     * @param str        要转换的字符串
     * @param defaultStr 默认字符串
     * @return 字符串本身或指定的默认字符串
     */
    public static String nullToDefault(CharSequence str, String defaultStr) {
        return (str == null) ? defaultStr : str.toString();
    }

    /**
     * 给定字符串是否以任何一个字符串结尾（忽略大小写）<br>
     * 给定字符串和数组为空都返回false
     *
     * @param str      给定字符串
     * @param suffixes 需要检测的结尾字符串
     * @return 给定字符串是否以任何一个字符串结尾
     * @since 5.5.9
     */
    public static boolean endWithAnyIgnoreCase(CharSequence str, CharSequence... suffixes) {
        if (isEmpty(str) || ArrayUtil.isEmpty(suffixes)) {
            return false;
        }

        for (CharSequence suffix : suffixes) {
            if (endWith(str, suffix, true)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否以指定字符串结尾<br>
     * 如果给定的字符串和开头字符串都为null则返回true，否则任意一个值为null返回false
     *
     * @param str        被监测字符串
     * @param suffix     结尾字符串
     * @param ignoreCase 是否忽略大小写
     * @return 是否以指定字符串结尾
     */
    public static boolean endWith(CharSequence str, CharSequence suffix, boolean ignoreCase) {
        return endWith(str, suffix, ignoreCase, false);
    }

    /**
     * 是否以指定字符串结尾<br>
     * 如果给定的字符串和开头字符串都为null则返回true，否则任意一个值为null返回false
     *
     * @param str          被监测字符串
     * @param suffix       结尾字符串
     * @param ignoreCase   是否忽略大小写
     * @param ignoreEquals 是否忽略字符串相等的情况
     * @return 是否以指定字符串结尾
     * @since 5.8.0
     */
    public static boolean endWith(CharSequence str, CharSequence suffix, boolean ignoreCase, boolean ignoreEquals) {
        if (null == str || null == suffix) {
            if (ignoreEquals) {
                return false;
            }
            return null == str && null == suffix;
        }

        final int strOffset = str.length() - suffix.length();
        boolean isEndWith = str.toString()
                .regionMatches(ignoreCase, strOffset, suffix.toString(), 0, suffix.length());

        if (isEndWith) {
            return (false == ignoreEquals) || (false == equals(str, suffix, ignoreCase));
        }
        return false;
    }

    /**
     * 截取指定字符串中间部分，不包括标识字符串<br>
     * <p>
     * 栗子：
     *
     * <pre>
     * CharSequenceUtil.subBetween("wx[b]yz", "[", "]") = "b"
     * CharSequenceUtil.subBetween(null, *, *)          = null
     * CharSequenceUtil.subBetween(*, null, *)          = null
     * CharSequenceUtil.subBetween(*, *, null)          = null
     * CharSequenceUtil.subBetween("", "", "")          = ""
     * CharSequenceUtil.subBetween("", "", "]")         = null
     * CharSequenceUtil.subBetween("", "[", "]")        = null
     * CharSequenceUtil.subBetween("yabcz", "", "")     = ""
     * CharSequenceUtil.subBetween("yabcz", "y", "z")   = "abc"
     * CharSequenceUtil.subBetween("yabczyabcz", "y", "z")   = "abc"
     * </pre>
     *
     * @param str    被切割的字符串
     * @param before 截取开始的字符串标识
     * @param after  截取到的字符串标识
     * @return 截取后的字符串
     * @since 3.1.1
     */
    public static String subBetween(CharSequence str, CharSequence before, CharSequence after) {
        if (str == null || before == null || after == null) {
            return null;
        }

        final String str2 = str.toString();
        final String before2 = before.toString();
        final String after2 = after.toString();

        final int start = str2.indexOf(before2);
        if (start != INDEX_NOT_FOUND) {
            final int end = str2.indexOf(after2, start + before2.length());
            if (end != INDEX_NOT_FOUND) {
                return str2.substring(start + before2.length(), end);
            }
        }
        return null;
    }

    /**
     * 去掉指定后缀
     *
     * @param str    字符串
     * @param suffix 后缀
     * @return 切掉后的字符串，若后缀不是 suffix， 返回原字符串
     */
    public static String removeSuffix(CharSequence str, CharSequence suffix) {
        if (isEmpty(str) || isEmpty(suffix)) {
            return str(str);
        }

        final String str2 = str.toString();
        if (str2.endsWith(suffix.toString())) {
            return subPre(str2, str2.length() - suffix.length());// 截取前半段
        }
        return str2;
    }

    /**
     * 切割指定位置之前部分的字符串
     *
     * @param string         字符串
     * @param toIndexExclude 切割到的位置（不包括）
     * @return 切割后的剩余的前半部分字符串
     */
    public static String subPre(CharSequence string, int toIndexExclude) {
        return sub(string, 0, toIndexExclude);
    }

    /**
     * 除去字符串头尾部的空白，如果字符串是{@code null}，依然返回{@code null}。
     *
     * <p>
     * 注意，和{@link String#trim()}不同，此方法使用{@link CharUtil#isBlankChar(char)} 来判定空白， 因而可以除去英文字符集之外的其它空白，如中文空格。
     *
     * <pre>
     * trim(null)          = null
     * trim(&quot;&quot;)            = &quot;&quot;
     * trim(&quot;     &quot;)       = &quot;&quot;
     * trim(&quot;abc&quot;)         = &quot;abc&quot;
     * trim(&quot;    abc    &quot;) = &quot;abc&quot;
     * </pre>
     *
     * @param str 要处理的字符串
     * @return 除去头尾空白的字符串，如果原字串为{@code null}，则返回{@code null}
     */
    public static String trim(CharSequence str) {
        return (null == str) ? null : trim(str, 0);
    }

    /**
     * 除去字符串头尾部的空白符，如果字符串是{@code null}，依然返回{@code null}。
     *
     * @param str  要处理的字符串
     * @param mode {@code -1}表示trimStart，{@code 0}表示trim全部， {@code 1}表示trimEnd
     * @return 除去指定字符后的的字符串，如果原字串为{@code null}，则返回{@code null}
     */
    public static String trim(CharSequence str, int mode) {
        return trim(str, mode, CharUtil::isBlankChar);
    }

    /**
     * 按照断言，除去字符串头尾部的断言为真的字符，如果字符串是{@code null}，依然返回{@code null}。
     *
     * @param str       要处理的字符串
     * @param mode      {@code -1}表示trimStart，{@code 0}表示trim全部， {@code 1}表示trimEnd
     * @param predicate 断言是否过掉字符，返回{@code true}表述过滤掉，{@code false}表示不过滤
     * @return 除去指定字符后的的字符串，如果原字串为{@code null}，则返回{@code null}
     * @since 5.7.4
     */
    public static String trim(CharSequence str, int mode, Predicate<Character> predicate) {
        String result;
        if (str == null) {
            result = null;
        } else {
            int length = str.length();
            int start = 0;
            int end = length;// 扫描字符串头部
            if (mode <= 0) {
                while ((start < end) && (predicate.test(str.charAt(start)))) {
                    start++;
                }
            }// 扫描字符串尾部
            if (mode >= 0) {
                while ((start < end) && (predicate.test(str.charAt(end - 1)))) {
                    end--;
                }
            }
            if ((start > 0) || (end < length)) {
                result = str.toString().substring(start, end);
            } else {
                result = str.toString();
            }
        }

        return result;
    }

    /**
     * 切分字符串，不去除切分后每个元素两边的空白符，不去除空白项
     *
     * @param str       被切分的字符串
     * @param separator 分隔符字符
     * @param limit     限制分片数，-1不限制
     * @return 切分后的集合
     */
    public static List<String> split(CharSequence str, char separator, int limit) {
        return split(str, separator, limit, false, false);
    }

    /**
     * 切分字符串
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
        return StrSplitter.split(str, separator, limit, isTrim, ignoreEmpty);
    }

    /**
     * 如果给定字符串不是以prefix开头的，在开头补充 prefix
     *
     * @param str    字符串
     * @param prefix 前缀
     * @return 补充后的字符串
     * @see #prependIfMissing(CharSequence, CharSequence, CharSequence...)
     */
    public static String addPrefixIfNot(CharSequence str, CharSequence prefix) {
        return prependIfMissing(str, prefix, prefix);
    }

    /**
     * 如果给定字符串不是以给定的一个或多个字符串为开头，则在首部添加起始字符串<br>
     * 不忽略大小写
     *
     * @param str      被检查的字符串
     * @param prefix   需要添加到首部的字符串
     * @param prefixes 需要额外检查的首部字符串，如果以这些中的一个为起始，则不再添加
     * @return 如果已经结尾，返回原字符串，否则返回添加结尾的字符串
     * @since 3.0.7
     */
    public static String prependIfMissing(CharSequence str, CharSequence prefix, CharSequence... prefixes) {
        return prependIfMissing(str, prefix, false, prefixes);
    }

    /**
     * 如果给定字符串不是以给定的一个或多个字符串为开头，则在首部添加起始字符串
     *
     * @param str        被检查的字符串
     * @param prefix     需要添加到首部的字符串
     * @param ignoreCase 检查结尾时是否忽略大小写
     * @param prefixes   需要额外检查的首部字符串，如果以这些中的一个为起始，则不再添加
     * @return 如果已经结尾，返回原字符串，否则返回添加结尾的字符串
     * @since 3.0.7
     */
    public static String prependIfMissing(CharSequence str, CharSequence prefix, boolean ignoreCase, CharSequence... prefixes) {
        if (str == null || isEmpty(prefix) || startWith(str, prefix, ignoreCase)) {
            return str(str);
        }
        if (prefixes != null) {
            for (final CharSequence s : prefixes) {
                if (startWith(str, s, ignoreCase)) {
                    return str.toString();
                }
            }
        }
        return prefix.toString().concat(str.toString());
    }

    /**
     * 如果字符串是{@code null}或者&quot;&quot;，则返回指定默认字符串，否则返回字符串本身。
     *
     * <pre>
     * emptyToDefault(null, &quot;default&quot;)  = &quot;default&quot;
     * emptyToDefault(&quot;&quot;, &quot;default&quot;)    = &quot;default&quot;
     * emptyToDefault(&quot;  &quot;, &quot;default&quot;)  = &quot;  &quot;
     * emptyToDefault(&quot;bat&quot;, &quot;default&quot;) = &quot;bat&quot;
     * </pre>
     *
     * @param str        要转换的字符串
     * @param defaultStr 默认字符串
     * @return 字符串本身或指定的默认字符串
     * @since 4.1.0
     */
    public static String emptyToDefault(CharSequence str, String defaultStr) {
        return isEmpty(str) ? defaultStr : str.toString();
    }

    /**
     * 指定字符串是否在字符串中出现过
     *
     * @param str       字符串
     * @param searchStr 被查找的字符串
     * @return 是否包含
     * @since 5.1.1
     */
    public static boolean contains(CharSequence str, CharSequence searchStr) {
        if (null == str || null == searchStr) {
            return false;
        }
        return str.toString().contains(searchStr);
    }

    /**
     * 是否包含特定字符，忽略大小写，如果给定两个参数都为{@code null}，返回true
     *
     * @param str     被检测字符串
     * @param testStr 被测试是否包含的字符串
     * @return 是否包含
     */
    public static boolean containsIgnoreCase(CharSequence str, CharSequence testStr) {
        if (null == str) {
            // 如果被监测字符串和
            return null == testStr;
        }
        return indexOfIgnoreCase(str, testStr) > -1;
    }

    /**
     * 指定范围内查找字符串，忽略大小写<br>
     *
     * <pre>
     * CharSequenceUtil.indexOfIgnoreCase(null, *, *)          = -1
     * CharSequenceUtil.indexOfIgnoreCase(*, null, *)          = -1
     * CharSequenceUtil.indexOfIgnoreCase("", "", 0)           = 0
     * CharSequenceUtil.indexOfIgnoreCase("aabaabaa", "A", 0)  = 0
     * CharSequenceUtil.indexOfIgnoreCase("aabaabaa", "B", 0)  = 2
     * CharSequenceUtil.indexOfIgnoreCase("aabaabaa", "AB", 0) = 1
     * CharSequenceUtil.indexOfIgnoreCase("aabaabaa", "B", 3)  = 5
     * CharSequenceUtil.indexOfIgnoreCase("aabaabaa", "B", 9)  = -1
     * CharSequenceUtil.indexOfIgnoreCase("aabaabaa", "B", -1) = 2
     * CharSequenceUtil.indexOfIgnoreCase("aabaabaa", "", 2)   = 2
     * CharSequenceUtil.indexOfIgnoreCase("abc", "", 9)        = -1
     * </pre>
     *
     * @param str       字符串
     * @param searchStr 需要查找位置的字符串
     * @return 位置
     * @since 3.2.1
     */
    public static int indexOfIgnoreCase(final CharSequence str, final CharSequence searchStr) {
        return indexOfIgnoreCase(str, searchStr, 0);
    }

    /**
     * 指定范围内查找字符串
     *
     * <pre>
     * CharSequenceUtil.indexOfIgnoreCase(null, *, *)          = -1
     * CharSequenceUtil.indexOfIgnoreCase(*, null, *)          = -1
     * CharSequenceUtil.indexOfIgnoreCase("", "", 0)           = 0
     * CharSequenceUtil.indexOfIgnoreCase("aabaabaa", "A", 0)  = 0
     * CharSequenceUtil.indexOfIgnoreCase("aabaabaa", "B", 0)  = 2
     * CharSequenceUtil.indexOfIgnoreCase("aabaabaa", "AB", 0) = 1
     * CharSequenceUtil.indexOfIgnoreCase("aabaabaa", "B", 3)  = 5
     * CharSequenceUtil.indexOfIgnoreCase("aabaabaa", "B", 9)  = -1
     * CharSequenceUtil.indexOfIgnoreCase("aabaabaa", "B", -1) = 2
     * CharSequenceUtil.indexOfIgnoreCase("aabaabaa", "", 2)   = 2
     * CharSequenceUtil.indexOfIgnoreCase("abc", "", 9)        = -1
     * </pre>
     *
     * @param str       字符串
     * @param searchStr 需要查找位置的字符串
     * @param fromIndex 起始位置
     * @return 位置
     * @since 3.2.1
     */
    public static int indexOfIgnoreCase(final CharSequence str, final CharSequence searchStr, int fromIndex) {
        return indexOf(str, searchStr, fromIndex, true);
    }

    /**
     * 指定范围内查找字符串
     *
     * @param text       字符串，空则返回-1
     * @param searchStr  需要查找位置的字符串，空则返回-1
     * @param from       起始位置（包含）
     * @param ignoreCase 是否忽略大小写
     * @return 位置
     * @since 3.2.1
     */
    public static int indexOf(CharSequence text, CharSequence searchStr, int from, boolean ignoreCase) {
        if (isEmpty(text) || isEmpty(searchStr)) {
            if (equals(text, searchStr)) {
                return 0;
            } else {
                return INDEX_NOT_FOUND;
            }
        }
        return new StrFinder(searchStr, ignoreCase).setText(text).start(from);
    }

    /**
     * 比较两个字符串（大小写敏感）。
     *
     * <pre>
     * equals(null, null)   = true
     * equals(null, &quot;abc&quot;)  = false
     * equals(&quot;abc&quot;, null)  = false
     * equals(&quot;abc&quot;, &quot;abc&quot;) = true
     * equals(&quot;abc&quot;, &quot;ABC&quot;) = false
     * </pre>
     *
     * @param str1 要比较的字符串1
     * @param str2 要比较的字符串2
     * @return 如果两个字符串相同，或者都是{@code null}，则返回{@code true}
     */
    public static boolean equals(CharSequence str1, CharSequence str2) {
        return equals(str1, str2, false);
    }

    /**
     * 截取两个字符串的不同部分（长度一致），判断截取的子串是否相同<br>
     * 任意一个字符串为null返回false
     *
     * @param str1       第一个字符串
     * @param start1     第一个字符串开始的位置
     * @param str2       第二个字符串
     * @param start2     第二个字符串开始的位置
     * @param length     截取长度
     * @param ignoreCase 是否忽略大小写
     * @return 子串是否相同
     * @since 3.2.1
     */
    public static boolean isSubEquals(CharSequence str1, int start1, CharSequence str2, int start2, int length, boolean ignoreCase) {
        if (null == str1 || null == str2) {
            return false;
        }

        return str1.toString().regionMatches(ignoreCase, start1, str2.toString(), start2, length);
    }

    /**
     * <p>如果对象是字符串是否为空串，空的定义如下：</p><br>
     * <ol>
     *     <li>{@code null}</li>
     *     <li>空字符串：{@code ""}</li>
     * </ol>
     *
     * <p>例：</p>
     * <ul>
     *     <li>{@code StrUtil.isEmptyIfStr(null)     // true}</li>
     *     <li>{@code StrUtil.isEmptyIfStr("")       // true}</li>
     *     <li>{@code StrUtil.isEmptyIfStr(" \t\n")  // false}</li>
     *     <li>{@code StrUtil.isEmptyIfStr("abc")    // false}</li>
     * </ul>
     *
     *
     * @param obj 对象
     * @return 如果为字符串是否为空串
     * @since 3.3.0
     */
    public static boolean isEmptyIfStr(Object obj) {
        if (null == obj) {
            return true;
        } else if (obj instanceof CharSequence) {
            return 0 == ((CharSequence) obj).length();
        }
        return false;
    }

    /**
     * 查找指定字符串是否包含指定字符串列表中的任意一个字符串
     *
     * @param str      指定字符串
     * @param testStrs 需要检查的字符串数组
     * @return 是否包含任意一个字符串
     * @since 3.2.0
     */
    public static boolean containsAny(CharSequence str, CharSequence... testStrs) {
        return null != getContainsStr(str, testStrs);
    }

    /**
     * 查找指定字符串是否包含指定字符串列表中的任意一个字符串，如果包含返回找到的第一个字符串
     *
     * @param str      指定字符串
     * @param testStrs 需要检查的字符串数组
     * @return 被包含的第一个字符串
     * @since 3.2.0
     */
    public static String getContainsStr(CharSequence str, CharSequence... testStrs) {
        if (isEmpty(str) || ArrayUtil.isEmpty(testStrs)) {
            return null;
        }
        for (CharSequence checkStr : testStrs) {
            if (null != checkStr && str.toString().contains(checkStr)) {
                return checkStr.toString();
            }
        }
        return null;
    }

    /**
     * 查找指定字符串是否包含指定字符列表中的任意一个字符
     *
     * @param str       指定字符串
     * @param testChars 需要检查的字符数组
     * @return 是否包含任意一个字符
     * @since 4.1.11
     */
    public static boolean containsAny(CharSequence str, char... testChars) {
        if (false == isEmpty(str)) {
            int len = str.length();
            for (int i = 0; i < len; i++) {
                if (ArrayUtil.contains(testChars, str.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否以指定字符串结尾
     *
     * @param str    被监测字符串
     * @param suffix 结尾字符串
     * @return 是否以指定字符串结尾
     */
    public static boolean endWith(CharSequence str, CharSequence suffix) {
        return endWith(str, suffix, false);
    }

    /**
     * 字符串是否以给定字符结尾
     *
     * @param str 字符串
     * @param c   字符
     * @return 是否结尾
     */
    public static boolean endWith(CharSequence str, char c) {
        if (isEmpty(str)) {
            return false;
        }
        return c == str.charAt(str.length() - 1);
    }

    /**
     * 大写首字母<br>
     * 例如：str = name, return Name
     *
     * @param str 字符串
     * @return 字符串
     */
    public static String upperFirst(CharSequence str) {
        if (null == str) {
            return null;
        }
        if (str.length() > 0) {
            char firstChar = str.charAt(0);
            if (Character.isLowerCase(firstChar)) {
                return Character.toUpperCase(firstChar) + subSuf(str, 1);
            }
        }
        return str.toString();
    }

    /**
     * 忽略大小写去掉指定前缀
     *
     * @param str    字符串
     * @param prefix 前缀
     * @return 切掉后的字符串，若前缀不是 prefix， 返回原字符串
     */
    public static String removePrefixIgnoreCase(CharSequence str, CharSequence prefix) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return str(str);
        }

        final String str2 = str.toString();
        if (startWithIgnoreCase(str, prefix)) {
            return subSuf(str2, prefix.length());// 截取后半段
        }
        return str2;
    }

    /**
     * 除去字符串头部的空白，如果字符串是{@code null}，则返回{@code null}。
     *
     * <p>
     * 注意，和{@link String#trim()}不同，此方法使用{@link CharUtil#isBlankChar(char)} 来判定空白， 因而可以除去英文字符集之外的其它空白，如中文空格。
     *
     * <pre>
     * trimStart(null)         = null
     * trimStart(&quot;&quot;)           = &quot;&quot;
     * trimStart(&quot;abc&quot;)        = &quot;abc&quot;
     * trimStart(&quot;  abc&quot;)      = &quot;abc&quot;
     * trimStart(&quot;abc  &quot;)      = &quot;abc  &quot;
     * trimStart(&quot; abc &quot;)      = &quot;abc &quot;
     * </pre>
     *
     * @param str 要处理的字符串
     * @return 除去空白的字符串，如果原字串为{@code null}或结果字符串为{@code ""}，则返回 {@code null}
     */
    public static String trimStart(CharSequence str) {
        return trim(str, -1);
    }

    /**
     * 比较两个字符串（大小写不敏感）。
     *
     * <pre>
     * equalsIgnoreCase(null, null)   = true
     * equalsIgnoreCase(null, &quot;abc&quot;)  = false
     * equalsIgnoreCase(&quot;abc&quot;, null)  = false
     * equalsIgnoreCase(&quot;abc&quot;, &quot;abc&quot;) = true
     * equalsIgnoreCase(&quot;abc&quot;, &quot;ABC&quot;) = true
     * </pre>
     *
     * @param str1 要比较的字符串1
     * @param str2 要比较的字符串2
     * @return 如果两个字符串相同，或者都是{@code null}，则返回{@code true}
     */
    public static boolean equalsIgnoreCase(CharSequence str1, CharSequence str2) {
        return equals(str1, str2, true);
    }

    /**
     * 创建StringBuilder对象
     *
     * @param capacity 初始大小
     * @return StringBuilder对象
     */
    public static StringBuilder builder(int capacity) {
        return new StringBuilder(capacity);
    }

    /**
     * 是否以指定字符串结尾，忽略大小写
     *
     * @param str    被监测字符串
     * @param suffix 结尾字符串
     * @return 是否以指定字符串结尾
     */
    public static boolean endWithIgnoreCase(final CharSequence str, final CharSequence suffix) {
        return endWith(str, suffix, true);
    }

    /**
     * 清理空白字符
     *
     * @param str 被清理的字符串
     * @return 清理后的字符串
     */
    public static String cleanBlank(CharSequence str) {
        return filter(str, c -> false == CharUtil.isBlankChar(c));
    }

    /**
     * 过滤字符串
     *
     * @param str    字符串
     * @param filter 过滤器，{@link Filter#accept(Object)}返回为{@code true}的保留字符
     * @return 过滤后的字符串
     * @since 5.4.0
     */
    public static String filter(CharSequence str, final Filter<Character> filter) {
        if (str == null || filter == null) {
            return str(str);
        }

        int len = str.length();
        final StringBuilder sb = new StringBuilder(len);
        char c;
        for (int i = 0; i < len; i++) {
            c = str.charAt(i);
            if (filter.accept(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 去掉指定前缀
     *
     * @param str    字符串，空返回原字符串
     * @param prefix 前缀，空返回原字符串
     * @return 切掉后的字符串，若前缀不是 prefix， 返回原字符串
     */
    public static String removePrefix(final CharSequence str, final CharSequence prefix) {
        return removePrefix(str, prefix, false);
    }

    /**
     * 去掉指定前缀
     *
     * @param str        字符串，空返回原字符串
     * @param prefix     前缀，空返回原字符串
     * @param ignoreCase 是否忽略大小写
     * @return 切掉后的字符串，若前缀不是 prefix， 返回原字符串
     */
    public static String removePrefix(final CharSequence str, final CharSequence prefix, final boolean ignoreCase) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return toStringOrNull(str);
        }

        final String str2 = str.toString();
        if (startWith(str, prefix, ignoreCase)) {
            return subSuf(str2, prefix.length());// 截取后半段
        }
        return str2;
    }

    /**
     * 切分字符串
     *
     * @param str       被切分的字符串
     * @param separator 分隔符字符
     * @return 切分后的数组
     */
    public static String[] splitToArray(CharSequence str, char separator) {
        return splitToArray(str, separator, 0);
    }

    /**
     * 切分字符串，如果分隔符不存在则返回原字符串
     *
     * @param str       被切分的字符串
     * @param separator 分隔符
     * @return 字符串
     * @since 5.6.7
     */
    public static String[] splitToArray(CharSequence str, CharSequence separator) {
        if (str == null) {
            return new String[]{};
        }

        return StrSplitter.splitToArray(str.toString(), str(separator), 0, false, false);
    }

    public static String toCamelCase(String input) {
        if (input == null) return null;
        StringBuilder sb = new StringBuilder();
        boolean nextUpper = false;
        for (char c : input.toCharArray()) {
            if (c == '_' || c == '-') {
                nextUpper = true;
            } else {
                sb.append(nextUpper ? Character.toUpperCase(c) : Character.toLowerCase(c));
                nextUpper = false;
            }
        }
        return sb.toString();
    }

}