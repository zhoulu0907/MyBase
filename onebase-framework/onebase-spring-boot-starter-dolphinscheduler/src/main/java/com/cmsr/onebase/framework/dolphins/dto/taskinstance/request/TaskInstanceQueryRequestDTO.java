package com.cmsr.onebase.framework.dolphins.dto.taskinstance.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 任务实例查询请求 DTO
 *
 * 对应 swagger 中的 TaskInstanceQueryRequest
 *
 * @author matianyu
 * @date 2025-10-17
 */
@Data
public class TaskInstanceQueryRequestDTO {

    /** 页大小 */
    @JsonProperty("pageSize")
    private Integer pageSize;

    /** 页码号 */
    @JsonProperty("pageNo")
    private Integer pageNo;

    /** 工作流实例ID */
    @JsonProperty("workflowInstanceId")
    private Integer workflowInstanceId;

    /** 流程实例名称 */
    @JsonProperty("workflowInstanceName")
    private String workflowInstanceName;

    /** 流程定义名称 */
    @JsonProperty("workflowDefinitionName")
    private String workflowDefinitionName;

    /** 搜索值 */
    @JsonProperty("searchVal")
    private String searchVal;

    /** 任务实例名 */
    @JsonProperty("taskName")
    private String taskName;

    /** 任务代码 */
    @JsonProperty("taskCode")
    private Long taskCode;

    /** 执行人名称 */
    @JsonProperty("executorName")
    private String executorName;

    /** 运行状态 */
    @JsonProperty("stateType")
    private String stateType;

    /** 运行主机IP */
    @JsonProperty("host")
    private String host;

    /** 开始时间 */
    @JsonProperty("startDate")
    private String startDate;

    /** 结束时间 */
    @JsonProperty("endDate")
    private String endDate;

    /** 任务执行类型 */
    @JsonProperty("taskExecuteType")
    private String taskExecuteType;
}
