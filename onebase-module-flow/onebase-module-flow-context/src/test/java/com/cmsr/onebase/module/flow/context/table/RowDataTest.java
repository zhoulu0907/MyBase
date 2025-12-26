package com.cmsr.onebase.module.flow.context.table;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RowData类的单元测试
 *
 * @Author：huangjie
 * @Date：2025/12/26
 */
class RowDataTest {

    private RowData rowData;

    @BeforeEach
    void setUp() {
        rowData = new RowData();
    }

    /**
     * 测试场景：没有子表字段，只有普通字段
     */
    @Test
    void testFlatRowData_NoSubTable() {
        // 准备测试数据
        rowData.addValue("name", ColumnType.SIMPLE, "张三");
        rowData.addValue("age", ColumnType.SIMPLE, 25);
        rowData.addValue("email", ColumnType.SIMPLE, "zhangsan@example.com");

        // 执行测试
        List<Map<String, Object>> result = rowData.flatRowData();

        // 验证结果
        assertEquals(1, result.size());
        Map<String, Object> row = result.get(0);
        assertEquals("张三", row.get("name"));
        assertEquals(25, row.get("age"));
        assertEquals("zhangsan@example.com", row.get("email"));
        assertEquals(3, row.size());
    }

    /**
     * 测试场景：有一个子表字段，子表为空
     */
    @Test
    void testFlatRowData_EmptySubTable() {
        // 准备测试数据
        rowData.addValue("name", ColumnType.SIMPLE, "张三");
        rowData.addValue("age", ColumnType.SIMPLE, 25);
        
        // 创建空的子表
        TableData emptyOrdersTable = new TableData();
        emptyOrdersTable.setTableName("orders");
        rowData.addValue("orders", ColumnType.SUBTABLE, emptyOrdersTable);

        // 执行测试
        List<Map<String, Object>> result = rowData.flatRowData();

        // 验证结果
        assertEquals(1, result.size());
        Map<String, Object> row = result.get(0);
        assertEquals("张三", row.get("name"));
        assertEquals(25, row.get("age"));
        assertNull(row.get("orders"));
        assertEquals(3, row.size());
    }

    /**
     * 测试场景：有一个子表字段，子表有数据
     */
    @Test
    void testFlatRowData_SingleSubTableWithData() {
        // 准备测试数据
        rowData.addValue("name", ColumnType.SIMPLE, "张三");
        rowData.addValue("age", ColumnType.SIMPLE, 25);
        
        // 创建有数据的子表
        TableData ordersTable = new TableData();
        ordersTable.setTableName("orders");
        
        // 子表第一行
        RowData order1 = new RowData();
        order1.addValue("orderId", ColumnType.SIMPLE, "ORD001");
        order1.addValue("amount", ColumnType.SIMPLE, 100.50);
        ordersTable.addRowData(order1);
        
        // 子表第二行
        RowData order2 = new RowData();
        order2.addValue("orderId", ColumnType.SIMPLE, "ORD002");
        order2.addValue("amount", ColumnType.SIMPLE, 200.75);
        ordersTable.addRowData(order2);
        
        rowData.addValue("orders", ColumnType.SUBTABLE, ordersTable);

        // 执行测试
        List<Map<String, Object>> result = rowData.flatRowData();

        // 验证结果
        assertEquals(2, result.size());
        
        // 验证第一行
        Map<String, Object> row1 = result.get(0);
        assertEquals("张三", row1.get("name"));
        assertEquals(25, row1.get("age"));
        assertEquals("ORD001", row1.get("orders.orderId"));
        assertEquals(100.50, row1.get("orders.amount"));
        
        // 验证第二行
        Map<String, Object> row2 = result.get(1);
        assertEquals("张三", row2.get("name"));
        assertEquals(25, row2.get("age"));
        assertEquals("ORD002", row2.get("orders.orderId"));
        assertEquals(200.75, row2.get("orders.amount"));
    }

    /**
     * 测试场景：有多个子表字段，每个都有数据
     */
    @Test
    void testFlatRowData_MultipleSubTables() {
        // 准备测试数据
        rowData.addValue("name", ColumnType.SIMPLE, "张三");
        rowData.addValue("age", ColumnType.SIMPLE, 25);
        
        // 创建第一个子表（订单）
        TableData ordersTable = new TableData();
        ordersTable.setTableName("orders");
        
        RowData order1 = new RowData();
        order1.addValue("orderId", ColumnType.SIMPLE, "ORD001");
        order1.addValue("amount", ColumnType.SIMPLE, 100.50);
        ordersTable.addRowData(order1);
        
        RowData order2 = new RowData();
        order2.addValue("orderId", ColumnType.SIMPLE, "ORD002");
        order2.addValue("amount", ColumnType.SIMPLE, 200.75);
        ordersTable.addRowData(order2);
        
        rowData.addValue("orders", ColumnType.SUBTABLE, ordersTable);
        
        // 创建第二个子表（地址）
        TableData addressesTable = new TableData();
        addressesTable.setTableName("addresses");
        
        RowData addr1 = new RowData();
        addr1.addValue("addressId", ColumnType.SIMPLE, "ADDR001");
        addr1.addValue("street", ColumnType.SIMPLE, "中山路123号");
        addressesTable.addRowData(addr1);
        
        RowData addr2 = new RowData();
        addr2.addValue("addressId", ColumnType.SIMPLE, "ADDR002");
        addr2.addValue("street", ColumnType.SIMPLE, "解放路456号");
        addressesTable.addRowData(addr2);
        
        rowData.addValue("addresses", ColumnType.SUBTABLE, addressesTable);

        // 执行测试
        List<Map<String, Object>> result = rowData.flatRowData();

        // 验证结果 - 应该产生笛卡尔积（2个订单 × 2个地址 = 4行）
        assertEquals(4, result.size());
        
        // 验证所有行都包含主表字段
        for (Map<String, Object> row : result) {
            assertEquals("张三", row.get("name"));
            assertEquals(25, row.get("age"));
        }
        
        // 验证笛卡尔积结果
        Set<String> orderIds = new HashSet<>();
        Set<String> streets = new HashSet<>();
        for (Map<String, Object> row : result) {
            orderIds.add((String) row.get("orders.orderId"));
            streets.add((String) row.get("addresses.street"));
        }
        
        assertEquals(2, orderIds.size());
        assertEquals(2, streets.size());
        assertTrue(orderIds.contains("ORD001"));
        assertTrue(orderIds.contains("ORD002"));
        assertTrue(streets.contains("中山路123号"));
        assertTrue(streets.contains("解放路456号"));
    }

    /**
     * 测试场景：有多个子表字段，其中一个为空
     */
    @Test
    void testFlatRowData_MultipleSubTables_OneEmpty() {
        // 准备测试数据
        rowData.addValue("name", ColumnType.SIMPLE, "张三");
        rowData.addValue("age", ColumnType.SIMPLE, 25);
        
        // 创建第一个子表（订单）
        TableData ordersTable = new TableData();
        ordersTable.setTableName("orders");
        
        RowData order1 = new RowData();
        order1.addValue("orderId", ColumnType.SIMPLE, "ORD001");
        order1.addValue("amount", ColumnType.SIMPLE, 100.50);
        ordersTable.addRowData(order1);
        
        rowData.addValue("orders", ColumnType.SUBTABLE, ordersTable);
        
        // 创建空的第二个子表（地址）
        TableData emptyAddressesTable = new TableData();
        emptyAddressesTable.setTableName("addresses");
        rowData.addValue("addresses", ColumnType.SUBTABLE, emptyAddressesTable);

        // 执行测试
        List<Map<String, Object>> result = rowData.flatRowData();

        // 验证结果 - 只有一个订单行，空地址表生成null值
        assertEquals(1, result.size());
        
        Map<String, Object> row = result.get(0);
        assertEquals("张三", row.get("name"));
        assertEquals(25, row.get("age"));
        assertEquals("ORD001", row.get("orders.orderId"));
        assertEquals(100.50, row.get("orders.amount"));
        assertNull(row.get("addresses"));
    }

    /**
     * 测试场景：子表字段值不是TableData类型，应该抛出IllegalArgumentException
     */
    @Test
    void testFlatRowData_SubTableFieldWithInvalidType() {
        // 准备测试数据
        rowData.addValue("name", ColumnType.SIMPLE, "张三");
        rowData.addValue("age", ColumnType.SIMPLE, 25);
        
        // 故意添加错误类型的子表字段
        rowData.addValue("invalidSubTable", ColumnType.SUBTABLE, "not a table data");

        // 执行测试并验证异常抛出
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            rowData.flatRowData();
        });

        // 验证异常信息
        String expectedMessage = "子表字段 'invalidSubTable' 的数据类型错误，期望为TableData类型，但实际为: java.lang.String";
        assertEquals(expectedMessage, exception.getMessage());
    }

    /**
     * 测试场景：子表字段值为null，应该抛出IllegalArgumentException
     */
    @Test
    void testFlatRowData_SubTableFieldWithNullValue() {
        // 准备测试数据
        rowData.addValue("name", ColumnType.SIMPLE, "张三");
        rowData.addValue("age", ColumnType.SIMPLE, 25);
        
        // 故意添加null值的子表字段
        rowData.addValue("nullSubTable", ColumnType.SUBTABLE, null);

        // 执行测试并验证异常抛出
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            rowData.flatRowData();
        });

        // 验证异常信息
        String expectedMessage = "子表字段 'nullSubTable' 的数据类型错误，期望为TableData类型，但实际为: null";
        assertEquals(expectedMessage, exception.getMessage());
    }

    /**
     * 测试场景：子表字段值是其他对象类型，应该抛出IllegalArgumentException
     */
    @Test
    void testFlatRowData_SubTableFieldWithObjectValue() {
        // 准备测试数据
        rowData.addValue("name", ColumnType.SIMPLE, "张三");
        rowData.addValue("age", ColumnType.SIMPLE, 25);
        
        // 故意添加其他对象类型的子表字段
        rowData.addValue("objectSubTable", ColumnType.SUBTABLE, new ArrayList<String>());

        // 执行测试并验证异常抛出
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            rowData.flatRowData();
        });

        // 验证异常信息包含正确的类型信息
        String message = exception.getMessage();
        assertTrue(message.contains("子表字段 'objectSubTable' 的数据类型错误"));
        assertTrue(message.contains("期望为TableData类型"));
        assertTrue(message.contains("java.util.ArrayList"));
    }

    /**
     * 测试场景：大量数据性能测试
     */
    @Test
    void testFlatRowData_PerformanceWithLargeData() {
        // 准备大量数据
        rowData.addValue("userId", ColumnType.SIMPLE, 12345);
        rowData.addValue("username", ColumnType.SIMPLE, "testuser");
        
        // 创建大量子表数据
        TableData largeTable = new TableData();
        largeTable.setTableName("largeTable");
        
        // 创建1000行子表数据
        for (int i = 0; i < 1000; i++) {
            RowData row = new RowData();
            row.addValue("recordId", ColumnType.SIMPLE, i);
            row.addValue("data", ColumnType.SIMPLE, "data_" + i);
            largeTable.addRowData(row);
        }
        
        rowData.addValue("largeTable", ColumnType.SUBTABLE, largeTable);

        // 执行测试并验证性能
        long startTime = System.currentTimeMillis();
        List<Map<String, Object>> result = rowData.flatRowData();
        long endTime = System.currentTimeMillis();

        // 验证结果
        assertEquals(1000, result.size());
        
        // 验证数据正确性
        for (int i = 0; i < result.size(); i++) {
            Map<String, Object> row = result.get(i);
            assertEquals(12345, row.get("userId"));
            assertEquals("testuser", row.get("username"));
            assertEquals(i, row.get("largeTable.recordId"));
            assertEquals("data_" + i, row.get("largeTable.data"));
        }
        
        // 性能检查 - 应该在合理时间内完成（这里设置为5秒）
        assertTrue(endTime - startTime < 5000, "处理大量数据耗时过长: " + (endTime - startTime) + "ms");
    }

    /**
     * 测试场景：边界情况 - 空值处理
     */
    @Test
    void testFlatRowData_NullValues() {
        // 准备包含null值的数据
        rowData.addValue("name", ColumnType.SIMPLE, null);
        rowData.addValue("age", ColumnType.SIMPLE, null);
        
        TableData emptyTable = new TableData();
        emptyTable.setTableName("orders");
        rowData.addValue("orders", ColumnType.SUBTABLE, emptyTable);

        // 执行测试
        List<Map<String, Object>> result = rowData.flatRowData();

        // 验证结果
        assertEquals(1, result.size());
        Map<String, Object> row = result.get(0);
        assertNull(row.get("name"));
        assertNull(row.get("age"));
        assertNull(row.get("orders"));
    }

    /**
     * 测试hasSubTable方法
     */
    @Test
    void testHasSubTable() {
        // 初始状态 - 没有子表
        assertFalse(rowData.hasSubTable());
        
        // 添加普通字段 - 仍然没有子表
        rowData.addValue("name", ColumnType.SIMPLE, "张三");
        assertFalse(rowData.hasSubTable());
        
        // 添加子表字段 - 现在有子表
        TableData table = new TableData();
        rowData.addValue("orders", ColumnType.SUBTABLE, table);
        assertTrue(rowData.hasSubTable());
    }
}