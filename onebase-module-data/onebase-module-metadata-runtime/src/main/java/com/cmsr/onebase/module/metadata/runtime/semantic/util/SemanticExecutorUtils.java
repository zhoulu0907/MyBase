package com.cmsr.onebase.module.metadata.runtime.semantic.util;

import com.cmsr.onebase.module.metadata.runtime.semantic.dto.RecordDTO;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.ValueDTO;

import java.util.HashMap;
import java.util.Map;

public final class SemanticExecutorUtils {
    private SemanticExecutorUtils() {}

    public static String methodCodeOf(RecordDTO record) {
        return record.getContext().getMethodCode().name();
    }

    public static Long idOf(RecordDTO record) {
        var v = record.getValue().getData().get("id");
        Object id = v.getValue();
        return Long.valueOf(String.valueOf(id));
    }

    public static Map<String, Object> nameValueMapOf(RecordDTO record) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, ValueDTO> e : record.getValue().getData().entrySet()) {
            Object val = e.getValue().getValue();
            result.put(e.getKey(), val);
        }
        return result;
    }
}
