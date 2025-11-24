package com.cmsr.onebase.module.system.service.user;

import com.cmsr.onebase.framework.common.enums.XFromSceneTypeEnum;
import com.cmsr.onebase.module.system.dal.database.user.AbstractUserDataRepository;
import com.cmsr.onebase.module.system.dal.database.user.PlatformUserDataRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service("platformUserService")
@Validated
public class PlatformUserServiceImpl extends AbstractUserServiceImpl {

    @Resource
    private PlatformUserDataRepository platformUserDataRepository;

    @Override
    public AbstractUserDataRepository getAdminUserDataRepository() {
        return platformUserDataRepository;
    }

    @Override
    public String getXFromSceneType() {
        return XFromSceneTypeEnum.PLATFORM.getCode();
    }
}
