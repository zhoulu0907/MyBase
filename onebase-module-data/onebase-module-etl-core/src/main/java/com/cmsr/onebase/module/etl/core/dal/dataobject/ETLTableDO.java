package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.cmsr.onebase.module.etl.common.entity.ColumnData;
import com.cmsr.onebase.module.etl.common.entity.TableData;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Table(name = "etl_table")
public class ETLTableDO extends TenantBaseDO {

    @Column(name = "application_id")
    private Long applicationId;

    @Column(name = "datasource_id")
    private Long datasourceId;

    @Column(name = "catalog_id")
    private Long catalogId;

    @Column(name = "schema_id")
    private Long schemaId;

    @Column(name = "table_type")
    private String tableType;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "meta_info")
    private String metaInfo;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "declaration")
    private String declaration;

    public TableData getMetaInfo() {
        return JsonUtils.parseObject(this.metaInfo, TableData.class);
    }

    public void setMetaInfo(TableData metaInfo) {
        this.metaInfo = JsonUtils.toJsonString(metaInfo);
    }

    public static ETLTableDO of(Long applicationId, Long datasourceId, Long catalogId, Long schemaId,
                                org.anyline.metadata.Table<?> table,
                                Collection<org.anyline.metadata.Column> tableColumns) {
        ETLTableDO tableDO = new ETLTableDO();
        tableDO.setApplicationId(applicationId);
        tableDO.setDatasourceId(datasourceId);
        tableDO.setCatalogId(catalogId);
        tableDO.setSchemaId(schemaId);
        String tableName = table.getName();
        tableDO.setTableName(tableName);
        tableDO.setDisplayName(tableName);
        tableDO.setTableType(table.keyword().toLowerCase());
        TableData tableData = new TableData();
        tableData.setCatalogName(table.getCatalogName());
        tableData.setSchemaName(table.getSchemaName());
        tableData.setName(tableName);
        List<ColumnData> columns = parseColumnMeta(tableColumns);
        tableData.setColumns(columns);

        return tableDO;
    }

    private static List<ColumnData> parseColumnMeta(Collection<org.anyline.metadata.Column> tableColumns) {
        List<ColumnData> columnList = new ArrayList<>(tableColumns.size());
        for (org.anyline.metadata.Column columnDef : tableColumns) {
            ColumnData columnData = new ColumnData();
            int position = columnData.getPosition();
            String columnName = columnDef.getName();
            columnData.setName(columnName);
            columnData.setDisplayName(columnName);
            String comment = columnData.getComment();
            columnData.setComment(comment);
            columnData.setDeclaration(comment);
            columnData.setType(columnDef.getOriginType().toLowerCase());
            columnData.setPosition(position);
            columnData.setNullable(columnDef.getNullable());
            if (columnDef.ignoreLength() != 1) {
                columnData.setLength(columnDef.getLength());
            }
            if (columnDef.ignorePrecision() != 1) {
                columnData.setPrecision(columnDef.getPrecision());
            }
            if (columnDef.ignoreScale() != 1) {
                columnData.setScale(columnDef.getScale());
            }
            columnData.setDefaultValue(columnDef.getDefaultValue().toString());
            boolean isPrimaryKey = columnDef.getPrimaryKey();
            columnData.setPrimaryKey(isPrimaryKey);
            if (isPrimaryKey) {
                columnData.setAutoIncrement(columnDef.getAutoIncrement());
            }

            columnList.add(position - 1, columnData);
        }
        return columnList;
    }
}
