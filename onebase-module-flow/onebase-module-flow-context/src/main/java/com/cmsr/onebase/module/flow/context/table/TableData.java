package com.cmsr.onebase.module.flow.context.table;

import com.google.common.collect.ForwardingList;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/12/25 9:54
 */
@Data
public class TableData extends ForwardingList<RowData> {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 表头定义 - 列信息映射（列名 -> 列定义）
     */
    private Map<String, ColumnType> columns = new HashMap<>();

    /**
     * 表格数据 - 多行数据
     */
    private List<RowData> rows = new ArrayList<>();

    @Override
    protected List<RowData> delegate() {
        return rows;
    }

    public void addRowData(RowData rowData) {
        rows.add(rowData);
    }
}
