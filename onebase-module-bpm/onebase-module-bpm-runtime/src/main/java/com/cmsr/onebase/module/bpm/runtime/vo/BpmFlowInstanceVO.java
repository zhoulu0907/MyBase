package com.cmsr.onebase.module.bpm.runtime.vo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流程实例VO
 */
@Schema(description = "流程实例VO")
@Data
public class BpmFlowInstanceVO {

    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "对应flow_definition表的id")
    private Long definitionId;

    @Schema(description = "业务id，实际对应pageSetId")
    private String businessId;

    @Schema(description = "节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）")
    private Integer nodeType;

    @Schema(description = "流程节点编码")
    private String nodeCode;

    @Schema(description = "流程节点名称")
    private String nodeName;

    @Schema(description = "任务变量")
    private String variable;

    @Schema(description = "流程状态（0待提交 1审批中 2审批通过 4终止 5作废 6撤销 8已完成 9已退回 10失效 11拿回）")
    private String flowStatus;

    @Schema(description = "流程激活状态（0挂起 1激活）")
    private Integer activityStatus;

    @Schema(description = "流程定义json")
    private String defJson;

    @Schema(description = "乐观锁")
    private Long lockVersion;

    @Schema(description = "创建人")
    private Long creator;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新人")
    private Long updater;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "扩展字段，预留给业务系统使用")
    private String ext;

}
