package com.cmsr.onebase.module.metadata.core.semantic.strategy;

import com.cmsr.onebase.module.metadata.core.dal.dataobject.entity.MetadataEntityFieldDO;
import org.anyline.entity.DataRow;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Deprecated
public class SemanticRowMapper {
    public Map<String, Object> toMap(DataRow dataRow, List<MetadataEntityFieldDO> fields) {
        Map<String, Object> resultMap = new HashMap<>();
        for (MetadataEntityFieldDO field : fields) {
            String fieldName = field.getFieldName();
            String fieldType = field.getFieldType();
            Object value = dataRow.get(fieldName);
            if (value != null) {
                if (needsJsonDeserialization(fieldType, value)) {
                    try {
                        Object deserializedValue = com.cmsr.onebase.framework.common.util.json.JsonUtils.parseObject(value.toString(), Object.class);
                        resultMap.put(fieldName, deserializedValue);
                    } catch (Exception e) {
                        resultMap.put(fieldName, value);
                    }
                } else if ("DATETIME".equals(fieldType) && value instanceof Timestamp timestamp) {
                    Instant instant = timestamp.toInstant();
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
                    String timeStr = dateTimeFormatter.format(instant);
                    resultMap.put(fieldName, timeStr);
                } else {
                    resultMap.put(fieldName, value);
                }
            }
        }
        return resultMap;
    }

    private boolean needsJsonDeserialization(String fieldType, Object fieldValue) {
        if (fieldType == null || fieldValue == null) { return false; }
        if (!(fieldValue instanceof String)) { return false; }
        String upperFieldType = fieldType.toUpperCase();
        boolean isComplexType = upperFieldType.contains("SELECT") || upperFieldType.contains("MULTI") ||
                upperFieldType.contains("ADDRESS") || upperFieldType.contains("FILE") || upperFieldType.contains("ATTACHMENT") ||
                upperFieldType.contains("IMAGE") || upperFieldType.contains("USER") || upperFieldType.contains("DEPARTMENT") ||
                upperFieldType.contains("DATA") || upperFieldType.contains("GEOGRAPHY") || upperFieldType.contains("GEO") ||
                upperFieldType.equals("JSONB") || upperFieldType.equals("JSON");
        String strValue = fieldValue.toString().trim();
        boolean looksLikeJson = strValue.startsWith("{") || strValue.startsWith("[");
        return isComplexType && looksLikeJson;
    }
}
