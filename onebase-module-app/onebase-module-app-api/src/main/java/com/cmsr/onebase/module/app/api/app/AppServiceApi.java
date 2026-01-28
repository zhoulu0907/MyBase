package com.cmsr.onebase.module.app.api.app;

public interface AppServiceApi {
    // 根据ID和名称删除应用
    void deleteApplication(Long id, String name);
}
