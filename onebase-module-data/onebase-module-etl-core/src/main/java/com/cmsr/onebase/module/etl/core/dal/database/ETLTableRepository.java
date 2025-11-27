package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlTableDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.EtlTableMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Repository
@Slf4j
public class EtlTableRepository extends BaseAppRepository<EtlTableMapper, EtlTableDO> {

    public List<EtlTableDO> findAllByCatalogAndSchemaAndDatasource(String datasourceUuid, String catalogUuid, String schemaUuid) {
        QueryWrapper queryWrapper = query()
                .eq(EtlTableDO::getDatasourceUuid, datasourceUuid)
                .eq(EtlTableDO::getCatalogUuid, catalogUuid)
                .eq(EtlTableDO::getSchemaUuid, schemaUuid)
                .orderBy(EtlTableDO::getUpdateTime, false)
                .orderBy(EtlTableDO::getCreateTime, false)
                .orderBy(EtlTableDO::getTableName, true);

        return list(queryWrapper);
    }

    public void deleteAllByDatasource(String datasourceUuid) {
        this.updateChain()
                .eq(EtlTableDO::getDatasourceUuid, datasourceUuid)
                .remove();
    }

    // 优化方法名：更简洁但保持语义清晰
    public EtlTableDO findOneByQualifiedName(Long applicationId, String datasourceUuid, String catalogUuid, String schemaUuid, String tableName) {
        QueryWrapper queryWrapper = query()
                .eq(EtlTableDO::getApplicationId, applicationId)
                .eq(EtlTableDO::getDatasourceUuid, datasourceUuid)
                .eq(EtlTableDO::getCatalogUuid, catalogUuid)
                .eq(EtlTableDO::getSchemaUuid, schemaUuid)
                .eq(EtlTableDO::getTableName, tableName);
        return getOne(queryWrapper);
    }

    public String getNameByUuid(String tableUuid) {
        QueryWrapper queryWrapper = query().select(EtlTableDO::getDisplayName)
                .eq(EtlTableDO::getTableUuid, tableUuid);
        return getObjAs(queryWrapper, String.class);
    }

    public List<String> getNameByIds(Collection<String> uuids) {
        if (CollectionUtils.isEmpty(uuids)) {
            return Collections.emptyList();
        }
        QueryWrapper queryWrapper = query()
                .select(EtlTableDO::getDisplayName)
                .in(EtlTableDO::getTableUuid, uuids)
                .orderBy(EtlTableDO::getTableName, true);
        return objListAs(queryWrapper, String.class);
    }

    public List<EtlTableDO> findAllByDatasource(String datasourceUuid, Boolean writable) {
        QueryWrapper queryWrapper = query().eq(EtlTableDO::getDatasourceUuid, datasourceUuid)
                .eq(EtlTableDO::getTableType, "table", writable)
                .orderBy(EtlTableDO::getTableName, true);
        return list(queryWrapper);
    }

    public EtlTableDO getByUuid(String tableUuid) {
        QueryWrapper queryWrapper = this.query()
                .eq(EtlTableDO::getTableUuid, tableUuid);
        return getOne(queryWrapper);
    }
}
