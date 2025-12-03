package com.cmsr.onebase.module.system.vo.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 -批量新增管理员 Request VO")
@Data
public class TenantAdminUserReqVO {
    @Schema(description = "管理员昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "小王")
    @NotBlank(message = "管理员昵称不能为空")
    private String adminNickName;

    @Schema(description = "管理员账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "wang")
    @NotBlank(message = "管理员账号不能为空")
    private String adminUserName;

    @Schema(description = "管理员手机", example = "15888888888")
    @NotBlank(message = "管理员手机不能为空")
    private String adminMobile;

    @Schema(description = "管理员邮箱", example = "user@abc.com")
    private String adminEmail;

    @Schema(description = "来自平台克隆的用户id", example = "1024")
    private Long platformUserId;

}
