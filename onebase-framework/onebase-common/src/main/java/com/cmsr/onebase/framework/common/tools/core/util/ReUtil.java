package com.cmsr.onebase.framework.common.tools.core.util;


import com.cmsr.onebase.framework.common.tools.core.lang.Assert;
import com.cmsr.onebase.framework.common.tools.core.lang.PatternPool;
import com.cmsr.onebase.framework.common.tools.core.lang.RegexPool;
import com.cmsr.onebase.framework.common.tools.core.lang.mutable.MutableObj;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则工具类
 */
public class ReUtil {

    /**
     * 正则表达式匹配中文字符串
     */
    public final static String RE_CHINESES = RegexPool.CHINESES;

    /**
     * 获得匹配的字符串，获得正则中分组1的内容
     *
     * @param regex   匹配的正则
     * @param content 被匹配的内容
     * @return 匹配后得到的字符串，未匹配返回null
     * @since 3.1.2
     */
    public static String getGroup1(String regex, CharSequence content) {
        return get(regex, content, 1);
    }

    /**
     * 指定内容中是否有表达式匹配的内容
     *
     * @param regex   正则表达式
     * @param content 被查找的内容
     * @return 指定内容中是否有表达式匹配的内容
     * @since 3.3.1
     */
    public static boolean contains(String regex, CharSequence content) {
        if (null == regex || null == content) {
            return false;
        }

        final Pattern pattern = PatternPool.get(regex, Pattern.DOTALL);
        return contains(pattern, content);
    }

    /**
     * 指定内容中是否有表达式匹配的内容
     *
     * @param pattern 编译后的正则模式
     * @param content 被查找的内容
     * @return 指定内容中是否有表达式匹配的内容
     * @since 3.3.1
     */
    public static boolean contains(Pattern pattern, CharSequence content) {
        if (null == pattern || null == content) {
            return false;
        }
        return pattern.matcher(content).find();
    }
    
    /**
     * 取得匹配正则表达式的所有分组的第1组结果
     *
     * @param pattern 编译后的正则模式
     * @param content 被查找的内容
     * @return 匹配结果中第1组的内容列表
     */
    public static List<String> findAllGroup1(Pattern pattern, String content) {
        if (pattern == null || content == null) {
            return new ArrayList<>();
        }
        
        List<String> result = new ArrayList<>();
        Matcher matcher = pattern.matcher(content);
        
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        
        return result;
    }

    /**
     * 给定内容是否匹配正则
     *
     * @param pattern 模式
     * @param content 内容
     * @return 正则为null或者""则不检查，返回true，内容为null返回false
     */
    public static boolean isMatch(Pattern pattern, CharSequence content) {
        if (content == null || pattern == null) {
            // 提供null的字符串为不匹配
            return false;
        }
        return pattern.matcher(content).matches();
    }

    /**
     * 给定内容是否匹配正则
     *
     * @param regex   正则
     * @param content 内容
     * @return 正则为null或者""则不检查，返回true，内容为null返回false
     */
    public static boolean isMatch(String regex, CharSequence content) {
        if (content == null) {
            // 提供null的字符串为不匹配
            return false;
        }

        if (StrUtil.isEmpty(regex)) {
            // 正则不存在则为全匹配
            return true;
        }

        // Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        final Pattern pattern = PatternPool.get(regex, Pattern.DOTALL);
        return isMatch(pattern, content);
    }

    /**
     * 取得内容中匹配的所有结果
     *
     * @param <T>        集合类型
     * @param regex      正则
     * @param content    被查找的内容
     * @param group      正则的分组
     * @param collection 返回的集合类型
     * @return 结果集
     */
    public static <T extends Collection<String>> T findAll(String regex, CharSequence content, int group, T collection) {
        if (null == regex) {
            return collection;
        }

        return findAll(PatternPool.get(regex, Pattern.DOTALL), content, group, collection);
    }

    /**
     * 取得内容中匹配的所有结果
     *
     * @param <T>        集合类型
     * @param pattern    编译后的正则模式
     * @param content    被查找的内容
     * @param group      正则的分组
     * @param collection 返回的集合类型
     * @return 结果集
     */
    public static <T extends Collection<String>> T findAll(Pattern pattern, CharSequence content, int group, T collection) {
        if (null == pattern || null == content) {
            return null;
        }
        Assert.notNull(collection, "Collection must be not null !");

        findAll(pattern, content, (matcher) -> collection.add(matcher.group(group)));
        return collection;
    }

    /**
     * 取得内容中匹配的所有结果，使用{@link Consumer}完成匹配结果处理
     *
     * @param pattern  编译后的正则模式
     * @param content  被查找的内容
     * @param consumer 匹配结果处理函数
     * @since 5.7.15
     */
    public static void findAll(Pattern pattern, CharSequence content, Consumer<Matcher> consumer) {
        if (null == pattern || null == content) {
            return;
        }

        final Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            consumer.accept(matcher);
        }
    }

    /**
     * 获得匹配的字符串
     *
     * @param regex      匹配的正则
     * @param content    被匹配的内容
     * @param groupIndex 匹配正则的分组序号
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String get(String regex, CharSequence content, int groupIndex) {
        if (null == content || null == regex) {
            return null;
        }

        final Pattern pattern = PatternPool.get(regex, Pattern.DOTALL);
        return get(pattern, content, groupIndex);
    }

    /**
     * 获得匹配的字符串，对应分组0表示整个匹配内容，1表示第一个括号分组内容，依次类推
     *
     * @param pattern    编译后的正则模式
     * @param content    被匹配的内容
     * @param groupIndex 匹配正则的分组序号，0表示整个匹配内容，1表示第一个括号分组内容，依次类推
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String get(Pattern pattern, CharSequence content, int groupIndex) {
        if (null == content || null == pattern) {
            return null;
        }

        final MutableObj<String> result = new MutableObj<>();
        get(pattern, content, matcher -> result.set(matcher.group(groupIndex)));
        return result.get();
    }

    /**
     * 在给定字符串中查找给定规则的字符，如果找到则使用{@link Consumer}处理之<br>
     * 如果内容中有多个匹配项，则只处理找到的第一个结果。
     *
     * @param pattern  匹配的正则
     * @param content  被匹配的内容
     * @param consumer 匹配到的内容处理器
     * @since 5.7.15
     */
    public static void get(Pattern pattern, CharSequence content, Consumer<Matcher> consumer) {
        if (null == content || null == pattern || null == consumer) {
            return;
        }
        final Matcher m = pattern.matcher(content);
        if (m.find()) {
            consumer.accept(m);
        }
    }

}
