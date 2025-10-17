package com.cmsr.onebase.framework.dolphins.dto.response;

import com.cmsr.onebase.framework.dolphins.dto.model.ProjectDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 项目分页响应 DTO
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Data
public class ProjectPageResponseDTO {

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
     * 分页数据
     */
    @JsonProperty("data")
    private PageInfoDTO<ProjectDTO> data;

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
