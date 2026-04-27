package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlCatalogDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.EtlCatalogMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class EtlCatalogRepository extends BaseAppRepository<EtlCatalogMapper, EtlCatalogDO> {

    public void deleteAllByDatasource(String datasourceUuid) {
        this.updateChain()
                .eq(EtlCatalogDO::getDatasourceUuid, datasourceUuid)
                .remove();
    }

    public EtlCatalogDO findCatalogByDatasource(Long applicationId, String datasourceUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(EtlCatalogDO::getApplicationId, applicationId)
                .eq(EtlCatalogDO::getDatasourceUuid, datasourceUuid);
        return getOne(queryWrapper);
    }
}
