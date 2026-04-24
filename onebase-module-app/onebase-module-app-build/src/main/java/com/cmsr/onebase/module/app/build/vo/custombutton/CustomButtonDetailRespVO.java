package com.cmsr.onebase.module.app.build.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "自定义按钮-详情")
public class CustomButtonDetailRespVO {

    @Schema(description = "按钮ID")
    private Long id;

    @Schema(description = "按钮编码")
    private String buttonCode;

    @Schema(description = "页面集ID")
    private Long pageSetId;

    @Schema(description = "页面ID")
    private Long pageId;

    @Schema(description = "按钮名称")
    private String buttonName;

    @Schema(description = "按钮描述")
    private String buttonDesc;

    @Schema(description = "是否展示描述")
    private Integer showDesc;

    @Schema(description = "样式类型")
    private String styleType;

    @Schema(description = "颜色")
    private String colorHex;

    @Schema(description = "透明度")
    private Integer colorAlpha;

    @Schema(description = "图标编码")
    private String iconCode;

    @Schema(description = "操作范围")
    private String operationScope;

    @Schema(description = "是否展示在表单视图")
    private Integer showInForm;

    @Schema(description = "是否展示在行内操作")
    private Integer showInRowAction;

    @Schema(description = "是否展示在批量操作")
    private Integer showInBatchAction;

    @Schema(description = "动作类型")
    private String actionType;

    @Schema(description = "排序号")
    private Integer sortNo;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "自动化流动作配置")
    private CustomButtonFlowActionReqVO flowAction;
}
