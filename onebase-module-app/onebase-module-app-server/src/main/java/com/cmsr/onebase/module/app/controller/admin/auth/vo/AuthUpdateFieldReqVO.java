package com.cmsr.onebase.module.app.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/20 15:42
 */
@Schema(description = "应用管理 - 权限 字段权限 Request VO")
@Data
public class AuthUpdateFieldReqVO extends AuthPermissionReqVO {

    @Schema(description = "所有字段可操作，当下面情况必须传值：从全部到自定义，或从自定义到全部")
    private Integer isAllFieldsAllowed;

    @Schema(description = "字段权限，当前下面情况必须传值：单个字段选择或不选择")
    private AuthFieldVO authField;

    @Schema(description = "字段权限列表，当前下面情况必须传值：从全部到自定义")
    private List<AuthFieldVO> authFields;

}
