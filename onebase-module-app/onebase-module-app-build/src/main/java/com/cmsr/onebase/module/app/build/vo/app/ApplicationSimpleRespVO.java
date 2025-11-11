package com.cmsr.onebase.module.app.build.vo.app;

import cn.hutool.core.annotation.Alias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ApplicationSimpleRespVO {

    @Schema(description = "应用Id")
    private String Id;

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

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;


}
