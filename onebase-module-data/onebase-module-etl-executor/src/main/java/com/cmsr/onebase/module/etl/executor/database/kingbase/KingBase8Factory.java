package com.cmsr.onebase.module.etl.executor.database.kingbase;

import org.apache.flink.connector.jdbc.postgres.database.PostgresFactory;

public class KingBase8Factory extends PostgresFactory {
    public boolean acceptsURL(String url) {
        return url.startsWith("jdbc:kingbase8:");
    }
}
