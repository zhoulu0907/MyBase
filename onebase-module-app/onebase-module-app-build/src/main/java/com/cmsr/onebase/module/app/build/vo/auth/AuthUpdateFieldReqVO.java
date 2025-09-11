package com.cmsr.onebase.module.app.build.vo.auth;

import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/20 15:42
 */
@Schema(description = "应用管理 - 权限 字段权限 Request VO")
@Data
public class AuthUpdateFieldReqVO   {

    @Schema(description = "应用管理 - 权限基础参数")
    @NotNull(message = "应用管理 - 权限基础参数不能为空")
    private AuthPermissionReqVO permissionReq;

    @Schema(description = "所有字段可操作，当下面情况必须传值：从全部到自定义，或从自定义到全部")
    private Integer isAllFieldsAllowed;

    @Schema(description = "字段权限，修改单个字段")
    private AuthFieldVO authField;

    @Schema(description = "字段权限列表，同时修改多个字段")
    private List<AuthFieldVO> authFields;

}
