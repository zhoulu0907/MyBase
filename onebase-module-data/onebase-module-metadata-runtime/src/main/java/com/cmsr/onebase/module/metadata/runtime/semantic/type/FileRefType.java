package com.cmsr.onebase.module.metadata.runtime.semantic.type;

import lombok.Data;
import java.util.Map;

@Data
public class FileRefType extends RefType {
    @Override
    protected void fillFromMap(Map<String, Object> map) {
        if (map == null) return;
        Object id = map.get("id");
        Object name = map.get("name");
        if (id != null) setId(String.valueOf(id));
        if (name != null) setName(String.valueOf(name));
    }
}
