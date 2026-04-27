package com.cmsr.onebase.module.system.vo.user;

import com.cmsr.onebase.framework.desensitize.annotation.MobileDesensitize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 用户精简信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDeptSimpleRespVO extends UserSimpleRespVO{

    @Schema(description = "部门名称", example = "IT 部")
    private String deptName;

    @Schema(description = "手机号", example = "")
    @MobileDesensitize
    private String mobile;

}
