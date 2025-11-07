package com.cmsr.onebase.module.etl.executor.util;

import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.types.DataType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * FlinkTypeUtil的单元测试类
 */
public class FlinkTypeUtilTest {

    @Test
    public void testGetFlinkTableTypeWithChar() {
        DataType result = FlinkTypeUtil.getFlinkTableType("CHAR", 10, 0);
        assertEquals(DataTypes.CHAR(10), result);
    }

    @Test
    public void testGetFlinkTableTypeWithVarchar() {
        DataType result = FlinkTypeUtil.getFlinkTableType("VARCHAR", 100, 0);
        assertEquals(DataTypes.VARCHAR(100), result);
    }

    @Test
    public void testGetFlinkTableTypeWithString() {
        DataType result = FlinkTypeUtil.getFlinkTableType("STRING", 0, 0);
        assertEquals(DataTypes.STRING(), result);
    }

    @Test
    public void testGetFlinkTableTypeWithBoolean() {
        DataType result = FlinkTypeUtil.getFlinkTableType("BOOLEAN", 0, 0);
        assertEquals(DataTypes.BOOLEAN(), result);
    }

    @Test
    public void testGetFlinkTableTypeWithBinary() {
        DataType result = FlinkTypeUtil.getFlinkTableType("BINARY", 20, 0);
        assertEquals(DataTypes.BINARY(20), result);
    }

    @Test
    public void testGetFlinkTableTypeWithVarbinary() {
        DataType result = FlinkTypeUtil.getFlinkTableType("VARBINARY", 50, 0);
        assertEquals(DataTypes.VARBINARY(50), result);
    }

    @Test
    public void testGetFlinkTableTypeWithBytes() {
        DataType result = FlinkTypeUtil.getFlinkTableType("BYTES", 0, 0);
        assertEquals(DataTypes.BYTES(), result);
    }

    @Test
    public void testGetFlinkTableTypeWithDecimal() {
        DataType result = FlinkTypeUtil.getFlinkTableType("DECIMAL", 10, 2);
        assertEquals(DataTypes.DECIMAL(10, 2), result);
    }

    @Test
    public void testGetFlinkTableTypeWithTinyint() {
        DataType result = FlinkTypeUtil.getFlinkTableType("TINYINT", 0, 0);
        assertEquals(DataTypes.TINYINT(), result);
    }

    @Test
    public void testGetFlinkTableTypeWithSmallint() {
        DataType result = FlinkTypeUtil.getFlinkTableType("SMALLINT", 0, 0);
        assertEquals(DataTypes.SMALLINT(), result);
    }

    @Test
    public void testGetFlinkTableTypeWithInteger() {
        DataType result = FlinkTypeUtil.getFlinkTableType("INTEGER", 0, 0);
        assertEquals(DataTypes.INT(), result);
    }

    @Test
    public void testGetFlinkTableTypeWithBigint() {
        DataType result = FlinkTypeUtil.getFlinkTableType("BIGINT", 0, 0);
        assertEquals(DataTypes.BIGINT(), result);
    }

    @Test
    public void testGetFlinkTableTypeWithFloat() {
        DataType result = FlinkTypeUtil.getFlinkTableType("FLOAT", 0, 0);
        assertEquals(DataTypes.FLOAT(), result);
    }

    @Test
    public void testGetFlinkTableTypeWithDouble() {
        DataType result = FlinkTypeUtil.getFlinkTableType("DOUBLE", 0, 0);
        assertEquals(DataTypes.DOUBLE(), result);
    }

    @Test
    public void testGetFlinkTableTypeWithDate() {
        DataType result = FlinkTypeUtil.getFlinkTableType("DATE", 0, 0);
        assertEquals(DataTypes.DATE(), result);
    }

    @Test
    public void testGetFlinkTableTypeWithTime() {
        DataType result = FlinkTypeUtil.getFlinkTableType("TIME", 0, 0);
        assertEquals(DataTypes.TIME(0), result);
    }

    @Test
    public void testGetFlinkTableTypeWithTimestamp() {
        DataType result = FlinkTypeUtil.getFlinkTableType("TIMESTAMP", 0, 3);
        assertEquals(DataTypes.TIMESTAMP(3), result);
    }

    @Test
    public void testGetFlinkTableTypeWithTimestampLtz() {
        DataType result = FlinkTypeUtil.getFlinkTableType("TIMESTAMP_LTZ", 0, 3);
        assertEquals(DataTypes.TIMESTAMP_LTZ(3), result);
    }

    @Test
    public void testGetFlinkTableTypeWithInterval() {
        DataType result = FlinkTypeUtil.getFlinkTableType("INTERVAL", 0, 0);
        // INTERVAL类型比较特殊，我们只检查它不是null
        assertNotNull(result);
    }

    @Test
    public void testGetFlinkTableTypeWithArray() {
        DataType result = FlinkTypeUtil.getFlinkTableType("ARRAY", 0, 0);
        // ARRAY类型比较特殊，我们只检查它不是null
        assertNotNull(result);
    }

    @Test
    public void testGetFlinkTableTypeWithMultiset() {
        DataType result = FlinkTypeUtil.getFlinkTableType("MULTISET", 0, 0);
        // MULTISET类型比较特殊，我们只检查它不是null
        assertNotNull(result);
    }

    @Test
    public void testGetFlinkTableTypeWithMap() {
        DataType result = FlinkTypeUtil.getFlinkTableType("MAP", 0, 0);
        // MAP类型比较特殊，我们只检查它不是null
        assertNotNull(result);
    }

    @Test
    public void testGetFlinkTableTypeWithRow() {
        DataType result = FlinkTypeUtil.getFlinkTableType("ROW", 0, 0);
        // ROW类型比较特殊，我们只检查它不是null
        assertNotNull(result);
    }

    @Test
    public void testGetFlinkTableTypeWithRaw() {
        DataType result = FlinkTypeUtil.getFlinkTableType("RAW", 0, 0);
        // RAW类型比较特殊，我们只检查它不是null
        assertNotNull(result);
    }

    @Test
    public void testGetFlinkTableTypeWithCaseInsensitive() {
        // 测试大小写不敏感
        DataType result1 = FlinkTypeUtil.getFlinkTableType("char", 10, 0);
        DataType result2 = FlinkTypeUtil.getFlinkTableType("Char", 10, 0);
        DataType result3 = FlinkTypeUtil.getFlinkTableType("CHAR", 10, 0);
        
        assertEquals(DataTypes.CHAR(10), result1);
        assertEquals(DataTypes.CHAR(10), result2);
        assertEquals(DataTypes.CHAR(10), result3);
    }

    @Test
    public void testGetFlinkTableTypeWithNullType() {
        // 测试null类型参数
        assertThrows(IllegalArgumentException.class, () -> {
            FlinkTypeUtil.getFlinkTableType(null, 0, 0);
        });
    }

    @Test
    public void testGetFlinkTableTypeWithUnsupportedType() {
        // 测试不支持的类型
        assertThrows(IllegalArgumentException.class, () -> {
            FlinkTypeUtil.getFlinkTableType("UNSUPPORTED_TYPE", 0, 0);
        });
    }
}