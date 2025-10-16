package com.cmsr.onebase.framework.remote.dto.project;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 项目响应 DTO
 * 对应 DolphinScheduler 3.3.1 的 Project 实体
 *
 * @author matianyu
 * @date 2025-10-16
 */
@Data
public class ProjectRespDTO {

    /**
     * 项目ID
     */
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 项目Code
     */
    private Long code;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 权限
     */
    private Integer perm;

    /**
     * 定义数量
     */
    private Integer defCount;
}
