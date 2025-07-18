package com.cmsr.onebase.framework.common.anyline.utils;

import jakarta.persistence.Table;

/**
 * @ClassName JpaUtils
 * @Description JPA工具类，提供JPA相关的通用操作
 * @Author mickey
 * @Date 2025/7/7 11:05
 */
public class JpaUtils {
    public static String getTableName(Class<?> entityClass) {
        Table annotation = entityClass.getAnnotation(Table.class);
        return annotation != null ? annotation.name() : null;
    }
}
