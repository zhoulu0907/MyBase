package com.cmsr.onebase.module.metadata.api.datamethod.dto;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

@Data
public class InsertDataReqDTO {
    @Schema(description = "链路ID")
    private String traceId;
    /**
     * 实体ID
     */
    private Long entityId;
    /**
     * 数据内容 key为字段ID，value为字段值
     */
    @Schema(description = "数据内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Map<Long, Object>> data;

}
