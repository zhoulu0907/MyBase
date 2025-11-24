package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.mybatis.BaseRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLSchemaDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.ETLSchemaMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ETLSchemaRepository extends BaseRepository<ETLSchemaMapper, ETLSchemaDO> {

    // 优化方法名：更简洁但保持语义清晰
    public ETLSchemaDO findOneByQualifiedName(Long applicationId, Long datasourceId, Long catalogId, String name) {
        QueryWrapper queryWrapper = query()
                .eq(ETLSchemaDO::getApplicationId, applicationId)
                .eq(ETLSchemaDO::getDatasourceId, datasourceId)
                .eq(ETLSchemaDO::getCatalogId, catalogId)
                .eq(ETLSchemaDO::getSchemaName, name);
        return getOne(queryWrapper);
    }

    public void deleteAllByDatasourceId(Long datasourceId) {
        QueryWrapper queryWrapper = query().eq(ETLSchemaDO::getDatasourceId, datasourceId);
        remove(queryWrapper);
    }

    public ETLSchemaDO upsert(ETLSchemaDO schemaDO) {
        if (schemaDO == null) return null;
        Long applicationId = schemaDO.getApplicationId();
        Long datasourceId = schemaDO.getDatasourceId();
        Long catalogId = schemaDO.getCatalogId();
        String schemaName = schemaDO.getSchemaName();
        // 调用优化后的方法名
        ETLSchemaDO oldSchema = findOneByQualifiedName(applicationId, datasourceId, catalogId, schemaName);
        if (oldSchema != null) {
            schemaDO.setId(oldSchema.getId());
        }
        saveOrUpdate(schemaDO);
        return schemaDO;
    }
}
