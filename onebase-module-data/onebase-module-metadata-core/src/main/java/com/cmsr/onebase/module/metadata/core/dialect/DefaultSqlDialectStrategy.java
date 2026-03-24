package com.cmsr.onebase.module.metadata.core.dialect;

import com.mybatisflex.core.dialect.DbType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 默认 SQL 方言策略
 * 提供通用的 SQL 方言实现，适用于大部分数据库
 * 特殊数据库（如 Oracle）通过子类覆盖差异
 *
 * @author claude
 * @date 2026-03-23
 */
@Component
public class DefaultSqlDialectStrategy implements SqlDialectStrategy {

    /**
     * 默认 CAST 目标类型
     * 大部分数据库使用 DECIMAL
     */
    protected static final String DEFAULT_CAST_TYPE = "DECIMAL";

    @Override
    public DbType getSupportedDbType() {
        // 返回 null 表示默认策略
        return null;
    }

    @Override
    public String getCastType() {
        return DEFAULT_CAST_TYPE;
    }

    @Override
    public String sum(String column) {
        validateColumn(column);
        return "SUM(CAST(" + column + " AS " + getCastType() + "))";
    }

    @Override
    public String avg(String column) {
        validateColumn(column);
        return "AVG(CAST(" + column + " AS " + getCastType() + "))";
    }

    @Override
    public String max(String column) {
        validateColumn(column);
        return "MAX(" + column + ")";
    }

    @Override
    public String min(String column) {
        validateColumn(column);
        return "MIN(" + column + ")";
    }

    @Override
    public String countDistinct(String column) {
        validateColumn(column);
        return "COUNT(DISTINCT " + column + ")";
    }

    /**
     * 校验字段名是否合法（防止 SQL 注入）
     */
    protected void validateColumn(String column) {
        if (StringUtils.isBlank(column)) {
            throw new IllegalArgumentException("column cannot be blank");
        }
        if (!column.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
            throw new IllegalArgumentException("invalid column name: " + column);
        }
    }
}