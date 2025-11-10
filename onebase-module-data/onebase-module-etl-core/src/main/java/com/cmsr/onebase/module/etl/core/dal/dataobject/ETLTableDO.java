package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.cmsr.onebase.module.etl.common.meta.ColumnMeta;
import com.cmsr.onebase.module.etl.common.meta.TableMeta;
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

    public TableMeta getMetaInfo() {
        return JsonUtils.parseObject(this.metaInfo, TableMeta.class);
    }

    public void setMetaInfo(TableMeta metaInfo) {
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
        TableMeta tableMeta = new TableMeta();
        tableMeta.setCatalog(table.getCatalogName());
        tableMeta.setSchema(table.getSchemaName());
        tableMeta.setName(tableName);
        List<ColumnMeta> columns = parseColumnMeta(tableColumns);
        tableMeta.setColumns(columns);

        return tableDO;
    }

    private static List<ColumnMeta> parseColumnMeta(Collection<org.anyline.metadata.Column> tableColumns) {
        List<ColumnMeta> columnList = new ArrayList<>(tableColumns.size());
        for (org.anyline.metadata.Column columnDef : tableColumns) {
            ColumnMeta columnMeta = new ColumnMeta();
            int position = columnMeta.getPosition();
            String columnName = columnDef.getName();
            columnMeta.setName(columnName);
            columnMeta.setDisplayName(columnName);
            String comment = columnMeta.getComment();
            columnMeta.setComment(comment);
            columnMeta.setDeclaration(comment);
            columnMeta.setType(columnDef.getOriginType().toLowerCase());
            columnMeta.setPosition(position);
            columnMeta.setNullable(columnDef.getNullable());
            if (columnDef.ignoreLength() != 1) {
                columnMeta.setLength(columnDef.getLength());
            }
            if (columnDef.ignorePrecision() != 1) {
                columnMeta.setPrecision(columnDef.getPrecision());
            }
            if (columnDef.ignoreScale() != 1) {
                columnMeta.setScale(columnDef.getScale());
            }
            columnMeta.setDefaultValue(columnDef.getDefaultValue().toString());
            boolean isPrimaryKey = columnDef.getPrimaryKey();
            columnMeta.setPrimaryKey(isPrimaryKey);
            if (isPrimaryKey) {
                columnMeta.setAutoIncrement(columnDef.getAutoIncrement());
            }

            columnList.add(position - 1, columnMeta);
        }
        return columnList;
    }
}
