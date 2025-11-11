package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLCatalogDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ETLCatalogRepository extends DataRepository<ETLCatalogDO> {

    public ETLCatalogRepository() {
        super(ETLCatalogDO.class);
    }

    public void deleteAllByDatasourceId(Long datasourceId) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("datasource_id", datasourceId);

        deleteByConfig(cs);
    }

    @Override
    public ETLCatalogDO upsert(ETLCatalogDO catalogDO) {
        Long applicationId = catalogDO.getApplicationId();
        Long datasourceId = catalogDO.getDatasourceId();
        String catalogName = catalogDO.getCatalogName();
        // 调用优化后的方法名
        ETLCatalogDO oldCatalog = findOneByQualifiedName(applicationId, datasourceId, catalogName);
        if (oldCatalog == null) {
            catalogDO = insert(catalogDO);
        } else {
            catalogDO.setId(oldCatalog.getId());
            update(catalogDO);
        }
        return catalogDO;
    }

    // 优化方法名：更简洁但保持语义清晰
    public ETLCatalogDO findOneByQualifiedName(Long applicationId, Long datasourceId, String catalogName) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("application_id", applicationId);
        cs.eq("datasource_id", datasourceId);
        cs.eq("catalog_name", catalogName);
        return findOne(cs);
    }
}
