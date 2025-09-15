package com.cmsr.onebase.module.system.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Schema(description = "管理后台 - 用户更新邮箱 Request VO")
@Data
public class UserUpdateEmailReqVO {

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "用户编号不能为空")
    private Long id;

    @Schema(description = "用户邮箱", requiredMode = Schema.RequiredMode.REQUIRED, example = "example@onebase.com")
    @NotNull(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

}