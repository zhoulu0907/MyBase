package com.cmsr.onebase.module.app.core.dal.provider.auth;

import com.cmsr.onebase.module.app.core.dal.database.auth.AppAuthRoleRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthRoleDO;
import com.cmsr.onebase.module.app.core.dto.auth.UserRole;
import com.cmsr.onebase.module.app.core.enums.auth.AuthRoleTypeEnum;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/10/25 14:58
 */
@Slf4j
@Setter
@Service
public class AppAuthRoleProvider {

    @Autowired
    private AppAuthRoleRepository appAuthRoleRepository;

    public UserRole findByUserIdAndApplicationId(Long userId, Long applicationId) {
        List<AuthRoleDO> authRoleDOS = appAuthRoleRepository.findByUserIdAndApplicationId(userId, applicationId);
        UserRole userRole = new UserRole();
        userRole.setAdminRole(false);
        if (authRoleDOS != null && !authRoleDOS.isEmpty()) {
            for (AuthRoleDO authRoleDO : authRoleDOS) {
                if (AuthRoleTypeEnum.isSystemAdminRole(authRoleDO.getRoleType())) {
                    userRole.setAdminRole(true);
                }
            }
            Set<Long> ids = authRoleDOS.stream().map(AuthRoleDO::getId).collect(Collectors.toSet());
            userRole.setRoleIds(ids);
        } else {
            userRole.setRoleIds(Set.of());
        }
        return userRole;
    }

}
