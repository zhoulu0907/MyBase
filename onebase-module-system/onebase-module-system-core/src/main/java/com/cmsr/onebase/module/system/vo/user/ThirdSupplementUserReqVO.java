package com.cmsr.onebase.module.system.vo.user;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 补充用户信息 VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThirdSupplementUserReqVO {

    @Schema(description = "设备Id", example = "a@b.cn")
    private String deviceId;

    @Schema(description = "应用ID")
    private Long appId;

    @Schema(description = "手机号", example = "1024")
    @NotBlank(message = "用户昵称不能为空")
    private String mobile;

    @ExcelProperty("用户名称")
    @NotBlank(message = "用户昵称不能为空")
    private String nickName;

    @Schema(description = "用户邮箱", example = "a@b.cn")
    private String email;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "用户头像", example = "")
    private String avatar;
}
