package com.cmsr.onebase.module.app.api.app;

import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/8/13 10:30
 */
public interface AppApplicationApi {

    Long countApplicationByTenantId(Long tenantId);

    List findAppApplicationByAppName(String appName);

    Map<Integer,Integer> findAppApplicationAll();
}
