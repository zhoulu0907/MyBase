package com.cmsr.onebase.module.etl.build.service.collector;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.etl.common.entity.CatalogData;
import com.cmsr.onebase.module.etl.common.entity.SchemaData;
import com.cmsr.onebase.module.etl.common.entity.TableData;
import com.cmsr.onebase.module.etl.core.dal.database.ETLCatalogRepository;
import com.cmsr.onebase.module.etl.core.dal.database.ETLDatasourceRepository;
import com.cmsr.onebase.module.etl.core.dal.database.ETLSchemaRepository;
import com.cmsr.onebase.module.etl.core.dal.database.ETLTableRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLCatalogDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLSchemaDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLTableDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MetadataManager {

    @Resource
    private ETLDatasourceRepository datasourceRepository;

    @Resource
    private ETLCatalogRepository catalogRepository;

    @Resource
    private ETLSchemaRepository schemaRepository;

    @Resource
    private ETLTableRepository tableRepository;

    public void saveMetadata(Long applicationId, Long datasourceId, CatalogData catalogData) {
        ETLCatalogDO catalogDO = extractCatalogDO(applicationId, datasourceId, catalogData);
        catalogDO = catalogRepository.upsert(catalogDO);
        Long catalogId = catalogDO.getId();
        for (SchemaData schemaData : catalogData.getSchemas()) {
            ETLSchemaDO schemaDO = extractSchemaDO(applicationId, datasourceId, catalogId, schemaData);
            schemaDO = schemaRepository.upsert(schemaDO);
            Long schemaId = schemaDO.getId();
            for (TableData tableData : schemaData.getTables()) {
                ETLTableDO etlTableDO = extractTableDO(applicationId, datasourceId, catalogId, schemaId, tableData);
                tableRepository.upsert(etlTableDO);
            }
            List<ETLTableDO> tableDOS = tableRepository.findAllByCatalogIdAndSchemaIdAndDatasourceId(datasourceId, catalogId, schemaId);
            for (ETLTableDO tableDO : tableDOS) {
                Optional<TableData> optional = schemaData.getTables().stream()
                        .filter(tableData -> tableData.getName().equals(tableDO.getTableName())).findAny();
                if (!optional.isPresent()) {
                    tableRepository.delete(tableDO);
                }
            }
        }
    }

    private ETLCatalogDO extractCatalogDO(Long applicationId, Long datasourceId, CatalogData catalogData) {
        ETLCatalogDO catalogDO = new ETLCatalogDO();
        catalogDO.setApplicationId(applicationId);
        catalogDO.setDatasourceId(datasourceId);
        String name = catalogData.getName();
        catalogDO.setCatalogName(name);
        catalogDO.setDisplayName(name);
        catalogDO.setMetaInfo(JsonUtils.toJsonString(catalogData));

        return catalogDO;
    }

    private ETLSchemaDO extractSchemaDO(Long applicationId, Long datasourceId, Long catalogId, SchemaData schemaData) {
        ETLSchemaDO schemaDO = new ETLSchemaDO();
        schemaDO.setApplicationId(applicationId);
        schemaDO.setDatasourceId(datasourceId);
        schemaDO.setCatalogId(catalogId);
        String name = schemaData.getName();
        schemaDO.setSchemaName(name);
        schemaDO.setDisplayName(name);
        schemaDO.setMetaInfo(JsonUtils.toJsonString(schemaData));

        return schemaDO;
    }

    private ETLTableDO extractTableDO(Long applicationId, Long datasourceId, Long catalogId, Long schemaId, TableData tableData) {
        ETLTableDO tableDO = new ETLTableDO();
        tableDO.setApplicationId(applicationId);
        tableDO.setDatasourceId(datasourceId);
        tableDO.setCatalogId(catalogId);
        tableDO.setSchemaId(schemaId);
        String tableName = tableData.getName();
        tableDO.setTableName(tableName);
        tableDO.setDisplayName(tableName);
        tableDO.setTableType(tableData.getType());
        tableDO.setMetaInfo(tableData);
        String comment = tableData.getComment();
        tableDO.setRemarks(comment);
        tableDO.setDeclaration(comment);

        return tableDO;
    }
}
