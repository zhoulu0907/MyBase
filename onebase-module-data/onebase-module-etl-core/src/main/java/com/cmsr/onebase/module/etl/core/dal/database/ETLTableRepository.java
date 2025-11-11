package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLTableDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@Slf4j
public class ETLTableRepository extends DataRepository<ETLTableDO> {

    public ETLTableRepository() {
        super(ETLTableDO.class);
    }

    public List<ETLTableDO> findAllByCatalogIdAndSchemaIdAndDatasourceId(Long datasourceId, Long catalogId, Long schemaId) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("datasource_id", datasourceId);
        cs.eq("catalog_id", catalogId);
        cs.eq("schema_id", schemaId);
        return findAllByConfig(cs);
    }

    public void deleteAllByDatasourceId(Long datasourceId) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("datasource_id", datasourceId);

        deleteByConfig(cs);
    }

    // 优化方法名：更简洁但保持语义清晰
    public ETLTableDO findOneByQualifiedName(Long applicationId, Long datasourceId, Long catalogId, Long schemaId, String tableName) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("application_id", applicationId);
        cs.eq("datasource_id", datasourceId);
        cs.eq("catalog_id", catalogId);
        cs.eq("schema_id", schemaId);
        cs.eq("table_name", tableName);

        return findOne(cs);
    }

    @Override
    public ETLTableDO upsert(ETLTableDO tableDO) {
        Long applicationId = tableDO.getApplicationId();
        Long datasourceId = tableDO.getDatasourceId();
        Long catalogId = tableDO.getCatalogId();
        Long schemaId = tableDO.getSchemaId();
        String tableName = tableDO.getTableName();
        // 调用优化后的方法名
        ETLTableDO old = findOneByQualifiedName(applicationId, datasourceId, catalogId, schemaId, tableName);
        if (old == null) {
            tableDO = insert(tableDO);
        } else {
            tableDO.setId(old.getId());
            update(tableDO);
        }
        return tableDO;
    }

    public String getNameById(Long id) {
        ETLTableDO tableDO = findById(id);
        return tableDO.getDisplayName();
    }

    public List<String> getNameByIds(Collection<Long> ids) {
        List<ETLTableDO> tables = findAllByIds(ids);
        return tables.stream().map(ETLTableDO::getDisplayName).toList();
    }

    public List<ETLTableDO> findAllByDatasourceId(Long datasourceId, Boolean writable) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("datasource_id", datasourceId);
        if (writable) {
            cs.eq("table_type", "table");
        }

        return findAllByConfig(cs);
    }
}
