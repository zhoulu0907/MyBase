package com.cmsr.onebase.module.app.build.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "自定义按钮-打开页面动作配置")
public class CustomButtonOpenPageActionReqVO {

    @NotBlank(message = "页面类型不能为空")
    @Schema(description = "页面类型：INNER_PAGE 应用内页面、OUTER_URL 外部链接", requiredMode = Schema.RequiredMode.REQUIRED)
    private String targetType;

    @Schema(description = "应用内目标页面集UUID。targetType=INNER_PAGE 时必填")
    private String targetPageSetUuid;

    @Schema(description = "应用内目标页面UUID。targetType=INNER_PAGE 时必填")
    private String targetPageUuid;

    @Schema(description = "外部链接URL。targetType=OUTER_URL 时必填，长度不超过200")
    private String targetUrl;

    @Schema(description = "打开方式：NEW_TAB 新页面、DIALOG 弹窗、DRAWER 抽屉")
    private String openMode;

    @Valid
    @Schema(description = "跳转自定义参数配置。保存时序列化到动作扩展配置中")
    private List<CustomButtonOpenPageParamReqVO> params;
}
