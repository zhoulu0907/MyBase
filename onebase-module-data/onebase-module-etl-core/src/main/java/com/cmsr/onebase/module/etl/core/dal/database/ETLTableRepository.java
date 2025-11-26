package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
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
public class ETLTableRepository extends BaseAppRepository<ETLTableMapper, ETLTableDO> {

    public List<ETLTableDO> findAllByCatalogAndSchemaAndDatasource(String datasourceUuid, String catalogUuid, String schemaUuid) {
        QueryWrapper queryWrapper = query()
                .eq(ETLTableDO::getDatasourceUuid, datasourceUuid)
                .eq(ETLTableDO::getCatalogUuid, catalogUuid)
                .eq(ETLTableDO::getSchemaUuid, schemaUuid)
                .orderBy(ETLTableDO::getUpdateTime, false)
                .orderBy(ETLTableDO::getCreateTime, false)
                .orderBy(ETLTableDO::getTableName, true);

        return list(queryWrapper);
    }

    public void deleteAllByDatasource(String datasourceUuid) {
        this.updateChain()
                .eq(ETLTableDO::getDatasourceUuid, datasourceUuid)
                .remove();
    }

    // 优化方法名：更简洁但保持语义清晰
    public ETLTableDO findOneByQualifiedName(Long applicationId, String datasourceUuid, String catalogUuid, String schemaUuid, String tableName) {
        QueryWrapper queryWrapper = query()
                .eq(ETLTableDO::getApplicationId, applicationId)
                .eq(ETLTableDO::getDatasourceUuid, datasourceUuid)
                .eq(ETLTableDO::getCatalogUuid, catalogUuid)
                .eq(ETLTableDO::getSchemaUuid, schemaUuid)
                .eq(ETLTableDO::getTableName, tableName);
        return getOne(queryWrapper);
    }

    public String getNameByUuid(String tableUuid) {
        QueryWrapper queryWrapper = query().select(ETLTableDO::getDisplayName)
                .eq(ETLTableDO::getTableUuid, tableUuid);
        return getObjAs(queryWrapper, String.class);
    }

    public List<String> getNameByIds(Collection<String> uuids) {
        if (CollectionUtils.isEmpty(uuids)) {
            return Collections.emptyList();
        }
        QueryWrapper queryWrapper = query()
                .select(ETLTableDO::getDisplayName)
                .in(ETLTableDO::getTableUuid, uuids)
                .orderBy(ETLTableDO::getTableName, true);
        return objListAs(queryWrapper, String.class);
    }

    public List<ETLTableDO> findAllByDatasource(String datasourceUuid, Boolean writable) {
        QueryWrapper queryWrapper = query().eq(ETLTableDO::getDatasourceUuid, datasourceUuid)
                .eq(ETLTableDO::getTableType, "table", writable)
                .orderBy(ETLTableDO::getTableName, true);
        return list(queryWrapper);
    }
}
