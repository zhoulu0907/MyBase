package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.sub.MetaTable;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

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

    public MetaTable getMetaInfo() {
        return JsonUtils.parseObject(metaInfo, MetaTable.class);
    }

    public void setMetaInfo(MetaTable metaInfo) {
        this.metaInfo = JsonUtils.toJsonString(metaInfo);
    }

    public static ETLTableDO convert(Long applicationId, Long datasourceId, Long catalogId, Long schemaId,
                                     org.anyline.metadata.Table table,
                                     Map<String, org.anyline.metadata.Column> columns) {
        ETLTableDO tableDO = new ETLTableDO();
        tableDO.setApplicationId(applicationId);
        tableDO.setDatasourceId(datasourceId);
        tableDO.setCatalogId(catalogId);
        tableDO.setSchemaId(schemaId);
        String name = table.getName();
        tableDO.setTableName(name);
        tableDO.setDisplayName(name);
        String comment = table.getComment();
        tableDO.setRemarks(comment);
        tableDO.setDeclaration(comment);
        String metaType = table.keyword().toLowerCase();
        tableDO.setTableType(metaType);
        MetaTable metaTable = MetaTable.convert(table, columns);
        tableDO.setMetaInfo(metaTable);

        return tableDO;
    }

    public static void applyChanges(ETLTableDO oldDO, ETLTableDO newDO) {
        MetaTable oldMetaTable = oldDO.getMetaInfo();
        MetaTable newMetaTable = newDO.getMetaInfo();
        MetaTable.applyChanges(oldMetaTable, newMetaTable);
        String oldTableName = oldDO.getTableName();
        String oldDisplayName = oldDO.getDisplayName();
        String oldRemarks = oldDO.getRemarks();
        String oldDeclaration = oldDO.getDeclaration();
        if (!StringUtils.equals(oldTableName, oldDisplayName)) {
            newDO.setDisplayName(oldDisplayName);
        }
        if (StringUtils.isNotBlank(oldDeclaration) && !StringUtils.equals(oldRemarks, oldDeclaration)) {
            newDO.setDeclaration(oldDeclaration);
        }
        newDO.setId(oldDO.getId());
    }

}
