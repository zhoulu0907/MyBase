package com.cmsr.onebase.module.system.vo.corp;

import com.cmsr.onebase.framework.common.validation.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CorpAdminReqVO {

    @Schema(description = "用户账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "onebase")
    @NotBlank(message = "管理员账号不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "用户账号由 数字、字母 组成")
    @Size(min = 4, max = 30, message = "用户账号长度为 4-30 个字符")
    private String username;

    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "onebase")
    @Size(max = 30, message = "用户昵称长度不能超过30个字符")
    @NotBlank(message = "管理员昵称不能为空")
    private String nickname;

    @Schema(description = "用户邮箱", example = "")
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过 50 个字符")
    @NotBlank(message = "管理员邮箱不能为空")
    private String email;

    @Schema(description = "手机号码", example = "")
    @Mobile
    @NotBlank(message = "管理员手机号不能为空")
    private String mobile;

}
