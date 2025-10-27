package com.cmsr.onebase.module.etl.core.service.collector;

import com.cmsr.onebase.module.etl.core.dal.database.ETLCatalogRepository;
import com.cmsr.onebase.module.etl.core.dal.database.ETLDatasourceRepository;
import com.cmsr.onebase.module.etl.core.dal.database.ETLSchemaRepository;
import com.cmsr.onebase.module.etl.core.dal.database.ETLTableRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLCatalogDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLDatasourceDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLSchemaDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLTableDO;
import com.cmsr.onebase.module.etl.core.enums.CollectStatus;
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
    public void submitCollectJob(Long datasourceId) {
        log.info("提交元数据采集任务，数据源ID：{}", datasourceId);
        threadPoolTaskExecutor.submit(() -> {
            LocalDateTime starTime = LocalDateTime.now();
            datasourceRepository.updateCollectStatusById(datasourceId, CollectStatus.RUNNING, starTime);
            boolean isJobSuccess = doCollection(datasourceId);
            LocalDateTime endTime = LocalDateTime.now();
            Duration duration = Duration.between(starTime, endTime);
            long timeCost = duration.toMillis();
            if (isJobSuccess) {
                datasourceRepository.updateCollectStatusById(datasourceId, CollectStatus.SUCCESS, starTime);
                log.info("元数据采集任务执行成功，数据源ID：{}，耗时：{} ms", datasourceId, timeCost);
            } else {
                datasourceRepository.updateCollectStatusById(datasourceId, CollectStatus.FAILED, starTime);
                log.info("元数据采集任务执行失败，数据源ID：{}，耗时：{} ms", datasourceId, timeCost);
            }
        });
    }

    public boolean doCollection(Long datasourceId) {
        DataSource datasource = dataSourceFactory.constructDataSource(datasourceId, false);
        // TODO: add precheck etc. to prevent multiply submit. --> cause data duplication or error response.
        String datasourceKey = "metadata-collector-" + datasourceId;
        try {
            DataSourceHolder.reg(datasourceKey, datasource);
            AnylineService<?> temporary = ServiceProxy.service(datasourceKey);
            // Catalog
            Catalog catalog = temporary.metadata().catalog();
            String catalogName = catalog.getName();
            ETLCatalogDO oldCatalogDO = catalogRepository.findOneByNameAndDatasourceId(datasourceId, catalogName);
            ETLCatalogDO newCatalogDO = ETLCatalogDO.convert(datasourceId, catalog);
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
            ETLSchemaDO newSchemaDO = ETLSchemaDO.convert(datasourceId, catalogId, schema);
            if (oldSchemaDO == null) {
                newSchemaDO = schemaRepository.insert(newSchemaDO);
                schemaId = newSchemaDO.getId();
            } else {
                ETLSchemaDO.applyChanges(oldSchemaDO, newSchemaDO);
                schemaRepository.update(newSchemaDO);
                schemaId = oldSchemaDO.getId();
            }
            // Table
            Map<String, Table> tables = temporary.metadata().tables(Table.TYPE.VIEW.value());
            Map<String, ETLTableDO> tableDOs = tableRepository.findAllByCatalogIdAndSchemaIdAndDatasourceId(datasourceId, catalogId, schemaId);
            List<ETLTableDO> tableDOList = Lists.newArrayList();
            for (Table table : tables.values()) {
                String tableName = table.getName();
                // 重新获取一遍，由于Anyline .tables()方法会忽略列(Column)
                Map<String, Column> tableColumn = temporary.metadata().columns(table);
                ETLTableDO newTableDO = ETLTableDO.convert(datasourceId, catalogId, schemaId, table, tableColumn);
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

    private void unregisterDataSource(String datasourceKey) {
        try {
            DataSourceHolder.destroy(datasourceKey);
        } catch (Exception ex) {
            log.error("注销数据源失败，数据源标识：{}", datasourceKey, ex);
        }
    }
}
