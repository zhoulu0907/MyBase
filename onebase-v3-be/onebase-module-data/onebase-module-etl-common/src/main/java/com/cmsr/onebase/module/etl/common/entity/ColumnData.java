package com.cmsr.onebase.module.etl.common.entity;

import lombok.Data;

@Data
public class ColumnData {

    /**
     * 列名
     */
    private String name;

    /**
     * 列注释
     */
    private String comment;

    /**
     * 展示名称
     */
    private String displayName;

    /**
     * 原始数据库类型名称
     */
    private String type;

    /**
     * 列位置
     */
    private Integer position;

    /**
     * 是否允许为空
     */
    private Boolean nullable;

    /**
     * 字段长度
     * 适用于字符类型和二进制类型，主要用于限制数据存储的最大长度
     * <p>
     * 字符类型：
     * - VARCHAR(n)：MySQL 中表示最大字符数，n 范围 0-65535（实际受编码影响）；PostgreSQL 中 n 可选，默认为无限长
     * - CHAR(n)/CHARACTER(n)：两种数据库中均表示固定字符数，MySQL 中 n 范围 0-255，PostgreSQL 中不足补空格
     * - 其他：PostgreSQL 的 BPCHAR（相当于 CHAR(n)）、NAME（内部使用，通常为 64 字节）；MySQL 的 TEXT 系列类型（表示最大字符数）
     * <p>
     * 二进制类型：
     * - MySQL 的 BINARY(n)（固定字节数，n 范围 0-255）、VARBINARY(n)（最大字节数，n 范围 0-65535）、BLOB 系列类型（表示最大存储字节数）
     * - PostgreSQL 的 BYTEA（二进制数据类型，length 表示最大字节数）
     * <p>
     * 注意：PostgreSQL 的 TEXT、JSON、JSONB 等类型不使用 length
     */
    private Integer length;

    /**
     * 数字精度（总位数）
     * 主要用于数值类型，表示数字的精度或显示特性
     * <p>
     * 精确数值类型：
     * - DECIMAL(precision, scale)/NUMERIC(precision, scale)：两种数据库中 precision 均表示数字总位数
     * - MySQL 中 precision 范围 1-65
     * - PostgreSQL 中 precision 范围 1-1000
     * <p>
     * 浮点数值类型：
     * - MySQL 中 FLOAT(precision)：precision 表示精度，范围 0-24 表示单精度，25-53 表示双精度
     * - MySQL 中 DOUBLE(precision)：precision 表示十进制精度，范围 0-53
     * - PostgreSQL 中 REAL/FLOAT4、DOUBLE PRECISION/FLOAT8：precision 无实际意义（分别约为 6 位和 15 位有效数字）
     * <p>
     * 整数类型：
     * - MySQL 中 INTEGER/INT、BIGINT、SMALLINT、TINYINT、MEDIUMINT：precision 仅用于显示宽度，不影响存储范围
     * - PostgreSQL 中 INTEGER、BIGINT、SMALLINT：precision 无实际意义（固定长度）
     */
    private Integer precision;

    /**
     * 小数位数
     * 主要用于表示数值的小数部分位数，以及日期时间类型的秒小数精度
     * <p>
     * 精确数值类型：
     * - DECIMAL(precision, scale)/NUMERIC(precision, scale)：
     * - MySQL 中 scale 表示小数位数，范围 0-30
     * - PostgreSQL 中 scale 表示小数位数，范围 0-precision
     * <p>
     * 浮点数值类型：
     * - MySQL 中 FLOAT(precision, scale)、DOUBLE(precision, scale)：scale 表示小数位数，范围 0-30
     * - PostgreSQL 中 REAL/FLOAT4、DOUBLE PRECISION/FLOAT8：scale 无实际意义（浮点数）
     * <p>
     * 整数类型：
     * - 两种数据库中的 INTEGER 系列类型：scale 均为 0（整数）
     * <p>
     * 日期时间类型：
     * - 两种数据库中的 TIME、TIMESTAMP：scale 均表示秒的小数部分位数，范围 0-6
     * - MySQL 中 DATE、DATETIME：scale 表示秒的小数部分位数，范围 0-6
     * <p>
     * 特殊类型：
     * - PostgreSQL 中的 MONEY：scale 通常为 2（货币类型，精确到分）
     */
    private Integer scale;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 是否自增
     */
    private Boolean autoIncrement;

    /**
     * 是否为主键
     */
    private Boolean primaryKey;


    /**
     * 用户自定义描述
     */
    private String declaration;
}
