package com.cmsr.onebase.module.app.controller.admin.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/7/22 16:15
 */
@Schema(description = "应用管理 - 应用创建/修改 Request VO")
@Data
public class ApplicationCreateReqVO {

    @Schema(description = "应用ID")
    private Long id;

    @Schema(description = "应用Key")
    @NotBlank(message = "应用Key不能为空")
    private String appKey;

    @Schema(description = "应用名称")
    @NotBlank(message = "应用名称不能为空")
    private String appName;

    @Schema(description = "应用编码")
    private String appCode;

    @Schema(description = "应用模式")
    private String appMode;

    @Schema(description = "主题色")
    private String themeColor;

    @Schema(description = "图标类型")
    @NotBlank(message = "图标类型不能为空")
    private String iconName;

    @Schema(description = "图标颜色")
    @NotBlank(message = "图标颜色不能为空")
    private String iconColor;

    @Schema(description = "数据源ID")
    @NotNull(message = "数据源ID不能为空")
    private Long datasourceId;

    @Schema(description = "应用描述")
    private String description;

    @Schema(description = "标签ID")
    private List<Long> tagIds;
}
