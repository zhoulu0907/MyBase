package com.cmsr.onebase.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Table(name = "metadata_catalog")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataCatalogDO extends TenantBaseDO {

    public MetadataCatalogDO setId(Long id) {
        super.setId(id);
        return this;
    }

    private Long datasourceId;

    private String fqnHash;

    private String catalogName;

    private String displayName;

    private String metaInfo;

}
