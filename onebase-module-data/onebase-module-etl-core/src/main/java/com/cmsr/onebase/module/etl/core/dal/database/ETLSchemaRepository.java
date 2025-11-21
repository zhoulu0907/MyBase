package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLSchemaDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ETLSchemaRepository extends DataRepository<ETLSchemaDO> {

    public ETLSchemaRepository() {
        super(ETLSchemaDO.class);
    }


    // 优化方法名：更简洁但保持语义清晰
    public ETLSchemaDO findOneByQualifiedName(Long applicationId, Long datasourceId, Long catalogId, String name) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("application_id", applicationId);
        cs.eq("datasource_id", datasourceId);
        cs.eq("catalog_id", catalogId);
        cs.eq("schema_name", name);

        return findOne(cs);
    }

    public void deleteAllByDatasourceId(Long datasourceId) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("datasource_id", datasourceId);

        deleteByConfig(cs);
    }

    @Override
    public ETLSchemaDO upsert(ETLSchemaDO schemaDO) {
        if (schemaDO == null) return null;
        Long applicationId = schemaDO.getApplicationId();
        Long datasourceId = schemaDO.getDatasourceId();
        Long catalogId = schemaDO.getCatalogId();
        String schemaName = schemaDO.getSchemaName();
        // 调用优化后的方法名
        ETLSchemaDO oldSchema = findOneByQualifiedName(applicationId, datasourceId, catalogId, schemaName);
        if (oldSchema == null) {
            schemaDO = insert(schemaDO);
        } else {
            schemaDO.setId(oldSchema.getId());
            update(schemaDO);
        }
        return schemaDO;
    }
}
