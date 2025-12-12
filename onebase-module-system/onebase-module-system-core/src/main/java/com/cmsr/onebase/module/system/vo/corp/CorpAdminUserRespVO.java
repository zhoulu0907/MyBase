package com.cmsr.onebase.module.system.vo.corp;


import com.cmsr.onebase.framework.desensitize.annotation.MobileDesensitize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CorpAdminUserRespVO {
    @Schema(description = "管理员账号")
    private String username;
    @Schema(description = "管理员密码")
    private String password;
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "企业管理员手机号")
    @MobileDesensitize
    private String mobile;

    @Schema(description = "企业id")
    private Long corpId;
}
