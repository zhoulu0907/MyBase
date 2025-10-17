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

    public DataFactoryCatalogDO findOneByFqnHash(String fqnHash) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("fqn_hash", fqnHash);
        return findOne(cs);
    }
}
