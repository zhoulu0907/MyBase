package com.cmsr.onebase.module.etl.core.service.collector;

import com.cmsr.onebase.module.etl.core.dal.database.DataFactoryCatalogRepository;
import com.cmsr.onebase.module.etl.core.dal.database.DataFactoryDatasourceRepository;
import com.cmsr.onebase.module.etl.core.dal.database.DataFactorySchemaRepository;
import com.cmsr.onebase.module.etl.core.dal.database.DataFactoryTableRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.DataFactoryCatalogDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.DataFactoryDatasourceDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.DataFactorySchemaDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.DataFactoryTableDO;
import com.cmsr.onebase.module.etl.core.enums.CollectStatus;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.datasource.DataSourceHolder;
import org.anyline.metadata.*;
import org.anyline.proxy.ServiceProxy;
import org.anyline.service.AnylineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Map;

@Slf4j
@Service
public class MetadataCollectorServiceImpl implements MetadataCollectorService {
    @Resource
    private DataSourceFactory dataSourceFactory;

    @Resource
    private DataFactoryDatasourceRepository datasourceRepository;

    @Resource
    private DataFactoryCatalogRepository catalogRepository;

    @Resource
    private DataFactorySchemaRepository schemaRepository;

    @Resource
    private DataFactoryTableRepository tableRepository;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public boolean testConnection(DataFactoryDatasourceDO datasourceDO) {
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
        datasourceRepository.updateCollectStatusById(datasourceId, CollectStatus.RUNNING);
        threadPoolTaskExecutor.submit(() -> {
            long starTime = System.currentTimeMillis();
            boolean isJobDone = doCollection(datasourceId);
            long endTime = System.currentTimeMillis();
            long timeCost = endTime - starTime;
            if (isJobDone) {
                datasourceRepository.updateCollectStatusById(datasourceId, CollectStatus.SUCCESS);
                log.info("元数据采集任务执行成功，数据源ID：{}，耗时：{} ms", datasourceId, timeCost);
            } else {
                datasourceRepository.updateCollectStatusById(datasourceId, CollectStatus.FAILED);
                log.info("元数据采集任务执行失败，数据源ID：{}，耗时：{} ms", datasourceId, timeCost);
            }
        });
    }

    public boolean doCollection(Long datasourceId) {
        DataSource datasource = dataSourceFactory.constructDataSource(datasourceId, false);
        String datasourceKey = "metadata-collector-" + datasourceId;
        try {
            DataSourceHolder.reg(datasourceKey, datasource);
            AnylineService<?> temporary = ServiceProxy.service(datasourceKey);
            // Catalog
            Catalog catalog = temporary.metadata().catalog();
            String catalogName = catalog.getName();
            DataFactoryCatalogDO oldCatalogDO = catalogRepository.findOneByNameAndDatasourceId(datasourceId, catalogName);
            DataFactoryCatalogDO newCatalogDO = DataFactoryCatalogDO.convert(datasourceId, catalog);
            Long catalogId;
            if (oldCatalogDO == null) {
                newCatalogDO = catalogRepository.insert(newCatalogDO);
                catalogId = newCatalogDO.getId();
            } else {
                DataFactoryCatalogDO.applyChanges(oldCatalogDO, newCatalogDO);
                catalogRepository.update(newCatalogDO);
                catalogId = oldCatalogDO.getId();
            }
            // Schema
            Schema schema = temporary.metadata().schema();
            String schemaName = schema.getName();
            DataFactorySchemaDO oldSchemaDO = schemaRepository.findOneByNameAndCatalogIdAndDatasourceId(datasourceId, catalogId, schemaName);
            Long schemaId;
            DataFactorySchemaDO newSchemaDO = DataFactorySchemaDO.convert(datasourceId, catalogId, schema);
            if (oldSchemaDO == null) {
                newSchemaDO = schemaRepository.insert(newSchemaDO);
                schemaId = newSchemaDO.getId();
            } else {
                DataFactorySchemaDO.applyChanges(oldSchemaDO, newSchemaDO);
                schemaRepository.update(newSchemaDO);
                schemaId = oldSchemaDO.getId();
            }
            // Table
            Map<String, Table> tables = temporary.metadata().tables();
            Map<String, DataFactoryTableDO> tableDOs = tableRepository.findAllByCatalogIdAndSchemaIdAndDatasourceId(datasourceId, catalogId, schemaId);
            for (Table table : tables.values()) {
                String tableName = table.getName();
                // 重新获取一遍，由于Anyline .tables()方法会忽略列(Column)
                Map<String, Column> tableColumn = temporary.metadata().columns(table);
                DataFactoryTableDO newTableDO = DataFactoryTableDO.convert(datasourceId, catalogId, schemaId, table, tableColumn);
                if (tableDOs.containsKey(tableName)) {
                    DataFactoryTableDO oldTableDO = tableDOs.get(tableName);
                    DataFactoryTableDO.applyChanges(oldTableDO, newTableDO);
                    tableRepository.update(newTableDO);
                } else {
                    tableRepository.insert(newTableDO);
                }
            }
            Map<String, View> views = temporary.metadata().views();
            for (View view : views.values()) {
                String viewName = view.getName();
                // 重新获取一遍，由于Anyline .views()方法会忽略列(Column)
                Map<String, Column> viewColumn = temporary.metadata().columns(view);
                DataFactoryTableDO newViewDO = DataFactoryTableDO.convert(datasourceId, catalogId, schemaId, view, viewColumn);
                if (tableDOs.containsKey(viewName)) {
                    DataFactoryTableDO oldViewDO = tableDOs.get(viewName);
                    DataFactoryTableDO.applyChanges(oldViewDO, newViewDO);
                    tableRepository.update(newViewDO);
                } else {
                    tableRepository.insert(newViewDO);
                }
            }
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
