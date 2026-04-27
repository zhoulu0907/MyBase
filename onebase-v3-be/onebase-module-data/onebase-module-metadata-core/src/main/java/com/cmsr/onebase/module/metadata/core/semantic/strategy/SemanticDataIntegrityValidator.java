package com.cmsr.onebase.module.metadata.core.semantic.strategy;

import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldValueDTO;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.cmsr.onebase.module.metadata.core.enums.ErrorCodeConstants.BUSINESS_ENTITY_NOT_EXISTS;

@Component
public class SemanticDataIntegrityValidator {

    public void validate(SemanticRecordDTO record) {
        SemanticDataMethodOpEnum op = record.getRecordContext().getOperationType();
        
        // if (requiresId(op)) { validateId(record); }
        // if (op == MetadataDataMethodOpEnum.CREATE) { validateCreateData(record); }
        
    }

    private boolean requiresId(SemanticDataMethodOpEnum op) {
        return op == SemanticDataMethodOpEnum.GET;
    }

    private void validateId(SemanticRecordDTO record) {
        Long id = idOf(record);
        if (id == null) { throw exception(BUSINESS_ENTITY_NOT_EXISTS, "缺少主键id"); }
    }

    private void validateCreateData(SemanticRecordDTO record) {
        // Map<String, Object> data = nameValueMapOf(record);
        // if (data == null || data.isEmpty()) { throw exception(DATA_METHOD_EXEC_FAIL, "数据不能为空"); }
    }


    private Long idOf(SemanticRecordDTO record) {
        if (record == null || record.getEntityValue() == null) { return null; }
        Map<String, SemanticFieldValueDTO<Object>> map = record.getEntityValue().getFieldValueMap();
        if (map == null) { return null; }
        SemanticFieldValueDTO<Object> v = map.get("id");
        if (v == null) { return null; }
        Object id = v.getRawValue();
        if (id == null) { return null; }
        try { return Long.valueOf(String.valueOf(id)); } catch (Exception ignore) { return null; }
    }

    private Map<String, Object> nameValueMapOf(SemanticRecordDTO record) {
        Map<String, Object> result = new HashMap<>();
        if (record == null || record.getEntityValue() == null) { return result; }
        Map<String, SemanticFieldValueDTO<Object>> map = record.getEntityValue().getFieldValueMap();
        if (map == null) { return result; }
        for (Map.Entry<String, SemanticFieldValueDTO<Object>> e : map.entrySet()) {
            Object val = e.getValue() == null ? null : e.getValue().getRawValue();
            result.put(e.getKey(), val);
        }
        return result;
    }
}
