package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLTableDO;
import com.cmsr.onebase.module.etl.core.enums.ETLErrorCodeConstants;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

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


    //
//    @Override
//    public List<ETLTableDO> upsertBatch(List<ETLTableDO> tableDOList) {
//        if (CollectionUtils.isEmpty(tableDOList)) return List.of();
//
//        // 1. 根据是否有主键,拆分 insert / update
//        List<ETLTableDO> insertBatch = tableDOList.stream().filter(o -> o.getId() == null).toList();
//        List<ETLTableDO> updateBatch = tableDOList.stream().filter(o -> o.getId() != null).toList();
//        List<ETLTableDO> results = Lists.newArrayList();
//        try {
//            // 1. 先执行update
//
//            results.addAll(updateBatch);
//            // 2. 再执行insert
//            List<ETLTableDO> inserted = insertBatch(insertBatch);
//            results.addAll(inserted);
//        } catch (Exception e) {
//            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.METADATA_COLLECT_FAILED);
//        }
//        return results;
//    }
}
