package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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

    private String metaInfo;

}
