package com.cmsr.onebase.module.metadata.runtime.semantic.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
    private SemanticFieldTypeEnum fieldTypeEnum;

    @Schema(description = "字段 ID")
    private Long fieldId;

    @Schema(description = "字段 UUID")
    private String fieldUuid;

    @Schema(description = "字段名称")
    private String fieldName;

    @Schema(description = "字段名称")
    private String tableName;

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
            String[] parts = s.split(",");
            for (String p : parts) { result.add(castValue(p.trim(), elementType)); }
            return result;
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
            if (raw instanceof RefType r) return r.getId();
            return raw;
        }
        return rawValue;
    }

    private String extractStoreScalar(Object o) {
        if (o == null) return null;
        if (o instanceof RefType r) return r.getId();
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
}
