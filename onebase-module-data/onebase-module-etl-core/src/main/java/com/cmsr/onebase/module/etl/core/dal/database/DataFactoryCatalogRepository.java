package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.module.etl.core.dal.dataobject.DataFactoryCatalogDO;
import com.cmsr.onebase.framework.aynline.DataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class DataFactoryCatalogRepository extends DataRepository<DataFactoryCatalogDO> {

    public DataFactoryCatalogRepository() {
        super(DataFactoryCatalogDO.class);
    }


}
