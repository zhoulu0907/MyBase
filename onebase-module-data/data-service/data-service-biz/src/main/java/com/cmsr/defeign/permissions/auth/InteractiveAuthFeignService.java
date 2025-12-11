package com.cmsr.defeign.permissions.auth;

import com.cmsr.api.permissions.auth.api.InteractiveAuthApi;
import com.cmsr.feign.DeFeign;

@DeFeign(value = "xpack-permissions", path = "/interactive")
public interface InteractiveAuthFeignService extends InteractiveAuthApi {
}
