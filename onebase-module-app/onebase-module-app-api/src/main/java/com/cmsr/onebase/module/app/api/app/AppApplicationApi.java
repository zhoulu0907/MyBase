package com.cmsr.onebase.module.app.api.app;

import com.cmsr.onebase.module.app.api.app.dto.ApplicationDTO;
import com.cmsr.onebase.module.app.api.app.dto.TagVO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/8/13 10:30
 */
public interface AppApplicationApi {

    Long countApplicationByTenantId(Long tenantId);

    List<ApplicationDTO> findAppApplicationByAppName(String appName);

    List<ApplicationDTO> findAppApplicationByAppIds(Collection<Long> appIds);

    ApplicationDTO findAppApplicationById(Long appId);

    Map<Long, Integer> countAppByTenantId();

    void updateAppTimeById(Long appId);

    Map<Long, List<TagVO>> queryAppTags(List<Long> appIds);
}
