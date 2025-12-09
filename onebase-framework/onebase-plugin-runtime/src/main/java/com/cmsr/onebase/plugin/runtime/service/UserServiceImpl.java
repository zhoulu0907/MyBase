package com.cmsr.onebase.plugin.runtime.service;

import com.cmsr.onebase.plugin.model.UserInfo;
import com.cmsr.onebase.plugin.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户服务实现
 * <p>
 * 桥接平台的用户服务，提供给插件使用。
 * TODO: 实际使用时需要注入平台的UserService来实现真实的用户操作。
 * </p>
 *
 * @author matianyu
 * @date 2025-11-29
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    // TODO: 注入平台的用户服务
    // @Resource
    // private com.cmsr.onebase.module.system.service.UserService platformUserService;

    @Override
    public UserInfo getCurrentUser() {
        log.debug("UserService.getCurrentUser");
        // TODO: 从SecurityContext获取当前用户信息
        return null;
    }

    @Override
    public Long getCurrentUserId() {
        log.debug("UserService.getCurrentUserId");
        // TODO: 从SecurityContext获取当前用户ID
        return null;
    }

    @Override
    public Set<String> getCurrentUserRoles() {
        log.debug("UserService.getCurrentUserRoles");
        // TODO: 从SecurityContext获取当前用户角色
        return new HashSet<>();
    }

    @Override
    public Set<String> getCurrentUserPermissions() {
        log.debug("UserService.getCurrentUserPermissions");
        // TODO: 从SecurityContext获取当前用户权限
        return new HashSet<>();
    }

    @Override
    public boolean hasPermission(String permission) {
        log.debug("UserService.hasPermission: permission={}", permission);
        // TODO: 调用平台权限校验
        return false;
    }

    @Override
    public boolean hasRole(String roleCode) {
        log.debug("UserService.hasRole: roleCode={}", roleCode);
        // TODO: 调用平台角色校验
        return false;
    }

    @Override
    public boolean hasAnyPermission(String... permissions) {
        log.debug("UserService.hasAnyPermission: permissions={}", (Object) permissions);
        // TODO: 调用平台权限校验
        return false;
    }

    @Override
    public boolean hasAnyRole(String... roleCodes) {
        log.debug("UserService.hasAnyRole: roleCodes={}", (Object) roleCodes);
        // TODO: 调用平台角色校验
        return false;
    }

    @Override
    public UserInfo getById(Long userId) {
        log.debug("UserService.getById: userId={}", userId);
        // TODO: 调用平台服务实现
        return null;
    }

    @Override
    public UserInfo getByUsername(String username) {
        log.debug("UserService.getByUsername: username={}", username);
        // TODO: 调用平台服务实现
        return null;
    }

    @Override
    public List<UserInfo> listByIds(List<Long> userIds) {
        log.debug("UserService.listByIds: userIds={}", userIds);
        // TODO: 调用平台服务实现
        return Collections.emptyList();
    }

    @Override
    public List<UserInfo> listByDeptId(Long deptId) {
        log.debug("UserService.listByDeptId: deptId={}", deptId);
        // TODO: 调用平台服务实现
        return Collections.emptyList();
    }

    @Override
    public List<UserInfo> listByRoleCode(String roleCode) {
        log.debug("UserService.listByRoleCode: roleCode={}", roleCode);
        // TODO: 调用平台服务实现
        return Collections.emptyList();
    }
}
