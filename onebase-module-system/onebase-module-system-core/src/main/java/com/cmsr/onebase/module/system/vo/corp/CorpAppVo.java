package com.cmsr.onebase.module.system.vo.corp;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CorpAppVo {
    @Schema(description = "应用ID")
    private Long appId;

    @Schema(description = "应用名称")
    private String appName;

    @Schema(description = "应用图标")
    private String iconName;


    @Schema(description = "图标颜色")
    private String iconColor;
}
