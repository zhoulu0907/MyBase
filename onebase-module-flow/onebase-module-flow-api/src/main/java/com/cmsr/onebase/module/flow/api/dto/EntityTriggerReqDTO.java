package com.cmsr.onebase.module.flow.api.dto;

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

    @Schema(description = "实体ID")
    private Long entityId;

    @Schema(description = "触发事件，beforeCreate,afterCreate,beforeUpdate,afterUpdate,beforeDelete,afterDelete")
    private TriggerEventEnum triggerEvent;

    @Schema(description = "修改的字段ID")
    private List<Long> changedFieldIds;

    @Schema(description = "数据，字段名称和字段数据")
    private Map<String, Object> fieldData;

}
