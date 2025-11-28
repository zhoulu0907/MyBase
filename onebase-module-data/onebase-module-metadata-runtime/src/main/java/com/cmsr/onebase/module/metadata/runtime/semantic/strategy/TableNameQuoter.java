package com.cmsr.onebase.module.metadata.runtime.semantic.strategy;

import org.springframework.stereotype.Component;

@Component
public class TableNameQuoter {
    public String quote(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) { return tableName; }
        if (tableName.startsWith("\"") && tableName.endsWith("\"")) { return tableName; }
        return tableName;
    }
}

