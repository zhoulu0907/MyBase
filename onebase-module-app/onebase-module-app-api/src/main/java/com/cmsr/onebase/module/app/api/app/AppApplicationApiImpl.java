package com.cmsr.onebase.module.app.api.app;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.app.core.dal.database.app.AppApplicationRepository;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author：huangjie
 * @Date：2025/8/13 10:36
 */
@Setter
@RestController
@Validated
public class AppApplicationApiImpl implements AppApplicationApi {

    @Resource
    private AppApplicationRepository appApplicationRepository;

    @Override
    public CommonResult<Long> countApplicationByTenantId(Long tenantId) {
        Long count = appApplicationRepository.countByTenantId(tenantId);
        return CommonResult.success(count);
    }

}
