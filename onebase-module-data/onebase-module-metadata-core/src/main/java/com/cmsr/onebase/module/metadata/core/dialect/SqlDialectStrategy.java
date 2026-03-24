package com.cmsr.onebase.module.metadata.core.dialect;

import com.mybatisflex.core.dialect.DbType;

/**
 * SQL 方言策略接口
 * 为不同数据库提供 SQL 差异适配能力
 *
 * @author claude
 * @date 2026-03-23
 */
public interface SqlDialectStrategy {

    /**
     * 获取支持的数据库类型
     * 返回 null 表示默认策略，适用于所有未特殊配置的数据库
     *
     * @return DbType 枚举值，或 null
     */
    DbType getSupportedDbType();

    /**
     * 获取 CAST 目标类型
     * Oracle -> NUMBER, 其他 -> DECIMAL
     *
     * @return 数值类型名称
     */
    String getCastType();

    // ========== 聚合函数 ==========

    /**
     * 生成 SUM 函数 SQL
     *
     * @param column 字段名
     * @return SQL 片段，如 "SUM(CAST(amount AS DECIMAL))"
     */
    String sum(String column);

    /**
     * 生成 AVG 函数 SQL
     *
     * @param column 字段名
     * @return SQL 片段，如 "AVG(CAST(amount AS DECIMAL))"
     */
    String avg(String column);

    /**
     * 生成 MAX 函数 SQL
     *
     * @param column 字段名
     * @return SQL 片段，如 "MAX(column)"
     */
    String max(String column);

    /**
     * 生成 MIN 函数 SQL
     *
     * @param column 字段名
     * @return SQL 片段，如 "MIN(column)"
     */
    String min(String column);

    /**
     * 生成 COUNT DISTINCT 函数 SQL
     *
     * @param column 字段名
     * @return SQL 片段，如 "COUNT(DISTINCT column)"
     */
    String countDistinct(String column);
}