package com.cmsr.onebase.module.app.api.permission;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @Author：huangjie
 */
@Tag(name = "RPC 服务 - 应用")
public interface AppPermissionApi {

    Boolean checkPermssion(AppPermissionCheckDTO permissionCheckDTO);

}
