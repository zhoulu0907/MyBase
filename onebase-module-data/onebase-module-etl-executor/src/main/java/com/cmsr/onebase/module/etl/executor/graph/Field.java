package com.cmsr.onebase.module.etl.executor.graph;

import com.cmsr.onebase.module.etl.executor.provider.dao.EtlColumn;
import lombok.Data;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.types.DataType;

@Data
public class Field {

    private String fieldId;

    private String fieldName;

    private String fieldType;

    private Integer length;

    private Integer precision;

    private Integer scale;

    public static Field of(EtlColumn etlColumn) {
        Field field = new Field();
        return Field.of(field, etlColumn);
    }

    public static Field of(Field field, EtlColumn etlColumn) {
        field.setFieldId(etlColumn.getId());
        field.setFieldName(etlColumn.getName());
        field.setFieldType(etlColumn.getFlinkType());
        int ignoreLength = etlColumn.getIgnoreLength();
        if (ignoreLength == 0) {
            field.setLength(etlColumn.getLength());
        }
        int ignorePrecision = etlColumn.getIgnorePrecision();
        if (ignorePrecision == 0) {
            field.setPrecision(etlColumn.getPrecision());
        }
        int ignoreScale = etlColumn.getIgnoreScale();
        if (ignoreScale == 0) {
            field.setScale(etlColumn.getScale());
        }
        return field;
    }

    /**
     * 根据类型字符串获取对应的Flink DataType
     *
     * @return Flink DataType对象
     */
    public DataType toFlinkTableType() {
        if (fieldType == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        switch (fieldType.toUpperCase()) {
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
                throw new IllegalArgumentException("Unsupported Flink data type: " + fieldType);
        }
    }


}
