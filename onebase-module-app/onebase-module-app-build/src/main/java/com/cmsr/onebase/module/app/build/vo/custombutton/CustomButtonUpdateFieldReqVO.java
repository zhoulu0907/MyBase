package com.cmsr.onebase.module.app.build.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "自定义按钮-修改当前表单字段配置。fieldMode=EDIT 表示用户可编辑字段；fieldMode=AUTO 表示提交时系统自动写入字段")
public class CustomButtonUpdateFieldReqVO {

    @NotBlank(message = "字段模式不能为空")
    @Schema(description = "字段模式：EDIT 用户可编辑；AUTO 系统自动更新", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fieldMode;

    @NotBlank(message = "字段UUID不能为空")
    @Schema(description = "字段UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fieldUuid;

    @Schema(description = "字段编码")
    private String fieldCode;

    @Schema(description = "是否必填：0否 1是。仅 fieldMode=EDIT 时生效")
    private Integer requiredFlag;

    @Schema(description = "自动更新值类型。仅 fieldMode=AUTO 时生效，示例：CONST 固定值、CURRENT_USER 当前用户、CURRENT_TIME 当前时间、FIELD_VALUE 当前记录字段值")
    private String valueType;

    @Schema(description = "自动更新值配置。仅 fieldMode=AUTO 时生效，建议存JSON字符串")
    private String valueConfig;

    @Schema(description = "排序号")
    private Integer sortNo;
}
