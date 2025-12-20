package com.cmsr.onebase.module.app.runtime.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/12/20 11:23
 */
@Data
public class AppLeastInfo {

    @Schema(description = "应用Id")
    private Long id;

    @Schema(description = "图标名称")
    private String iconName;

    @Schema(description = "图标颜色")
    private String iconColor;

    @Schema(description = "应用名称")
    private String appName;

    @Schema(description = "应用描述")
    private String description;

}
