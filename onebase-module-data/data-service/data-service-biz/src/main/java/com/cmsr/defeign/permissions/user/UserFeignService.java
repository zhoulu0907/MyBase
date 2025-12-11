package com.cmsr.defeign.permissions.user;

import com.cmsr.api.permissions.user.api.UserApi;
import com.cmsr.feign.DeFeign;

@DeFeign(value = "xpack-permissions", path = "/user")
public interface UserFeignService extends UserApi {
}
