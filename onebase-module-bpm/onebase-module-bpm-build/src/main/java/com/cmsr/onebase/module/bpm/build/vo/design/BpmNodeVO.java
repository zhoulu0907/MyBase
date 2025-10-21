package com.cmsr.onebase.module.bpm.build.vo.design;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 流程节点视图
 */
@Schema(description = "流程节点表")
@Data
public class BpmNodeVO {

    @Schema(description = "节点类型")
    private String nodeType;

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
    private BpmNodeCoordinateVo coordinate;

    @Schema(description = "任意结点跳转")
    private String anyNodeSkip;

    @Schema(description = "业务ID")
    private String businessId;

    @Schema(description = "版本")
    private String version;

    @Schema(description = "节点跳转关系列表")
    private List<BpmSkipVO> skipList;

}
