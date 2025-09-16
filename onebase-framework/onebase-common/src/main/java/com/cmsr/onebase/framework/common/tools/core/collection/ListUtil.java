package com.cmsr.onebase.framework.common.tools.core.collection;

import com.cmsr.onebase.framework.common.tools.core.lang.Assert;
import com.cmsr.onebase.framework.common.tools.core.util.ArrayUtil;
import com.cmsr.onebase.framework.common.tools.core.util.StrUtil;

import java.util.*;

public class ListUtil {

    /**
     * 将对应List转换为不可修改的List
     *
     * @param list List
     * @param <T>  元素类型
     * @return 不可修改List
     * @since 5.2.6
     */
    public static <T> List<T> unmodifiable(List<T> list) {
        if (null == list) {
            return null;
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * 获取一个空List，这个空List不可变
     *
     * @param <T> 元素类型
     * @return 空的List
     * @see Collections#emptyList()
     * @since 5.2.6
     */
    public static <T> List<T> empty() {
        return Collections.emptyList();
    }

    /**
     * 在指定位置设置元素。当index小于List的长度时，替换指定位置的值，否则追加{@code null}直到到达index后，设置值
     *
     * @param <T>     元素类型
     * @param list    List列表
     * @param index   位置
     * @param element 新元素
     * @return 原List
     * @since 5。8.4
     */
    public static <T> List<T> setOrPadding(List<T> list, int index, T element) {
        return setOrPadding(list, index, element, null);
    }

    /**
     * 截取集合的部分<br>
     * 此方法与{@link List#subList(int, int)} 不同在于子列表是新的副本，操作子列表不会影响原列表。
     *
     * @param <T>   集合元素类型
     * @param list  被截取的数组
     * @param start 开始位置（包含）
     * @param end   结束位置（不包含）
     * @param step  步进
     * @return 截取后的数组，当开始位置超过最大时，返回空的List
     * @since 4.0.6
     */
    public static <T> List<T> sub(List<T> list, int start, int end, int step) {
        if (list == null) {
            return null;
        }

        if (list.isEmpty()) {
            return new ArrayList<>(0);
        }

        final int size = list.size();
        if (start < 0) {
            start += size;
        }
        if (end < 0) {
            end += size;
        }
        if (start == size) {
            return new ArrayList<>(0);
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > size) {
            if (start >= size) {
                return new ArrayList<>(0);
            }
            end = size;
        }

        if (step < 1) {
            step = 1;
        }

        final List<T> result = new ArrayList<>();
        for (int i = start; i < end; i += step) {
            result.add(list.get(i));
        }
        return result;
    }

    /**
     * 在指定位置设置元素。当index小于List的长度时，替换指定位置的值，否则追加{@code paddingElement}直到到达index后，设置值<br>
     * 注意：为避免OOM问题，此方法限制index的最大值为{@code (list.size() + 1) * 10}
     *
     * @param <T>            元素类型
     * @param list           List列表
     * @param index          位置
     * @param element        新元素
     * @param paddingElement 填充的值
     * @return 原List
     * @since 5.8.4
     */
    public static <T> List<T> setOrPadding(List<T> list, int index, T element, T paddingElement) {
        return setOrPadding(list, index, element, paddingElement, (list.size() + 1) * 10);
    }

    /**
     * 在指定位置设置元素。当index小于List的长度时，替换指定位置的值，否则追加{@code paddingElement}直到到达index后，设置值
     *
     * @param <T>            元素类型
     * @param list           List列表
     * @param index          位置
     * @param element        新元素
     * @param paddingElement 填充的值
     * @param indexLimit     最大索引限制
     * @return 原List
     * @since 5.8.28
     */
    public static <T> List<T> setOrPadding(List<T> list, int index, T element, T paddingElement, int indexLimit) {
        Assert.notNull(list, "List must be not null !");
        final int size = list.size();
        if (index < size) {
            list.set(index, element);
        } else {
            if (indexLimit > 0) {
                // issue#3286, 增加安全检查
                if (index > indexLimit) {
                    throw new RuntimeException(StrUtil.format("Index [{}] is too large for limit: [{}]", index, indexLimit));
                }
            }
            for (int i = size; i < index; i++) {
                list.add(paddingElement);
            }
            list.add(element);
        }
        return list;
    }

    /**
     * 新建一个{@link ArrayList}<br>
     * 如果提供的初始化数组为空，新建默认初始长度的List<br>
     * 替换之前的：CollUtil.newArrayList()
     *
     * @param <T>    集合元素类型
     * @param values 数组，可以为{@code null}
     * @return List对象
     * @since 4.1.2
     */
    @SafeVarargs
    public static <T> ArrayList<T> of(final T... values) {
        if (ArrayUtil.isEmpty(values)) {
            return new ArrayList<>();
        }
        final ArrayList<T> arrayList = new ArrayList<>(values.length);
        Collections.addAll(arrayList, values);
        return arrayList;
    }

    /**
     * 获取一个初始大小为0的List，这个空List可变
     *
     * @param <T> 元素类型
     * @return 空的List
     * @since 6.0.0
     */
    public static <T> List<T> zero() {
        return new ArrayList<>(0);
    }

    /**
     * 新建一个ArrayList
     *
     * @param <T>    集合元素类型
     * @param values 数组
     * @return ArrayList对象
     */
    @SafeVarargs
    public static <T> ArrayList<T> toList(T... values) {
        return (ArrayList<T>) list(false, values);
    }

    /**
     * 新建一个ArrayList
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @return ArrayList对象
     */
    public static <T> ArrayList<T> toList(Collection<T> collection) {
        return (ArrayList<T>) list(false, collection);
    }

    /**
     * 新建一个ArrayList<br>
     * 提供的参数为null时返回空{@link ArrayList}
     *
     * @param <T>      集合元素类型
     * @param iterator {@link Iterator}
     * @return ArrayList对象
     * @since 3.0.8
     */
    public static <T> ArrayList<T> toList(Iterator<T> iterator) {
        return (ArrayList<T>) list(false, iterator);
    }

    /**
     * 新建一个List
     *
     * @param <T>      集合元素类型
     * @param isLinked 是否新建LinkedList
     * @param values   数组
     * @return List对象
     * @since 4.1.2
     */
    @SafeVarargs
    public static <T> List<T> list(boolean isLinked, T... values) {
        if (ArrayUtil.isEmpty(values)) {
            return list(isLinked);
        }
        final List<T> arrayList = isLinked ? new LinkedList<>() : new ArrayList<>(values.length);
        Collections.addAll(arrayList, values);
        return arrayList;
    }

    /**
     * 新建一个List<br>
     * 提供的参数为null时返回空{@link ArrayList}
     *
     * @param <T>      集合元素类型
     * @param isLinked 是否新建LinkedList
     * @param iterable {@link Iterable}
     * @return List对象
     * @since 4.1.2
     */
    public static <T> List<T> list(boolean isLinked, Iterable<T> iterable) {
        if (null == iterable) {
            return list(isLinked);
        }
        return list(isLinked, iterable.iterator());
    }

    /**
     * 新建一个List<br>
     * 提供的参数为null时返回空{@link ArrayList}
     *
     * @param <T>      集合元素类型
     * @param isLinked 是否新建LinkedList
     * @param iter     {@link Iterator}
     * @return ArrayList对象
     * @since 4.1.2
     */
    public static <T> List<T> list(boolean isLinked, Iterator<T> iter) {
        final List<T> list = list(isLinked);
        if (null != iter) {
            while (iter.hasNext()) {
                list.add(iter.next());
            }
        }
        return list;
    }
}
