package com.cmsr.onebase.module.flow.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/9/19 10:59
 */
@Data
@Schema(description = "实体操作触发响应DTO")
public class EntityTriggerRespDTO {

    private String traceId;

    private Long processId;

    private boolean success = true;

    private boolean triggered = false;

    private String code;

    private String message;

    private String detail;

    private Exception cause;

    private boolean executionEnd;

    public EntityTriggerRespDTO(String traceId) {
        this.traceId = traceId;
    }

    public EntityTriggerRespDTO(String traceId, Long processId) {
        this.traceId = traceId;
        this.processId = processId;
    }

}
