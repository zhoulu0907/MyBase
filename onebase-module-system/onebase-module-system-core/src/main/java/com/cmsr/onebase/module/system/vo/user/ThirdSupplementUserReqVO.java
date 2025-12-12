package com.cmsr.onebase.module.system.vo.user;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 用户精简信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThirdSupplementUserReqVO {

    @Schema(description = "用户编号", example = "1024")
    private Long userId;

    @Schema(description = "用户账号", example = "onebase")
    @NotBlank(message = "密码不能为空")
    private String username;

    @ExcelProperty("用户名称")
    @NotBlank(message = "用户昵称不能为空")
    private String nickname;

    @Schema(description = "用户邮箱", example = "a@b.cn")
    private String email;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "用户头像", example = "")
    private String avatar;
}
