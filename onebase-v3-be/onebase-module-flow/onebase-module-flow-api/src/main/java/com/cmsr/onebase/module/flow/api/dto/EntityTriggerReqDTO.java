package com.cmsr.onebase.module.flow.api.dto;

import com.cmsr.onebase.module.flow.context.table.RowData;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldValueDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

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

    @Schema(description = "流程上下文，key是上下文变量名，value是上下文变量值, 现在包含SystemFieldConstants REQUIRE中的常量")
    private Map<String, String> flowContext;

    @Schema(description = "数据，字段名称和字段数据, key是字段的columnName, value是字段值")
    private List<SemanticFieldValueDTO<Object>> fieldData;

    public RowData toInputData() {
        RowData inputData = new RowData();
        for (SemanticFieldValueDTO<?> fieldValueDTO : fieldData) {
            inputData.put(tableName + "." + fieldValueDTO.getFieldName(), fieldValueDTO.getRawValue());
        }
        return inputData;
    }
}
