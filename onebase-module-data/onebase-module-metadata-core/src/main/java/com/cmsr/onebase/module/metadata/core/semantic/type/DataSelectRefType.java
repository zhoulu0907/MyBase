package com.cmsr.onebase.module.metadata.core.semantic.type;

public class DataSelectRefType extends RefType {
    @Override
    protected void fillFromMap(java.util.Map<String, Object> map) {
        if (map == null) return;
        Object id = map.get("id");
        Object name = map.get("name");
        if (id != null) setId(String.valueOf(id));
        if (name != null) setName(String.valueOf(name));
    }
}
