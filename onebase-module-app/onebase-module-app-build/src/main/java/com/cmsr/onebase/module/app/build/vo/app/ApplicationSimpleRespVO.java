package com.cmsr.onebase.module.app.build.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class ApplicationSimpleRespVO {

    @Schema(description = "应用名称")
    private String appName;

    @Schema(description = "应用编码")
    private String appCode;

    @Schema(description = "图标名称")
    private String iconName;

    @Schema(description = "图标颜色")
    private String iconColor;

    @Schema(description = "版本号")
    private String versionNumber;

}
