package com.cmsr.onebase.module.system.service.user;

import com.cmsr.onebase.framework.common.enums.XFromSceneTypeEnum;
import com.cmsr.onebase.module.system.dal.database.user.AbstractUserDataRepository;
import com.cmsr.onebase.module.system.dal.database.user.AdminUserDataRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service("userService")
public class UserServiceImpl extends AbstractUserServiceImpl {

    @Resource
    private AdminUserDataRepository adminUserDataRepository;

    @Override
    public AbstractUserDataRepository getAdminUserDataRepository() {
        return adminUserDataRepository;
    }

    @Override
    public String getXFromSceneType() {
        return XFromSceneTypeEnum.ALL.getCode();
    }
}
