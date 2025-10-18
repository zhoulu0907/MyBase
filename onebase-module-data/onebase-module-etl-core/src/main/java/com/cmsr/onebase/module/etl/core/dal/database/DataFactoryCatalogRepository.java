package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.DataFactoryCatalogDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class DataFactoryCatalogRepository extends DataRepository<DataFactoryCatalogDO> {

    public DataFactoryCatalogRepository() {
        super(DataFactoryCatalogDO.class);
    }

    public DataFactoryCatalogDO findOneByNameAndDatasourceId(Long datasourceId, String catalogName) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("datasource_id", datasourceId);
        cs.eq("catalog_name", catalogName);

        return findOne(cs);
    }
}
