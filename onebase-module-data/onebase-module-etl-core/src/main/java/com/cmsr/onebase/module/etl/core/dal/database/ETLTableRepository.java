package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLTableDO;
import com.cmsr.onebase.module.etl.core.enums.ETLErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ETLTableRepository extends DataRepository<ETLTableDO> {

    public ETLTableRepository() {
        super(ETLTableDO.class);
    }

    public Map<String, ETLTableDO> findAllByCatalogIdAndSchemaIdAndDatasourceId(Long datasourceId, Long catalogId, Long schemaId) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("datasource_id", datasourceId);
        cs.eq("catalog_id", catalogId);
        cs.eq("schema_id", schemaId);
        List<ETLTableDO> tableList = findAllByConfig(cs);
        return tableList.stream()
                .collect(Collectors.toMap(ETLTableDO::getTableName, table -> table));
    }

    public void deleteAllByDatasourceId(Long datasourceId) {
        ConfigStore cs = new DefaultConfigStore();
        cs.eq("datasource_id", datasourceId);

        deleteByConfig(cs);
    }

    @Override
    public ETLTableDO upsert(ETLTableDO tableDO) {
        if (tableDO == null) return null;
        try {
            Long id = tableDO.getId();
            if (id == null) {
                tableDO = insert(tableDO);
            } else {
                update(tableDO);
            }
        } catch (Exception e) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.METADATA_COLLECT_FAILED);
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
