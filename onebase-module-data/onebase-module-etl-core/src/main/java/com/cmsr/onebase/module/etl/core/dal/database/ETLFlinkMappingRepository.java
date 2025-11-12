package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLFlinkMappingDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ETLFlinkMappingRepository extends DataRepository<ETLFlinkMappingDO> {
    public ETLFlinkMappingRepository() {
        super(ETLFlinkMappingDO.class);
    }

    public Map<String, String> findAllMappingsByDatasourceType(String datasourceType) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("datasource_type", datasourceType);

        List<ETLFlinkMappingDO> flinkMappingDOs = findAllByConfig(cs);
        return flinkMappingDOs.stream().collect(
                Collectors.toMap(
                        ETLFlinkMappingDO::getOriginType,
                        ETLFlinkMappingDO::getFlinkType
                )
        );
    }
}
