package com.cmsr.onebase.module.app.build.util;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

/**
 * @Author：huangjie
 * @Date：2025/8/7 13:33
 */
public class AuthUtils {

    /**
     * 创建角色编码，编码必须英文开头，且只能包含英文、数字、下划线
     *
     * @return
     */
    public static String createRoleCode() {
        return "ROLE_" + UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 实现两个List的full outer join，比较使用compare函数，返回结果为List<Pair<T, U>>
     *
     * @param list1
     * @param list2
     * @param compare
     * @param <T>
     * @return
     */
    public static <T, U> List<Pair<T, U>> fullOuterJoin(List<T> list1, List<U> list2, BiFunction<T, U, Boolean> compare) {
        if (list1 == null) list1 = new ArrayList<>();
        if (list2 == null) list2 = new ArrayList<>();

        List<Pair<T, U>> result = new ArrayList<>();
        boolean[] matched1 = new boolean[list1.size()];
        boolean[] matched2 = new boolean[list2.size()];

        // 查找匹配的元素对
        for (int i = 0; i < list1.size(); i++) {
            T item1 = list1.get(i);
            for (int j = 0; j < list2.size(); j++) {
                U item2 = list2.get(j);
                if (compare.apply(item1, item2)) {
                    result.add(Pair.of(item1, item2));
                    matched1[i] = true;
                    matched2[j] = true;
                    break;
                }
            }
        }

        // 添加list1中未匹配的元素
        for (int i = 0; i < list1.size(); i++) {
            if (!matched1[i]) {
                result.add(Pair.of(list1.get(i), null));
            }
        }

        // 添加list2中未匹配的元素
        for (int j = 0; j < list2.size(); j++) {
            if (!matched2[j]) {
                result.add(Pair.of(null, list2.get(j)));
            }
        }

        return result;
    }

    /**
     * 实现两个List的left outer join，比较使用compare函数，返回结果为List<Pair<T, U>>
     * 保留list1中元素的顺序
     *
     * @param list1   左侧列表
     * @param list2   右侧列表
     * @param compare 比较函数，用于判断两个元素是否匹配
     * @param <T>     左侧列表元素类型
     * @param <U>     右侧列表元素类型
     * @return 匹配结果列表，包含左侧所有元素，未匹配的右侧元素为null
     */
    public static <T, U> List<Pair<T, U>> leftOuterJoin(List<T> list1, List<U> list2, BiFunction<T, U, Boolean> compare) {
        if (list1 == null) list1 = new ArrayList<>();
        if (list2 == null) list2 = new ArrayList<>();

        List<Pair<T, U>> result = new ArrayList<>();

        // 遍历list1中的每个元素
        for (T item1 : list1) {
            Pair<T, U> pair = Pair.of(item1, null); // 默认右侧为null

            // 在list2中查找匹配的元素
            for (U item2 : list2) {
                if (compare.apply(item1, item2)) {
                    pair = Pair.of(item1, item2); // 找到匹配项
                    break;
                }
            }

            result.add(pair);
        }

        return result;
    }
}
