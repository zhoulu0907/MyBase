package com.cmsr.onebase.framework.dolphins.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目 DTO
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Data
public class ProjectDTO {

    /**
     * 项目 ID
     */
    @JsonProperty("id")
    private Integer id;

    /**
     * 用户 ID
     */
    @JsonProperty("userId")
    private Integer userId;

    /**
     * 用户名
     */
    @JsonProperty("userName")
    private String userName;

    /**
     * 项目编码
     */
    @JsonProperty("code")
    private Long code;

    /**
     * 项目名称
     */
    @JsonProperty("name")
    private String name;

    /**
     * 项目描述
     */
    @JsonProperty("description")
    private String description;

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

    /**
     * 权限
     */
    @JsonProperty("perm")
    private Integer perm;

    /**
     * 工作流定义数量
     */
    @JsonProperty("defCount")
    private Integer defCount;
}
