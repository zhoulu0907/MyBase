package com.cmsr.onebase.module.flow.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticFieldValueDTO;

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

   @Deprecated
   @Schema(description = "数据，字段名称和字段数据, key是字段的columnName, value是字段值")
   private Map<String, Object> colFieldData;

    public void setTableName(String tableName) {
        //TODO 要修改 使用 tableName
    }

    public void setFieldData(List<SemanticFieldValueDTO<Object>> fieldData) {
        //TODO 要修改 fieldData
    }
}
