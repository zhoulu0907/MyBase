package com.cmsr.onebase.module.etl.core.dal.dataobject.metainfo;

import com.google.common.collect.Lists;
import lombok.Data;
import org.anyline.metadata.Column;
import org.anyline.metadata.Table;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class MetaTable {

    private String fullyQualifiedName;

    private String baseType;

    private List<MetaColumn> columns;

//    /**
//     * {@link MetadataChange}
//     */
//    private Integer status;

    public static MetaTable convert(Table table, Map<String, Column> columnList) {
        MetaTable metaTable = new MetaTable();
        metaTable.setFullyQualifiedName(String.join(".",
                table.getCatalogName(),
                table.getSchemaName(),
                table.getName()));
        metaTable.setBaseType(table.getType());

        List<MetaColumn> columns = Lists.newArrayList();
        columnList.forEach((key, column) -> columns.add(MetaColumn.convert(column)));
        metaTable.setColumns(columns);

        return metaTable;
    }

    public Map<String, MetaColumn> acquireColumnMap() {
        return this.getColumns()
                .stream()
                .collect(Collectors.toMap(MetaColumn::getFullyQualifiedName, column -> column));
    }

    public static void applyChanges(MetaTable oldMeta, MetaTable newMeta) {
        Map<String, MetaColumn> oldColumns = oldMeta.acquireColumnMap();
        newMeta.getColumns().stream()
                .filter(column -> oldColumns.containsKey(column.getFullyQualifiedName()))
                .forEach(column -> {
                    MetaColumn.applyChanges(oldColumns.get(column.getFullyQualifiedName()), column);
                });
    }
}
