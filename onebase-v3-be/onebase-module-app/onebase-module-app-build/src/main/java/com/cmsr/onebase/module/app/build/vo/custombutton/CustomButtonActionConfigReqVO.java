package com.cmsr.onebase.module.app.build.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "自定义按钮-统一动作配置。按 actionType 使用对应字段：UPDATE_FORM 使用 openMode/submitSuccessText；CREATE_RELATED_RECORD 使用 target*；TRIGGER_FLOW 使用 flow* 和 confirm*；OPEN_PAGE 使用 targetType/targetPage*/targetUrl/openMode")
public class CustomButtonActionConfigReqVO {

    @Schema(description = "打开方式。示例：DIALOG 弹窗、DRAWER 抽屉、CURRENT_PAGE 当前页、NEW_TAB 新页签")
    private String openMode;

    @Schema(description = "提交成功提示文案。主要用于 UPDATE_FORM")
    private String submitSuccessText;

    @Schema(description = "目标类型。主要用于 OPEN_PAGE，示例：INNER_PAGE 内部页面、OUTER_URL 外部链接")
    private String targetType;

    @Schema(description = "目标页面集UUID。用于 CREATE_RELATED_RECORD 或 OPEN_PAGE 的内部页面")
    private String targetPageSetUuid;

    @Schema(description = "目标页面UUID。用于 CREATE_RELATED_RECORD 或 OPEN_PAGE 的内部页面")
    private String targetPageUuid;

    @Schema(description = "目标外部链接。用于 OPEN_PAGE 且 targetType=OUTER_URL")
    private String targetUrl;

    @Schema(description = "目标实体UUID。用于 CREATE_RELATED_RECORD")
    private String targetEntityUuid;

    @Schema(description = "目标关联字段UUID。用于 CREATE_RELATED_RECORD，表示目标表单中承接当前记录ID的字段")
    private String targetRelationFieldUuid;

    @Schema(description = "目标关联范围。用于 CREATE_RELATED_RECORD，示例：CURRENT_RECORD 当前记录、SELECTED_RECORDS 选中记录")
    private String targetRelationScope;

    @Schema(description = "自动化流流程ID。用于 TRIGGER_FLOW")
    private Long flowProcessId;

    @Schema(description = "自动化流流程UUID。用于 TRIGGER_FLOW")
    private String flowProcessUuid;

    @Schema(description = "是否执行前二次确认：0否 1是。用于 TRIGGER_FLOW")
    private Integer confirmRequired;

    @Schema(description = "二次确认文案。用于 TRIGGER_FLOW")
    private String confirmText;

    @Schema(description = "扩展配置JSON。用于暂未结构化的动作配置，建议存JSON字符串")
    private String configJson;
}
