package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlSchemaDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.EtlSchemaMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class EtlSchemaRepository extends BaseAppRepository<EtlSchemaMapper, EtlSchemaDO> {

    public EtlSchemaDO findByDatasourceAndCatalog(Long applicationId, String datasourceUuid, String catalogUuid) {
        QueryWrapper queryWrapper = query()
                .eq(EtlSchemaDO::getApplicationId, applicationId)
                .eq(EtlSchemaDO::getDatasourceUuid, datasourceUuid)
                .eq(EtlSchemaDO::getCatalogUuid, catalogUuid);
        return getOne(queryWrapper);
    }

    public void deleteAllByDatasource(String datasourceUuid) {
        QueryWrapper queryWrapper = query().eq(EtlSchemaDO::getDatasourceUuid, datasourceUuid);
        remove(queryWrapper);
    }
}
