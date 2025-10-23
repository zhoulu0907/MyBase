package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.sub.MetaCatalog;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.anyline.metadata.Catalog;
import org.apache.commons.lang3.StringUtils;

@Table(name = "etl_catalog")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ETLCatalogDO extends TenantBaseDO {

    public ETLCatalogDO setId(Long id) {
        super.setId(id);
        return this;
    }

    @Column(name = "datasource_id")
    private Long datasourceId;

    @Column(name = "fqn_hash")
    private String fqnHash;

    @Column(name = "catalog_name")
    private String catalogName;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "meta_info")
    private String metaInfo;

    public MetaCatalog getMetaInfo() {
        return JsonUtils.parseObject(metaInfo, MetaCatalog.class);
    }

    public void setMetaInfo(MetaCatalog metaInfo) {
        this.metaInfo = JsonUtils.toJsonString(metaInfo);
    }

    public static ETLCatalogDO convert(Long datasourceId, Catalog catalog) {
        ETLCatalogDO catalogDO = new ETLCatalogDO();
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

    public static void applyChanges(ETLCatalogDO oldCatalogDO, ETLCatalogDO newCatalogDO) {
        String oldName = oldCatalogDO.getCatalogName();
        String oldDisplayName = oldCatalogDO.getDisplayName();
        String oldComment = oldCatalogDO.getMetaInfo().getComment();
        if (!StringUtils.equals(oldDisplayName, oldName) && !StringUtils.equals(oldDisplayName, oldComment)) {
            newCatalogDO.setDisplayName(oldDisplayName);
        }
        newCatalogDO.setId(oldCatalogDO.getId());
    }
}
