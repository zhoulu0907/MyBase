package com.cmsr.onebase.module.etl.core.service.collector;

import com.cmsr.onebase.module.etl.core.dal.dataobject.DataFactoryDatasourceDO;

public interface MetadataCollectorService {

    boolean testConnection(DataFactoryDatasourceDO datasourceDO);

    boolean doCollection(Long datasourceId);
}
