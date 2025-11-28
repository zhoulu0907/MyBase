package com.cmsr.onebase.module.metadata.runtime.semantic.strategy.validation;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.service.entity.MetadataEntityFieldCoreService;
import com.cmsr.onebase.module.metadata.core.service.number.AutoNumberService;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.RecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.ValueDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ValidationManager {

    private final List<ValidationService> validationServices;

    @Resource
    private AutoNumberService autoNumberService;

    @Resource
    private MetadataEntityFieldCoreService metadataEntityFieldCoreService;

    public ValidationManager(List<ValidationService> validationServices) { this.validationServices = validationServices; }


    public void validate(RecordDTO recordDTO) {
        Long entityId = recordDTO.getEntity().getId();
        List<MetadataEntityFieldDO> fields = metadataEntityFieldCoreService.getEntityFieldListByEntityId(entityId);
        Map<String, Object> data = extractData(recordDTO);
        MetadataDataMethodOpEnum operationType = recordDTO.getContext().getOperationType();
        validateEntity(fields, data, operationType);
    }

    public void validateField(MetadataEntityFieldDO field, Object value, Map<String, Object> data) {
        Long fieldId = field.getId();
        String fieldType = field.getFieldType();

        if (autoNumberService.hasAutoNumber(fieldId)) { return; }

        for (ValidationService service : validationServices) {
            if (service.supports(fieldType)) { service.validate(field, value, data); }
        }
    }

    public void validateEntity(List<MetadataEntityFieldDO> fields, Map<String, Object> data, MetadataDataMethodOpEnum operationType) {
        for (MetadataEntityFieldDO field : fields) {
            String fieldName = field.getFieldName();
            Object value = data.get(fieldName);
            if (field.getIsSystemField() != null && field.getIsSystemField() == 1) { continue; }
            if (field.getIsPrimaryKey() != null && field.getIsPrimaryKey() == 1) { continue; }
            if (operationType == MetadataDataMethodOpEnum.UPDATE && value == null) { continue; }
            validateField(field, value, data);
        }
    }

    private Map<String, Object> extractData(RecordDTO recordDTO) {
        Map<String, Object> result = new HashMap<>();
        Map<String, ValueDTO> data = recordDTO.getValue() != null ? recordDTO.getValue().getData() : null;
        if (data == null) { return result; }
        for (Map.Entry<String, ValueDTO> e : data.entrySet()) {
            result.put(e.getKey(), e.getValue() == null ? null : e.getValue().getValue());
        }
        return result;
    }
}
