package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.DataFactoryDatasourceDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class DataFactoryDatasourceRepository extends DataRepository<DataFactoryDatasourceDO> {
    public DataFactoryDatasourceRepository() {
        super(DataFactoryDatasourceDO.class);
    }

    public DataFactoryDatasourceDO findOneByDatasourceCode(String datasourceCode) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("datasource_code", datasourceCode);
        return findOne(cs);
    }

    public DataFactoryDatasourceDO findOneByDatasourceCodeAndIdNe(String datasourceCode, Long datasourceId) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("datasource_code", datasourceCode);
        cs.ne("id", datasourceId);
        return findOne(cs);
    }
}
