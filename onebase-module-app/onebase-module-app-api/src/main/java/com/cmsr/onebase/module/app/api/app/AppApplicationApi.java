package com.cmsr.onebase.module.app.api.app;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/13 10:30
 */
public interface AppApplicationApi {

    Long countApplicationByTenantId(Long tenantId);

    List finAppApplicationAll();
}
