package com.cmsr.onebase.framework.dolphins.dto.schedule.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 定时调度分页响应 DTO
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Data
public class SchedulePageResponseDTO {

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
    private SchedulePageInfoDTO data;

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
