package com.cmsr.onebase.framework.dolphins.dto.workflow.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工作流定义版本 DTO
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Data
public class WorkflowVersionDTO {

    /**
     * ID
     */
    @JsonProperty("id")
    private Integer id;

    /**
     * 流程定义编码
     */
    @JsonProperty("processDefinitionCode")
    private Long processDefinitionCode;

    /**
     * 流程定义名称
     */
    @JsonProperty("processDefinitionName")
    private String processDefinitionName;

    /**
     * 版本号
     */
    @JsonProperty("version")
    private Integer version;

    /**
     * 描述
     */
    @JsonProperty("description")
    private String description;

    /**
     * 创建时间
     */
    @JsonProperty("createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
