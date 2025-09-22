package com.cmsr.onebase.module.metadata.core.enums;

import lombok.Getter;

/**
 * 数据源类型枚举
 *
 * @author bty418
 * @date 2025-01-25
 */
@Getter
public enum DatasourceTypeEnum {

    MYSQL("MYSQL", "MySQL数据库", "支持MySQL 5.7及以上版本", 3306,
          "com.mysql.cj.jdbc.Driver",
          "jdbc:mysql://{host}:{port}/{database}?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai"),

    POSTGRESQL("POSTGRESQL", "PostgreSQL数据库", "支持PostgreSQL 9.6及以上版本", 5432,
               "org.postgresql.Driver",
               "jdbc:postgresql://{host}:{port}/{database}"),

    ORACLE("ORACLE", "Oracle数据库", "支持Oracle 11g及以上版本", 1521,
           "oracle.jdbc.OracleDriver",
           "jdbc:oracle:thin:@{host}:{port}:{database}"),

    SQLSERVER("SQLSERVER", "SQL Server数据库", "支持SQL Server 2012及以上版本", 1433,
              "com.microsoft.sqlserver.jdbc.SQLServerDriver",
              "jdbc:sqlserver://{host}:{port};DatabaseName={database}");

    /**
     * 数据源类型编码
     */
    private final String code;

    /**
     * 显示名称
     */
    private final String displayName;

    /**
     * 描述信息
     */
    private final String description;

    /**
     * 默认端口
     */
    private final Integer defaultPort;

    /**
     * JDBC驱动类
     */
    private final String jdbcDriverClass;

    /**
     * URL模板
     */
    private final String urlTemplate;

    /**
     * 构造函数
     *
     * @param code 数据源类型编码
     * @param displayName 显示名称
     * @param description 描述信息
     * @param defaultPort 默认端口
     * @param jdbcDriverClass JDBC驱动类
     * @param urlTemplate URL模板
     */
    DatasourceTypeEnum(String code, String displayName, String description, Integer defaultPort,
                       String jdbcDriverClass, String urlTemplate) {
        this.code = code;
        this.displayName = displayName;
        this.description = description;
        this.defaultPort = defaultPort;
        this.jdbcDriverClass = jdbcDriverClass;
        this.urlTemplate = urlTemplate;
    }

    /**
     * 根据编码获取枚举
     *
     * @param code 数据源类型编码
     * @return 数据源类型枚举
     */
    public static DatasourceTypeEnum getByCode(String code) {
        for (DatasourceTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
