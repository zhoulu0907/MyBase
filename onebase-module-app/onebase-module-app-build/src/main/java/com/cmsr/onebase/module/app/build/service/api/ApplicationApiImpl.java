package com.cmsr.onebase.module.app.build.service.api;

import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.app.api.app.dto.ApplicationDTO;
import com.cmsr.onebase.module.app.api.app.dto.TagVO;
import com.cmsr.onebase.module.app.build.service.app.AppApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service("applicationApiImpl")
public class ApplicationApiImpl implements AppApplicationApi {

    @Autowired
    private AppApplicationService appApplicationService;

    @Override
    public Long countApplicationByTenantId(Long tenantId) {
        return 0L;
    }

    @Override
    public List<ApplicationDTO> findAppApplicationByAppName(String appName) {
        return List.of();
    }

    @Override
    public List<ApplicationDTO> findAppApplicationByAppIds(Collection<Long> appIds) {
        return List.of();
    }

    @Override
    public ApplicationDTO findAppApplicationById(Long appId) {
        return null;
    }

    @Override
    public Map<Long, Integer> countAppByTenantId() {
        return Map.of();
    }

    @Override
    public void updateAppTimeById(Long appId) {

    }

    @Override
    public Map<Long, List<TagVO>> queryAppTags(List<Long> appIds) {
        return Map.of();
    }

    @Override
    public boolean existsEntityRelation(String entityUuid, String entityName) {
        return false;
    }

    @Override
    public boolean existsEntityFieldRelation(String entityUuid, String entityName, String fieldUuid, String fieldName) {
        return false;
    }

    @Override
    public List<ApplicationDTO> getSimpleAllAppList(Long tenantId) {
        return List.of();
    }

    @Override
    public void deleteApplication(Long id, String name) {
        appApplicationService.deleteApplication(id, name);
    }
}