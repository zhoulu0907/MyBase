package com.cmsr.onebase.module.system.vo.user;

import com.cmsr.onebase.framework.desensitize.annotation.EMailDesensitize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 用户精简信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSimpleRespVO {

    @Schema(description = "用户编号", example = "1024")
    private Long id;

    @Schema(description = "用户昵称", example = "昵称")
    private String nickname;

    @Schema(description = "用户账号", example = "onebase")
    private String username;

    @Schema(description = "用户邮箱", example = "a@b.cn")
    @EMailDesensitize
    private String email;

    @Schema(description = "部门ID", example = "我是一个用户")
    private Long deptId;
}
