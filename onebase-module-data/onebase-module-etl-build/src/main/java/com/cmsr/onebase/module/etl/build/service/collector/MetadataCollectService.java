package com.cmsr.onebase.module.etl.build.service.collector;

import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLCatalogDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLDatasourceDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLSchemaDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLTableDO;

import java.util.List;

public interface MetadataCollectService {

    ETLCatalogDO collectCatalog(ETLDatasourceDO datasourceDO);

    ETLSchemaDO collectSchema(ETLDatasourceDO datasourceDO, Long catalogId);

    List<ETLTableDO> collectTables(ETLDatasourceDO datasourceDO, Long catalogId, Long schemaId);

}
