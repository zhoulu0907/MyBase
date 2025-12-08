package com.cmsr.onebase.module.system.vo.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TenantAdminUserResVO {
    @Schema(description = "管理员昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "adminNickName")
    private String adminNickName;

    @Schema(description = "管理员账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "adminUserName")
    private String adminUserName;

    @Schema(description = "管理员手机", requiredMode = Schema.RequiredMode.REQUIRED, example = "adminMobile")
    private String adminMobile;

    @Schema(description = "管理员邮箱", example = "user@abc.com")
    private String adminEmail;

    @Schema(description = "管理员id", requiredMode = Schema.RequiredMode.REQUIRED, example = "adminUserId")
    private Long adminUserId;

    @Schema(description = "来自平台克隆的用户id", requiredMode = Schema.RequiredMode.REQUIRED, example = "platformUserId")
    private Long platformUserId;

    @Schema(description = "管理员头像", example = "")
    private String adminAvatar;


}
