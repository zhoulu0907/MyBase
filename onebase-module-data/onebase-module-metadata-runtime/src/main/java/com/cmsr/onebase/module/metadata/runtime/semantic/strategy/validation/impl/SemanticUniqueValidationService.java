package com.cmsr.onebase.module.metadata.runtime.semantic.strategy.validation.impl;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.datasource.MetadataDatasourceDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataBusinessEntityDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationUniqueDO;
import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationUniqueRepository;
import com.cmsr.onebase.module.metadata.core.service.datasource.MetadataDatasourceCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataBusinessEntityCoreService;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.runtime.semantic.service.impl.SemanticTemporaryDatasourceService;
import com.cmsr.onebase.module.metadata.runtime.semantic.strategy.validation.SemanticValidationService;
import jakarta.annotation.Resource;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.DataSet;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.DATASOURCE_NOT_EXISTS;

@Component
public class SemanticUniqueValidationService implements SemanticValidationService {
    private final MetadataValidationUniqueRepository uniqueRepository;

    @Resource
    protected MetadataBusinessEntityCoreService metadataBusinessEntityCoreService;
    @Resource
    protected MetadataDatasourceCoreService metadataDatasourceCoreService;
    @Resource
    protected SemanticTemporaryDatasourceService semanticTemporaryDatasourceService;
    @Resource
    protected MetadataEntityFieldCoreService metadataEntityFieldCoreService;

    public SemanticUniqueValidationService(MetadataValidationUniqueRepository uniqueRepository) { this.uniqueRepository = uniqueRepository; }

    @Override
    public void validate(MetadataEntityFieldDO field, Object value, Map<String, Object> data) {
        if (value == null) { return; }
        List<MetadataValidationUniqueDO> rules = uniqueRepository.findByFieldId(field.getId());
        if (rules.isEmpty()) { return; }
        boolean hasEnabledRule = rules.stream().anyMatch(rule -> rule.getIsEnabled() != null && rule.getIsEnabled() == 1);
        if (!hasEnabledRule) { return; }
        for (MetadataValidationUniqueDO rule : rules) {
            if (rule.getIsEnabled() == null || rule.getIsEnabled() != 1) { continue; }
            boolean isExist = validateDateExistByField(field.getEntityId(), field, value, data);
            if (isExist) { throw new IllegalArgumentException("字段[" + field.getDisplayName() + "]值已存在"); }
        }
    }

    @Override
    public String getValidationType() { return "UNIQUE"; }

    @Override
    public boolean supports(String fieldType) { return true; }

    private boolean validateDateExistByField(Long entityId, MetadataEntityFieldDO field, Object value, Map<String, Object> data) {
        MetadataBusinessEntityDO entity = metadataBusinessEntityCoreService.getBusinessEntity(entityId);
        MetadataDatasourceDO datasource = metadataDatasourceCoreService.getDatasource(entity.getDatasourceId());
        if (datasource == null) { throw exception(DATASOURCE_NOT_EXISTS); }
        AnylineService<?> temporaryService = semanticTemporaryDatasourceService.createTemporaryService(datasource);
        ConfigStore configs = new DefaultConfigStore();
        configs.and(field.getFieldName(), value);
        List<MetadataEntityFieldDO> entityFields = metadataEntityFieldCoreService.getEntityFieldListByEntityId(entityId);
        boolean hasDeletedColumn = entityFields.stream().anyMatch(item -> "deleted".equalsIgnoreCase(item.getFieldName()));
        if (hasDeletedColumn) { configs.and("deleted", 0); }
        Optional<String> primaryKeyField = entityFields.stream()
                .filter(item -> item.getIsPrimaryKey() != null && item.getIsPrimaryKey() == 1)
                .map(MetadataEntityFieldDO::getFieldName)
                .findFirst();
        Object currentId = null;
        if (data != null) {
            if (primaryKeyField.isPresent()) { currentId = data.get(primaryKeyField.get()); }
            if (currentId == null) { currentId = data.get("id"); }
        }
        if (currentId != null) { configs.and(Compare.NOT_EQUAL, primaryKeyField.orElse("id"), currentId); }
        DataSet dataSet = temporaryService.querys(quoteTableName(entity.getTableName()), configs);
        return !dataSet.isEmpty();
    }

    private String quoteTableName(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) { return tableName; }
        if (tableName.startsWith("\"") && tableName.endsWith("\"")) { return tableName; }
        return "\"" + tableName + "\"";
    }
}
