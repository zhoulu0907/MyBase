package com.cmsr.onebase.module.app.core.impl.auth;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.app.api.auth.AppAuthRoleUser;
import com.cmsr.onebase.module.app.api.auth.dto.AuthRoleDTO;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleRepository;
import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleUserRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleUserDO;
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
        return appAuthRoleUserRepository.findByByRoleIds(roleIds).stream().map(AppAuthRoleUserDO::getUserId).toList();
    }

    @Override
    public List<Long> findRoleIdsByAppId(Long appId) {
        return appAuthRoleRepository.findByApplicationId(appId).stream().map(AppAuthRoleDO::getId).toList();
    }

    @Override
    public List<AuthRoleDTO> findRolesByUserId(Long userId) {
        List<Long> list = appAuthRoleUserRepository.findByUserId(userId).stream().map(AppAuthRoleUserDO::getRoleId).toList();
        List<AppAuthRoleDO> authRoleDOS = appAuthRoleRepository.listByIds(list);
        List<AuthRoleDTO> authRoleDTOS = BeanUtils.toBean(authRoleDOS, AuthRoleDTO.class);
        return authRoleDTOS;
    }

    @Override
    public void grantThirdpartyUserPrivileges(Long userId, Long applicationId) {
        // TODO: 理论上这边的insert方法是要手动添加租户条件的，由于上层调用未配置，暂时忽略
        AppAuthRoleDO roleDO = appAuthRoleRepository.findThirdpartyRoleByApplicationId(applicationId);
        if (roleDO == null) {
            throw new IllegalStateException("应用" + applicationId + "不存在外部用户");
        }
        boolean exists = appAuthRoleUserRepository.existsByUserIdAndRoleId(roleDO.getId(), userId);
        if (!exists) {
            AppAuthRoleUserDO authRoleUserDO = new AppAuthRoleUserDO();
            authRoleUserDO.setRoleId(roleDO.getId());
            authRoleUserDO.setUserId(userId);
            appAuthRoleUserRepository.save(authRoleUserDO);
        }
    }

}