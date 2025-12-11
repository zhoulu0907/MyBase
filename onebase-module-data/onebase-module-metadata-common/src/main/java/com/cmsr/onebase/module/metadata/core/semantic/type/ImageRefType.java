package com.cmsr.onebase.module.metadata.core.semantic.type;

import lombok.Data;

@Data
public class ImageRefType extends RefType {
    private String path;
    private String type;
    private Integer size;

    @Override
    protected void fillFromMap(java.util.Map<String, Object> map) {
        if (map == null) return;
        Object id = map.get("id");
        Object name = map.get("name");
        Object path = map.get("path");
        Object type = map.get("type");
        Object size = map.get("size");
        if (id != null) setId(String.valueOf(id));
        if (name != null) setName(String.valueOf(name));
        if (path != null) this.path = String.valueOf(path);
        if (type != null) this.type = String.valueOf(type);
        if (size != null) {
            try { this.size = Integer.valueOf(String.valueOf(size)); } catch (Exception ignore) {}
        }
    }
}
