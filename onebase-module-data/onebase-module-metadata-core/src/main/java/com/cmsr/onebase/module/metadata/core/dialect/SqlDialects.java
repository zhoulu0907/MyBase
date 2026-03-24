package com.cmsr.onebase.module.metadata.core.dialect;

import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DbTypeUtil;
import org.springframework.stereotype.Component;

/**
 * SQL 方言工具类
 * 提供静态方法简化调用，自动根据当前数据库类型生成 SQL
 *
 * @author claude
 * @date 2026-03-23
 */
@Component
public class SqlDialects {

    private static SqlDialectStrategyRegistry registry;

    public SqlDialects(SqlDialectStrategyRegistry registry) {
        SqlDialects.registry = registry;
    }

    private static SqlDialectStrategy strategy() {
        if (registry == null) {
            throw new IllegalStateException("SqlDialects not initialized");
        }
        return registry.getCurrentStrategy();
    }

    // ========== 聚合函数 ==========

    /**
     * SUM 函数，自动添加 CAST
     */
    public static String sum(String column) {
        return strategy().sum(column);
    }

    /**
     * AVG 函数，自动添加 CAST
     */
    public static String avg(String column) {
        return strategy().avg(column);
    }

    /**
     * MAX 函数
     */
    public static String max(String column) {
        return strategy().max(column);
    }

    /**
     * MIN 函数
     */
    public static String min(String column) {
        return strategy().min(column);
    }

    /**
     * COUNT DISTINCT 函数
     */
    public static String countDistinct(String column) {
        return strategy().countDistinct(column);
    }

    // ========== 工具方法 ==========

    /**
     * 获取当前数据库类型
     */
    public static DbType currentDbType() {
        return DbTypeUtil.getCurrentDbType();
    }

    /**
     * 获取当前数据库的 CAST 类型
     */
    public static String castType() {
        return strategy().getCastType();
    }
}