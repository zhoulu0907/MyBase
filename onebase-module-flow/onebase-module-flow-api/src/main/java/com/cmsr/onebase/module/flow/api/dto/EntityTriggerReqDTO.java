package com.cmsr.onebase.module.flow.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

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

//    @Schema(description = "实体ID")
//    private Long entityId;
    
//    @Schema(description = "实体UUID")
//    private String entityUuId;

    @Schema(description = "实体类型")
    private String entityTableName;

    @Schema(description = "触发事件，beforeCreate,afterCreate,beforeUpdate,afterUpdate,beforeDelete,afterDelete")
    private TriggerEventEnum triggerEvent;

    @Schema(description = "数据，字段名称和字段数据, key是字段的uuid, value是字段值")
    @Deprecated
    private Map<String, Object> fieldData;

    @Schema(description = "数据，字段名称和字段数据, key是字段的columnName, value是字段值")
    private Map<String, Object> colFieldData;
}
