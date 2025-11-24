package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.mybatis.BaseTenantRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLTableDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.ETLTableMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Repository
@Slf4j
public class ETLTableRepository extends BaseTenantRepository<ETLTableMapper, ETLTableDO> {

    public List<ETLTableDO> findAllByCatalogIdAndSchemaIdAndDatasourceId(Long datasourceId, Long catalogId, Long schemaId) {
        QueryWrapper queryWrapper = query()
                .eq(ETLTableDO::getDatasourceId, datasourceId)
                .eq(ETLTableDO::getCatalogId, catalogId)
                .eq(ETLTableDO::getSchemaId, schemaId)
                .orderBy(ETLTableDO::getUpdateTime, false)
                .orderBy(ETLTableDO::getCreateTime, false)
                .orderBy(ETLTableDO::getTableName, true);

        return list(queryWrapper);
    }

    public void deleteAllByDatasourceId(Long datasourceId) {
        QueryWrapper queryWrapper = query().eq(ETLTableDO::getDatasourceId, datasourceId);
        remove(queryWrapper);
    }

    // 优化方法名：更简洁但保持语义清晰
    public ETLTableDO findOneByQualifiedName(Long applicationId, Long datasourceId, Long catalogId, Long schemaId, String tableName) {
        QueryWrapper queryWrapper = query()
                .eq(ETLTableDO::getApplicationId, applicationId)
                .eq(ETLTableDO::getDatasourceId, datasourceId)
                .eq(ETLTableDO::getCatalogId, catalogId)
                .eq(ETLTableDO::getSchemaId, schemaId)
                .eq(ETLTableDO::getTableName, tableName);
        return getOne(queryWrapper);
    }

    public ETLTableDO upsert(ETLTableDO tableDO) {
        Long applicationId = tableDO.getApplicationId();
        Long datasourceId = tableDO.getDatasourceId();
        Long catalogId = tableDO.getCatalogId();
        Long schemaId = tableDO.getSchemaId();
        String tableName = tableDO.getTableName();
        // 调用优化后的方法名
        ETLTableDO old = findOneByQualifiedName(applicationId, datasourceId, catalogId, schemaId, tableName);
        if (old != null) {
            tableDO.setId(old.getId());
        }
        saveOrUpdate(tableDO);
        return tableDO;
    }

    public String getNameById(Long id) {
        QueryWrapper queryWrapper = query().select(ETLTableDO::getDisplayName).eq(ETLTableDO::getId, id);
        ETLTableDO tableDO = getOne(queryWrapper);
        return tableDO.getDisplayName();
    }

    public List<String> getNameByIds(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        QueryWrapper queryWrapper = query().select(ETLTableDO::getDisplayName).in(ETLTableDO::getId, ids).orderBy(ETLTableDO::getTableName, true);
        List<ETLTableDO> tables = list(queryWrapper);
        return tables.stream().map(ETLTableDO::getDisplayName).toList();
    }

    public List<ETLTableDO> findAllByDatasourceId(Long datasourceId, Boolean writable) {
        QueryWrapper queryWrapper = query().eq(ETLTableDO::getDatasourceId, datasourceId)
                .eq(ETLTableDO::getTableType, "table", writable)
                .orderBy(ETLTableDO::getTableName, true);
        return list(queryWrapper);
    }
}
