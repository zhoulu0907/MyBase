package com.cmsr.onebase.framework.dolphins.dto.workflow.request;

import com.cmsr.onebase.framework.dolphins.dto.workflow.enums.ExecutionTypeEnum;
import com.cmsr.onebase.framework.dolphins.dto.workflow.enums.ReleaseStateEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 工作流定义创建请求 DTO
 * 
 * 对应 DolphinScheduler API: POST /v2/workflows
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Data
public class WorkflowCreateRequestDTO {

    /**
     * 项目编码(必填)
     */
    @NotNull(message = "项目编码不能为空")
    @JsonProperty("projectCode")
    private Long projectCode;

    /**
     * 工作流定义名称(必填)
     */
    @NotBlank(message = "工作流定义名称不能为空")
    @JsonProperty("name")
    private String name;

    /**
     * 工作流定义描述
     */
    @JsonProperty("description")
    private String description;

    /**
     * 发布状态(ONLINE/OFFLINE,默认OFFLINE)
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
     * 执行类型(PARALLEL并行/SERIAL_WAIT串行等待/SERIAL_DISCARD串行丢弃/SERIAL_PRIORITY串行优先,默认PARALLEL)
     */
    @JsonProperty("executionType")
    private ExecutionTypeEnum executionType;
}
