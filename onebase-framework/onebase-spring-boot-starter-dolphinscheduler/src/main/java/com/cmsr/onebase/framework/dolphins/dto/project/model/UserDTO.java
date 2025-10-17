package com.cmsr.onebase.framework.dolphins.dto.project.model;

import com.cmsr.onebase.framework.dolphins.dto.project.enums.UserTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户 DTO
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Data
public class UserDTO {

    /**
     * 用户 ID
     */
    @JsonProperty("id")
    private Integer id;

    /**
     * 用户名
     */
    @JsonProperty("userName")
    private String userName;

    /**
     * 用户密码
     */
    @JsonProperty("userPassword")
    private String userPassword;

    /**
     * 邮箱
     */
    @JsonProperty("email")
    private String email;

    /**
     * 电话
     */
    @JsonProperty("phone")
    private String phone;

    /**
     * 用户类型
     */
    @JsonProperty("userType")
    private UserTypeEnum userType;

    /**
     * 租户 ID
     */
    @JsonProperty("tenantId")
    private Integer tenantId;

    /**
     * 状态：0-禁用，1-启用
     */
    @JsonProperty("state")
    private Integer state;

    /**
     * 租户编码
     */
    @JsonProperty("tenantCode")
    private String tenantCode;

    /**
     * 队列名称
     */
    @JsonProperty("queueName")
    private String queueName;

    /**
     * 告警组
     */
    @JsonProperty("alertGroup")
    private String alertGroup;

    /**
     * 队列
     */
    @JsonProperty("queue")
    private String queue;

    /**
     * 时区
     */
    @JsonProperty("timeZone")
    private String timeZone;

    /**
     * 创建时间
     */
    @JsonProperty("createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonProperty("updateTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
