package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLSchemaDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.ETLSchemaMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ETLSchemaRepository extends BaseAppRepository<ETLSchemaMapper, ETLSchemaDO> {

    public ETLSchemaDO findByDatasourceAndCatalog(Long applicationId, String datasourceUuid, String catalogUuid) {
        QueryWrapper queryWrapper = query()
                .eq(ETLSchemaDO::getApplicationId, applicationId)
                .eq(ETLSchemaDO::getDatasourceUuid, datasourceUuid)
                .eq(ETLSchemaDO::getCatalogUuid, catalogUuid);
        return getOne(queryWrapper);
    }

    public void deleteAllByDatasource(String datasourceUuid) {
        QueryWrapper queryWrapper = query().eq(ETLSchemaDO::getDatasourceUuid, datasourceUuid);
        remove(queryWrapper);
    }
}
