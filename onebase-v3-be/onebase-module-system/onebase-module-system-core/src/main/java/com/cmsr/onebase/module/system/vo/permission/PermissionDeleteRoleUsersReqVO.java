package com.cmsr.onebase.module.system.vo.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

/**
 * 从角色中移除用户请求VO
 *
 * @author matianyu
 * @date 2025-01-22
 */
@Schema(description = "管理后台 - 从角色中移除用户 Request VO")
@Data
public class PermissionDeleteRoleUsersReqVO {

    @Schema(description = "角色编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "角色编号不能为空")
    private Long roleId;

    @Schema(description = "用户编号列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 2, 3]")
    @NotEmpty(message = "用户编号列表不能为空")
    private Set<Long> userIds;

}
