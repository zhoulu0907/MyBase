package com.cmsr.onebase.module.metadata.core.semantic.type;

import lombok.Data;
import java.util.Map;

@Data
public class DeptRefType extends RefType {
    private String deptNo;
    private Long deptLevel;

    @Override
    protected void fillFromMap(Map<String, Object> map) {
        if (map == null) return;
        Object id = map.get("id");
        Object name = map.get("name");
        if (id != null) setId(String.valueOf(id));
        if (name != null) setName(String.valueOf(name));
        Object no = map.containsKey("deptNo") ? map.get("deptNo")
                : map.containsKey("dept_no") ? map.get("dept_no")
                : map.containsKey("departmentNo") ? map.get("departmentNo")
                : map.get("department_no");
        if (no != null) setDeptNo(String.valueOf(RefType.convertValue(String.class, no)));
        Object lvl = map.containsKey("deptLevel") ? map.get("deptLevel")
                : map.containsKey("dept_level") ? map.get("dept_level")
                : map.get("level");
        if (lvl != null) setDeptLevel((Long) RefType.convertValue(Long.class, lvl));
    }
}
