package com.cmsr.onebase.module.metadata.core.service.datamethod.strategy;

import com.cmsr.onebase.module.metadata.core.dal.database.MetadataEntityFieldOptionRepository;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import com.cmsr.onebase.module.metadata.core.dal.dataobject.field.MetadataEntityFieldOptionDO;
import com.cmsr.onebase.module.metadata.core.domain.query.ProcessContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 单选/多选列表字段值存储策略
 * <p>
 * 处理SELECT和MULTI_SELECT类型字段的存储和读取转换：
 * - 存储时：提取选项UUID，以逗号分隔存储
 * - 读取时：根据选项UUID查询metadata_entity_field_option表，返回包含UUID和对应标签的对象
 *
 * @author bty418
 * @date 2026-02-10
 */
@Slf4j
@Component
public class SelectFieldValueStorageStrategy implements FieldValueStorageStrategy {

    private static final Set<String> SELECT_FIELD_TYPES = Set.of("SELECT", "MULTI_SELECT");

    @Resource
    private MetadataEntityFieldOptionRepository fieldOptionRepository;

    @Override
    public boolean supports(String fieldType) {
        if (!StringUtils.hasText(fieldType)) {
            return false;
        }
        return SELECT_FIELD_TYPES.contains(fieldType.toUpperCase());
    }

    @Override
    public Object transform(Object rawValue, FieldValueTransformMode mode) {
        if (mode == FieldValueTransformMode.READ) {
            // 无字段上下文时，无法查询选项信息，保持原值
            return rawValue;
        }
        // 存储模式：提取ID列表，以逗号分隔存储
        return extractAndJoinIds(rawValue);
    }

    @Override
    public Object transform(Object rawValue, FieldValueTransformMode mode, ProcessContext context, MetadataEntityFieldDO field) {
        if (mode == FieldValueTransformMode.STORE) {
            return extractAndJoinIds(rawValue);
        }
        // 读取模式：根据字段UUID查询选项表，返回包含UUID和标签的完整信息
        if (field == null) {
            return rawValue;
        }
        return enrichWithOptionLabels(rawValue, field);
    }

    @Override
    public int getOrder() {
        // 优先级高于 ComplexObjectFieldValueStorageStrategy(0) 和 DataSelectionFieldValueStorageStrategy(-1)
        return -2;
    }

    /**
     * 根据选项UUID查询选项表，将原始UUID值丰富为包含标签信息的对象
     *
     * @param rawValue 原始值（逗号分隔的UUID字符串或列表）
     * @param field    字段定义
     * @return 丰富后的值：SELECT返回单个对象{id, label}，MULTI_SELECT返回对象列表
     */
    private Object enrichWithOptionLabels(Object rawValue, MetadataEntityFieldDO field) {
        if (rawValue == null) {
            return null;
        }

        String fieldUuid = field.getFieldUuid();
        if (!StringUtils.hasText(fieldUuid)) {
            return rawValue;
        }

        // 查询该字段的所有选项
        List<MetadataEntityFieldOptionDO> options = fieldOptionRepository.findAllByFieldUuid(fieldUuid);
        if (CollectionUtils.isEmpty(options)) {
            return rawValue;
        }

        // 构建选项UUID -> 选项DO的映射
        Map<String, MetadataEntityFieldOptionDO> optionMap = options.stream()
                .filter(o -> StringUtils.hasText(o.getOptionUuid()))
                .collect(Collectors.toMap(MetadataEntityFieldOptionDO::getOptionUuid, o -> o, (a, b) -> a));

        return enrichValueWithOptions(rawValue, field.getFieldType(), optionMap);
    }

    /**
     * 根据预加载的选项映射丰富字段值（供批量查询场景复用）
     *
     * @param rawValue  原始存储值
     * @param fieldType 字段类型
     * @param optionMap 选项UUID -> 选项DO映射
     * @return 丰富后的值
     */
    public static Object enrichValueWithOptions(Object rawValue, String fieldType,
                                                 Map<String, MetadataEntityFieldOptionDO> optionMap) {
        if (rawValue == null || optionMap == null || optionMap.isEmpty()) {
            return rawValue;
        }

        // 解析存储的UUID值
        List<String> storedUuids = parseStoredValue(rawValue);
        if (CollectionUtils.isEmpty(storedUuids)) {
            return rawValue;
        }

        boolean isMulti = "MULTI_SELECT".equalsIgnoreCase(fieldType);

        // 构建丰富后的选项列表
        List<Map<String, Object>> enrichedList = new ArrayList<>();
        for (String uuid : storedUuids) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", uuid);
            MetadataEntityFieldOptionDO option = optionMap.get(uuid);
            if (option != null) {
                item.put("label", option.getOptionLabel());
            }
            enrichedList.add(item);
        }

        if (isMulti) {
            return enrichedList;
        } else {
            return enrichedList.isEmpty() ? null : enrichedList.get(0);
        }
    }

    /**
     * 解析存储的值，提取UUID列表
     *
     * @param rawValue 原始存储值
     * @return UUID列表
     */
    public static List<String> parseStoredValue(Object rawValue) {
        if (rawValue == null) {
            return Collections.emptyList();
        }

        if (rawValue instanceof Collection<?> collection) {
            return collection.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .collect(Collectors.toList());
        }

        String strValue = rawValue.toString().trim();
        if (!StringUtils.hasText(strValue)) {
            return Collections.emptyList();
        }

        // 逗号分隔
        if (strValue.contains(",")) {
            return Arrays.stream(strValue.split(","))
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .collect(Collectors.toList());
        }

        return List.of(strValue);
    }

    /**
     * 提取UUID并以逗号分隔（存储模式）
     *
     * @param rawValue 原始值
     * @return 逗号分隔的UUID字符串
     */
    private String extractAndJoinIds(Object rawValue) {
        if (rawValue == null) {
            return null;
        }

        List<String> ids = extractIdentifiers(rawValue);
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        return String.join(",", ids);
    }

    /**
     * 从原始值中提取标识符列表
     *
     * @param rawValue 原始值（可能是字符串、Map或集合）
     * @return 标识符列表
     */
    private List<String> extractIdentifiers(Object rawValue) {
        if (rawValue == null) {
            return new ArrayList<>();
        }

        if (rawValue instanceof String stringValue) {
            if (!StringUtils.hasText(stringValue)) {
                return new ArrayList<>();
            }
            String trimmed = stringValue.trim();
            if (!trimmed.contains(",")) {
                return List.of(trimmed);
            }
            return Arrays.stream(trimmed.split(","))
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .collect(Collectors.toList());
        }

        if (rawValue instanceof Map<?, ?> mapValue) {
            Object idValue = mapValue.get("id");
            if (idValue == null) {
                idValue = mapValue.get("value");
            }
            String id = idValue != null ? idValue.toString() : null;
            return StringUtils.hasText(id) ? List.of(id) : new ArrayList<>();
        }

        if (rawValue instanceof Collection<?> collection) {
            return collection.stream()
                    .map(this::extractIdFromElement)
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .collect(Collectors.toList());
        }

        return List.of(rawValue.toString());
    }

    /**
     * 从元素中提取标识符
     *
     * @param element 集合中的单个元素
     * @return 标识符字符串
     */
    private String extractIdFromElement(Object element) {
        if (element == null) {
            return null;
        }
        if (element instanceof Map<?, ?> map) {
            Object idValue = map.get("id");
            if (idValue == null) {
                idValue = map.get("value");
            }
            return idValue != null ? idValue.toString() : null;
        }
        return element.toString();
    }
}
