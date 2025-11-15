package com.cmsr.onebase.module.metadata.core.service.datamethod.strategy;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 复杂对象字段存储策略，将对象列表转换为逗号分隔的 ID 列表
 *
 * @author matianyu
 * @date 2025-11-15
 */
@Component
public class ComplexObjectFieldValueStorageStrategy implements FieldValueStorageStrategy {

    private static final Set<String> COMPLEX_FIELD_TYPES = Set.of(
            "FILE",
            "IMAGE",
            "MULTI_SELECT",
            "MULTI_USER",
            "MULTI_DEPARTMENT",
            "MULTI_DATA_SELECTION",
            "SELECT",
            "DATA_SELECTION",
            "DEPARTMENT",
            "USER",
            "GEOGRAPHY",
            "ADDRESS"
    );

    private static final Set<String> COMPLEX_KEYWORDS = Set.of(
            "MULTI", "SELECT", "DATA", "USER", "DEPARTMENT", "FILE",
            "ATTACHMENT", "IMAGE", "STRUCTURE", "ARRAY", "RELATION", "GEO"
    );

    private static final String DEFAULT_ID_KEY = "id";
    private static final String FALLBACK_ID_KEY = "value";

    @Override
    public boolean supports(String fieldType) {
        if (!StringUtils.hasText(fieldType)) {
            return false;
        }
        String upperType = fieldType.toUpperCase();
        if (COMPLEX_FIELD_TYPES.contains(upperType)) {
            return true;
        }
        return COMPLEX_KEYWORDS.stream().anyMatch(upperType::contains);
    }

    @Override
    public Object transform(Object rawValue, FieldValueTransformMode mode) {
        if (mode == FieldValueTransformMode.READ) {
            return rawValue;
        }
        List<String> ids = extractIdentifiers(rawValue);
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        return String.join(",", ids);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private List<String> extractIdentifiers(Object rawValue) {
        if (rawValue == null) {
            return new ArrayList<>();
        }

        if (rawValue instanceof String stringValue) {
            if (!StringUtils.hasText(stringValue)) {
                return new ArrayList<>();
            }
            String trimmed = stringValue.trim();
            if (trimmed.startsWith("[")) {
                return parseJsonArray(trimmed);
            }
            if (trimmed.startsWith("{")) {
                return parseJsonObject(trimmed);
            }
            if (!trimmed.contains(",")) {
                return List.of(trimmed);
            }
            String[] segments = trimmed.split(",");
            return collectFromArray(segments);
        }

        if (rawValue instanceof Map<?, ?> mapValue) {
            String identifier = extractIdFromMap(mapValue);
            return StringUtils.hasText(identifier) ? List.of(identifier) : new ArrayList<>();
        }

        if (rawValue instanceof Collection<?> collection) {
            return collection.stream()
                    .map(this::extractIdentifierFromElement)
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .collect(Collectors.toList());
        }

        return List.of(rawValue.toString());
    }

    protected List<String> collectFromArray(String[] values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                result.add(value.trim());
            }
        }
        return result;
    }

    protected List<String> parseJsonArray(String json) {
        try {
            List<?> array = JsonUtils.parseArray(json, Object.class);
            if (CollectionUtils.isEmpty(array)) {
                return new ArrayList<>();
            }
            return array.stream()
                    .map(this::extractIdentifierFromElement)
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    protected List<String> parseJsonObject(String json) {
        try {
            Map<?, ?> map = JsonUtils.parseObject(json, Map.class);
            String identifier = extractIdFromMap(map);
            return StringUtils.hasText(identifier) ? List.of(identifier.trim()) : new ArrayList<>();
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    protected String extractIdentifierFromElement(Object element) {
        if (element == null) {
            return null;
        }
        if (element instanceof Map<?, ?> map) {
            return extractIdFromMap(map);
        }
        return element.toString();
    }

    protected String extractIdFromMap(Map<?, ?> map) {
        Object idValue = map.get(DEFAULT_ID_KEY);
        if (idValue == null) {
            idValue = map.get(FALLBACK_ID_KEY);
        }
        return idValue == null ? null : idValue.toString();
    }
}

