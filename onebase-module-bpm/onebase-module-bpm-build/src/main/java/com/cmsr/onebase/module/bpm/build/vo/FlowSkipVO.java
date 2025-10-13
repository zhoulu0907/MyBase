package com.cmsr.onebase.module.bpm.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 节点跳转关联表 实体类
 */
@Schema(description = "节点跳转关联表")
@Data
public class FlowSkipVO {

    @Schema(description = "流程定义id")
    private Long definitionId;

    @Schema(description = "当前流程节点的编码")
    private String nowNodeCode;

    @Schema(description = "当前节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）")
    private Integer nowNodeType;

    @Schema(description = "下一个流程节点的编码")
    private String nextNodeCode;

    @Schema(description = "下一个节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）")
    private Integer nextNodeType;

    @Schema(description = "跳转名称")
    private String skipName;

    @Schema(description = "跳转类型（PASS审批通过 REJECT退回）")
    private String skipType;

    @Schema(description = "跳转条件")
    private String skipCondition;

    @Schema(description = "坐标")
    private String coordinate;

}