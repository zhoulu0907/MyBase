package com.cmsr.onebase.framework.common.tools.core.collection;

import com.cmsr.onebase.framework.common.tools.core.lang.Assert;
import com.cmsr.onebase.framework.common.tools.core.lang.Matcher;
import com.cmsr.onebase.framework.common.tools.core.text.StrJoiner;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * {@link Iterable} 和 {@link Iterator} 相关工具类
 *
 */
public class IterUtil {

    /**
     * 以 conjunction 为分隔符将集合转换为字符串<br>
     * 如果集合元素为数组、{@link Iterable}或{@link Iterator}，则递归组合其为字符串
     *
     * @param <T>         集合元素类型
     * @param iterator    集合
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static <T> String join(Iterator<T> iterator, CharSequence conjunction) {
        return StrJoiner.of(conjunction).append(iterator).toString();
    }

    /**
     * 获取{@link Iterator}
     *
     * @param iterable {@link Iterable}
     * @param <T>      元素类型
     * @return 当iterable为null返回{@code null}，否则返回对应的{@link Iterator}
     * @since 5.7.2
     */
    public static <T> Iterator<T> getIter(Iterable<T> iterable) {
        return null == iterable ? null : iterable.iterator();
    }

    /**
     * 获得{@link Iterator}对象的元素类型（通过第一个非空元素判断）<br>
     * 注意，此方法至少会调用多次next方法
     *
     * @param iterator {@link Iterator}，为 {@code null}返回{@code null}
     * @return 元素类型，当列表为空或元素全部为{@code null}时，返回{@code null}
     */
    public static Class<?> getElementType(Iterator<?> iterator) {
        if (null == iterator) {
            return null;
        }
        final Object ele = getFirstNoneNull(iterator);
        return null == ele ? null : ele.getClass();
    }

    /**
     * 获取集合的第一个非空元素
     *
     * @param <T>      集合元素类型
     * @param iterator {@link Iterator}
     * @return 第一个非空元素，null表示未找到
     * @since 5.7.2
     */
    public static <T> T getFirstNoneNull(Iterator<T> iterator) {
        return firstMatch(iterator, Objects::nonNull);
    }

    /**
     * 返回{@link Iterator}中第一个匹配规则的值
     *
     * @param <T>      数组元素类型
     * @param iterator {@link Iterator}
     * @param matcher  匹配接口，实现此接口自定义匹配规则
     * @return 匹配元素，如果不存在匹配元素或{@link Iterator}为空，返回 {@code null}
     * @since 5.7.5
     */
    public static <T> T firstMatch(Iterator<T> iterator, Matcher<T> matcher) {
        Assert.notNull(matcher, "Matcher must be not null !");
        if (null != iterator) {
            while (iterator.hasNext()) {
                final T next = iterator.next();
                if (matcher.match(next)) {
                    return next;
                }
            }
        }
        return null;
    }

    /**
     * 按照给定函数，转换{@link Iterator}为另一种类型的{@link Iterator}
     *
     * @param <F>      源元素类型
     * @param <T>      目标元素类型
     * @param iterator 源{@link Iterator}
     * @param function 转换函数
     * @return 转换后的{@link Iterator}
     * @since 5.4.3
     */
    public static <F, T> Iterator<T> trans(Iterator<F> iterator, Function<? super F, ? extends T> function) {
        return new TransIter<>(iterator, function);
    }

    /**
     * Iterable是否为空
     *
     * @param iterable Iterable对象
     * @return 是否为空
     */
    public static boolean isEmpty(Iterable<?> iterable) {
        return null == iterable || isEmpty(iterable.iterator());
    }

    /**
     * Iterator是否为空
     *
     * @param Iterator Iterator对象
     * @return 是否为空
     */
    public static boolean isEmpty(Iterator<?> Iterator) {
        return null == Iterator || false == Iterator.hasNext();
    }

    /**
     * Iterator转List<br>
     * 不判断，直接生成新的List
     *
     * @param <E>  元素类型
     * @param iter {@link Iterator}
     * @return List
     * @since 4.0.6
     */
    public static <E> List<E> toList(Iterable<E> iter) {
        if (null == iter) {
            return null;
        }
        return toList(iter.iterator());
    }

    /**
     * Iterator转List<br>
     * 不判断，直接生成新的List
     *
     * @param <E>  元素类型
     * @param iter {@link Iterator}
     * @return List
     * @since 4.0.6
     */
    public static <E> List<E> toList(Iterator<E> iter) {
        return ListUtil.toList(iter);
    }
}
