package com.cmsr.onebase.module.app.util;

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
     * 实现两个List的full outer join，比较使用compare函数，返回结果为List<Pair<T, T>>
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

}
