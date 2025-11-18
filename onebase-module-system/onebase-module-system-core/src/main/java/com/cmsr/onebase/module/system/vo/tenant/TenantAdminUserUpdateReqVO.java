package com.cmsr.onebase.module.system.vo.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 -批量修改管理员 Request VO")
@Data
public class TenantAdminUserUpdateReqVO {
    @Schema(description = "管理员昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "adminNickName")
    private String adminNickName;

    @Schema(description = "管理员账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "adminUserName")
    private String adminUserName;

    @Schema(description = "管理员手机", example = "")
    private String adminMobile;

    @Schema(description = "来自平台克隆的用户id", example = "")
    private Long platformUserId;

}
