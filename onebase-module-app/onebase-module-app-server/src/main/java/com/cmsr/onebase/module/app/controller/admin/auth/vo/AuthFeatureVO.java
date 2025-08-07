package com.cmsr.onebase.module.app.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/7 19:20
 */
@Data
@Schema(description = "应用管理 - 功能权限 Request VO")
public class AuthFeatureVO {

    @Schema(description = "页面是否可访问")
    private Boolean pageAllowed;

    @Schema(description = "关联的所有实体是否可访问")
    private Boolean allEntitiesAllowed;

}
