package com.cmsr.onebase.module.metadata.core.enums;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 数据库保留关键字常量类
 * <p>
 * 包含PostgreSQL/人大金仓等数据库的Reserved关键字（约92个），
 * 这些关键字不能直接用作表名、字段名等标识符，否则会导致SQL语法错误。
 * </p>
 *
 * @author matianyu
 * @date 2025-12-29
 */
public final class DatabaseReservedKeywords {

    private DatabaseReservedKeywords() {
        // 私有构造函数，防止实例化
    }

    /**
     * PostgreSQL Reserved 关键字集合（大写存储，校验时忽略大小写）
     * <p>
     * 数据来源：PostgreSQL 18 官方文档 Appendix C. SQL Key Words
     * https://www.postgresql.org/docs/current/sql-keywords-appendix.html
     * </p>
     * <p>
     * 人大金仓（KingbaseES）基于PostgreSQL，关键字基本兼容，复用此列表。
     * </p>
     */
    private static final Set<String> RESERVED_KEYWORDS;

    static {
        Set<String> keywords = new HashSet<>();
        
        // A
        keywords.add("ALL");
        keywords.add("ANALYSE");
        keywords.add("ANALYZE");
        keywords.add("AND");
        keywords.add("ANY");
        keywords.add("ARRAY");
        keywords.add("AS");
        keywords.add("ASC");
        keywords.add("ASYMMETRIC");
        keywords.add("AUTHORIZATION");
        
        // B
        keywords.add("BINARY");
        keywords.add("BOTH");
        
        // C
        keywords.add("CASE");
        keywords.add("CAST");
        keywords.add("CHECK");
        keywords.add("COLLATE");
        keywords.add("COLLATION");
        keywords.add("COLUMN");
        keywords.add("CONCURRENTLY");
        keywords.add("CONSTRAINT");
        keywords.add("CREATE");
        keywords.add("CROSS");
        keywords.add("CURRENT_CATALOG");
        keywords.add("CURRENT_DATE");
        keywords.add("CURRENT_ROLE");
        keywords.add("CURRENT_SCHEMA");
        keywords.add("CURRENT_TIME");
        keywords.add("CURRENT_TIMESTAMP");
        keywords.add("CURRENT_USER");
        
        // D
        keywords.add("DEFAULT");
        keywords.add("DEFERRABLE");
        keywords.add("DESC");
        keywords.add("DISTINCT");
        keywords.add("DO");
        
        // E
        keywords.add("ELSE");
        keywords.add("END");
        keywords.add("EXCEPT");
        
        // F
        keywords.add("FALSE");
        keywords.add("FETCH");
        keywords.add("FOR");
        keywords.add("FOREIGN");
        keywords.add("FREEZE");
        keywords.add("FROM");
        keywords.add("FULL");
        
        // G
        keywords.add("GRANT");
        keywords.add("GROUP");
        
        // H
        keywords.add("HAVING");
        
        // I
        keywords.add("IF");
        keywords.add("ILIKE");
        keywords.add("IN");
        keywords.add("INITIALLY");
        keywords.add("INNER");
        keywords.add("INTERSECT");
        keywords.add("INTO");
        keywords.add("IS");
        keywords.add("ISNULL");
        
        // J
        keywords.add("JOIN");
        
        // L
        keywords.add("LATERAL");
        keywords.add("LEADING");
        keywords.add("LEFT");
        keywords.add("LIKE");
        keywords.add("LIMIT");
        keywords.add("LOCALTIME");
        keywords.add("LOCALTIMESTAMP");
        
        // N
        keywords.add("NATURAL");
        keywords.add("NOT");
        keywords.add("NOTNULL");
        keywords.add("NULL");
        
        // O
        keywords.add("OFFSET");
        keywords.add("ON");
        keywords.add("ONLY");
        keywords.add("OR");
        keywords.add("ORDER");
        keywords.add("OUTER");
        keywords.add("OVERLAPS");
        
        // P
        keywords.add("PLACING");
        keywords.add("PRIMARY");
        
        // R
        keywords.add("REFERENCES");
        keywords.add("RETURNING");
        keywords.add("RIGHT");
        
        // S
        keywords.add("SELECT");
        keywords.add("SESSION_USER");
        keywords.add("SIMILAR");
        keywords.add("SOME");
        keywords.add("SYMMETRIC");
        keywords.add("SYSTEM_USER");
        
        // T
        keywords.add("TABLE");
        keywords.add("TABLESAMPLE");
        keywords.add("THEN");
        keywords.add("TO");
        keywords.add("TRAILING");
        keywords.add("TRUE");
        
        // U
        keywords.add("UNION");
        keywords.add("UNIQUE");
        keywords.add("USER");
        keywords.add("USING");
        
        // V
        keywords.add("VARIADIC");
        keywords.add("VERBOSE");
        
        // W
        keywords.add("WHEN");
        keywords.add("WHERE");
        keywords.add("WINDOW");
        keywords.add("WITH");
        
        RESERVED_KEYWORDS = Collections.unmodifiableSet(keywords);
    }

    /**
     * 判断指定名称是否为数据库保留关键字
     *
     * @param name 待校验的名称（字段名、表名等）
     * @return true-是保留关键字，false-不是保留关键字
     */
    public static boolean isReservedKeyword(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return RESERVED_KEYWORDS.contains(name.trim().toUpperCase());
    }

    /**
     * 获取所有保留关键字集合（只读）
     *
     * @return 保留关键字集合
     */
    public static Set<String> getReservedKeywords() {
        return RESERVED_KEYWORDS;
    }

    /**
     * 获取保留关键字数量
     *
     * @return 关键字数量
     */
    public static int getKeywordCount() {
        return RESERVED_KEYWORDS.size();
    }
}
