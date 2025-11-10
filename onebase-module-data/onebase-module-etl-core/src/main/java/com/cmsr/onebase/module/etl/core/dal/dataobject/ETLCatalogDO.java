package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import org.anyline.metadata.Catalog;
import org.apache.commons.lang3.StringUtils;

@Data
@Table(name = "etl_catalog")
public class ETLCatalogDO extends TenantBaseDO {

    @Column(name = "application_id")
    private Long applicationId;

    @Column(name = "datasource_id")
    private Long datasourceId;

    @Column(name = "catalog_name")
    private String catalogName;

    @Column(name = "display_name")
    private String displayName;

    // currently no usage, ;.
    @Column(name = "meta_info")
    private String metaInfo;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "declaration")
    private String declaration;

    public static ETLCatalogDO convert(Long applicationId, Long datasourceId, Catalog catalog) {
        ETLCatalogDO catalogDO = new ETLCatalogDO();
        catalogDO.setApplicationId(applicationId);
        catalogDO.setDatasourceId(datasourceId);
        String name = catalog.getName();
        catalogDO.setCatalogName(name);
        catalogDO.setDisplayName(name);
        String comment = catalog.getComment();
        catalogDO.setRemarks(comment);
        catalogDO.setDeclaration(comment);

        return catalogDO;
    }

    public static void applyChanges(ETLCatalogDO oldDO, ETLCatalogDO newDO) {
        String oldCatalogName = oldDO.getCatalogName();
        String oldDisplayName = oldDO.getDisplayName();
        String oldRemarks = oldDO.getRemarks();
        String oldDeclaration = oldDO.getDeclaration();
        // 采集中，保留用户自定义的别名及备注
        if (!StringUtils.equals(oldDisplayName, oldCatalogName)) {
            newDO.setDisplayName(oldDisplayName);
        }
        if (StringUtils.isNotBlank(oldDeclaration) && !StringUtils.equals(oldDeclaration, oldRemarks)) {
            newDO.setDeclaration(oldDeclaration);
        }
        newDO.setId(oldDO.getId());
    }
}
