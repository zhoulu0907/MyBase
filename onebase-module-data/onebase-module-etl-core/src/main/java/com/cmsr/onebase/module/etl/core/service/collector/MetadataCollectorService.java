package com.cmsr.onebase.module.etl.core.service.collector;

import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLDatasourceDO;

public interface MetadataCollectorService {

    boolean testConnection(ETLDatasourceDO datasourceDO);

    void submitCollectJob(ETLDatasourceDO datasourceDO);
}
