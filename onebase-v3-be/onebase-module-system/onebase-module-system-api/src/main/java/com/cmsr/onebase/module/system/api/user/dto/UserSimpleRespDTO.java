package com.cmsr.onebase.module.system.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户精简信息 Response DTO")
@Data
public class UserSimpleRespDTO {

    @Schema(description = "用户编号", example = "1024")
    private Long id;

    @Schema(description = "用户昵称", example = "昵称")
    private String nickname;

    @Schema(description = "用户账号", example = "onebase")
    private String username;

    @Schema(description = "用户邮箱", example = "a@b.cn")
    private String email;

    @Schema(description = "部门ID", example = "我是一个用户")
    private Long deptId;
}