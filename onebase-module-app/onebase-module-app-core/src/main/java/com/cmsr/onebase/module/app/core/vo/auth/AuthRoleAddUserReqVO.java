package com.cmsr.onebase.module.app.core.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "应用管理 - 角色添加成员 Request VO")
public class AuthRoleAddUserReqVO {

    @Schema(description = "角色id")
    @NotNull(message = "角色编码不能为空")
    private Long roleId;

    @Schema(description = "用户ID列表")
    @NotNull(message = "用户ID列表不能为空")
    private List<Long> userIds;

}
