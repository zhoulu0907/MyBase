package com.cmsr.onebase.framework.aynline;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Table;

/**
 * @ClassName JpaUtils
 * @Description JPA工具类，提供JPA相关的通用操作
 * @Author mickey
 * @Date 2025/7/7 11:05
 */
public class JpaUtils {
    public static String getTableName(Class<?> entityClass) {
        TableName annotation = entityClass.getAnnotation(TableName.class);
        return annotation != null ? annotation.value() : null;
    }
}
