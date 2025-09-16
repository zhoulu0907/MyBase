package com.cmsr.onebase.module.app.api.app;

import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * @Author：huangjie
 * @Date：2025/8/13 10:36
 */
@Setter
@Validated
@Service
public class AppApplicationApiImpl implements AppApplicationApi {

    @Resource
    private AppApplicationRepository appApplicationRepository;

    @Override
    public Long countApplicationByTenantId(Long tenantId) {
        Long count = appApplicationRepository.countByTenantId(tenantId);
        return count;
    }

}
