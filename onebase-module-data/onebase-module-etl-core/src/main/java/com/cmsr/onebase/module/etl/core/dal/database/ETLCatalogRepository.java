package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLCatalogDO;
import com.cmsr.onebase.module.etl.core.enums.ETLErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ETLCatalogRepository extends DataRepository<ETLCatalogDO> {

    public ETLCatalogRepository() {
        super(ETLCatalogDO.class);
    }

    public ETLCatalogDO findOneByNameAndDatasourceId(Long datasourceId, String catalogName) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("datasource_id", datasourceId);
        cs.eq("catalog_name", catalogName);

        return findOne(cs);
    }

    public void deleteAllByDatasourceId(Long datasourceId) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("datasource_id", datasourceId);

        deleteByConfig(cs);
    }

    @Override
    public ETLCatalogDO upsert(ETLCatalogDO catalogDO) {
        if (catalogDO == null) return null;
        try {
            Long id = catalogDO.getId();
            if (id == null) {
                catalogDO = insert(catalogDO);
            } else {
                update(catalogDO);
            }
        } catch (Exception e) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.METADATA_COLLECT_FAILED);
        }
        return catalogDO;
    }
}
