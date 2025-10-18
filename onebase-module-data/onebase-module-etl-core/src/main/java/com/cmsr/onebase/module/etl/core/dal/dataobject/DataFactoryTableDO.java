package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.sub.MetaTable;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

@Table(name = "datafactory_table")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DataFactoryTableDO extends TenantBaseDO {

    public DataFactoryTableDO setId(Long id) {
        super.setId(id);
        return this;
    }

    private Long datasourceId;

    private Long catalogId;

    private Long schemaId;

    private String fqnHash;

    private String tableName;

    private String displayName;

    private MetaTable metaInfo;

    public static DataFactoryTableDO convert(Long datasourceId, Long catalogId, Long schemaId, org.anyline.metadata.Table table) {
        DataFactoryTableDO tableDO = new DataFactoryTableDO();
        tableDO.setDatasourceId(datasourceId);
        tableDO.setCatalogId(catalogId);
        tableDO.setSchemaId(schemaId);
        String name = table.getName();
        tableDO.setTableName(name);
        String comment = table.getComment();
        if (StringUtils.isNotBlank(comment)) {
            tableDO.setDisplayName(comment);
        } else {
            tableDO.setDisplayName(name);
        }
        MetaTable metaCatalog = MetaTable.convert(table);
        tableDO.setMetaInfo(metaCatalog);

        return tableDO;
    }

    public static void applyChanges(DataFactoryTableDO oldTableDO, DataFactoryTableDO newTableDO) {
        MetaTable oldMetaTable = oldTableDO.getMetaInfo();
        MetaTable newMetaTable = newTableDO.getMetaInfo();
        MetaTable.applyChanges(oldMetaTable, newMetaTable);
        String oldName = oldTableDO.getTableName();
        String oldDisplayName = oldTableDO.getDisplayName();
        String oldComment = oldMetaTable.getComment();
        if (!StringUtils.equals(oldDisplayName, oldName) && !StringUtils.equals(oldDisplayName, oldComment)) {
            newTableDO.setDisplayName(oldDisplayName);
        }
        newTableDO.setId(oldTableDO.getId());
    }

}
