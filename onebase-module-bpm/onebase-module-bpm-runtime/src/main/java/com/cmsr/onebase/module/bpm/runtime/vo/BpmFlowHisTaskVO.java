package com.cmsr.onebase.module.bpm.runtime.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 历史任务记录表 实体类
 */
@Schema(description = "历史任务记录表")
@Data
public class BpmFlowHisTaskVO {

    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "对应flow_definition表的id")
    private Long definitionId;

    @Schema(description = "对应flow_instance表的id")
    private Long instanceId;

    @Schema(description = "对应flow_task表的id")
    private Long taskId;

    @Schema(description = "开始节点编码")
    private String nodeCode;

    @Schema(description = "开始节点名称")
    private String nodeName;

    @Schema(description = "开始节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）")
    private Integer nodeType;

    @Schema(description = "目标节点编码")
    private String targetNodeCode;

    @Schema(description = "结束节点名称")
    private String targetNodeName;

    @Schema(description = "审批者")
    private String approver;

    @Schema(description = "协作方式(1审批 2转办 3委派 4会签 5票签 6加签 7减签)")
    private Integer cooperateType;

    @Schema(description = "协作人")
    private String collaborator;

    @Schema(description = "流转类型（PASS通过 REJECT退回 NONE无动作）")
    private String skipType;

    @Schema(description = "流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回）")
    private String flowStatus;

    @Schema(description = "审批表单是否自定义（Y是 N否）")
    private String formCustom;

    @Schema(description = "审批表单路径")
    private String formPath;

    @Schema(description = "扩展字段，预留给业务系统使用")
    private String ext;

    @Schema(description = "审批意见")
    private String message;

    @Schema(description = "任务变量")
    private String variable;

    @Schema(description = "任务开始时间")
    private LocalDateTime createTime;

    @Schema(description = "审批完成时间")
    private LocalDateTime updateTime;

}