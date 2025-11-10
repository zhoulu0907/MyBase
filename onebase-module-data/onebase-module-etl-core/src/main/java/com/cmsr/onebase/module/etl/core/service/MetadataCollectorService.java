package com.cmsr.onebase.module.etl.core.service;

import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLDatasourceDO;

public interface MetadataCollectorService {
    void submitCollectJob(ETLDatasourceDO datasourceDO);

    MetadataCollectResult doCollection(Long applicationId, Long datasourceId, String datasourceType);
}
