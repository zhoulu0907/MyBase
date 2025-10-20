package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.sub.MetaCatalog;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.anyline.metadata.Catalog;
import org.apache.commons.lang3.StringUtils;

@Table(name = "datafactory_catalog")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DataFactoryCatalogDO extends TenantBaseDO {

    public DataFactoryCatalogDO setId(Long id) {
        super.setId(id);
        return this;
    }

    private Long datasourceId;

    private String fqnHash;

    private String catalogName;

    private String displayName;

    private MetaCatalog metaInfo;

    public static DataFactoryCatalogDO convert(Long datasourceId, Catalog catalog) {
        DataFactoryCatalogDO catalogDO = new DataFactoryCatalogDO();
        catalogDO.setDatasourceId(datasourceId);
        String name = catalog.getName();
        catalogDO.setCatalogName(name);
        String comment = catalog.getComment();
        if (StringUtils.isNotBlank(comment)) {
            catalogDO.setDisplayName(comment);
        } else {
            catalogDO.setDisplayName(name);
        }
        MetaCatalog metaCatalog = MetaCatalog.convert(catalog);
        catalogDO.setMetaInfo(metaCatalog);

        return catalogDO;
    }

    public static void applyChanges(DataFactoryCatalogDO oldCatalogDO, DataFactoryCatalogDO newCatalogDO) {
        String oldName = oldCatalogDO.getCatalogName();
        String oldDisplayName = oldCatalogDO.getDisplayName();
        String oldComment = oldCatalogDO.getMetaInfo().getComment();
        if (!StringUtils.equals(oldDisplayName, oldName) && !StringUtils.equals(oldDisplayName, oldComment)) {
            newCatalogDO.setDisplayName(oldDisplayName);
        }
        newCatalogDO.setId(oldCatalogDO.getId());
    }
}
