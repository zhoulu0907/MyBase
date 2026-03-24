package com.cmsr.onebase.module.metadata.core.dialect.impl;

import com.cmsr.onebase.module.metadata.core.dialect.DefaultSqlDialectStrategy;
import com.mybatisflex.core.dialect.DbType;
import org.springframework.stereotype.Component;

/**
 * Oracle 数据库方言策略
 * 覆盖 Oracle 特有的 SQL 差异
 *
 * @author claude
 * @date 2026-03-23
 */
@Component
public class OracleSqlDialectStrategy extends DefaultSqlDialectStrategy {

    /**
     * Oracle 的 CAST 目标类型
     */
    private static final String ORACLE_CAST_TYPE = "NUMBER";

    @Override
    public DbType getSupportedDbType() {
        return DbType.ORACLE;
    }

    @Override
    public String getCastType() {
        return ORACLE_CAST_TYPE;
    }
}