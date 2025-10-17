package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.module.etl.core.dal.dataobject.DataFactorySchemaDO;
import com.cmsr.onebase.framework.aynline.DataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class DataFactorySchemaRepository extends DataRepository<DataFactorySchemaDO> {

    public DataFactorySchemaRepository() {
        super(DataFactorySchemaDO.class);
    }


}
