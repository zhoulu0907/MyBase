package com.cmsr.onebase.module.metadata.core.semantic.type;

import lombok.Data;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.lang.reflect.Field;
import java.math.BigDecimal;

@Data
public abstract class RefType {
    private String id;
    private String name;

    @JsonIgnore
    public Object getStoreData() {
        return this.id;
    }

    public static <R extends RefType> R fromId(Class<R> clazz, Object id) {
        if (clazz == null || id == null) return null;
        try {
            R instance = clazz.getDeclaredConstructor().newInstance();
            instance.setId(String.valueOf(id));
            return instance;
        } catch (Exception e) {
            return null;
        }
    }

    public static <R extends RefType> R fromMap(Map<String, Object> map, Class<R> clazz) {
        if (clazz == null || map == null) return null;
        try {
            R instance = clazz.getDeclaredConstructor().newInstance();
            Object idVal = map.get("id");
            Object nameVal = map.get("name");
            if (idVal != null) instance.setId(String.valueOf(idVal));
            if (nameVal != null) instance.setName(Objects.toString(nameVal, null));
            instance.fillFromMap(map);
            return instance;
        } catch (Exception e) {
            return null;
        }
    }

    protected void fillFromMap(Map<String, Object> map) {
        Class<?> clazz = this.getClass();
        for (Field f : clazz.getDeclaredFields()) {
            String n = f.getName();
            if (map.containsKey(n)) {
                try {
                    f.setAccessible(true);
                    Object v = convertValue(f.getType(), map.get(n));
                    if (v != null) f.set(this, v);
                } catch (Exception ignore) {}
            }
        }
    }

    protected static Object convertValue(Class<?> type, Object v) {
        if (v == null) return null;
        if (type.isInstance(v)) return v;
        if (type == String.class) return String.valueOf(v);
        if (type == Long.class) {
            try { return new BigDecimal(String.valueOf(v)).longValue(); } catch (Exception e) { return null; }
        }
        if (type == Integer.class) {
            try { return new BigDecimal(String.valueOf(v)).intValue(); } catch (Exception e) { return null; }
        }
        if (type == BigDecimal.class) {
            try { return new BigDecimal(String.valueOf(v)); } catch (Exception e) { return null; }
        }
        if (type == Double.class) {
            try { return new BigDecimal(String.valueOf(v)).doubleValue(); } catch (Exception e) { return null; }
        }
        if (type == Float.class) {
            try { return new BigDecimal(String.valueOf(v)).floatValue(); } catch (Exception e) { return null; }
        }
        if (type == Short.class) {
            try { return new BigDecimal(String.valueOf(v)).shortValue(); } catch (Exception e) { return null; }
        }
        if (type == Byte.class) {
            try { return new BigDecimal(String.valueOf(v)).byteValue(); } catch (Exception e) { return null; }
        }
        if (type == Boolean.class) {
            String s = String.valueOf(v).trim().toLowerCase();
            if ("true".equals(s) || "1".equals(s) || "yes".equals(s) || "y".equals(s) || "on".equals(s) || "t".equals(s)) return Boolean.TRUE;
            if ("false".equals(s) || "0".equals(s) || "no".equals(s) || "n".equals(s) || "off".equals(s) || "f".equals(s)) return Boolean.FALSE;
            return null;
        }
        return null;
    }
}
