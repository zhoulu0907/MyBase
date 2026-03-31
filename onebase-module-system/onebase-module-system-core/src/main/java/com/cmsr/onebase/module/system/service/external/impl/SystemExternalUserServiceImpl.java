package com.cmsr.onebase.module.system.service.external.impl;

import com.cmsr.onebase.module.system.dal.dataobject.external.SystemExternalUserDO;
import com.cmsr.onebase.module.system.dal.database.SystemExternalUserDataRepository;
import com.cmsr.onebase.module.system.service.external.SystemExternalUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 外部用户关联服务实现
 *
 * @author matianyu
 * @date 2026-03-17
 */
@Service
public class SystemExternalUserServiceImpl implements SystemExternalUserService {

    @Resource
    private SystemExternalUserDataRepository systemExternalUserDataRepository;

    @Override
    public List<SystemExternalUserDO> getByObUserId(String obUserId) {
        return systemExternalUserDataRepository.findByObUserId(obUserId);
    }

    @Override
    public SystemExternalUserDO getByExternalUserId(String externalUserId, String platformType) {
        return systemExternalUserDataRepository.findByExternalUserIdAndTenantId(externalUserId, platformType);
    }
}

