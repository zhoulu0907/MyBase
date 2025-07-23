package com.cmsr.onebase.module.app.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/7/22 16:15
 */
@Schema(description = "应用管理 - 应用创建/修改 Request VO")
@Data
public class ApplicationCreateReqVO {

    @Schema(description = "应用名称")
    private String name;

    @Schema(description = "应用描述")
    private String description;

    @Schema(description = "图标类型")
    private String iconType;

    @Schema(description = "图标颜色")
    private String iconColor;


}
