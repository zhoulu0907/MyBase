package com.cmsr.onebase.module.system.service.user;

import com.cmsr.onebase.framework.common.enums.XFromSceneTypeEnum;
import com.cmsr.onebase.module.system.dal.database.user.AbstractUserDataRepository;
import com.cmsr.onebase.module.system.dal.database.user.CorpUserDataRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service("corpUserService")
@Validated
public class CorpUserServiceImpl extends AbstractUserServiceImpl {

    @Resource
    private CorpUserDataRepository corpUserDataRepository;

    @Override
    public AbstractUserDataRepository getAdminUserDataRepository() {
        return corpUserDataRepository;
    }

    @Override
    public String getXFromSceneType() {
        return XFromSceneTypeEnum.CORP.getCode();
    }
}
