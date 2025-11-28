package com.cmsr.onebase.module.metadata.runtime.semantic.strategy;

import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.domain.query.ProcessContext;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.dal.database.TemporaryDatasourceService;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataDatasourceCoreService;
import com.cmsr.onebase.module.metadata.core.service.datamethod.strategy.FieldValueTransformMode;
import jakarta.annotation.Resource;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.DataRow;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.DATASOURCE_NOT_EXISTS;

@Component
public class DataStoreExecutor {
    @Resource
    private MetadataDatasourceCoreService metadataDatasourceCoreService;
    @Resource
    private TemporaryDatasourceService temporaryDatasourceService;
    @Resource
    private TableNameQuoter tableNameQuoter;
    @Resource
    private SubEntityProcessor subEntityProcessor;
    @Resource
    private com.cmsr.onebase.module.metadata.core.service.datamethod.strategy.FieldValueStorageStrategyFactory fieldValueStorageStrategyFactory;

    public void execute(ProcessContext context) {
        MetadataBusinessEntityDO entity = context.getEntity();
        List<MetadataEntityFieldDO> fields = context.getFields();
        AnylineService<?> temporaryService = context.getTemporaryService();
        MetadataDataMethodOpEnum op = context.getOperationType();
        if (temporaryService == null) {
            MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entity.getDatasourceId());
            if (datasource == null) { throw exception(DATASOURCE_NOT_EXISTS); }
            AnylineService<?> created = temporaryDatasourceService.createTemporaryService(datasource);
            context.setTemporaryService(created);
            temporaryService = created;
        }
        final AnylineService<?> service = temporaryService;
        TenantUtils.executeIgnore(() -> {
            if (op == MetadataDataMethodOpEnum.CREATE) {
                applyFieldStorageStrategies(context.getProcessedData(), fields, FieldValueTransformMode.STORE, null);
                Object insertResult = service.insert(tableNameQuoter.quote(entity.getTableName()), new DataRow(context.getProcessedData()));
                Object pk = getPrimaryKeyValue(context.getProcessedData(), fields);
                if (pk == null && insertResult != null) { pk = insertResult; }
                if (pk != null) { context.setId(pk); applyFieldStorageStrategies(context.getProcessedData(), fields, FieldValueTransformMode.STORE, context); }
                subEntityProcessor.process(service, context.getSubEntities(), op);
            } else if (op == MetadataDataMethodOpEnum.UPDATE) {
                applyFieldStorageStrategies(context.getProcessedData(), fields, FieldValueTransformMode.STORE, context);
                String pkField = getPrimaryKeyFieldName(fields);
                var cs = new DefaultConfigStore();
                cs.and(pkField, context.getId());
                service.update(tableNameQuoter.quote(entity.getTableName()), new DataRow(context.getProcessedData()), cs);
            } else if (op == MetadataDataMethodOpEnum.DELETE) {
                String pkField = getPrimaryKeyFieldName(fields);
                var cs = new DefaultConfigStore();
                cs.and(pkField, context.getId());
                boolean hasDeleted = fields.stream().anyMatch(f -> "deleted".equalsIgnoreCase(f.getFieldName()));
                if (hasDeleted) {
                    DataRow row = new DataRow();
                    row.put("deleted", 1);
                    service.update(tableNameQuoter.quote(entity.getTableName()), row, cs);
                } else {
                    service.delete(tableNameQuoter.quote(entity.getTableName()), cs);
                }
            }
            return null;
        });
    }

    private String getPrimaryKeyFieldName(List<MetadataEntityFieldDO> fields) {
        java.util.List<MetadataEntityFieldDO> pkCandidates = fields.stream()
                .filter(field -> com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum.isYes(field.getIsPrimaryKey()))
                .filter(field -> !com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum.isYes(field.getIsSystemField()))
                .toList();
        java.util.Optional<String> idNamed = pkCandidates.stream()
                .map(MetadataEntityFieldDO::getFieldName)
                .filter(java.util.Objects::nonNull)
                .filter(name -> "id".equalsIgnoreCase(name))
                .findFirst();
        if (idNamed.isPresent()) { return idNamed.get(); }
        java.util.Optional<String> firstPk = pkCandidates.stream()
                .map(MetadataEntityFieldDO::getFieldName)
                .filter(java.util.Objects::nonNull)
                .findFirst();
        if (firstPk.isPresent()) { return firstPk.get(); }
        boolean hasId = fields.stream().map(MetadataEntityFieldDO::getFieldName)
                .anyMatch(name -> name != null && "id".equalsIgnoreCase(name));
        if (hasId) { return "id"; }
        return "id";
    }

    private Object getPrimaryKeyValue(java.util.Map<String, Object> data, java.util.List<MetadataEntityFieldDO> fields) {
        String primaryKeyField = getPrimaryKeyFieldName(fields);
        return data.get(primaryKeyField);
    }

    private void applyFieldStorageStrategies(java.util.Map<String, Object> data, java.util.List<MetadataEntityFieldDO> fields,
                                             FieldValueTransformMode mode, ProcessContext context) {
        if (data == null || fields == null || fields.isEmpty()) { return; }
        for (MetadataEntityFieldDO field : fields) {
            String fieldName = field.getFieldName();
            if (!data.containsKey(fieldName)) { continue; }
            Object val = data.get(fieldName);
            var strategy = fieldValueStorageStrategyFactory.getStrategy(field.getFieldType());
            Object transformed = strategy.transform(val, mode, context, field);
            data.put(fieldName, transformed);
        }
    }
}

