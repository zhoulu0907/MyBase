package com.cmsr.onebase.module.app.build.vo.auth;

import com.cmsr.onebase.module.app.core.vo.auth.AuthOperationVO;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/20 15:42
 */
@Schema(description = "应用管理 - 权限 操作权限 Request VO")
@Data
public class AuthUpdateOperationReqVO   {

    @Schema(description = "应用管理 - 权限基础参数")
    @NotNull(message = "应用管理 - 权限基础参数不能为空")
    private AuthPermissionReqVO permissionReq;

    @Schema(description = "操作权限，修改单个")
    private AuthOperationVO authOperation;

    @Schema(description = "操作权限列表，修改多个")
    private List<AuthOperationVO> authOperations;
}
