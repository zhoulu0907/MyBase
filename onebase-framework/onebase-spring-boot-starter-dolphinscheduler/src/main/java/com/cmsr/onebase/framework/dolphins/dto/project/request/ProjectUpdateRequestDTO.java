package com.cmsr.onebase.framework.dolphins.dto.project.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 项目更新请求 DTO
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Data
public class ProjectUpdateRequestDTO {

    /**
     * 项目名称
     */
    @NotBlank(message = "项目名称不能为空")
    @JsonProperty("projectName")
    private String projectName;

    /**
     * 项目描述
     */
    @JsonProperty("description")
    private String description;
}
