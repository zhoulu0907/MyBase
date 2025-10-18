package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.DataFactoryTableDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class DataFactoryTableRepository extends DataRepository<DataFactoryTableDO> {

    public DataFactoryTableRepository() {
        super(DataFactoryTableDO.class);
    }

    public Map<String, DataFactoryTableDO> findAllByCatalogIdAndSchemaIdAndDatasourceId(Long datasourceId, Long catalogId, Long schemaId) {
        ConfigStore cs = new DefaultConfigStore();
        List<DataFactoryTableDO> tableList = findAllByConfig(cs);
        return tableList.stream()
                .collect(Collectors.toMap(DataFactoryTableDO::getTableName, table -> table));
    }

    public void deleteAllByDatasourceId(Long datasourceId) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("datasource_id", datasourceId);

        deleteByConfig(cs);
    }
}
