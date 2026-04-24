package com.cmsr.onebase.module.app.build.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "自定义按钮-新建关联表单动作配置")
public class CustomButtonCreateRelatedActionReqVO {

    @Schema(description = "打开方式：DIALOG 弹窗、DRAWER 抽屉、NEW_TAB 新页签")
    private String openMode;

    @NotBlank(message = "目标页面集UUID不能为空")
    @Schema(description = "目标表单视图所在页面集UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String targetPageSetUuid;

    @NotBlank(message = "目标页面UUID不能为空")
    @Schema(description = "目标表单视图页面UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String targetPageUuid;

    @Schema(description = "目标实体UUID。目标表单绑定的数据资产")
    private String targetEntityUuid;

    @NotBlank(message = "目标关联字段UUID不能为空")
    @Schema(description = "目标表单中承接当前记录ID的数据选择字段UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String targetRelationFieldUuid;

    @Schema(description = "目标关联范围：CURRENT_RECORD 当前记录、SELECTED_RECORDS 选中记录；为空由操作类型推导")
    private String targetRelationScope;
}
