package com.cmsr.onebase.module.app.build.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "应用管理 - 角色添加部门 Request VO")
public class AuthRoleAddDeptReqVO {

    @Schema(description = "角色id")
    @NotNull(message = "角色编码不能为空")
    private Long roleId;

    @Schema(description = "部门ID列表")
    @NotNull(message = "部门ID列表不能为空")
    private List<Long> deptIds;

    @Schema(description = "是否包含子部门")
    private Integer isIncludeChild;

}
