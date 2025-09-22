package com.cmsr.onebase.framework.common.tools.core.collection;

import com.cmsr.onebase.framework.common.tools.core.bean.BeanUtil;
import com.cmsr.onebase.framework.common.tools.core.comparator.CompareUtil;
import com.cmsr.onebase.framework.common.tools.core.convert.ConverterRegistry;
import com.cmsr.onebase.framework.common.tools.core.exceptions.UtilException;
import com.cmsr.onebase.framework.common.tools.core.lang.Assert;
import com.cmsr.onebase.framework.common.tools.core.lang.Filter;
import com.cmsr.onebase.framework.common.tools.core.lang.Matcher;
import com.cmsr.onebase.framework.common.tools.core.map.MapUtil;
import com.cmsr.onebase.framework.common.tools.core.util.*;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

/**
 * Collection工具类
 */
public class CollUtil {

    /**
     * 计算集合的单差集，即只返回【集合1】中有，但是【集合2】中没有的元素，例如：
     *
     * <pre>
     *     subtract([1,2,3,4],[2,3,4,5]) -》 [1]
     * </pre>
     *
     * @param coll1 集合1
     * @param coll2 集合2
     * @param <T>   元素类型
     * @return 单差集
     */
    public static <T> Collection<T> subtract(Collection<T> coll1, Collection<T> coll2) {
        if(isEmpty(coll1) || isEmpty(coll2)){
            return coll1;
        }

        Collection<T> result = ObjUtil.clone(coll1);
        try {
            if (null == result) {
                result = CollUtil.create(coll1.getClass());
                result.addAll(coll1);
            }
            result.removeAll(coll2);
        } catch (UnsupportedOperationException e) {
            // 针对 coll1 为只读集合的补偿
            result = CollUtil.create(AbstractCollection.class);
            result.addAll(coll1);
            result.removeAll(coll2);
        }
        return result;
    }

    /**
     * 循环遍历 {@link Iterable}，使用{@link Consumer} 接受遍历的每条数据，并针对每条数据做处理
     *
     * @param <T>      集合元素类型
     * @param iterable {@link Iterable}
     * @param consumer {@link Consumer} 遍历的每条数据处理器
     * @since 5.4.7
     */
    public static <T> void forEach(Iterable<T> iterable, Consumer<T> consumer) {
        if (iterable == null) {
            return;
        }
        forEach(iterable.iterator(), consumer);
    }

    /**
     * 循环遍历 {@link Iterator}，使用{@link Consumer} 接受遍历的每条数据，并针对每条数据做处理
     *
     * @param <T>      集合元素类型
     * @param iterator {@link Iterator}
     * @param consumer {@link Consumer} 遍历的每条数据处理器
     */
    public static <T> void forEach(Iterator<T> iterator, Consumer<T> consumer) {
        if (iterator == null) {
            return;
        }
        int index = 0;
        while (iterator.hasNext()) {
            consumer.accept(iterator.next(), index);
            index++;
        }
    }

    /**
     * 创建新的集合对象，返回具体的泛型集合
     *
     * @param <T>            集合元素类型
     * @param collectionType 集合类型，rawtype 如 ArrayList.class, EnumSet.class ...
     * @param elementType    集合元素类型
     * @return 集合类型对应的实例
     * @since v5
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Collection<T> create(Class<?> collectionType, Class<T> elementType) {
        final Collection<T> list;
        if (collectionType.isAssignableFrom(AbstractCollection.class)) {
            // 抽象集合默认使用ArrayList
            list = new ArrayList<>();
        }

        // Set
        else if (collectionType.isAssignableFrom(HashSet.class)) {
            list = new HashSet<>();
        } else if (collectionType.isAssignableFrom(LinkedHashSet.class)) {
            list = new LinkedHashSet<>();
        } else if (collectionType.isAssignableFrom(TreeSet.class)) {
            list = new TreeSet<>((o1, o2) -> {
                // 优先按照对象本身比较，如果没有实现比较接口，默认按照toString内容比较
                if (o1 instanceof Comparable) {
                    return ((Comparable<T>) o1).compareTo(o2);
                }
                return CompareUtil.compare(o1.toString(), o2.toString());
            });
        } else if (collectionType.isAssignableFrom(EnumSet.class)) {
            list = (Collection<T>) EnumSet.noneOf(Assert.notNull((Class<Enum>) elementType));
        }

        // List
        else if (collectionType.isAssignableFrom(ArrayList.class)) {
            list = new ArrayList<>();
        } else if (collectionType.isAssignableFrom(LinkedList.class)) {
            list = new LinkedList<>();
        }

        // Others，直接实例化
        else {
            try {
                list = (Collection<T>) ReflectUtil.newInstance(collectionType);
            } catch (final Exception e) {
                // 无法创建当前类型的对象，尝试创建父类型对象
                final Class<?> superclass = collectionType.getSuperclass();
                if (null != superclass && collectionType != superclass) {
                    return create(superclass);
                }
                throw new UtilException(e);
            }
        }
        return list;
    }

    /**
     * 创建新的集合对象
     *
     * @param <T>            集合类型
     * @param collectionType 集合类型
     * @return 集合类型对应的实例
     * @since 3.0.8
     */
    public static <T> Collection<T> create(Class<?> collectionType) {
        return create(collectionType, null);
    }

    /**
     * 通过func自定义一个规则，此规则将原集合中的元素转换成新的元素，生成新的列表返回<br>
     * 例如提供的是一个Bean列表，通过Function接口实现获取某个字段值，返回这个字段值组成的新列表
     *
     * @param <T>        集合元素类型
     * @param <R>        返回集合元素类型
     * @param collection 原集合
     * @param func       编辑函数
     * @param ignoreNull 是否忽略空值，这里的空值包括函数处理前和处理后的null值
     * @return 抽取后的新列表
     * @since 5.3.5
     */
    public static <T, R> List<R> map(Iterable<T> collection, Function<? super T, ? extends R> func, boolean ignoreNull) {
        final List<R> fieldValueList = new ArrayList<>();
        if (null == collection) {
            return fieldValueList;
        }

        R value;
        for (T t : collection) {
            if (null == t && ignoreNull) {
                continue;
            }
            value = func.apply(t);
            if (null == value && ignoreNull) {
                continue;
            }
            fieldValueList.add(value);
        }
        return fieldValueList;
    }

    /**
     * 截取集合的部分
     *
     * @param <T>        集合元素类型
     * @param collection 被截取的数组
     * @param start      开始位置（包含）
     * @param end        结束位置（不包含）
     * @param step       步进
     * @return 截取后的数组，当开始位置超过最大时，返回空集合
     * @since 4.0.6
     */
    public static <T> List<T> sub(Collection<T> collection, int start, int end, int step) {
        if (isEmpty(collection)) {
            return ListUtil.empty();
        }

        final List<T> list = collection instanceof List ? (List<T>) collection : ListUtil.toList(collection);
        return sub(list, start, end, step);
    }

    /**
     * 截取列表的部分
     *
     * @param <T>   集合元素类型
     * @param list  被截取的数组
     * @param start 开始位置（包含）
     * @param end   结束位置（不包含）
     * @param step  步进
     * @return 截取后的数组，当开始位置超过最大时，返回空的List
     * @see ListUtil#sub(List, int, int, int)
     * @since 4.0.6
     */
    public static <T> List<T> sub(List<T> list, int start, int end, int step) {
        return ListUtil.sub(list, start, end, step);
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
            if (ArrayUtil.isArray(array)) {
                return 0 == Array.getLength(array);
            }
            return false;
        }
        return true;
    }

    /**
     * 集合是否为空
     *
     * @param collection 集合
     * @return 是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }


    /**
     * 多个集合的非重复并集，类似于SQL中的“UNION DISTINCT”<br>
     * 针对一个集合中存在多个相同元素的情况，只保留一个<br>
     * 例如：集合1：[a, b, c, c, c]，集合2：[a, b, c, c]<br>
     * 结果：[a, b, c]，此结果中只保留了一个c
     *
     * @param <T>        集合元素类型
     * @param coll1      集合1
     * @param coll2      集合2
     * @param otherColls 其它集合
     * @return 并集的集合，返回 {@link LinkedHashSet}
     */
    @SafeVarargs
    public static <T> Set<T> unionDistinct(Collection<T> coll1, Collection<T> coll2, Collection<T>... otherColls) {
        final Set<T> result;
        if (isEmpty(coll1)) {
            result = new LinkedHashSet<>();
        } else {
            result = new LinkedHashSet<>(coll1);
        }

        if (isNotEmpty(coll2)) {
            result.addAll(coll2);
        }

        if (ArrayUtil.isNotEmpty(otherColls)) {
            for (Collection<T> otherColl : otherColls) {
                if (isEmpty(otherColl)) {
                    continue;
                }
                result.addAll(otherColl);
            }
        }

        return result;
    }

    /**
     * 集合是否为非空
     *
     * @param collection 集合
     * @return 是否为非空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * Map是否为非空
     *
     * @param map 集合
     * @return 是否为非空
     * @see MapUtil#isNotEmpty(Map)
     * @since 5.7.4
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return MapUtil.isNotEmpty(map);
    }

    /**
     * 新建一个HashSet
     *
     * @param <T> 集合元素类型
     * @param ts  元素数组
     * @return HashSet对象
     */
    @SafeVarargs
    public static <T> HashSet<T> newHashSet(T... ts) {
        return set(false, ts);
    }

    /**
     * 新建一个HashSet
     *
     * @param <T>      集合元素类型
     * @param isSorted 是否有序，有序返回 {@link LinkedHashSet}，否则返回 {@link HashSet}
     * @param ts       元素数组
     * @return HashSet对象
     */
    @SafeVarargs
    public static <T> HashSet<T> set(boolean isSorted, T... ts) {
        if (null == ts) {
            return isSorted ? new LinkedHashSet<>() : new HashSet<>();
        }
        int initialCapacity = Math.max((int) (ts.length / .75f) + 1, 16);
        final HashSet<T> set = isSorted ? new LinkedHashSet<>(initialCapacity) : new HashSet<>(initialCapacity);
        Collections.addAll(set, ts);
        return set;
    }

    /**
     * 判断指定集合是否包含指定值，如果集合为空（null或者空），返回{@code false}，否则找到元素返回{@code true}
     *
     * @param collection 集合
     * @param value      需要查找的值
     * @return 如果集合为空（null或者空），返回{@code false}，否则找到元素返回{@code true}
     * @throws ClassCastException   如果类型不一致会抛出转换异常
     * @throws NullPointerException 当指定的元素 值为 null ,或集合类不支持null 时抛出该异常
     * @see Collection#contains(Object)
     * @since 4.1.10
     */
    public static boolean contains(Collection<?> collection, Object value) {
        return isNotEmpty(collection) && collection.contains(value);
    }

    /**
     * 集合1中是否包含集合2中所有的元素。<br>
     * 当集合1和集合2都为空时，返回{@code true}
     * 当集合2为空时，返回{@code true}
     *
     * @param coll1 集合1
     * @param coll2 集合2
     * @return 集合1中是否包含集合2中所有的元素
     * @since 4.5.12
     */
    @SuppressWarnings("SuspiciousMethodCalls")
    public static boolean containsAll(Collection<?> coll1, Collection<?> coll2) {
        if (isEmpty(coll1)) {
            return isEmpty(coll2);
        }

        if (isEmpty(coll2)) {
            return true;
        }

        // Set直接判定
        if(coll1 instanceof Set){
            return coll1.containsAll(coll2);
        }

        // 参考Apache commons collection4
        // 将时间复杂度降低到O(n + m)
        final Iterator<?> it = coll1.iterator();
        final Set<Object> elementsAlreadySeen = new HashSet<>(coll1.size(), 1);
        for (final Object nextElement : coll2) {
            if (elementsAlreadySeen.contains(nextElement)) {
                continue;
            }

            boolean foundCurrentElement = false;
            while (it.hasNext()) {
                final Object p = it.next();
                elementsAlreadySeen.add(p);
                if (Objects.equals(nextElement, p)) {
                    foundCurrentElement = true;
                    break;
                }
            }

            if (false == foundCurrentElement) {
                return false;
            }
        }
        return true;
    }

    /**
     * 多个集合的交集<br>
     * 针对一个集合中存在多个相同元素的情况，只保留一个<br>
     * 例如：集合1：[a, b, c, c, c]，集合2：[a, b, c, c]<br>
     * 结果：[a, b, c]，此结果中只保留了一个c
     *
     * @param <T>        集合元素类型
     * @param coll1      集合1
     * @param coll2      集合2
     * @param otherColls 其它集合
     * @return 交集的集合，返回 {@link LinkedHashSet}
     * @since 5.3.9
     */
    @SafeVarargs
    public static <T> Set<T> intersectionDistinct(Collection<T> coll1, Collection<T> coll2, Collection<T>... otherColls) {
        final Set<T> result;
        if (isEmpty(coll1) || isEmpty(coll2)) {
            // 有一个空集合就直接返回空
            return new LinkedHashSet<>();
        } else {
            result = new LinkedHashSet<>(coll1);
        }

        if (ArrayUtil.isNotEmpty(otherColls)) {
            for (Collection<T> otherColl : otherColls) {
                if (isNotEmpty(otherColl)) {
                    result.retainAll(otherColl);
                } else {
                    // 有一个空集合就直接返回空
                    return new LinkedHashSet<>();
                }
            }
        }

        result.retainAll(coll2);

        return result;
    }

    /**
     * 获取集合的第一个元素，如果集合为空（null或者空集合），返回{@code null}
     *
     * @param <T>      集合元素类型
     * @param iterable {@link Iterable}
     * @return 第一个元素，为空返回{@code null}
     */
    public static <T> T getFirst(Iterable<T> iterable) {
        if (iterable instanceof List) {
            final List<T> list = (List<T>) iterable;
            return CollUtil.isEmpty(list) ? null: list.get(0);
        }

        return getFirst(getIter(iterable));
    }

    /**
     * 获取集合的第一个元素
     *
     * @param <T>      集合元素类型
     * @param iterator {@link Iterator}
     * @return 第一个元素
     */
    public static <T> T getFirst(Iterator<T> iterator) {
        return get(iterator, 0);
    }

    /**
     * 获取集合的最后一个元素
     *
     * @param <T>        集合元素类型
     * @param collection {@link Collection}
     * @return 最后一个元素
     * @since 4.1.10
     */
    public static <T> T getLast(Collection<T> collection) {
        return get(collection, -1);
    }

    /**
     * 获取集合中指定下标的元素值，下标可以为负数，例如-1表示最后一个元素<br>
     * 如果元素越界，返回null
     *
     * @param <T>        元素类型
     * @param collection 集合
     * @param index      下标，支持负数
     * @return 元素值
     * @since 4.0.6
     */
    public static <T> T get(Collection<T> collection, int index) {
        if (null == collection) {
            return null;
        }

        final int size = collection.size();
        if (0 == size) {
            return null;
        }

        if (index < 0) {
            index += size;
        }

        // 检查越界
        if (index >= size || index < 0) {
            return null;
        }

        if (collection instanceof List) {
            final List<T> list = ((List<T>) collection);
            return list.get(index);
        } else {
            return get(collection.iterator(), index);
        }
    }

    /**
     * 遍历{@link Iterator}，获取指定index位置的元素
     *
     * @param iterator {@link Iterator}
     * @param index    位置
     * @param <E>      元素类型
     * @return 元素，找不到元素返回{@code null}
     * @since 5.8.0
     */
    public static <E> E get(final Iterator<E> iterator, int index) throws IndexOutOfBoundsException {
        if(null == iterator){
            return null;
        }
        Assert.isTrue(index >= 0, "[index] must be >= 0");
        while (iterator.hasNext()) {
            index--;
            if (-1 == index) {
                return iterator.next();
            }
            iterator.next();
        }
        return null;
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
     * 如果提供的集合为{@code null}，返回一个不可变的默认空集合，否则返回原集合<br>
     * 空集合使用{@link Collections#emptySet()}
     *
     * @param <T> 集合元素类型
     * @param set 提供的集合，可能为null
     * @return 原集合，若为null返回空集合
     * @since 4.6.3
     */
    public static <T> Set<T> emptyIfNull(Set<T> set) {
        return (null == set) ? Collections.emptySet() : set;
    }

    /**
     * 查找第一个匹配元素对象
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @param filter     过滤器，满足过滤条件的第一个元素将被返回
     * @return 满足过滤条件的第一个元素
     * @since 3.1.0
     */
    public static <T> T findOne(Iterable<T> collection, Filter<T> filter) {
        if (null != collection) {
            for (T t : collection) {
                if (filter.accept(t)) {
                    return t;
                }
            }
        }
        return null;
    }

    /**
     * 其中一个集合在另一个集合中是否至少包含一个元素，即是两个集合是否至少有一个共同的元素
     *
     * @param coll1 集合1
     * @param coll2 集合2
     * @return 其中一个集合在另一个集合中是否至少包含一个元素
     * @since 2.1
     */
    public static boolean containsAny(Collection<?> coll1, Collection<?> coll2) {
        if (isEmpty(coll1) || isEmpty(coll2)) {
            return false;
        }
        if (coll1.size() < coll2.size()) {
            for (Object object : coll1) {
                if (coll2.contains(object)) {
                    return true;
                }
            }
        } else {
            for (Object object : coll2) {
                if (coll1.contains(object)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 加入全部
     *
     * @param <T>        集合元素类型
     * @param collection 被加入的集合 {@link Collection}
     * @param iterable   要加入的内容{@link Iterable}
     * @return 原集合
     */
    public static <T> Collection<T> addAll(Collection<T> collection, Iterable<T> iterable) {
        if (iterable == null) {
            return collection;
        }
        return addAll(collection, iterable.iterator());
    }

    /**
     * 将指定对象全部加入到集合中<br>
     * 提供的对象如果为集合类型，会自动转换为目标元素类型<br>
     *
     * @param <T>        元素类型
     * @param collection 被加入的集合
     * @param value      对象，可能为Iterator、Iterable、Enumeration、Array
     * @return 被加入集合
     */
    public static <T> Collection<T> addAll(Collection<T> collection, Object value) {
        return addAll(collection, value, TypeUtil.getTypeArgument(collection.getClass()));
    }

    /**
     * 加入全部
     *
     * @param <T>        集合元素类型
     * @param collection 被加入的集合 {@link Collection}
     * @param iterator   要加入的{@link Iterator}
     * @return 原集合
     */
    public static <T> Collection<T> addAll(Collection<T> collection, Iterator<T> iterator) {
        if (null != collection && null != iterator) {
            while (iterator.hasNext()) {
                collection.add(iterator.next());
            }
        }
        return collection;
    }

    /**
     * 将指定对象全部加入到集合中<br>
     * 提供的对象如果为集合类型，会自动转换为目标元素类型<br>
     * 如果为String，支持类似于[1,2,3,4] 或者 1,2,3,4 这种格式
     *
     * @param <T>         元素类型
     * @param collection  被加入的集合
     * @param value       对象，可能为Iterator、Iterable、Enumeration、Array，或者与集合元素类型一致
     * @param elementType 元素类型，为空时，使用Object类型来接纳所有类型
     * @return 被加入集合
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Collection<T> addAll(Collection<T> collection, Object value, Type elementType) {
        if (null == collection || null == value) {
            return collection;
        }
        if (TypeUtil.isUnknown(elementType)) {
            // 元素类型为空时，使用Object类型来接纳所有类型
            elementType = Object.class;
        }

        Iterator iter;
        if (value instanceof Iterator) {
            iter = (Iterator) value;
        } else if (value instanceof Iterable) {
            if(value instanceof Map && BeanUtil.isBean(TypeUtil.getClass(elementType))){
                //https://github.com/dromara/hutool/issues/3139
                // 如果值为Map，而目标为一个Bean，则Map应整体转换为Bean，而非拆分成Entry转换
                iter = new ArrayIter<>(new Object[]{value});
            }else{
                iter = ((Iterable) value).iterator();
            }
        } else if (value instanceof Enumeration) {
            iter = new EnumerationIter<>((Enumeration) value);
        } else if (ArrayUtil.isArray(value)) {
            iter = new ArrayIter<>(value);
        } else if (value instanceof CharSequence) {
            // String按照逗号分隔的列表对待
            final String ArrayStr = StrUtil.unWrap((CharSequence) value, '[', ']');
            iter = StrUtil.splitTrim(ArrayStr, CharUtil.COMMA).iterator();
        } else {
            // 其它类型按照单一元素处理
            iter = CollUtil.newArrayList(value).iterator();
        }

        final ConverterRegistry convert = ConverterRegistry.getInstance();
        while (iter.hasNext()) {
            collection.add(convert.convert(elementType, iter.next()));
        }

        return collection;
    }

    /**
     * 以 conjunction 为分隔符将集合转换为字符串<br>
     * 如果集合元素为数组、{@link Iterable}或{@link Iterator}，则递归组合其为字符串
     *
     * @param <T>         集合元素类型
     * @param iterable    {@link Iterable}
     * @param conjunction 分隔符
     * @return 连接后的字符串
     * @see IterUtil#join(Iterator, CharSequence)
     */
    public static <T> String join(Iterable<T> iterable, CharSequence conjunction) {
        if (null == iterable) {
            return null;
        }
        return IterUtil.join(iterable.iterator(), conjunction);
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
     * 使用给定的转换函数，转换源集合为新类型的集合
     *
     * @param <F>        源元素类型
     * @param <T>        目标元素类型
     * @param collection 集合
     * @param function   转换函数
     * @return 新类型的集合
     * @since 5.4.3
     */
    public static <F, T> Collection<T> trans(Collection<F> collection, Function<? super F, ? extends T> function) {
        return new TransCollection<>(collection, function);
    }

    /**
     * 新建一个ArrayList
     *
     * @param <T>    集合元素类型
     * @param values 数组
     * @return ArrayList对象
     */
    @SafeVarargs
    public static <T> ArrayList<T> newArrayList(T... values) {
        return ListUtil.toList(values);
    }

    /**
     * 新建一个ArrayList
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @return ArrayList对象
     */
    public static <T> ArrayList<T> newArrayList(Collection<T> collection) {
        return ListUtil.toList(collection);
    }

    /**
     * 获取集合中指定多个下标的元素值，下标可以为负数，例如-1表示最后一个元素
     *
     * @param <T>        元素类型
     * @param collection 集合
     * @param indexes    下标，支持负数
     * @return 元素值列表
     * @since 4.0.6
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getAny(Collection<T> collection, int... indexes) {
        final int size = collection.size();
        final ArrayList<T> result = new ArrayList<>();
        if (collection instanceof List) {
            final List<T> list = ((List<T>) collection);
            for (int index : indexes) {
                if (index < 0) {
                    index += size;
                }
                result.add(list.get(index));
            }
        } else {
            final Object[] array = collection.toArray();
            for (int index : indexes) {
                if (index < 0) {
                    index += size;
                }
                result.add((T) array[index]);
            }
        }
        return result;
    }

    /**
     * 获取匹配规则定义中匹配到元素的所有位置<br>
     * 此方法对于某些无序集合的位置信息，以转换为数组后的位置为准。
     *
     * @param <T>        元素类型
     * @param collection 集合
     * @param matcher    匹配器，为空则全部匹配
     * @return 位置数组
     * @since 5.2.5
     */
    public static <T> int[] indexOfAll(Collection<T> collection, Matcher<T> matcher) {
        final List<Integer> indexList = new ArrayList<>();
        if (null != collection) {
            int index = 0;
            for (T t : collection) {
                if (null == matcher || matcher.match(t)) {
                    indexList.add(index);
                }
                index++;
            }
        }
        int[] result = new int[indexList.size()];
        for (int i = 0; i < indexList.size(); i++) {
            result[i] = indexList.get(i);
        }
        return result;
    }

    /**
     * 针对一个参数做相应的操作<br>
     * 此函数接口与JDK8中Consumer不同是多提供了index参数，用于标记遍历对象是第几个。
     *
     * @param <T> 处理参数类型
     * @author Looly
     */
    @FunctionalInterface
    public interface Consumer<T> extends Serializable {
        /**
         * 接受并处理一个参数
         *
         * @param value 参数值
         * @param index 参数在集合中的索引
         */
        void accept(T value, int index);
    }

}
