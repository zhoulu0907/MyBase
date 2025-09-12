package com.cmsr.onebase.module.system.vo.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

/**
 * 从角色中移除菜单请求VO
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Schema(description = "管理后台 - 从角色中移除菜单 Request VO")
@Data
public class PermissionDeleteRoleMenusReqVO {

    @Schema(description = "角色编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "角色编号不能为空")
    private Long roleId;

    @Schema(description = "菜单编号列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 2, 3]")
    @NotEmpty(message = "菜单编号列表不能为空")
    private Set<Long> menuIds;

}
