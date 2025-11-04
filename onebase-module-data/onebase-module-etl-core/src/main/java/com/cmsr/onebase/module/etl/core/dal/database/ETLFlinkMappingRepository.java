package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLFlinkMappingDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ETLFlinkMappingRepository extends DataRepository<ETLFlinkMappingDO> {
    public ETLFlinkMappingRepository() {
        super(ETLFlinkMappingDO.class);
    }

    // TODO: cachable method
    public String findFlinkTypeByDatasourceTypeAndOriginType(String databaseType, String originType) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("database_type", databaseType);
        cs.eq("origin_type", originType);

        ETLFlinkMappingDO mapping = findOne(cs);
        if (mapping == null) { // by default, using string to capture this type
            log.error("Unable to find compatible field mapping in Flink, database type: {}, origin field type: {}", databaseType, originType);
            return "string";
        }
        return mapping.getFlinkType();
    }
}
