package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLCatalogDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.ETLCatalogMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ETLCatalogRepository extends BaseAppRepository<ETLCatalogMapper, ETLCatalogDO> {

    public void deleteAllByDatasource(String datasourceUuid) {
        this.updateChain()
                .eq(ETLCatalogDO::getDatasourceUuid, datasourceUuid)
                .remove();
    }

    public ETLCatalogDO findCatalogByDatasource(Long applicationId, String datasourceUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(ETLCatalogDO::getApplicationId, applicationId)
                .eq(ETLCatalogDO::getDatasourceUuid, datasourceUuid);
        return getOne(queryWrapper);
    }
}
