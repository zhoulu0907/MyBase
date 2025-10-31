package com.cmsr.onebase.module.app.core.biz.app;

import com.cmsr.onebase.module.app.api.app.AppApplicationApi;
import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.app.ApplicationDO;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/8/13 10:36
 */
@Setter
@Service
public class AppApplicationApiImpl implements AppApplicationApi {

    @Resource
    private AppApplicationRepository appApplicationRepository;

    @Override
    public Long countApplicationByTenantId(Long tenantId) {
        Long count = appApplicationRepository.countByTenantId(tenantId);
        return count;
    }

    @Override
    public List<ApplicationDO> finAppApplicationByAppName(String appName) {
        return  appApplicationRepository.finAppApplicationByAppName(appName);
    }
}
