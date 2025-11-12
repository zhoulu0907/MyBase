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
        switch (type.toUpperCase()) {
            case "CHAR":
                return DataTypes.CHAR(length);
            case "VARCHAR":
                return DataTypes.VARCHAR(length);
            case "STRING":
                return DataTypes.STRING();
            case "BOOLEAN":
                return DataTypes.BOOLEAN();
            case "BINARY":
                return DataTypes.BINARY(length);
            case "VARBINARY":
                return DataTypes.VARBINARY(length);
            case "BYTES":
                return DataTypes.BYTES();
            case "DECIMAL":
                return DataTypes.DECIMAL(precision, scale);
            case "TINYINT":
                return DataTypes.TINYINT();
            case "SMALLINT":
                return DataTypes.SMALLINT();
            case "INTEGER":
                return DataTypes.INT();
            case "BIGINT":
                return DataTypes.BIGINT();
            case "FLOAT":
                return DataTypes.FLOAT();
            case "DOUBLE":
                return DataTypes.DOUBLE();
            case "DATE":
                return DataTypes.DATE();
            case "TIME":
                return DataTypes.TIME(0);
            case "TIMESTAMP":
                return DataTypes.TIMESTAMP(scale);
            case "TIMESTAMP_LTZ":
                return DataTypes.TIMESTAMP_LTZ(scale);
            case "INTERVAL":
                return DataTypes.INTERVAL(DataTypes.SECOND(3));
            case "ARRAY":
                return DataTypes.ARRAY(DataTypes.STRING());
            case "MULTISET":
                return DataTypes.MULTISET(DataTypes.STRING());
            case "MAP":
                return DataTypes.MAP(DataTypes.STRING(), DataTypes.STRING());
            case "ROW":
                return DataTypes.ROW();
            default:
                throw new IllegalArgumentException("Unsupported Flink data type: " + type);
        }
    }
}
