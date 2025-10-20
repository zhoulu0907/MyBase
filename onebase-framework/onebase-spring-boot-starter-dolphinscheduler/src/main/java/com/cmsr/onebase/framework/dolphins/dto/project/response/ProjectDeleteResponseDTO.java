package com.cmsr.onebase.framework.dolphins.dto.project.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 项目删除响应 DTO
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Data
public class ProjectDeleteResponseDTO {

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
     * 删除结果（布尔值）
     */
    @JsonProperty("data")
    private Boolean data;

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
