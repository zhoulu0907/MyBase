package com.cmsr.onebase.module.etl.core.dal.dataobject;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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

    private Long datasourceId;

    private String fqnHash;

    private String schemaName;

    private String displayName;

    private String metaInfo;

}
