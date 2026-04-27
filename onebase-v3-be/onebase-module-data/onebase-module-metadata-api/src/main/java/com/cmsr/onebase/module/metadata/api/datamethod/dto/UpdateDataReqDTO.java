package com.cmsr.onebase.module.metadata.api.datamethod.dto;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

@Data
public class UpdateDataReqDTO {
    @Schema(description = "链路ID")
    private String traceId;
    /**
     * 目标实体ID
     */
    private Long entityId;

    /**
     * 父实体ID 如果目标实体是主表，parentEntityId则为null。如果目标实体是子表，parentEntityId则为目标实体的主表实体ID
     */
    private Long parentEntityId;

    /**
     * 更新类型 主表更新-mainEntity 子表更新-subEntity
     */
    private String updateType;

    /**
     * 数据内容 key为字段ID，value为字段值
     */
    @Schema(description = "数据内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Map<Long, Object>> data;

    /**
     * 规则定义二维数组，外层数组元素间为OR关系，内层数组元素间为AND关系
     */
    private List<List<ConditionDTO>> conditionDTO;
}
