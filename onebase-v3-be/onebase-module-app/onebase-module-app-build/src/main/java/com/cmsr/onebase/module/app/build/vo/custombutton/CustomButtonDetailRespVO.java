package com.cmsr.onebase.module.app.build.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "自定义按钮-详情")
public class CustomButtonDetailRespVO {

    @Schema(description = "按钮ID")
    private Long id;

    @Schema(description = "按钮编码。系统生成，全应用唯一")
    private String buttonCode;

    @Schema(description = "页面集ID。按钮归属的页面集")
    private Long pageSetId;

    @Schema(description = "页面ID。按钮归属的具体页面；为空表示页面集级按钮")
    private Long pageId;

    @Schema(description = "按钮名称。页面集内不可重复")
    private String buttonName;

    @Schema(description = "按钮描述")
    private String buttonDesc;

    @Schema(description = "是否展示描述：0否 1是")
    private Integer showDesc;

    @Schema(description = "样式类型。示例：PRIMARY、DEFAULT、LINK、WARNING")
    private String styleType;

    @Schema(description = "按钮颜色HEX值")
    private String colorHex;

    @Schema(description = "透明度")
    private Integer colorAlpha;

    @Schema(description = "图标编码")
    private String iconCode;

    @Schema(description = "操作范围：SINGLE 单条记录、BATCH 批量记录、FORM 表单视图")
    private String operationScope;

    @Schema(description = "是否展示在表单视图")
    private Integer showInForm;

    @Schema(description = "是否展示在行内操作")
    private Integer showInRowAction;

    @Schema(description = "是否展示在批量操作")
    private Integer showInBatchAction;

    @Schema(description = "动作类型：UPDATE_FORM、CREATE_RELATED_RECORD、TRIGGER_FLOW、OPEN_PAGE")
    private String actionType;

    @Schema(description = "排序号")
    private Integer sortNo;

    @Schema(description = "状态：ENABLE 启用、DISABLE 停用")
    private String status;

    @Schema(description = "修改当前表单动作配置。仅 actionType=UPDATE_FORM 时返回")
    private CustomButtonUpdateFormActionReqVO updateFormAction;

    @Schema(description = "新建关联表单动作配置。仅 actionType=CREATE_RELATED_RECORD 时返回")
    private CustomButtonCreateRelatedActionReqVO createRelatedAction;

    @Schema(description = "执行自动化流动作配置。仅 actionType=TRIGGER_FLOW 时返回")
    private CustomButtonTriggerFlowActionReqVO triggerFlowAction;

    @Schema(description = "打开页面动作配置。仅 actionType=OPEN_PAGE 时返回")
    private CustomButtonOpenPageActionReqVO openPageAction;

    @Schema(description = "可用条件配置。外层 OR，内层 AND")
    private CustomButtonAvailableConditionReqVO availableCondition;

    @Deprecated
    @Schema(description = "已废弃：统一动作配置", deprecated = true)
    private CustomButtonActionConfigReqVO actionConfig;

    @Deprecated
    @Schema(description = "已废弃：修改当前表单字段配置。请读取 updateFormAction.updateFields", deprecated = true)
    private List<CustomButtonUpdateFieldReqVO> updateFields;

    @Deprecated
    @Schema(description = "已废弃：自动化流动作配置。请读取 triggerFlowAction", deprecated = true)
    private CustomButtonFlowActionReqVO flowAction;
}
