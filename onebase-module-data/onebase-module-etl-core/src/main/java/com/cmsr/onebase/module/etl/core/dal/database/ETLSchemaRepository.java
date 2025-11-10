package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLSchemaDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLTableDO;
import com.cmsr.onebase.module.etl.core.enums.ETLErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ETLSchemaRepository extends DataRepository<ETLSchemaDO> {

    public ETLSchemaRepository() {
        super(ETLSchemaDO.class);
    }


    public ETLSchemaDO findOneByNameAndCatalogIdAndDatasourceId(Long datasourceId, Long catalogId, String name) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("datasource_id", datasourceId);
        cs.eq("catalog_id", catalogId);
        cs.eq("schema_name", name);

        return findOne(cs);
    }

    public void deleteAllByDatasourceId(Long datasourceId) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("datasource_id", datasourceId);

        deleteByConfig(cs);
    }

    @Override
    public ETLSchemaDO upsert(ETLSchemaDO schemaDO) {
        if (schemaDO == null) return null;
        try {
            Long id = schemaDO.getId();
            if (id == null) {
                schemaDO = insert(schemaDO);
            } else {
                update(schemaDO);
            }
        } catch (Exception e) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.METADATA_COLLECT_FAILED);
        }
        return schemaDO;
    }
}
