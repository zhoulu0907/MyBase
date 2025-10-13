package com.cmsr.onebase.module.bpm.build.vo;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 流程节点表 实体类
 */
@Schema(description = "流程节点表")
@Data
public class FlowNodeVO {

    @Schema(description = "节点类型（0开始节点 1中间节点 2结束节点 3互斥网关 4并行网关）")
    private Integer nodeType;

    @Schema(description = "流程定义id")
    private Long definitionId;

    @Schema(description = "流程节点编码")
    private String nodeCode;

    @Schema(description = "流程节点名称")
    private String nodeName;

    @Schema(description = "权限标识（权限类型:权限标识，可以多个，用@@隔开)")
    private String permissionFlag;

    @Schema(description = "流程签署比例值")
    private BigDecimal nodeRatio;

    @Schema(description = "坐标")
    private String coordinate;

    @Schema(description = "任意结点跳转")
    private String anyNodeSkip;

    @Schema(description = "监听器类型")
    private String listenerType;

    @Schema(description = "监听器路径")
    private String listenerPath;

    @Schema(description = "处理器类型")
    private String handlerType;

    @Schema(description = "处理器路径")
    private String handlerPath;

    @Schema(description = "审批表单是否自定义（Y是 N否）")
    private String formCustom = "N";

    @Schema(description = "审批表单路径")
    private String formPath;

    @Schema(description = "版本")
    private String version;

    @Schema(description = "节点扩展属性")
    private String ext;
    @Schema(description = "节点跳转关系列表")
    private List<FlowSkipVO> skipList;

}
