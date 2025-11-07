package com.cmsr.onebase.module.app.core.impl.auth;

import com.cmsr.onebase.module.app.api.auth.AppAuthRoleUser;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleUserRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthRoleDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthRoleUserDO;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/10/27 14:32
 */
@Setter
@Service
public class AppAuthRoleUserImpl implements AppAuthRoleUser {

    @Autowired
    private AppAuthRoleUserRepository appAuthRoleUserRepository;

    @Autowired
    private AppAuthRoleRepository appAuthRoleRepository;

    @Override
    public void deleteByUserId(Long userId) {
        appAuthRoleUserRepository.deleteByUserId(userId);
    }

    @Override
    public List<Long> findUserIdsByAppIdAndRoleIds(Long appId, List<Long> roleIds) {
        List<AuthRoleDO> authRoleDOS = appAuthRoleRepository.findByAppIdAndRoleIds(appId, roleIds);
        roleIds = authRoleDOS.stream().map(AuthRoleDO::getId).toList();
        return appAuthRoleUserRepository.findByByRoleIds(roleIds).stream().map(AuthRoleUserDO::getUserId).toList();
    }

}
