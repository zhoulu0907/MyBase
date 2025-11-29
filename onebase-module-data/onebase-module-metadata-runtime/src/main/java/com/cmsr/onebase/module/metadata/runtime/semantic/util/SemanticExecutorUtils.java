package com.cmsr.onebase.module.metadata.runtime.semantic.util;

import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticFieldValueDTO;

import java.util.HashMap;
import java.util.Map;

public final class SemanticExecutorUtils {
    private SemanticExecutorUtils() {}

    public static String methodCodeOf(SemanticRecordDTO record) {
        return record.getRecordContext().getMethodCode().name();
    }

    public static Long idOf(SemanticRecordDTO record) {
        var v = record.getEntityValue().getFieldValueMap().get("id");
        Object id = v.getRawValue();
        return Long.valueOf(String.valueOf(id));
    }

    public static Map<String, Object> nameValueMapOf(SemanticRecordDTO record) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, SemanticFieldValueDTO<Object>> e : record.getEntityValue().getFieldValueMap().entrySet()) {
            Object val = e.getValue().getRawValue();
            result.put(e.getKey(), val);
        }
        return result;
    }
}
