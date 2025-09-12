package com.cmsr.onebase.module.system.controller.admin.tenant.vo.tenant;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 租户创建/修改 Request VO")
@Data
public class TenantInsertReqVO {

    @Schema(description = "租户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "onebase")
    @NotNull(message = "租户名不能为空")
    private String name;

    @Schema(description = "租户编码")
    private String tenantCode;

    @Schema(description = "管理员昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "nickname")
    private String adminNickName;

    @Schema(description = "管理员账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "account")
    private String adminUserName;

    @Schema(description = "管理员手机", example = "15011112222")
    private String adminMobile;

    @Schema(description = "租户状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "绑定域名", example = "onebase")
    private String website;

    @Schema(description = "租户套餐编号", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1024")
    private Long packageId;

    @Schema(description = "过期时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime expireTime;

    @Schema(description = "账号数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "账号数量不能为空")
    private Integer accountCount;

}
