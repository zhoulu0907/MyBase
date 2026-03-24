package com.cmsr.onebase.module.metadata.core.dialect;

import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DbTypeUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SQL 方言策略注册器
 * 管理不同数据库的方言策略，自动选择合适的策略
 *
 * @author claude
 * @date 2026-03-23
 */
@Slf4j
@Component
public class SqlDialectStrategyRegistry {

    private final Map<DbType, SqlDialectStrategy> strategyMap = new ConcurrentHashMap<>();
    private SqlDialectStrategy defaultStrategy;

    @Resource
    private List<SqlDialectStrategy> strategies;

    @PostConstruct
    public void init() {
        if (strategies != null) {
            for (SqlDialectStrategy strategy : strategies) {
                DbType dbType = strategy.getSupportedDbType();
                if (dbType == null) {
                    // 默认策略
                    defaultStrategy = strategy;
                    log.info("Registered default SQL dialect strategy: {}", strategy.getClass().getSimpleName());
                } else {
                    strategyMap.put(dbType, strategy);
                    log.info("Registered SQL dialect strategy for DbType {}: {}", dbType, strategy.getClass().getSimpleName());
                }
            }
        }
        log.info("SqlDialectStrategyRegistry initialized with {} specific strategies", strategyMap.size());
    }

    /**
     * 根据数据库类型获取策略
     *
     * @param dbType 数据库类型
     * @return 对应的策略实现，如果未找到则返回默认策略
     */
    public SqlDialectStrategy getStrategy(DbType dbType) {
        SqlDialectStrategy strategy = strategyMap.get(dbType);
        if (strategy == null) {
            strategy = defaultStrategy;
        }
        if (strategy == null) {
            throw new IllegalStateException("No SQL dialect strategy found for DbType: " + dbType);
        }
        return strategy;
    }

    /**
     * 获取当前数据库对应的策略
     *
     * @return 当前数据库的策略实现
     */
    public SqlDialectStrategy getCurrentStrategy() {
        DbType dbType = DbTypeUtil.getCurrentDbType();
        if (dbType == null) {
            log.debug("Cannot determine current DbType, using default strategy");
            dbType = DbType.POSTGRE_SQL;
        }
        return getStrategy(dbType);
    }
}