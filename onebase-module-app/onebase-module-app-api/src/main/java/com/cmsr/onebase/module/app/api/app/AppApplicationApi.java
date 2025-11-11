package com.cmsr.onebase.module.app.api.app;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/8/13 10:30
 */
public interface AppApplicationApi {

    Long countApplicationByTenantId(Long tenantId);

    List findAppApplicationByAppName(String appName);

    List findAppApplicationByAppIds(Collection<Long> appIds);

    Map<Integer,Integer> findAppApplicationAll();
}
