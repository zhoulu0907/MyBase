package com.cmsr.onebase.module.app.build.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "自定义按钮-保存请求")
public class CustomButtonSaveReqVO {

    @Schema(description = "按钮ID。创建时不传，更新时必填")
    private Long id;

    @NotNull(message = "页面集ID不能为空")
    @Schema(description = "页面集ID。按钮归属的页面集", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long pageSetId;

    @Schema(description = "页面ID。按钮归属的具体页面；为空表示页面集级按钮")
    private Long pageId;

    @NotBlank(message = "按钮名称不能为空")
    @Schema(description = "按钮名称。页面集内不可重复", requiredMode = Schema.RequiredMode.REQUIRED)
    private String buttonName;

    @Schema(description = "按钮描述。用于按钮说明或提示")
    private String buttonDesc;

    @Schema(description = "是否展示描述：0否 1是；为空默认1")
    private Integer showDesc;

    @Schema(description = "样式类型。示例：PRIMARY 主按钮、DEFAULT 默认按钮、LINK 链接按钮、WARNING 警示按钮")
    private String styleType;

    @Schema(description = "按钮颜色HEX值。示例：#1677FF")
    private String colorHex;

    @Schema(description = "颜色透明度。0-100")
    private Integer colorAlpha;

    @Schema(description = "图标编码。由前端图标库约定，例如 edit、plus、link")
    private String iconCode;

    @NotBlank(message = "操作范围不能为空")
    @Schema(description = "操作范围：SINGLE 单条记录、BATCH 批量记录、FORM 表单视图", requiredMode = Schema.RequiredMode.REQUIRED)
    private String operationScope;

    @Schema(description = "是否展示在表单视图：0否 1是；为空默认0")
    private Integer showInForm;

    @Schema(description = "是否展示在列表行内操作：0否 1是；为空默认0")
    private Integer showInRowAction;

    @Schema(description = "是否展示在列表批量操作：0否 1是；为空默认0")
    private Integer showInBatchAction;

    @NotBlank(message = "动作类型不能为空")
    @Schema(description = "动作类型：UPDATE_FORM 修改当前表单、CREATE_RELATED_RECORD 新建关联表单、TRIGGER_FLOW 执行自动化流、OPEN_PAGE 打开页面", requiredMode = Schema.RequiredMode.REQUIRED)
    private String actionType;

    @Schema(description = "排序号。值越小越靠前；为空默认0")
    private Integer sortNo;

    @Schema(description = "状态：ENABLE 启用、DISABLE 停用；为空默认ENABLE")
    private String status;

    @Valid
    @Schema(description = "修改当前表单动作配置。仅 actionType=UPDATE_FORM 时使用")
    private CustomButtonUpdateFormActionReqVO updateFormAction;

    @Valid
    @Schema(description = "新建关联表单动作配置。仅 actionType=CREATE_RELATED_RECORD 时使用")
    private CustomButtonCreateRelatedActionReqVO createRelatedAction;

    @Valid
    @Schema(description = "执行自动化流动作配置。仅 actionType=TRIGGER_FLOW 时使用")
    private CustomButtonTriggerFlowActionReqVO triggerFlowAction;

    @Valid
    @Schema(description = "打开页面动作配置。仅 actionType=OPEN_PAGE 时使用")
    private CustomButtonOpenPageActionReqVO openPageAction;

    @Valid
    @Schema(description = "可用条件。外层数组为 OR，内层数组为 AND；不满足条件时运行态隐藏按钮")
    private CustomButtonAvailableConditionReqVO availableCondition;

    @Deprecated
    @Valid
    @Schema(description = "已废弃：统一动作配置。请改用 updateFormAction/createRelatedAction/triggerFlowAction/openPageAction", deprecated = true)
    private CustomButtonActionConfigReqVO actionConfig;

    @Deprecated
    @Valid
    @Schema(description = "已废弃：修改当前表单字段配置。请改用 updateFormAction.updateFields", deprecated = true)
    private List<CustomButtonUpdateFieldReqVO> updateFields;

    @Deprecated
    @Valid
    @Schema(description = "已废弃：自动化流动作配置。请改用 triggerFlowAction", deprecated = true)
    private CustomButtonFlowActionReqVO flowAction;
}
