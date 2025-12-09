package com.cmsr.onebase.module.flow.api.dto;

import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldValueDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/19 10:59
 */
@Data
@Schema(description = "实体操作触发请求DTO")
public class EntityTriggerReqDTO {

    @Schema(description = "链路ID")
    private String traceId;

    @Schema(description = "应用ID")
    private Long applicationId;

    @Schema(description = "实体类型")
    private String tableName;

    @Schema(description = "触发事件，beforeCreate,afterCreate,beforeUpdate,afterUpdate,beforeDelete,afterDelete")
    private TriggerEventEnum triggerEvent;

    @Schema(description = "数据，字段名称和字段数据, key是字段的columnName, value是字段值")
    private List<SemanticFieldValueDTO<Object>> fieldData;

    public Map<String, Object> toInputData() {
        Map<String, Object> inputData = new HashMap<>();
        for (SemanticFieldValueDTO<?> fieldValueDTO : fieldData) {
            inputData.put(fieldValueDTO.getFieldName(), fieldValueDTO.getRawValue());
        }
        return inputData;
    }
}
