package com.cmsr.onebase.module.etl.build.vo.preview;

import com.cmsr.onebase.module.etl.common.entity.ColumnData;
import com.cmsr.onebase.module.etl.common.entity.TableData;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

import java.util.ArrayList;
import java.util.List;

public class PreviewTableDef extends TableDef {

    private List<QueryColumn> queryColumns = new ArrayList<>();

    public PreviewTableDef(String tableName) {
        super("", tableName);
    }

    public static PreviewTableDef of(TableData tableData) {
        PreviewTableDef tableDef = new PreviewTableDef(tableData.getName());
        tableData.getColumns().forEach(tableDef::addColumn);

        return tableDef;
    }

    private void addColumn(ColumnData columnData) {
        this.queryColumns.add(new QueryColumn(this, columnData.getName()));
    }
}
