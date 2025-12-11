package com.cmsr.defeign.permissions.auth;

import com.cmsr.feign.DeFeign;
import com.cmsr.api.permissions.auth.api.AuthApi;


@DeFeign(value = "xpack-permissions", path = "/auth")
public interface PermissionFeignService extends AuthApi {

}
