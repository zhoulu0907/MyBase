package com.cmsr.onebase.module.etl.executor.database.kingbase;

import org.apache.flink.connector.jdbc.postgres.database.PostgresFactory;

/**
 * KingBase8 数据库工厂类, 用于创建 KingBase8 数据库连接.
 *
 * @author shiyutian
 * @date 2026-01-06
 */
public class KingBase8Factory extends PostgresFactory {
    public boolean acceptsURL(String url) {
        return url.startsWith("jdbc:kingbase8:");
    }
}
