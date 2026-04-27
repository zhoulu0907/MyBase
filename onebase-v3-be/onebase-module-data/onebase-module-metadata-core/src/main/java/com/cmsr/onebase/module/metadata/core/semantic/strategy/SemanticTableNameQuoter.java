package com.cmsr.onebase.module.metadata.core.semantic.strategy;

import org.springframework.stereotype.Component;

@Component
@Deprecated
public class SemanticTableNameQuoter {
    public String quote(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) { return tableName; }
        if (tableName.startsWith("\"") && tableName.endsWith("\"")) { return tableName; }
        return tableName;
    }
}
