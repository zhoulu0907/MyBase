package com.cmsr.onebase.module.app.core.impl.auth;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.auth.AppAuthRoleUser;
import com.cmsr.onebase.module.app.api.auth.dto.AuthRoleDTO;
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
    public List<Long> findUserIdsByRoleIds(List<Long> roleIds) {
        return appAuthRoleUserRepository.findByByRoleIds(roleIds).stream().map(AuthRoleUserDO::getUserId).toList();
    }

    @Override
    public List<Long> findRoleIdsByAppId(Long appId) {
        return appAuthRoleRepository.findByApplicationId(appId).stream().map(AuthRoleDO::getId).toList();
    }

    @Override
    public List<AuthRoleDTO> findRolesByUserId(Long userId) {
        List<Long> list = appAuthRoleUserRepository.findByUserId(userId).stream().map(AuthRoleUserDO::getRoleId).toList();
        List<AuthRoleDO> authRoleDOS = appAuthRoleRepository.findAllByIds(list);
        List<AuthRoleDTO> authRoleDTOS = BeanUtils.toBean(authRoleDOS, AuthRoleDTO.class);
        return authRoleDTOS;
    }

    @Override
    public boolean findAdminByRoleIdAndUserId(Long roleId, Long userId) {
      List<AuthRoleUserDO> userRoleDOList=  appAuthRoleUserRepository.findAdminByRoleIdAndUserId(roleId,userId);
        return !userRoleDOList.isEmpty();
    }

}