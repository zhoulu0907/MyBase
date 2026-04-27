package com.cmsr.onebase.module.app.runtime.service.api;

import com.cmsr.onebase.module.app.api.app.AppServiceApi;
import org.springframework.stereotype.Service;

@Service("AppServiceApiRuntime")
public class AppServiceApiRuntimeImpl implements AppServiceApi {

    @Override
    public void deleteApplication(Long id, String name) {
        throw new RuntimeException("该方法不支持在运行态调用");
    }
}