package com.cmsr.onebase.module.metadata.runtime.semantic.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.cmsr.onebase.framework.common.util.json.JsonUtils;

import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticFieldTypeEnum;
import com.cmsr.onebase.module.metadata.runtime.semantic.type.RefType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字段值 DTO
 *
 * <p>
 * 统一封装语义层字段的值与类型信息，用于运行期在不同层之间传递。
 * 提供：
 * - 原始值访问（rawValue）
 * - 业务类型（bizJavaType）与原始类型（rawJavaType）暴露
 * - 单值与列表值的安全类型转换（getValueAs / getValueAsList / getValueAsBizType / getValueAsBizList）
 * - 列表类型判定（isListType）
 * </p>
 */
@Schema(description = "字段值 DTO")
@Data
public class SemanticFieldValueDTO<T> {
    @Schema(description = "字段原始值")
    private T rawValue;

    @Schema(description = "字段类型枚举")
    private final SemanticFieldTypeEnum fieldTypeEnum;

    @Schema(description = "字段 ID")
    private Long fieldId;

    @Schema(description = "字段 UUID")
    private String fieldUuid;

    @Schema(description = "字段名称")
    private String fieldName;

    @Schema(description = "字段名称")
    private String tableName;

    @JsonIgnore
    public T getRawValue() {
        return rawValue;
    }

    private SemanticFieldValueDTO(SemanticFieldTypeEnum fieldTypeEnum) {
        this.fieldTypeEnum = Objects.requireNonNull(fieldTypeEnum, "fieldTypeEnum 不能为空");
    }

    @JsonGetter("rawValue")
    public Object getRawValueForJson() {
        Object raw = rawValue;
        if (fieldTypeEnum != null && fieldTypeEnum.isListType()) {
            if (raw == null) return null;
            if (raw instanceof List<?> list) {
                List<Object> out = new ArrayList<>(list.size());
                for (Object o : list) {
                    out.add(formatScalarForJson(o));
                }
                return out;
            }
            return formatScalarForJson(raw);
        }
        return formatScalarForJson(raw);
    }

    private Object formatScalarForJson(Object v) {
        if (v == null) return null;
        if (v instanceof LocalDate d) return d.toString();
        if (v instanceof LocalDateTime dt) return dt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        if (v instanceof RefType) return v;
        return v;
    }

    /**
     * 按指定类型返回值（非列表）
     *
     * <p>优先直接类型匹配，否则尝试通过 BigDecimal 中转进行数值转换，支持 String → 数值。</p>
     */
    public <R> R getValueAs(Class<R> type) {
        if (type == null) return null;
        Object raw = rawValue;
        return castValue(raw, type);
    }

    /**
     * 内部类型转换：统一走 BigDecimal 中转以减少分支冗余
     */
    private <R> R castValue(Object raw, Class<R> type) {
        if (type == null) return null;
        if (raw == null) return null;
        if (type.isInstance(raw)) return type.cast(raw);
        BigDecimal big = toBigDecimal(raw);
        if (big != null) {
            if (type == Integer.class) return type.cast(big.intValue());
            if (type == Long.class) return type.cast(big.longValue());
            if (type == Double.class) return type.cast(big.doubleValue());
            if (type == Float.class) return type.cast(big.floatValue());
            if (type == Short.class) return type.cast(big.shortValue());
            if (type == Byte.class) return type.cast(big.byteValue());
            if (type == BigDecimal.class) return type.cast(big);
        }
        return null;
    }

    /**
     * 尝试将输入转换为 BigDecimal（支持 Number 与可解析的 String）
     */
    private BigDecimal toBigDecimal(Object raw) {
        if (raw == null) return null;
        if (raw instanceof BigDecimal b) return b;
        if (raw instanceof Number n) return new BigDecimal(n.toString());
        if (raw instanceof String s) {
            try { return new BigDecimal(s.trim()); } catch (Exception ignore) {}
        }
        return null;
    }

    /**
     * 列表类型转换：将原始值转换为 List 元素（支持 List/Collection/数组/逗号分隔字符串）
     */
    public <E> List<E> getValueAsList(Class<E> elementType) {
        if (elementType == null) return null;
        Object raw = rawValue;
        if (raw == null) return null;
        List<E> result = new ArrayList<>();
        if (raw instanceof List<?> list) {
            for (Object o : list) { result.add(castValue(o, elementType)); }
            return result;
        }
        if (raw instanceof Collection<?> col) {
            for (Object o : col) { result.add(castValue(o, elementType)); }
            return result;
        }
        if (raw instanceof Object[] arr) {
            for (Object o : Arrays.asList(arr)) { result.add(castValue(o, elementType)); }
            return result;
        }
        if (raw instanceof String s) {
            if (JsonUtils.isJson(s)) {
                if (JsonUtils.isJsonObject(s)) {
                    E obj = JsonUtils.parseObject(s, new TypeReference<E>(){});
                    result.add(obj);
                } else {
                    result = JsonUtils.parseObject(s, new TypeReference<List<E>>(){});
                }
                return result;
            } else {
                String[] parts = s.split(",");
                for (String p : parts) { result.add(castValue(p.trim(), elementType)); }
                return result;
            }
          
        }
        return null;
    }

    /**
     * 获取原始 Java 类型（来自枚举 rawJavaType）
     */
    public Class<?> getRawJavaType() {
        return fieldTypeEnum == null ? null : fieldTypeEnum.getRawJavaType();
    }

    /**
     * 获取业务 Java 类型（来自枚举 bizJavaType）
     */
    public Class<?> getBizJavaType() {
        return fieldTypeEnum == null ? null : fieldTypeEnum.getBizJavaType();
    }

    /**
     * 是否为列表类型（来自枚举 listType）
     */
    public boolean isListType() {
        return fieldTypeEnum != null && fieldTypeEnum.isListType();
    }

    /**
     * 按业务类型返回单值（无法转换时返回原始值）
     */
    public Object getValueAsBizType() {
        Class<?> type = getBizJavaType();
        if (type == null) return rawValue;
        Object val = getValueAs(type);
        return val == null ? rawValue : val;
    }

    /**
     * 按业务类型返回列表值
     */
    public List<?> getValueAsBizList() {
        Class<?> type = getBizJavaType();
        if (type == null) return null;
        return getValueAsList(type);
    }

    public Object getStoreValue() {
        if (fieldTypeEnum == null) return rawValue;
        if (fieldTypeEnum.isListType()) {
            List<String> storeVals = new ArrayList<>();
            Object raw = rawValue;
            if (raw instanceof List<?> list) {
                for (Object o : list) storeVals.add(extractStoreScalar(o));
            } else if (raw instanceof Collection<?> col) {
                for (Object o : col) storeVals.add(extractStoreScalar(o));
            } else if (raw instanceof Object[] arr) {
                for (Object o : Arrays.asList(arr)) storeVals.add(extractStoreScalar(o));
            } else if (raw instanceof String s) {
                String[] parts = s.split(",");
                for (String p : parts) storeVals.add(p.trim());
            } else if (raw != null) {
                storeVals.add(String.valueOf(raw));
            }
            return toJsonStringArray(storeVals);
        }
        if (fieldTypeEnum.isRefType()) {
            Object raw = rawValue;
            if (raw instanceof RefType r) return r.getStoreData();
            return raw;
        }
        return rawValue;
    }

    private String extractStoreScalar(Object o) {
        if (o == null) return null;
        if (o instanceof RefType r) return String.valueOf(r.getStoreData());
        return String.valueOf(o);
    }

    private String toJsonStringArray(List<String> values) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < values.size(); i++) {
            String v = values.get(i);
            if (v == null) {
                sb.append("null");
            } else {
                sb.append('"').append(escapeJson(v)).append('"');
            }
            if (i < values.size() - 1) sb.append(',');
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public static <T> SemanticFieldValueDTO<T> ofType(SemanticFieldTypeEnum fieldTypeEnum) {
        return new SemanticFieldValueDTO<>(fieldTypeEnum);
    }

    public void setRawValue(T rawValue) {
        if (this.fieldTypeEnum == null) {
            throw new IllegalStateException("fieldTypeEnum 未设置");
        }
        Object normalized = normalizeValue(rawValue, this.fieldTypeEnum);
        T casted = (T) normalized;
        this.rawValue = casted;
    }

    private Object normalizeValue(Object value, SemanticFieldTypeEnum type) {
        if (value == null) return null;
        if (type.isListType()) {
            return normalizeList(value, type);
        }
        return normalizeScalar(value, type);
    }

    private List<?> normalizeList(Object value, SemanticFieldTypeEnum type) {
        List<Object> items = new ArrayList<>();
        if (value instanceof List<?> list) {
            for (Object o : list) items.add(normalizeScalar(o, type));
        } else if (value instanceof Collection<?> col) {
            for (Object o : col) items.add(normalizeScalar(o, type));
        } else if (value instanceof Object[] arr) {
            for (Object o : Arrays.asList(arr)) items.add(normalizeScalar(o, type));
        } else if (value instanceof String s) {
            String str = s.trim();
            if (JsonUtils.isJson(str)) {
                if (JsonUtils.isJsonObject(str)) {
                    Map<String, Object> obj = JsonUtils.parseObject(str, new TypeReference<Map<String, Object>>(){});
                    if (obj != null) items.add(normalizeScalar(obj, type));
                } else {
                    List<Object> arrList = JsonUtils.parseObject(str, new TypeReference<List<Object>>(){});
                    if (arrList != null) {
                        for (Object o : arrList) items.add(normalizeScalar(o, type));
                    }
                }
            } else {
                String[] parts = str.split(",");
                for (String p : parts) items.add(normalizeScalar(p.trim(), type));
            }
        } else {
            items.add(normalizeScalar(value, type));
        }
        return items;
    }

    private Object normalizeScalar(Object value, SemanticFieldTypeEnum type) {
        if (type.isRefType()) {
            Class<?> biz = type.getBizJavaType();
            if (biz.isInstance(value)) return value;
            if (value instanceof Map<?,?> m) {
                Map<String, Object> map = (Map<String, Object>) m;
                Object ref = RefType.fromMap(map, (Class<? extends RefType>) biz);
                if (ref == null) throw err("RefType 转换失败", biz);
                return ref;
            }
            if (value instanceof Number || value instanceof String) {
                Object ref = RefType.fromId((Class<? extends RefType>) biz, value);
                if (ref == null) throw err("RefType 构造失败", biz);
                return ref;
            }
            throw err("非法的引用类型值", biz);
        }
        Class<?> rawType = type.getRawJavaType();
        if (rawType == String.class) {
            String s = value instanceof String ? ((String) value).trim() : String.valueOf(value);
            if (type == SemanticFieldTypeEnum.EMAIL) {
                if (!isValidEmail(s)) throw err("邮箱格式不正确", String.class);
            } else if (type == SemanticFieldTypeEnum.PHONE) {
                if (!isValidPhone(s)) throw err("电话号码格式不正确", String.class);
            } else if (type == SemanticFieldTypeEnum.GEOGRAPHY) {
                s = normalizeGeographyString(value);
            }
            return s;
        }
        if (rawType == BigDecimal.class) {
            BigDecimal b = toBigDecimal(value);
            if (b == null) throw err("数值类型转换失败", BigDecimal.class);
            return b;
        }
        if (rawType == Long.class) {
            BigDecimal b = toBigDecimal(value);
            if (b == null) throw err("Long 类型转换失败", Long.class);
            return b.longValue();
        }
        if (rawType == Integer.class) {
            BigDecimal b = toBigDecimal(value);
            if (b == null) throw err("Integer 类型转换失败", Integer.class);
            return b.intValue();
        }
        if (rawType == Double.class) {
            BigDecimal b = toBigDecimal(value);
            if (b == null) throw err("Double 类型转换失败", Double.class);
            return b.doubleValue();
        }
        if (rawType == Float.class) {
            BigDecimal b = toBigDecimal(value);
            if (b == null) throw err("Float 类型转换失败", Float.class);
            return b.floatValue();
        }
        if (rawType == Short.class) {
            BigDecimal b = toBigDecimal(value);
            if (b == null) throw err("Short 类型转换失败", Short.class);
            return b.shortValue();
        }
        if (rawType == Byte.class) {
            BigDecimal b = toBigDecimal(value);
            if (b == null) throw err("Byte 类型转换失败", Byte.class);
            return b.byteValue();
        }
        if (rawType == LocalDate.class) {
            if (value instanceof LocalDate) return value;
            if (value instanceof String s) {
                LocalDate d = parseLocalDate(s.trim());
                if (d == null) throw err("LocalDate 解析失败", LocalDate.class);
                return d;
            }
            if (value instanceof Date d) {
                if (d instanceof java.sql.Date sd) {
                    return sd.toLocalDate();
                }
                return Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            }
            throw err("非法的 LocalDate 值", LocalDate.class);
        }
        if (rawType == LocalDateTime.class) {
            if (value instanceof LocalDateTime) return value;
            if (value instanceof String s) {
                LocalDateTime dt = parseLocalDateTime(s.trim());
                if (dt == null) throw err("LocalDateTime 解析失败", LocalDateTime.class);
                return dt;
            }
            if (value instanceof Number n) {
                long epoch = n.longValue();
                if (String.valueOf(epoch).length() <= 10) epoch *= 1000;
                Instant inst = Instant.ofEpochMilli(epoch);
                return LocalDateTime.ofInstant(inst, ZoneId.systemDefault());
            }
            if (value instanceof Date d) {
                if (d instanceof java.sql.Timestamp ts) {
                    return ts.toLocalDateTime();
                }
                if (d instanceof java.sql.Date sd) {
                    return sd.toLocalDate().atStartOfDay();
                }
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(d.getTime()), ZoneId.systemDefault());
            }
            throw err("非法的 LocalDateTime 值", LocalDateTime.class);
        }
        if (rawType == Boolean.class) {
            if (value instanceof Boolean) return value;
            if (value instanceof String s) {
                String t = s.trim().toLowerCase();
                if ("true".equals(t) || "1".equals(t) || "yes".equals(t) || "y".equals(t) || "on".equals(t) || "t".equals(t)) return Boolean.TRUE;
                if ("false".equals(t) || "0".equals(t) || "no".equals(t) || "n".equals(t) || "off".equals(t) || "f".equals(t)) return Boolean.FALSE;
            }
            throw err("布尔类型转换失败", Boolean.class);
        }
        if (rawType.isInstance(value)) return value;
        throw err("不支持的原始类型", rawType);
    }

    private boolean isValidEmail(String s) {
        if (s == null || s.isEmpty()) return false;
        return Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$").matcher(s).matches();
    }

    private boolean isValidPhone(String s) {
        if (s == null || s.isEmpty()) return false;
        return Pattern.compile("^\\+?[0-9\\-]{5,20}$").matcher(s).matches();
    }

    private String normalizeGeographyString(Object value) {
        if (value instanceof String str) {
            String s = str.trim();
            return s;
        }
        if (value instanceof Map<?,?> m) {
            Object lat = m.get("lat");
            Object lng = m.get("lng");
            if (lat == null || lng == null) throw err("地理位置缺少坐标", String.class);
            return String.valueOf(lat) + "," + String.valueOf(lng);
        }
        throw err("非法的地理位置值", String.class);
    }

    private LocalDate parseLocalDate(String s) {
        try { return LocalDate.parse(s); } catch (Exception ignore) {}
        String[] fmts = {"yyyy-MM-dd","yyyy/MM/dd","yyyyMMdd"};
        for (String f : fmts) {
            try { return LocalDate.parse(s, DateTimeFormatter.ofPattern(f)); } catch (Exception ignore) {}
        }
        return null;
    }

    private LocalDateTime parseLocalDateTime(String s) {
        try { return LocalDateTime.parse(s); } catch (Exception ignore) {}
        String[] fmts = {"yyyy-MM-dd HH:mm:ss","yyyy/MM/dd HH:mm:ss","yyyyMMddHHmmss","yyyy-MM-dd'T'HH:mm:ss"};
        for (String f : fmts) {
            try { return LocalDateTime.parse(s, DateTimeFormatter.ofPattern(f)); } catch (Exception ignore) {}
        }
        try {
            long epoch = Long.parseLong(s);
            if (String.valueOf(epoch).length() <= 10) epoch *= 1000;
            Instant inst = Instant.ofEpochMilli(epoch);
            return LocalDateTime.ofInstant(inst, ZoneId.systemDefault());
        } catch (Exception ignore) {}
        return null;
    }

    private IllegalArgumentException err(String base, Class<?> expected) {
        String code = fieldTypeEnum == null ? null : fieldTypeEnum.getCode();
        String name = fieldTypeEnum == null ? null : fieldTypeEnum.getName();
        String exp = expected == null ? null : expected.getSimpleName();
        String fn = fieldName;
        String msg = base + " [field=" + fn + ", type=" + code + ", typeName=" + name + (exp == null ? "" : ", expected=" + exp) + "]";
        return new IllegalArgumentException(msg);
    }
}
