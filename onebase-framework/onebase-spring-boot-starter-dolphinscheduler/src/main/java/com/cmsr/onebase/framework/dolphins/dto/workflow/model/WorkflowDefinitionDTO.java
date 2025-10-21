package com.cmsr.onebase.framework.dolphins.dto.workflow.model;

import com.cmsr.onebase.framework.dolphins.dto.task.enums.FlagEnum;
import com.cmsr.onebase.framework.dolphins.dto.workflow.enums.ReleaseStateEnum;
import com.cmsr.onebase.framework.dolphins.dto.workflow.enums.ExecutionTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工作流定义 DTO
 *
 * 对应 DolphinScheduler 的 ProcessDefinition
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Data
public class WorkflowDefinitionDTO {

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
     * 版本
     */
    @JsonProperty("version")
    private Integer version;

    /**
     * 发布状态
     */
    @JsonProperty("releaseState")
    private ReleaseStateEnum releaseState;

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

    /**
     * 描述
     */
    @JsonProperty("description")
    private String description;

    /**
     * 全局参数（JSON格式）
     */
    @JsonProperty("globalParams")
    private String globalParams;

    /**
     * 全局参数列表
     */
    @JsonProperty("globalParamList")
    private Object globalParamList;

    /**
     * 全局参数映射
     */
    @JsonProperty("globalParamMap")
    private Object globalParamMap;

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
     * 标志（YES/NO）
     */
    @JsonProperty("flag")
    private FlagEnum flag;

    /**
     * 用户ID
     */
    @JsonProperty("userId")
    private Integer userId;

    /**
     * 用户名
     */
    @JsonProperty("userName")
    private String userName;

    /**
     * 修改人
     */
    @JsonProperty("modifyBy")
    private String modifyBy;

    /**
     * 告警组ID
     */
    @JsonProperty("warningGroupId")
    private Integer warningGroupId;

    /**
     * 超时时间（秒）
     */
    @JsonProperty("timeout")
    private Integer timeout;

    /**
     * 租户ID
     */
    @JsonProperty("tenantId")
    private Integer tenantId;

    /**
     * 租户编码
     */
    @JsonProperty("tenantCode")
    private String tenantCode;

    /**
     * 执行类型
     */
    @JsonProperty("executionType")
    private ExecutionTypeEnum executionType;

    /**
     * 位置信息（JSON格式）
     */
    @JsonProperty("locations")
    private String locations;

    /**
     * 连接信息
     */
    @JsonProperty("connects")
    private Object connects;

    /**
     * 任务定义JSON
     */
    @JsonProperty("taskDefinitionJson")
    private String taskDefinitionJson;

    /**
     * 任务关系JSON
     */
    @JsonProperty("taskRelationJson")
    private String taskRelationJson;

    /**
     * 调度发布状态
     */
    @JsonProperty("scheduleReleaseState")
    private ReleaseStateEnum scheduleReleaseState;

    /**
     * 调度配置信息
     */
    @JsonProperty("schedule")
    private Object schedule;
}
