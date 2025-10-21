package com.cmsr.onebase.module.bpm.build.vo.design;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 节点跳转关联表 实体类
 *
 * @author liyang
 */
@Schema(description = "节点跳转视图")
@Data
public class BpmSkipVO {

    @Schema(description = "流程定义id")
    private Long definitionId;

    @Schema(description = "当前流程节点的编码")
    private String nowNodeCode;

    @Schema(description = "下一个流程节点的编码")
    private String nextNodeCode;

    @Schema(description = "跳转名称")
    private String skipName;

    @Schema(description = "跳转类型（PASS审批通过 REJECT退回）")
    private String skipType;

    @Schema(description = "跳转条件")
    private String skipCondition;

    @Schema(description = "坐标")
    private List<BpmSkipPointVo> points;

}