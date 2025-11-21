package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLCatalogDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.ETLCatalogMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.anyline.service.AnylineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ETLCatalogRepository extends ServiceImpl<ETLCatalogMapper, ETLCatalogDO> {

    private DataRepository<ETLCatalogDO> dataRepository;

    @Autowired
    private AnylineService<ETLCatalogDO> anylineService;

    @PostConstruct
    public void init() {
        dataRepository = new DataRepository<>(ETLCatalogDO.class);
        dataRepository.setAnylineService(anylineService);
    }

    public void deleteAllByDatasourceId(Long datasourceId) {
        QueryWrapper queryWrapper = query()
                .eq(ETLCatalogDO::getDatasourceId, datasourceId);

        getMapper().deleteByQuery(queryWrapper);
    }

    public ETLCatalogDO upsert(ETLCatalogDO catalogDO) {
        saveOrUpdate(catalogDO);
        return catalogDO;
    }
}
