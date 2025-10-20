package com.cmsr.onebase.framework.dolphins.dto.workflow.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 工作流定义简化 DTO
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Data
public class WorkflowSimpleDTO {

    /**
     * ID
     */
    @JsonProperty("id")
    private Integer id;

    /**
     * 编码
     */
    @JsonProperty("code")
    private Long code;

    /**
     * 名称
     */
    @JsonProperty("name")
    private String name;

    /**
     * 项目编码
     */
    @JsonProperty("projectCode")
    private Long projectCode;

    /**
     * 项目名称
     */
    @JsonProperty("projectName")
    private String projectName;
}
