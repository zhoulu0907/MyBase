package com.cmsr.onebase.framework.dolphins.dto.workflow.request;

import com.cmsr.onebase.framework.dolphins.dto.workflow.enums.ExecutionTypeEnum;
import com.cmsr.onebase.framework.dolphins.dto.workflow.enums.ReleaseStateEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 工作流定义更新请求 DTO
 * 
 * 对应 DolphinScheduler API: PUT /v2/workflows/{code}
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Data
public class WorkflowUpdateRequestDTO {

    /**
     * 工作流定义名称
     */
    @JsonProperty("name")
    private String name;

    /**
     * 工作流定义描述
     */
    @JsonProperty("description")
    private String description;

    /**
     * 发布状态(ONLINE/OFFLINE)
     */
    @JsonProperty("releaseState")
    private ReleaseStateEnum releaseState;

    /**
     * 全局参数(JSON格式)
     */
    @JsonProperty("globalParams")
    private String globalParams;

    /**
     * 告警组ID
     */
    @JsonProperty("warningGroupId")
    private Integer warningGroupId;

    /**
     * 超时时间(秒)
     */
    @JsonProperty("timeout")
    private Integer timeout;

    /**
     * 执行类型(PARALLEL并行/SERIAL_WAIT串行等待/SERIAL_DISCARD串行丢弃/SERIAL_PRIORITY串行优先)
     */
    @JsonProperty("executionType")
    private ExecutionTypeEnum executionType;

    /**
     * 位置信息(JSON格式)
     */
    @JsonProperty("location")
    private String location;
}
