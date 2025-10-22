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

    private boolean success;

    private String code;

    private String message;

    private Exception cause;

    private boolean executionEnd;

    public EntityTriggerRespDTO(String traceId) {
        this.traceId = traceId;
    }

    public EntityTriggerRespDTO(String traceId, boolean success) {
        this.traceId = traceId;
        this.success = success;
    }

    public EntityTriggerRespDTO(String traceId, boolean success, String message) {
        this.traceId = traceId;
        this.success = success;
        this.message = message;
    }

    public EntityTriggerRespDTO(String traceId, boolean success, Exception cause) {
        this.traceId = traceId;
        this.success = success;
        this.cause = cause;
    }

}
