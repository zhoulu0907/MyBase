package com.cmsr.onebase.module.app.api.app;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @Author：huangjie
 * @Date：2025/8/13 10:30
 */
@Tag(name = "RPC 服务 - 应用")
public interface AppApplicationApi {

    Long countApplicationByTenantId(Long tenantId);

}
