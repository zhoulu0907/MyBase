package com.cmsr.onebase.module.app.controller.admin.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/7/23 13:52
 */
@Schema(description = "应用管理 - 应用菜单分组创建/修改 Request VO")
@Data
public class ApplicationMenuGroupCreateReqVO {

    @Schema(description = "应用ID")
    private Long applicationId;

    @Schema(description = "父菜单ID")
    private String parentUuid;

    @Schema(description = "菜单名称")
    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

}
