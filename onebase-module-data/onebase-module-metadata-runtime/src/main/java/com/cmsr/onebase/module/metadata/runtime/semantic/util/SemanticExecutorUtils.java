package com.cmsr.onebase.module.metadata.runtime.semantic.util;

import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticRecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.SemanticValueDTO;

import java.util.HashMap;
import java.util.Map;

public final class SemanticExecutorUtils {
    private SemanticExecutorUtils() {}

    public static String methodCodeOf(SemanticRecordDTO record) {
        return record.getContext().getMethodCode().name();
    }

    public static Long idOf(SemanticRecordDTO record) {
        var v = record.getValue().getData().get("id");
        Object id = v.getValue();
        return Long.valueOf(String.valueOf(id));
    }

    public static Map<String, Object> nameValueMapOf(SemanticRecordDTO record) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, SemanticValueDTO> e : record.getValue().getData().entrySet()) {
            Object val = e.getValue().getValue();
            result.put(e.getKey(), val);
        }
        return result;
    }
}
