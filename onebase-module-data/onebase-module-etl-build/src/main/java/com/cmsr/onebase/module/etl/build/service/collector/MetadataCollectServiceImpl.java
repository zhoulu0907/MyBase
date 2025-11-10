package com.cmsr.onebase.module.etl.build.service.collector;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.module.etl.build.service.DatasourceFactory;
import com.cmsr.onebase.module.etl.core.dal.database.ETLCatalogRepository;
import com.cmsr.onebase.module.etl.core.dal.database.ETLDatasourceRepository;
import com.cmsr.onebase.module.etl.core.dal.database.ETLSchemaRepository;
import com.cmsr.onebase.module.etl.core.dal.database.ETLTableRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLCatalogDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLDatasourceDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLSchemaDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLTableDO;
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
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MetadataCollectServiceImpl implements MetadataCollectService {

    @Resource
    private DatasourceFactory datasourceFactory;

    @Resource
    private ETLDatasourceRepository datasourceRepository;

    @Resource
    private ETLCatalogRepository catalogRepository;

    @Resource
    private ETLSchemaRepository schemaRepository;

    @Resource
    private ETLTableRepository tableRepository;

    private void unregisterDataSource(String datasourceKey) {
        try {
            DataSourceHolder.destroy(datasourceKey);
        } catch (Exception ex) {
            log.error("注销数据源失败，数据源标识：{}", datasourceKey, ex);
        }
    }

    @Override
    public ETLCatalogDO collectCatalog(ETLDatasourceDO datasourceDO) {
        Long datasourceId = datasourceDO.getId();
        DataSource datasource = datasourceFactory.constructDataSource(datasourceDO, true);
        try {
            AnylineService<?> temporary = ServiceProxy.temporary(datasource);
            // 1. collect catalog
            Catalog catalog = temporary.metadata().catalog();
            String catalogName = catalog.getName();
            ETLCatalogDO oldOne = catalogRepository.findOneByNameAndDatasourceId(datasourceId, catalogName);
            ETLCatalogDO newOne = ETLCatalogDO.of(datasourceDO.getApplicationId(), datasourceId, catalog);
            if (oldOne != null) {
                newOne.setId(oldOne.getId());
            }
            return newOne;
        } catch (Exception e) {
            log.error("元数据采集时发生异常", e);
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.NO_CATALOG_AVAILABLE);
        }
    }

    @Override
    public ETLSchemaDO collectSchema(ETLDatasourceDO datasourceDO, Long catalogId) {
        Long datasourceId = datasourceDO.getId();
        DataSource datasource = datasourceFactory.constructDataSource(datasourceDO, true);
        try {
            AnylineService<?> temporary = ServiceProxy.temporary(datasource);
            // 1. collect schema
            Schema schema = temporary.metadata().schema();
            String schemaName = schema.getName();
            ETLSchemaDO oldOne = schemaRepository.findOneByNameAndCatalogIdAndDatasourceId(datasourceId, catalogId, schemaName);
            ETLSchemaDO newOne = ETLSchemaDO.of(datasourceDO.getApplicationId(), datasourceId, catalogId, schema);
            if (oldOne != null) {
                newOne.setId(oldOne.getId());
            }
            return newOne;
        } catch (Exception e) {
            log.error("元数据采集时发生异常", e);
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.NO_SCHEMA_AVAILABLE);
        }
    }

    @Override
    public List<ETLTableDO> collectTables(ETLDatasourceDO datasourceDO, Long catalogId, Long schemaId) {
        Long datasourceId = datasourceDO.getId();
        DataSource datasource = datasourceFactory.constructDataSource(datasourceDO, false);
        String runnerKey = "metadata-collect-" + datasourceId;
        try {
            DataSourceHolder.reg(runnerKey, datasource);
            AnylineService<?> temporary = ServiceProxy.service(runnerKey);
            // 1. collect tables
            Map<String, Table<?>> tables = temporary.metadata().tables(Table.TYPE.VIEW.value());
            Map<String, ETLTableDO> tableDOs = tableRepository.findAllByCatalogIdAndSchemaIdAndDatasourceId(datasourceId, catalogId, schemaId);
            List<ETLTableDO> tableDOList = Lists.newArrayList();
            for (Table<?> table : tables.values()) {
                String tableName = table.getName();
                Collection<Column> tableColumn = temporary.metadata().columns(table).values();
                ETLTableDO newOne = ETLTableDO.of(datasourceDO.getApplicationId(), datasourceId, catalogId, schemaId, table, tableColumn);
                if (tableDOs.containsKey(tableName)) {
                    newOne.setId(tableDOs.get(tableName).getId());
                }
                tableDOList.add(newOne);
            }
            return tableDOList;
        } catch (Exception e) {
            log.error("元数据采集时发生异常", e);
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.NO_SCHEMA_AVAILABLE);
        } finally {
            unregisterDataSource(runnerKey);
        }
    }
}
