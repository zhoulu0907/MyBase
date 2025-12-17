package com.cmsr.onebase.module.metadata.core.semantic.strategy.validation.impl;

import com.cmsr.onebase.module.metadata.core.dal.database.MetadataValidationChildNotEmptyRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.validation.MetadataValidationChildNotEmptyDO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntitySchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntityValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRelationSchemaDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticConnectorCardinalityEnum;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.validation.SemanticValidationContext;
import com.cmsr.onebase.module.metadata.core.semantic.strategy.validation.SemanticValidationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("semanticChildNotEmptyValidationService")
public class SemanticChildNotEmptyValidationService implements SemanticValidationService {

    @Resource
    private MetadataValidationChildNotEmptyRepository childNotEmptyRepository;

    @Override
    public void validateEntity(List<SemanticFieldSchemaDTO> fields, Map<String, Object> data, SemanticDataMethodOpEnum operationType, SemanticValidationContext context) {
        return;
    }

    public void validateChildEntities(SemanticEntitySchemaDTO entity, SemanticEntityValueDTO value) {
        if (entity == null || value == null) { return; }
        List<MetadataValidationChildNotEmptyDO> rules = childNotEmptyRepository.list(
                childNotEmptyRepository.query()
                        .eq(MetadataValidationChildNotEmptyDO::getEntityUuid, entity.getEntityUuid())
                        .eq(MetadataValidationChildNotEmptyDO::getIsEnabled, 1)
        );
        if (rules == null || rules.isEmpty()) { return; }
        for (MetadataValidationChildNotEmptyDO rule : rules) {
            String childUuid = rule.getChildEntityUuid();
            if (childUuid == null || entity.getConnectors() == null) { continue; }
            SemanticRelationSchemaDTO matched = null;
            for (SemanticRelationSchemaDTO c : entity.getConnectors()) {
                if (c != null && childUuid.equals(c.getTargetEntityUuid())) { matched = c; break; }
            }
            if (matched == null) { continue; }
            int minRows = rule.getMinRows() == null ? 1 : rule.getMinRows();
            int count = 0;
            if (matched.getCardinality() == SemanticConnectorCardinalityEnum.ONE) {
                Map<String, Object> row = value.getConnectorRawObject(matched.getTargetEntityTableName());
                count = row == null ? 0 : 1;
            } else if (matched.getCardinality() == SemanticConnectorCardinalityEnum.MANY) {
                List<Map<String, Object>> list = value.getConnectorRawList(matched.getTargetEntityTableName());
                count = list == null ? 0 : list.size();
            }
            if (count < minRows) {
                String name = matched.getTargetEntityTableName();
                String msg = rule.getPromptMessage();
                if (msg == null || msg.trim().isEmpty()) { msg = "子表[" + (name == null ? childUuid : name) + "]数据行不能为空"; }
                throw new IllegalArgumentException(msg);
            }
        }
    }

    @Override
    public String getValidationType() { return "CHILD_NOT_EMPTY"; }

    @Override
    public boolean supports(String fieldType) { return true; }
}
