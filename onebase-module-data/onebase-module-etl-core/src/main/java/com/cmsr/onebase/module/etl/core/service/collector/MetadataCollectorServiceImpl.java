package com.cmsr.onebase.module.etl.core.service.collector;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.module.etl.core.dal.database.DataFactoryCatalogRepository;
import com.cmsr.onebase.module.etl.core.dal.database.DataFactorySchemaRepository;
import com.cmsr.onebase.module.etl.core.dal.database.DataFactoryTableRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.DataFactoryCatalogDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.DataFactoryDatasourceDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.DataFactorySchemaDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.DataFactoryTableDO;
import com.cmsr.onebase.module.etl.core.enums.DataFactoryErrorCodeConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.metadata.Catalog;
import org.anyline.metadata.Schema;
import org.anyline.metadata.Table;
import org.anyline.proxy.ServiceProxy;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Map;

@Slf4j
@Service
public class MetadataCollectorServiceImpl implements MetadataCollectorService {
    @Resource
    private DataSourceFactory dataSourceFactory;

    @Resource
    private DataFactoryCatalogRepository catalogRepository;

    @Resource
    private DataFactorySchemaRepository schemaRepository;

    @Resource
    private DataFactoryTableRepository tableRepository;


    @Override
    public boolean testConnection(DataFactoryDatasourceDO datasourceDO) {
        DataSource datasource = dataSourceFactory.constructDataSource(datasourceDO);
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
    public boolean doCollection(DataFactoryDatasourceDO datasourceDO) {
        DataSource datasource = dataSourceFactory.constructDataSource(datasourceDO);
        Long datasourceId = datasourceDO.getId();
        try {
            AnylineService<?> temporary = ServiceProxy.temporary(datasource);
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
                DataFactoryTableDO newTableDO = DataFactoryTableDO.convert(datasourceId, catalogId, schemaId, table);
                if (tableDOs.containsKey(tableName)) {
                    DataFactoryTableDO oldTableDO = tableDOs.get(tableName);
                    DataFactoryTableDO.applyChanges(oldTableDO, newTableDO);
                    tableRepository.update(newTableDO);
                } else {
                    tableRepository.insert(newTableDO);
                }
            }
            return true;
        } catch (Exception ex) {
            log.error("元数据采集时发生异常", ex);
            throw ServiceExceptionUtil.exception(DataFactoryErrorCodeConstants.METADATA_COLLECT_FAILED);
        }
    }
}
