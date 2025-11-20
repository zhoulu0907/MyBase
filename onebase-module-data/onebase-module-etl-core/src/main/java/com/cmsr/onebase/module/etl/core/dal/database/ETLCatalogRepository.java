package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLCatalogDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.ETLCatalogMapper;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.anyline.service.AnylineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ETLCatalogRepository implements IService<ETLCatalogDO> {

    private DataRepository<ETLCatalogDO> dataRepository = new DataRepository<>();

    @Autowired
    private AnylineService<ETLCatalogDO> anylineService;

    @PostConstruct
    public void init() {
        dataRepository.setAnylineService(anylineService);
    }

    @Autowired
    private ETLCatalogMapper catalogMapper;

    public void deleteAllByDatasourceId(Long datasourceId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq(ETLCatalogDO::getDatasourceId, datasourceId);

        catalogMapper.deleteByQuery(queryWrapper);
    }

    public ETLCatalogDO upsert(ETLCatalogDO catalogDO) {
        catalogMapper.insertOrUpdate(catalogDO);
        return catalogDO;
    }

    @Override
    public BaseMapper<ETLCatalogDO> getMapper() {
        return catalogMapper;
    }
}
