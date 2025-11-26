package com.cmsr.onebase.module.etl.build.service.collector;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.etl.common.entity.CatalogData;
import com.cmsr.onebase.module.etl.common.entity.SchemaData;
import com.cmsr.onebase.module.etl.common.entity.TableData;
import com.cmsr.onebase.module.etl.core.dal.database.ETLCatalogRepository;
import com.cmsr.onebase.module.etl.core.dal.database.ETLSchemaRepository;
import com.cmsr.onebase.module.etl.core.dal.database.ETLTableRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLCatalogDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLSchemaDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLTableDO;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MetadataManager {

    @Resource
    private ETLCatalogRepository catalogRepository;

    @Resource
    private ETLSchemaRepository schemaRepository;

    @Resource
    private ETLTableRepository tableRepository;

    public void saveMetadata(Long applicationId, String datasourceUuid, CatalogData catalogData) {
        ETLCatalogDO catalogDO = extractCatalogDO(applicationId, datasourceUuid, catalogData);
        catalogRepository.saveOrUpdate(catalogDO);
        String catalogUuid = catalogDO.getCatalogUuid();
        for (SchemaData schemaData : catalogData.getSchemas()) {
            ETLSchemaDO schemaDO = extractSchemaDO(applicationId, datasourceUuid, catalogUuid, schemaData);
            schemaRepository.saveOrUpdate(schemaDO);
            String schemaUuid = schemaDO.getSchemaUuid();
            for (TableData tableData : schemaData.getTables()) {
                ETLTableDO etlTableDO = extractTableDO(applicationId, datasourceUuid, catalogUuid, schemaUuid, tableData);
                tableRepository.saveOrUpdate(etlTableDO);
            }
            Set<String> collectedTableNames = schemaData.getTables().stream().map(TableData::getName).collect(Collectors.toSet());
            List<ETLTableDO> tableDOS = tableRepository.findAllByCatalogAndSchemaAndDatasource(datasourceUuid, catalogUuid, schemaUuid);
            for (ETLTableDO tableDO : tableDOS) {
                if (!collectedTableNames.contains(tableDO.getTableName())) {
                    tableRepository.removeById(tableDO);
                }
            }
        }
    }

    private ETLCatalogDO extractCatalogDO(Long applicationId, String datasourceUuid, CatalogData catalogData) {
        ETLCatalogDO catalogDO = catalogRepository.findCatalogByDatasource(applicationId, datasourceUuid);
        if (catalogDO == null) {
            catalogDO = new ETLCatalogDO();
            catalogDO.setApplicationId(applicationId);
            catalogDO.setDatasourceUuid(datasourceUuid);
            catalogDO.setCatalogUuid(UuidCreator.getTimeOrderedEpoch().toString());
        }
        String name = catalogData.getName();
        catalogDO.setCatalogName(name);
        catalogDO.setDisplayName(name);
        catalogDO.setMetaInfo(JsonUtils.toJsonString(catalogData));

        return catalogDO;
    }

    private ETLSchemaDO extractSchemaDO(Long applicationId, String datasourceUuid, String catalogUuid, SchemaData schemaData) {

        ETLSchemaDO schemaDO = schemaRepository.findByDatasourceAndCatalog(applicationId, datasourceUuid, catalogUuid);
        if (schemaDO == null) {
            schemaDO = new ETLSchemaDO();
            schemaDO.setApplicationId(applicationId);
            schemaDO.setDatasourceUuid(datasourceUuid);
            schemaDO.setCatalogUuid(catalogUuid);
            schemaDO.setSchemaUuid(UuidCreator.getTimeOrderedEpoch().toString());
        }
        String name = schemaData.getName();
        schemaDO.setSchemaName(name);
        schemaDO.setDisplayName(name);
        schemaDO.setMetaInfo(JsonUtils.toJsonString(schemaData));

        return schemaDO;
    }

    private ETLTableDO extractTableDO(Long applicationId, String datasourceUuid, String catalogUuid, String schemaUuid, TableData tableData) {
        String tableName = tableData.getName();
        ETLTableDO tableDO = tableRepository.findOneByQualifiedName(applicationId, datasourceUuid, catalogUuid, schemaUuid, tableName);
        if (tableDO == null) {
            tableDO = new ETLTableDO();
            tableDO.setApplicationId(applicationId);
            tableDO.setDatasourceUuid(datasourceUuid);
            tableDO.setCatalogUuid(catalogUuid);
            tableDO.setSchemaUuid(schemaUuid);
            tableDO.setTableUuid(UuidCreator.getTimeOrderedEpoch().toString());
        }
        tableDO.setTableName(tableName);
        tableDO.setDisplayName(tableName);
        tableDO.setTableType(tableData.getType());
        tableDO.setMetaInfo(JsonUtils.toJsonString(tableData));
        String comment = tableData.getComment();
        tableDO.setRemarks(comment);
        tableDO.setDeclaration(comment);

        return tableDO;
    }
}
