package com.cmsr.onebase.framework.dolphins.dto.project.response;

import com.cmsr.onebase.framework.dolphins.dto.project.model.ProjectDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 项目列表响应 DTO
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Data
public class ProjectListResponseDTO {

    /**
     * 响应码
     */
    @JsonProperty("code")
    private Integer code;

    /**
     * 响应消息
     */
    @JsonProperty("msg")
    private String msg;

    /**
     * 项目列表数据
     */
    @JsonProperty("data")
    private List<ProjectDTO> data;

    /**
     * 是否成功
     */
    @JsonProperty("success")
    private Boolean success;

    /**
     * 是否失败
     */
    @JsonProperty("failed")
    private Boolean failed;
}
