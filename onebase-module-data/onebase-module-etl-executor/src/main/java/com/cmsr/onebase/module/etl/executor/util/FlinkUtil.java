package com.cmsr.onebase.module.etl.executor.util;

import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.types.DataType;

/**
 * @Author：huangjie
 * @Date：2025/11/9 12:59
 */
public class FlinkUtil {


    /**
     * 根据类型字符串获取对应的Flink DataType
     *
     * @return Flink DataType对象
     */
    public static DataType toFlinkTableType(String type, Integer length, Integer precision, Integer scale) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        return switch (type.toUpperCase()) {
            case "CHAR" -> DataTypes.CHAR(length);
            case "VARCHAR" -> DataTypes.VARCHAR(length);
            case "STRING" -> DataTypes.STRING();
            case "BOOLEAN" -> DataTypes.BOOLEAN();
            case "BINARY" -> DataTypes.BINARY(length);
            case "VARBINARY" -> DataTypes.VARBINARY(length);
            case "BYTES" -> DataTypes.BYTES();
            case "DECIMAL" -> DataTypes.DECIMAL(precision, scale);
            case "TINYINT" -> DataTypes.TINYINT();
            case "SMALLINT" -> DataTypes.SMALLINT();
            case "INTEGER" -> DataTypes.INT();
            case "BIGINT" -> DataTypes.BIGINT();
            case "FLOAT" -> DataTypes.FLOAT();
            case "DOUBLE" -> DataTypes.DOUBLE();
            case "DATE" -> DataTypes.DATE();
            case "TIME" -> DataTypes.TIME(0);
            case "TIMESTAMP" -> DataTypes.TIMESTAMP(scale);
            case "TIMESTAMP_LTZ" -> DataTypes.TIMESTAMP_LTZ(scale);
            case "INTERVAL" -> DataTypes.INTERVAL(DataTypes.SECOND(3));
            case "ARRAY" -> DataTypes.ARRAY(DataTypes.STRING());
            case "MULTISET" -> DataTypes.MULTISET(DataTypes.STRING());
            case "MAP" -> DataTypes.MAP(DataTypes.STRING(), DataTypes.STRING());
            case "ROW" -> DataTypes.ROW();
            default -> throw new IllegalArgumentException("Unsupported Flink data type: " + type);
        };
    }
}
