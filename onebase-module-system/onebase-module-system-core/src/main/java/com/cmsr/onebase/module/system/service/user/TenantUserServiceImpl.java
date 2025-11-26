package com.cmsr.onebase.module.system.service.user;

import com.cmsr.onebase.framework.common.enums.XFromSceneTypeEnum;
import com.cmsr.onebase.module.system.dal.database.user.AbstractUserDataRepository;
import com.cmsr.onebase.module.system.dal.database.user.TenantUserDataRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service("tenantUserService")
@Validated
public class TenantUserServiceImpl extends AbstractUserServiceImpl {

    @Resource
    private TenantUserDataRepository tenantUserDataRepository;

    @Override
    public AbstractUserDataRepository getAdminUserDataRepository() {
        return tenantUserDataRepository;
    }

    @Override
    public String getXFromSceneType() {
        return XFromSceneTypeEnum.TENANT.getCode();
    }
}
