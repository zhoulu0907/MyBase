package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.sub.MetaSchema;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.anyline.metadata.Schema;
import org.apache.commons.lang3.StringUtils;

@Data
@Table(name = "etl_schema")
public class ETLSchemaDO extends TenantBaseDO {

    @Column(name = "application_id")
    private Long applicationId;

    @Column(name = "datasource_id")
    private Long datasourceId;

    @Column(name = "catalog_id")
    private Long catalogId;

    @Column(name = "schema_name")
    private String schemaName;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "meta_info")
    private String metaInfo;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "declaration")
    private String declaration;

    public MetaSchema getMetaInfo() {
        return JsonUtils.parseObject(metaInfo, MetaSchema.class);
    }

    public void setMetaInfo(MetaSchema metaInfo) {
        this.metaInfo = JsonUtils.toJsonString(metaInfo);
    }

    public static ETLSchemaDO convert(Long applicationId, Long datasourceId, Long catalogId, Schema schema) {
        ETLSchemaDO schemaDO = new ETLSchemaDO();
        schemaDO.setApplicationId(applicationId);
        schemaDO.setDatasourceId(datasourceId);
        schemaDO.setCatalogId(catalogId);
        String name = schema.getName();
        schemaDO.setSchemaName(name);
        schemaDO.setDisplayName(name);
        String comment = schema.getComment();
        schemaDO.setRemarks(comment);
        schemaDO.setDeclaration(comment);
        MetaSchema metaInfo = MetaSchema.convert(schema);
        schemaDO.setMetaInfo(metaInfo);

        return schemaDO;
    }

    public static void applyChanges(ETLSchemaDO oldDO, ETLSchemaDO newDO) {
        String oldCatalogName = oldDO.getSchemaName();
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
