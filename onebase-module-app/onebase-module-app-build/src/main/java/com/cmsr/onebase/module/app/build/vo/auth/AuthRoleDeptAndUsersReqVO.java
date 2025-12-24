package com.cmsr.onebase.module.app.build.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/19 14:52
 */
@Data
@Schema(description = "应用管理 - 角色部门用户列表 Request VO")
public class AuthRoleDeptAndUsersReqVO {

    @Schema(description = "角色ID", example = "1024")
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @Schema(description = "部门ID", example = "1024")
    private Long deptId;

    @Schema(description = "搜索关键词", example = "onebase")
    private String keywords;

    @Schema(description = "用户类型", example = "")
    private Integer userType;



}
