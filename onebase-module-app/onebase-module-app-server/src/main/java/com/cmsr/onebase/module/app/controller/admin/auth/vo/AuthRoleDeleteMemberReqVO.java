package com.cmsr.onebase.module.app.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/7 12:05
 */
@Data
@Schema(description = "应用管理 - 角色删除成员 Request VO")
public class AuthRoleDeleteMemberReqVO {

    @Schema(description = "角色ID")
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @Schema(description = "用户ID列表")
    @NotNull(message = "用户ID列表不能为空")
    private List<Long> userIds;


}
