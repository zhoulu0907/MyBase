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
@Schema(description = "实体操作触发响应DTO")
public class EntityTriggerRespDTO {

    public final static EntityTriggerRespDTO SUCCESS = new EntityTriggerRespDTO(true);

    private boolean success;

    private Exception exception;

    public EntityTriggerRespDTO(boolean success) {
        this.success = success;
    }

    public EntityTriggerRespDTO(boolean success, Exception exception) {
        this.success = success;
        this.exception = exception;
    }

}
