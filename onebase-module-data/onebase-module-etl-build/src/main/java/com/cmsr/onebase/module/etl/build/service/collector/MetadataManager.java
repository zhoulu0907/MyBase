package com.cmsr.onebase.module.etl.build.service.collector;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.etl.common.entity.CatalogData;
import com.cmsr.onebase.module.etl.common.entity.SchemaData;
import com.cmsr.onebase.module.etl.common.entity.TableData;
import com.cmsr.onebase.module.etl.core.dal.database.EtlCatalogRepository;
import com.cmsr.onebase.module.etl.core.dal.database.EtlSchemaRepository;
import com.cmsr.onebase.module.etl.core.dal.database.EtlTableRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlCatalogDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlSchemaDO;
import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlTableDO;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MetadataManager {

    @Resource
    private EtlCatalogRepository catalogRepository;

    @Resource
    private EtlSchemaRepository schemaRepository;

    @Resource
    private EtlTableRepository tableRepository;

    public void saveMetadata(Long applicationId, String datasourceUuid, CatalogData catalogData) {
        EtlCatalogDO catalogDO = extractCatalogDO(applicationId, datasourceUuid, catalogData);
        catalogRepository.saveOrUpdate(catalogDO);
        String catalogUuid = catalogDO.getCatalogUuid();
        for (SchemaData schemaData : catalogData.getSchemas()) {
            EtlSchemaDO schemaDO = extractSchemaDO(applicationId, datasourceUuid, catalogUuid, schemaData);
            schemaRepository.saveOrUpdate(schemaDO);
            String schemaUuid = schemaDO.getSchemaUuid();
            for (TableData tableData : schemaData.getTables()) {
                EtlTableDO etlTableDO = extractTableDO(applicationId, datasourceUuid, catalogUuid, schemaUuid, tableData);
                tableRepository.saveOrUpdate(etlTableDO);
            }
            Set<String> collectedTableNames = schemaData.getTables().stream().map(TableData::getName).collect(Collectors.toSet());
            List<EtlTableDO> tableDOS = tableRepository.findAllByCatalogAndSchemaAndDatasource(datasourceUuid, catalogUuid, schemaUuid);
            for (EtlTableDO tableDO : tableDOS) {
                if (!collectedTableNames.contains(tableDO.getTableName())) {
                    tableRepository.removeById(tableDO);
                }
            }
        }
    }

    private EtlCatalogDO extractCatalogDO(Long applicationId, String datasourceUuid, CatalogData catalogData) {
        EtlCatalogDO catalogDO = catalogRepository.findCatalogByDatasource(applicationId, datasourceUuid);
        if (catalogDO == null) {
            catalogDO = new EtlCatalogDO();
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

    private EtlSchemaDO extractSchemaDO(Long applicationId, String datasourceUuid, String catalogUuid, SchemaData schemaData) {

        EtlSchemaDO schemaDO = schemaRepository.findByDatasourceAndCatalog(applicationId, datasourceUuid, catalogUuid);
        if (schemaDO == null) {
            schemaDO = new EtlSchemaDO();
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

    private EtlTableDO extractTableDO(Long applicationId, String datasourceUuid, String catalogUuid, String schemaUuid, TableData tableData) {
        String tableName = tableData.getName();
        EtlTableDO tableDO = tableRepository.findOneByQualifiedName(applicationId, datasourceUuid, catalogUuid, schemaUuid, tableName);
        if (tableDO == null) {
            tableDO = new EtlTableDO();
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
        if (StringUtils.isNotBlank(comment)) {
            tableDO.setDisplayName(comment);
        }

        return tableDO;
    }
}
