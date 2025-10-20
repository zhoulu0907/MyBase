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

@Table(name = "datafactory_schema")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DataFactorySchemaDO extends TenantBaseDO {

    public DataFactorySchemaDO setId(Long id) {
        super.setId(id);
        return this;
    }

    @Column(name = "datasource_id")
    private Long datasourceId;

    @Column(name = "catalog_id")
    private Long catalogId;

    @Column(name = "fqn_hash")
    private String fqnHash;

    @Column(name = "schema_name")
    private String schemaName;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "meta_info")
    private String metaInfo;

    public MetaSchema getMetaInfo() {
        return JsonUtils.parseObject(metaInfo, MetaSchema.class);
    }

    public void setMetaInfo(MetaSchema metaInfo) {
        this.metaInfo = JsonUtils.toJsonString(metaInfo);
    }

    public static DataFactorySchemaDO convert(Long datasourceId, Long catalogId, Schema schema) {
        DataFactorySchemaDO schemaDO = new DataFactorySchemaDO();
        schemaDO.setDatasourceId(datasourceId);
        schemaDO.setCatalogId(catalogId);
        String name = schema.getName();
        schemaDO.setSchemaName(name);
        String comment = schema.getComment();
        if (StringUtils.isNotBlank(comment)) {
            schemaDO.setDisplayName(comment);
        } else {
            schemaDO.setDisplayName(name);
        }
        MetaSchema metaCatalog = MetaSchema.convert(schema);
        schemaDO.setMetaInfo(metaCatalog);

        return schemaDO;
    }

    public static void applyChanges(DataFactorySchemaDO oldSchemaDO, DataFactorySchemaDO newSchemaDO) {
        String oldName = oldSchemaDO.getSchemaName();
        String oldDisplayName = oldSchemaDO.getDisplayName();
        String oldComment = oldSchemaDO.getMetaInfo().getComment();
        if (!StringUtils.equals(oldDisplayName, oldName) && !StringUtils.equals(oldDisplayName, oldComment)) {
            newSchemaDO.setDisplayName(oldDisplayName);
        }
        newSchemaDO.setId(oldSchemaDO.getId());
    }
}
