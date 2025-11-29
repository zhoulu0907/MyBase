package com.cmsr.onebase.module.metadata.runtime.semantic.type;

import lombok.Data;
import java.util.Map;

@Data
public class UserRefType extends RefType {
    private String userNo;

    @Override
    protected void fillFromMap(Map<String, Object> map) {
        if (map == null) return;
        Object id = map.get("id");
        Object name = map.get("name");
        if (id != null) setId(String.valueOf(id));
        if (name != null) setName(String.valueOf(name));
        Object v = map.containsKey("userNo") ? map.get("userNo")
                : map.containsKey("user_no") ? map.get("user_no")
                : map.containsKey("userId") ? map.get("userId")
                : map.get("user_id");
        if (v != null) setUserNo(String.valueOf(convertValue(String.class, v)));
    }
}
