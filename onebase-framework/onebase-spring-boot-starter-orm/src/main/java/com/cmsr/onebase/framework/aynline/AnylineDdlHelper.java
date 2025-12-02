package com.cmsr.onebase.framework.aynline;

import lombok.extern.slf4j.Slf4j;
import org.anyline.metadata.Column;
import org.anyline.metadata.Table;
import org.anyline.proxy.CacheProxy;
import org.anyline.service.AnylineService;

import java.util.LinkedHashMap;

/**
 * Anyline DDL 操作辅助工具类
 * <p>
 * 封装 Anyline 原生 API 的 DDL 操作，提供统一的建表、添加/修改/删除/重命名列等功能。
 * 支持跨数据库兼容（PostgreSQL、达梦、人大金仓等）。
 *
 * @author matianyu
 * @date 2025-11-29
 */
@Slf4j
public class AnylineDdlHelper {

    private AnylineDdlHelper() {
        // 工具类不允许实例化
    }

    /**
     * 清除 Anyline 元数据缓存
     * <p>
     * Anyline 会缓存表结构信息（默认缓存24小时），导致刚创建的表/列查询不到。
     * 在执行 DDL 操作前后应调用此方法清除缓存。
     */
    public static void clearMetadataCache() {
        try {
            CacheProxy.clear();
            log.debug("已清除 Anyline 元数据缓存");
        } catch (Exception e) {
            log.warn("清除 Anyline 元数据缓存失败: {}", e.getMessage());
        }
    }

    /**
     * 检查表是否存在
     *
     * @param service   AnylineService 实例
     * @param tableName 表名
     * @return true-存在，false-不存在
     */
    public static boolean tableExists(AnylineService<?> service, String tableName) {
        clearMetadataCache();
        Table<?> table = service.metadata().table(tableName);
        boolean exists = (table != null);
        log.debug("检查表 {} 是否存在: {}", tableName, exists);
        return exists;
    }

    /**
     * 检查列是否存在
     *
     * @param service    AnylineService 实例
     * @param tableName  表名
     * @param columnName 列名
     * @return true-存在，false-不存在
     */
    public static boolean columnExists(AnylineService<?> service, String tableName, String columnName) {
        clearMetadataCache();
        Table<?> table = service.metadata().table(tableName);
        if (table == null) {
            log.debug("表 {} 不存在，列 {} 检查返回 false", tableName, columnName);
            return false;
        }

        // 多种匹配策略：精确匹配 -> 小写匹配 -> 忽略大小写遍历
        Column column = table.getColumn(columnName);
        if (column != null) {
            return true;
        }

        column = table.getColumn(columnName.toLowerCase());
        if (column != null) {
            return true;
        }

        LinkedHashMap<String, Column> columns = table.getColumns();
        if (columns != null) {
            for (Column col : columns.values()) {
                if (col.getName().equalsIgnoreCase(columnName)) {
                    return true;
                }
            }
        }

        log.debug("列 {} 不存在于表 {} 中", columnName, tableName);
        return false;
    }

    /**
     * 创建表
     * <p>
     * 使用 Anyline 原生 API 创建表，自动适配不同数据库。
     *
     * @param service AnylineService 实例
     * @param table   Table 对象（包含表名、列定义、主键等）
     */
    public static void createTable(AnylineService<?> service, Table<?> table) {
        clearMetadataCache();
        log.info("开始创建表: {}", table.getName());
        try {
            service.ddl().create(table);
        } catch (Exception e) {
            log.error("创建表 {} 失败: {}", table.getName(), e.getMessage(), e);
            throw new RuntimeException("创建表失败: " + e.getMessage(), e);
        }
        clearMetadataCache();
        log.info("成功创建表: {}", table.getName());
    }

    /**
     * 删除表
     *
     * @param service   AnylineService 实例
     * @param tableName 表名
     */
    public static void dropTable(AnylineService<?> service, String tableName) {
        clearMetadataCache();
        if (!tableExists(service, tableName)) {
            log.info("表 {} 不存在，跳过删除", tableName);
            return;
        }
        Table<?> table = new Table<>(tableName);
        log.info("开始删除表: {}", tableName);
        try {
            service.ddl().drop(table);
        } catch (Exception e) {
            log.error("删除表 {} 失败: {}", tableName, e.getMessage(), e);
            throw new RuntimeException("删除表失败: " + e.getMessage(), e);
        }
        clearMetadataCache();
        log.info("成功删除表: {}", tableName);
    }

    /**
     * 添加列
     * <p>
     * 使用 Anyline 原生 API 添加列，自动适配不同数据库。
     *
     * @param service AnylineService 实例
     * @param column  Column 对象（需设置 table、name、typeName 等属性）
     */
    public static void addColumn(AnylineService<?> service, Column column) {
        String tableName = column.getTableName(true);
        String columnName = column.getName();

        clearMetadataCache();

        // 检查列是否已存在
        if (columnExists(service, tableName, columnName)) {
            log.warn("列 {} 已存在于表 {} 中，跳过添加", columnName, tableName);
            return;
        }

        log.info("开始为表 {} 添加列: {}", tableName, columnName);
        try {
            service.ddl().add(column);
        } catch (Exception e) {
            log.error("为表 {} 添加列 {} 失败: {}", tableName, columnName, e.getMessage(), e);
            throw new RuntimeException("添加列失败: " + e.getMessage(), e);
        }
        clearMetadataCache();
        log.info("成功为表 {} 添加列: {}", tableName, columnName);
    }

    /**
     * 删除列
     *
     * @param service    AnylineService 实例
     * @param tableName  表名
     * @param columnName 列名
     */
    public static void dropColumn(AnylineService<?> service, String tableName, String columnName) {
        clearMetadataCache();

        // 检查列是否存在
        if (!columnExists(service, tableName, columnName)) {
            log.info("列 {} 不存在于表 {}，跳过删除", columnName, tableName);
            return;
        }

        Column column = new Column(columnName);
        column.setTable(new Table<>(tableName));

        log.info("开始从表 {} 删除列: {}", tableName, columnName);
        try {
            service.ddl().drop(column);
        } catch (Exception e) {
            log.error("从表 {} 删除列 {} 失败: {}", tableName, columnName, e.getMessage(), e);
            throw new RuntimeException("删除列失败: " + e.getMessage(), e);
        }
        clearMetadataCache();
        log.info("成功从表 {} 删除列: {}", tableName, columnName);
    }

    /**
     * 重命名列
     *
     * @param service    AnylineService 实例
     * @param tableName  表名
     * @param oldName    旧列名
     * @param newName    新列名
     */
    public static void renameColumn(AnylineService<?> service, String tableName, String oldName, String newName) {
        clearMetadataCache();

        // 检查旧列是否存在
        if (!columnExists(service, tableName, oldName)) {
            log.warn("旧列 {} 不存在于表 {}，跳过重命名", oldName, tableName);
            return;
        }

        // 检查新列是否已存在
        if (columnExists(service, tableName, newName)) {
            log.warn("新列 {} 已存在于表 {}，跳过重命名", newName, tableName);
            return;
        }

        Column column = new Column(oldName);
        column.setTable(new Table<>(tableName));

        log.info("开始重命名表 {} 的列: {} -> {}", tableName, oldName, newName);
        try {
            service.ddl().rename(column, newName);
        } catch (Exception e) {
            log.error("重命名表 {} 的列 {} -> {} 失败: {}", tableName, oldName, newName, e.getMessage(), e);
            throw new RuntimeException("重命名列失败: " + e.getMessage(), e);
        }
        clearMetadataCache();
        log.info("成功重命名表 {} 的列: {} -> {}", tableName, oldName, newName);
    }

    /**
     * 修改列（简单场景）
     * <p>
     * 适用于修改列的类型、约束、默认值等简单场景。
     * 对于复杂的类型转换（如 VARCHAR→INTEGER），建议使用 alterColumnWithDDL 方法。
     *
     * @param service   AnylineService 实例
     * @param tableName 表名
     * @param column    Column 对象（包含新的列定义）
     */
    public static void alterColumn(AnylineService<?> service, String tableName, Column column) {
        String columnName = column.getName();

        clearMetadataCache();

        // 检查列是否存在
        if (!columnExists(service, tableName, columnName)) {
            log.warn("列 {} 不存在于表 {}，无法修改", columnName, tableName);
            return;
        }

        // 确保 Column 关联到正确的表
        if (column.getTable() == null) {
            column.setTable(new Table<>(tableName));
        }

        log.info("开始修改表 {} 的列: {}", tableName, columnName);
        try {
            service.ddl().alter(column);
        } catch (Exception e) {
            log.error("修改表 {} 的列 {} 失败: {}", tableName, columnName, e.getMessage(), e);
            throw new RuntimeException("修改列失败: " + e.getMessage(), e);
        }
        clearMetadataCache();
        log.info("成功修改表 {} 的列: {}", tableName, columnName);
    }

    /**
     * 修改列（带 USING 子句，用于复杂类型转换）
     * <p>
     * PostgreSQL/KingBase 在修改列类型时，如果数据类型不兼容，需要使用 USING 子句进行类型转换。
     * 此方法直接执行手动拼接的 DDL 语句。
     *
     * @param service        AnylineService 实例
     * @param tableName      表名
     * @param columnName     列名
     * @param alterColumnDDL 完整的 ALTER COLUMN DDL 语句（包含 USING 子句）
     */
    public static void alterColumnWithDDL(AnylineService<?> service, String tableName, String columnName,
                                          String alterColumnDDL) {
        clearMetadataCache();
        log.info("执行自定义 ALTER COLUMN DDL 修改表 {} 的列 {}", tableName, columnName);
        log.debug("DDL 内容: {}", alterColumnDDL);

        // 拆分多条语句分别执行
        String[] sqlStatements = alterColumnDDL.split(";\n");
        for (String sql : sqlStatements) {
            String trimmedSql = sql.trim();
            if (!trimmedSql.isEmpty()) {
                // 如果末尾没有分号，添加分号
                if (!trimmedSql.endsWith(";")) {
                    trimmedSql = trimmedSql + ";";
                }
                log.debug("执行 SQL: {}", trimmedSql);
                service.execute(trimmedSql);
            }
        }

        clearMetadataCache();
        log.info("成功修改表 {} 的列 {}", tableName, columnName);
    }

    /**
     * 构建 Column 对象（不指定表名）
     * <p>
     * 便捷方法，用于创建 Column 对象并设置常用属性。
     * 表名可在后续操作中通过 column.setTable() 设置。
     *
     * @param columnName   列名
     * @param typeName     数据类型（如 VARCHAR(255)、BIGINT、TIMESTAMP 等）
     * @param nullable     是否允许为空
     * @param defaultValue 默认值（可为 null）
     * @param comment      注释（可为 null）
     * @return Column 对象
     */
    public static Column buildColumn(String columnName, String typeName,
                                     boolean nullable, Object defaultValue, String comment) {
        Column column = new Column(columnName);
        column.setTypeName(typeName);
        column.setNullable(nullable);
        if (defaultValue != null) {
            column.setDefaultValue(defaultValue);
        }
        if (comment != null && !comment.trim().isEmpty()) {
            column.setComment(comment);
        }
        return column;
    }

    /**
     * 构建 Column 对象（指定表名）
     * <p>
     * 便捷方法，用于创建 Column 对象并设置常用属性，包括所属表名。
     *
     * @param tableName    表名
     * @param columnName   列名
     * @param typeName     数据类型（如 VARCHAR(255)、BIGINT、TIMESTAMP 等）
     * @param nullable     是否允许为空
     * @param defaultValue 默认值（可为 null）
     * @param comment      注释（可为 null）
     * @return Column 对象
     */
    public static Column buildColumn(String tableName, String columnName, String typeName,
                                     boolean nullable, Object defaultValue, String comment) {
        Column column = buildColumn(columnName, typeName, nullable, defaultValue, comment);
        column.setTable(new Table<>(tableName));
        return column;
    }

    /**
     * 构建主键列
     *
     * @param tableName  表名
     * @param columnName 列名
     * @param typeName   数据类型
     * @param comment    注释（可为 null）
     * @return Column 对象
     */
    public static Column buildPrimaryKeyColumn(String tableName, String columnName, String typeName, String comment) {
        Column column = buildColumn(tableName, columnName, typeName, false, null, comment);
        column.setPrimaryKey(true);
        return column;
    }

    /**
     * 判断是否需要 USING 子句进行类型转换
     * <p>
     * PostgreSQL/KingBase 在某些类型转换时需要 USING 子句，如 VARCHAR→INTEGER、VARCHAR→TIMESTAMP 等。
     *
     * @param oldTypeName 旧数据类型
     * @param newTypeName 新数据类型
     * @return true-需要 USING 子句，false-不需要
     */
    public static boolean needsUsingClause(String oldTypeName, String newTypeName) {
        if (oldTypeName == null || newTypeName == null) {
            return false;
        }

        String oldUpper = oldTypeName.toUpperCase();
        String newUpper = newTypeName.toUpperCase();

        // 相同类型或兼容类型不需要 USING
        if (oldUpper.equals(newUpper)) {
            return false;
        }

        // VARCHAR/TEXT 转其他类型通常需要 USING
        boolean isOldTextType = oldUpper.contains("VARCHAR") || oldUpper.contains("TEXT") || oldUpper.contains("CHAR");
        boolean isNewNumericType = newUpper.contains("INT") || newUpper.contains("NUMERIC") ||
                newUpper.contains("DECIMAL") || newUpper.contains("FLOAT") || newUpper.contains("DOUBLE");
        boolean isNewDateType = newUpper.contains("TIMESTAMP") || newUpper.contains("DATE") || newUpper.contains("TIME");
        boolean isNewBooleanType = newUpper.contains("BOOLEAN") || newUpper.contains("BOOL");

        if (isOldTextType && (isNewNumericType || isNewDateType || isNewBooleanType)) {
            return true;
        }

        // 数值类型转其他类型通常需要 USING
        boolean isOldNumericType = oldUpper.contains("INT") || oldUpper.contains("NUMERIC") ||
                oldUpper.contains("DECIMAL") || oldUpper.contains("FLOAT") || oldUpper.contains("DOUBLE");
        if (isOldNumericType && (isNewDateType || isNewBooleanType)) {
            return true;
        }

        return false;
    }

    /**
     * 生成 USING 子句（用于 PostgreSQL/KingBase 类型转换）
     * <p>
     * 使用 CASE WHEN 进行安全的类型转换，无效数据将被设为 NULL。
     *
     * @param targetFieldType 目标字段类型（业务字段类型，如 TIMESTAMP、INTEGER 等）
     * @param fieldName       字段名
     * @return USING 子句，如果不需要则返回 null
     */
    public static String generateUsingClause(String targetFieldType, String fieldName) {
        if (targetFieldType == null) {
            return null;
        }

        String quotedFieldName = "\"" + fieldName + "\"";
        // 先将列转换为 TEXT 类型，这样可以处理从任何类型的转换
        String textFieldName = quotedFieldName + "::text";

        // 需要显式类型转换的字段类型，使用 CASE WHEN 进行安全转换
        switch (targetFieldType.toUpperCase()) {
            case "DATETIME":
            case "TIMESTAMP":
                // VARCHAR/TEXT 转 TIMESTAMP：使用 CASE WHEN 处理无效数据
                return "CASE WHEN " + textFieldName + " ~ '^\\\\d{4}-\\\\d{2}-\\\\d{2}' " +
                        "THEN " + textFieldName + "::timestamp " +
                        "ELSE NULL END";

            case "DATE":
                // VARCHAR/TEXT 转 DATE
                return "CASE WHEN " + textFieldName + " ~ '^\\\\d{4}-\\\\d{2}-\\\\d{2}' " +
                        "THEN " + textFieldName + "::date " +
                        "ELSE NULL END";

            case "NUMBER":
            case "NUMERIC":
            case "DECIMAL":
                // VARCHAR/TEXT 转 NUMERIC：检查是否为数字格式（支持小数和负数）
                return "CASE WHEN " + textFieldName + " ~ '^-?\\\\d+\\\\.?\\\\d*$' " +
                        "THEN " + textFieldName + "::numeric " +
                        "ELSE NULL END";

            case "INTEGER":
            case "INT":
            case "BIGINT":
                // VARCHAR/TEXT 转 INTEGER：检查是否为整数格式
                return "CASE WHEN " + textFieldName + " ~ '^-?\\\\d+$' " +
                        "THEN " + textFieldName + "::integer " +
                        "ELSE NULL END";

            case "BOOLEAN":
            case "BOOL":
                // VARCHAR/TEXT 转 BOOLEAN：支持常见的布尔值表示
                return "CASE WHEN " + textFieldName
                        + " IN ('true', 'false', 't', 'f', '1', '0', 'yes', 'no', 'y', 'n') " +
                        "THEN " + textFieldName + "::boolean " +
                        "ELSE NULL END";

            default:
                // 其他类型通常可以自动转换，不需要 USING 子句
                return null;
        }
    }

    /**
     * 格式化默认值用于 SQL 语句
     * <p>
     * 根据字段类型判断是否需要用单引号包裹默认值。
     *
     * @param fieldType    字段类型
     * @param defaultValue 默认值
     * @return 格式化后的默认值
     */
    public static String formatDefaultValue(String fieldType, String defaultValue) {
        if (defaultValue == null || defaultValue.trim().isEmpty()) {
            return null;
        }

        // 数值类型：不需要单引号
        if (fieldType.contains("NUMBER") || fieldType.contains("INTEGER") ||
                fieldType.contains("DECIMAL") || fieldType.contains("FLOAT") ||
                fieldType.contains("DOUBLE") || fieldType.contains("BIGINT") ||
                fieldType.contains("SMALLINT") || fieldType.contains("TINYINT") ||
                fieldType.contains("BOOLEAN") || fieldType.contains("BOOL")) {
            return defaultValue;
        }

        // 特殊函数或表达式（如 CURRENT_TIMESTAMP、NOW() 等）：不需要单引号
        String upperValue = defaultValue.toUpperCase();
        if (upperValue.contains("CURRENT_") || upperValue.contains("NOW(") ||
                upperValue.contains("UUID") || upperValue.contains("NULL")) {
            return defaultValue;
        }

        // 如果已经包含单引号，直接返回
        if (defaultValue.startsWith("'") && defaultValue.endsWith("'")) {
            return defaultValue;
        }

        // 其他类型：需要单引号，对单引号进行转义处理
        String escapedValue = defaultValue.replace("'", "''");
        return "'" + escapedValue + "'";
    }

}
