package com.cmsr.onebase.module.app.build.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "自定义按钮-保存请求")
public class CustomButtonSaveReqVO {

    @Schema(description = "按钮ID，更新时必填")
    private Long id;

    @NotNull(message = "页面集ID不能为空")
    @Schema(description = "页面集ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long pageSetId;

    @Schema(description = "页面ID")
    private Long pageId;

    @NotBlank(message = "按钮名称不能为空")
    @Schema(description = "按钮名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String buttonName;

    @Schema(description = "按钮描述")
    private String buttonDesc;

    @Schema(description = "是否展示描述：0否 1是")
    private Integer showDesc;

    @Schema(description = "样式类型")
    private String styleType;

    @Schema(description = "颜色")
    private String colorHex;

    @Schema(description = "透明度")
    private Integer colorAlpha;

    @Schema(description = "图标编码")
    private String iconCode;

    @NotBlank(message = "操作范围不能为空")
    @Schema(description = "操作范围：single/batch", requiredMode = Schema.RequiredMode.REQUIRED)
    private String operationScope;

    @Schema(description = "是否展示在表单视图：0否 1是")
    private Integer showInForm;

    @Schema(description = "是否展示在列表行内操作：0否 1是")
    private Integer showInRowAction;

    @Schema(description = "是否展示在列表批量操作：0否 1是")
    private Integer showInBatchAction;

    @NotBlank(message = "动作类型不能为空")
    @Schema(description = "动作类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String actionType;

    @Schema(description = "排序号")
    private Integer sortNo;

    @Schema(description = "状态：ENABLE/DISABLE")
    private String status;

    @Valid
    @Schema(description = "自动化流动作配置")
    private CustomButtonFlowActionReqVO flowAction;
}
