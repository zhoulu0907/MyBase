package com.cmsr.onebase.framework.dolphins.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 通用响应结果 DTO
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Data
public class ResultDTO<T> {

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
     * 响应数据
     */
    @JsonProperty("data")
    private T data;

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
