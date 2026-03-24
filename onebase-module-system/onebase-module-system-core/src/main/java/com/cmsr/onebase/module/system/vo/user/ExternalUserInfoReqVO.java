package com.cmsr.onebase.module.system.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Schema(description = "外部用户 - 获得用户基本信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalUserInfoReqVO {

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private String id;

    @Schema(description = "用户账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "onebase")
    private String username;

    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "onebase")
    private String nickname;

    @Schema(description = "用户邮箱", example = "onebase@aaa.com")
    private String email;
    @Schema(description = "手机号码", example = "15601691300")
    private String phone;

    @Schema(description = "用户性别，参见 SexEnum 枚举类", example = "1")
    private Integer sex;

    @Schema(description = "用户头像", example = "https://www.cmsr.com")
    private String avatar;

    @Schema(description = "租户id")
    private String currentTenantId;

    @Schema(description = "平台类型")
    private String platformType;


}
