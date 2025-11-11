package com.cmsr.onebase.module.etl.build.service;

import com.cmsr.onebase.module.etl.common.entity.CatalogData;
import com.cmsr.onebase.module.etl.common.entity.ColumnData;
import com.cmsr.onebase.module.etl.common.entity.SchemaData;
import com.cmsr.onebase.module.etl.common.entity.TableData;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.datasource.DataSourceHolder;
import org.anyline.metadata.Catalog;
import org.anyline.metadata.Column;
import org.anyline.metadata.Schema;
import org.anyline.metadata.Table;
import org.anyline.proxy.ServiceProxy;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.*;

@Slf4j
@Service
public class MetadataCollector {


    public CatalogData collectCatalog(Long datasourceId, DataSource datasource) throws Exception {
        String runnerKey = "metadata-collect-" + datasourceId;
        try {
            DataSourceHolder.reg(runnerKey, datasource);
            CatalogData catalogData = new CatalogData();
            AnylineService<?> temporary = ServiceProxy.temporary(datasource);
            // 1. collect catalog
            Catalog catalog = temporary.metadata().catalog();
            String catalogName = catalog.getName();
            catalogData.setName(catalogName);
            //
            Schema schema = temporary.metadata().schema();
            String schemaName = schema.getName();
            SchemaData schemaData = new SchemaData();
            schemaData.setCatalogName(catalogName);
            schemaData.setName(schemaName);
            catalogData.getSchemas().add(schemaData);
            //
            LinkedHashMap<String, Table> tables = temporary.metadata().tables();
            for (Table<?> table : tables.values()) {
                TableData tableData = new TableData();
                tableData.setCatalogName(catalogName);
                tableData.setSchemaName(schemaName);
                tableData.setName(table.getName());
                Collection<Column> tableColumn = temporary.metadata().columns(table).values();
                List<ColumnData> columns = toColumnData(tableColumn);
                tableData.setColumns(columns);
            }
            return catalogData;
        } finally {
            unregisterDataSource(runnerKey);
        }
    }

    private void unregisterDataSource(String datasourceKey) {
        try {
            DataSourceHolder.destroy(datasourceKey);
        } catch (Exception ex) {
            log.error("注销数据源失败，数据源标识：{}", datasourceKey, ex);
        }
    }

    private List<ColumnData> toColumnData(Collection<Column> tableColumn) {
        List<ColumnData> columnList = new ArrayList<>(tableColumn.size());
        for (Column column : tableColumn) {
            ColumnData columnData = new ColumnData();
            int position = columnData.getPosition();
            String columnName = column.getName();
            columnData.setName(columnName);
            columnData.setDisplayName(columnName);
            String comment = columnData.getComment();
            columnData.setComment(comment);
            columnData.setDeclaration(comment);
            columnData.setType(column.getOriginType().toLowerCase());
            columnData.setPosition(position);
            columnData.setNullable(column.getNullable());
            if (column.ignoreLength() != 1) {
                columnData.setLength(column.getLength());
            }
            if (column.ignorePrecision() != 1) {
                columnData.setPrecision(column.getPrecision());
            }
            if (column.ignoreScale() != 1) {
                columnData.setScale(column.getScale());
            }
            columnData.setDefaultValue(column.getDefaultValue().toString());
            boolean isPrimaryKey = column.getPrimaryKey();
            columnData.setPrimaryKey(isPrimaryKey);
            if (isPrimaryKey) {
                columnData.setAutoIncrement(column.getAutoIncrement());
            }
        }
        Collections.sort(columnList, Comparator.comparingInt(ColumnData::getPosition));
        return columnList;
    }

}
