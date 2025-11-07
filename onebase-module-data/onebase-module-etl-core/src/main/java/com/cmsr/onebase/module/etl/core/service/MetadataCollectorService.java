package com.cmsr.onebase.module.etl.core.service;

import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLDatasourceDO;

public interface MetadataCollectorService {

    boolean testConnection(ETLDatasourceDO datasourceDO);

    void submitCollectJob(ETLDatasourceDO datasourceDO);

    boolean doCollection(Long applicationId, Long datasourceId, String datasourceType);
}
