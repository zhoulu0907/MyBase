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

    public final static EntityTriggerRespDTO SUCCESS = new EntityTriggerRespDTO(true);

    private boolean success;

    private String code;

    private String message;

    private Exception cause;

    private boolean executionEnd;

    public EntityTriggerRespDTO() {
    }

    public EntityTriggerRespDTO(boolean success) {
        this.success = success;
    }

    public EntityTriggerRespDTO(boolean success, Exception cause) {
        this.success = success;
        this.cause = cause;
    }

}
