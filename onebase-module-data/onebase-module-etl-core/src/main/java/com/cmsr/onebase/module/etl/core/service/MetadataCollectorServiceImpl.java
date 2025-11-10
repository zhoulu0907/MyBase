package com.cmsr.onebase.module.etl.core.service;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.module.etl.core.dal.database.*;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLCatalogDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLDatasourceDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLSchemaDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLTableDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.metainfo.MetaColumn;
import com.cmsr.onebase.module.etl.core.dal.dataobject.metainfo.MetaTable;
import com.cmsr.onebase.module.etl.core.enums.CollectStatus;
import com.cmsr.onebase.module.etl.core.enums.ETLErrorCodeConstants;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.datasource.DataSourceHolder;
import org.anyline.metadata.Catalog;
import org.anyline.metadata.Column;
import org.anyline.metadata.Schema;
import org.anyline.metadata.Table;
import org.anyline.proxy.ServiceProxy;
import org.anyline.service.AnylineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MetadataCollectorServiceImpl implements MetadataCollectorService {
    @Resource
    private DataSourceFactory dataSourceFactory;

    @Resource
    private ETLDatasourceRepository datasourceRepository;

    @Resource
    private ETLCatalogRepository catalogRepository;

    @Resource
    private ETLSchemaRepository schemaRepository;

    @Resource
    private ETLTableRepository tableRepository;

    @Resource
    private ETLFlinkMappingRepository flinkMappingRepository;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public boolean testConnection(ETLDatasourceDO datasourceDO) {
        DataSource datasource = dataSourceFactory.constructDataSource(datasourceDO, true);
        try {
            boolean validity = ServiceProxy.temporary(datasource).validity();
            boolean hit = ServiceProxy.temporary(datasource).hit();
            return validity || hit;
        } catch (Exception ex) {
            log.error("测试数据源连接异常，数据源信息: {}", datasourceDO, ex);
            return false;
        }
    }

    @Override
    public void submitCollectJob(ETLDatasourceDO datasourceDO) {
        Long datasourceId = datasourceDO.getId();
        Long applicationId = datasourceDO.getApplicationId();
        String databaseType = datasourceDO.getDatasourceType();
        LocalDateTime planTime = LocalDateTime.now();
        isAbleToSubmitJob(datasourceDO, planTime);
        log.info("提交元数据采集任务，数据源ID：{}", datasourceId);
        threadPoolTaskExecutor.submit(() -> {
            datasourceRepository.changeCollectStatusById(datasourceId, CollectStatus.RUNNING);
            boolean isJobSuccess = doCollection(applicationId, datasourceId, databaseType);
            LocalDateTime endTime = LocalDateTime.now();
            Duration duration = Duration.between(planTime, endTime);
            long timeCost = duration.toMillis();
            if (isJobSuccess) {
                datasourceRepository.changeCollectStatusById(datasourceId, CollectStatus.SUCCESS);
                log.info("元数据采集任务执行成功，数据源ID：{}，耗时：{} ms", datasourceId, timeCost);
            } else {
                datasourceRepository.changeCollectStatusById(datasourceId, CollectStatus.FAILED);
                log.info("元数据采集任务执行失败，数据源ID：{}，耗时：{} ms", datasourceId, timeCost);
            }
        });
    }

    //TODO 数据采集和数据保存的逻辑交织在一起，太乱了
    public boolean doCollection(Long applicationId, Long datasourceId, String databaseType) {
        DataSource datasource = dataSourceFactory.constructDataSource(datasourceId, false);
        String datasourceKey = "metadata-collector-" + datasourceId;
        try {
            DataSourceHolder.reg(datasourceKey, datasource);
            AnylineService<?> temporary = ServiceProxy.service(datasourceKey);
            // Catalog
            Catalog catalog = temporary.metadata().catalog();
            String catalogName = catalog.getName();
            ETLCatalogDO oldCatalogDO = catalogRepository.findOneByNameAndDatasourceId(datasourceId, catalogName);
            ETLCatalogDO newCatalogDO = ETLCatalogDO.convert(applicationId, datasourceId, catalog);
            Long catalogId;
            if (oldCatalogDO == null) {
                newCatalogDO = catalogRepository.insert(newCatalogDO);
                catalogId = newCatalogDO.getId();
            } else {
                ETLCatalogDO.applyChanges(oldCatalogDO, newCatalogDO);
                catalogRepository.update(newCatalogDO);
                catalogId = oldCatalogDO.getId();
            }
            // Schema
            Schema schema = temporary.metadata().schema();
            String schemaName = schema.getName();
            ETLSchemaDO oldSchemaDO = schemaRepository.findOneByNameAndCatalogIdAndDatasourceId(datasourceId, catalogId, schemaName);
            Long schemaId;
            ETLSchemaDO newSchemaDO = ETLSchemaDO.convert(applicationId, datasourceId, catalogId, schema);
            if (oldSchemaDO == null) {
                newSchemaDO = schemaRepository.insert(newSchemaDO);
                schemaId = newSchemaDO.getId();
            } else {
                ETLSchemaDO.applyChanges(oldSchemaDO, newSchemaDO);
                schemaRepository.update(newSchemaDO);
                schemaId = oldSchemaDO.getId();
            }
            // Table
            Map<String, Table<?>> tables = temporary.metadata().tables(Table.TYPE.VIEW.value());
            Map<String, ETLTableDO> tableDOs = tableRepository.findAllByCatalogIdAndSchemaIdAndDatasourceId(datasourceId, catalogId, schemaId);
            List<ETLTableDO> tableDOList = Lists.newArrayList();
            for (Table<?> table : tables.values()) {
                String tableName = table.getName();
                // 重新获取一遍，由于Anyline .tables()方法会忽略列(Column)
                Map<String, Column> tableColumn = temporary.metadata().columns(table);
                ETLTableDO newTableDO = ETLTableDO.convert(applicationId, datasourceId, catalogId, schemaId, table, tableColumn);
                MetaTable metaInfo = newTableDO.getMetaInfo();
                List<MetaColumn> columns = metaInfo.getColumns();
                for (MetaColumn metaColumn : columns) {
                    String originType = metaColumn.getOriginType();
                    String compatibleType = flinkMappingRepository.findFlinkTypeByDatasourceTypeAndOriginType(databaseType, originType);
                    metaColumn.setFlinkType(compatibleType);
                }
                metaInfo.setColumns(columns);
                newTableDO.setMetaInfo(metaInfo);
                if (tableDOs.containsKey(tableName)) {
                    ETLTableDO oldTableDO = tableDOs.get(tableName);
                    ETLTableDO.applyChanges(oldTableDO, newTableDO);
                    tableDOList.add(newTableDO);
                } else {
                    tableDOList.add(newTableDO);
                }
            }
            tableRepository.upsertBatch(tableDOList);
            return true;
        } catch (Exception ex) {
            log.error("元数据采集时发生异常", ex);
            return false;
        } finally {
            unregisterDataSource(datasourceKey);
        }
    }

    private void isAbleToSubmitJob(ETLDatasourceDO datasourceDO, LocalDateTime plannedTime) {
        CollectStatus currentStatus = datasourceDO.getCollectStatus();
        // case (none, required, success, failed) -> running
        if (!CollectStatus.RUNNING.equals(currentStatus)) {
            return;
        }
        // case running -> running
        LocalDateTime perviousStartTime = datasourceDO.getCollectStartTime();
        Duration timeBetween = Duration.between(perviousStartTime, plannedTime);
        long minuteScale = timeBetween.toMinutes();
        if (minuteScale < 5L) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.METADATA_COLLECT_RUNNING);
        }
    }

    private void unregisterDataSource(String datasourceKey) {
        try {
            DataSourceHolder.destroy(datasourceKey);
        } catch (Exception ex) {
            log.error("注销数据源失败，数据源标识：{}", datasourceKey, ex);
        }
    }
}
