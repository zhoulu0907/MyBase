package com.cmsr.onebase.module.app.api.auth;

import com.cmsr.onebase.module.app.api.auth.dto.AuthRoleDTO;

import java.util.Collection;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/10/27 14:29
 */
public interface AppAuthRoleUserService {

    void deleteByUserId(Long userId);

    void deleteByUserIds(Collection<Long> userIds);

    void deleteByTenant(Long tenantId);

    List<Long> findUserIdsByRoleIds(List<Long> roleIds);

    List<Long> findRoleIdsByAppId(Long appId);

    List<AuthRoleDTO> findRolesByUserId(Long userId);

    void grantThirdpartyUserPrivileges(Long userId, Long applicationId);

}
