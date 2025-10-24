package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.sub.MetaTable;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Table(name = "etl_table")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ETLTableDO extends TenantBaseDO {

    public ETLTableDO setId(Long id) {
        super.setId(id);
        return this;
    }

    @Column(name = "datasource_id")
    private Long datasourceId;

    @Column(name = "catalog_id")
    private Long catalogId;

    @Column(name = "schema_id")
    private Long schemaId;

    @Column(name = "fqn_hash")
    private String fqnHash;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "description")
    private String description;

    @Column(name = "meta_info")
    private String metaInfo;

    @Column(name = "meta_type")
    private String metaType;

    public MetaTable getMetaInfo() {
        return JsonUtils.parseObject(metaInfo, MetaTable.class);
    }

    public void setMetaInfo(MetaTable metaInfo) {
        this.metaInfo = JsonUtils.toJsonString(metaInfo);
    }

    public static ETLTableDO convert(Long datasourceId, Long catalogId, Long schemaId,
                                     org.anyline.metadata.Table table,
                                     Map<String, org.anyline.metadata.Column> columns) {
        ETLTableDO tableDO = new ETLTableDO();
        tableDO.setDatasourceId(datasourceId);
        tableDO.setCatalogId(catalogId);
        tableDO.setSchemaId(schemaId);
        String name = table.getName();
        tableDO.setTableName(name);
        String comment = table.getComment();
        tableDO.setDescription(comment);
        if (StringUtils.isNotBlank(comment)) {
            tableDO.setDisplayName(comment);
        } else {
            tableDO.setDisplayName(name);
        }
        MetaTable metaTable = MetaTable.convert(table, columns);
        tableDO.setMetaInfo(metaTable);

        return tableDO;
    }

    public static void applyChanges(ETLTableDO oldTableDO, ETLTableDO newTableDO) {
        MetaTable oldMetaTable = oldTableDO.getMetaInfo();
        MetaTable newMetaTable = newTableDO.getMetaInfo();
        MetaTable.applyChanges(oldMetaTable, newMetaTable);
        String oldName = oldTableDO.getTableName();
        String oldDisplayName = oldTableDO.getDisplayName();
        String oldComment = oldTableDO.getDescription();
        if (!StringUtils.equals(oldDisplayName, oldName) && !StringUtils.equals(oldDisplayName, oldComment)) {
            newTableDO.setDisplayName(oldDisplayName);
        }
        newTableDO.setId(oldTableDO.getId());
    }

}
