package com.cmsr.onebase.plugin.service;

import com.cmsr.onebase.plugin.model.UserInfo;

import java.util.List;
import java.util.Set;

/**
 * 用户信息服务
 * <p>
 * 提供用户、角色、权限等信息查询能力。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-18
 */
public interface UserService {

    // ==================== 当前用户 ====================

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    UserInfo getCurrentUser();

    /**
     * 获取当前用户ID
     *
     * @return 用户ID
     */
    Long getCurrentUserId();

    /**
     * 获取当前用户的角色编码列表
     *
     * @return 角色编码集合
     */
    Set<String> getCurrentUserRoles();

    /**
     * 获取当前用户的权限标识列表
     *
     * @return 权限标识集合
     */
    Set<String> getCurrentUserPermissions();

    // ==================== 权限校验 ====================

    /**
     * 检查当前用户是否拥有指定权限
     *
     * @param permission 权限标识
     * @return true表示拥有权限
     */
    boolean hasPermission(String permission);

    /**
     * 检查当前用户是否拥有指定角色
     *
     * @param roleCode 角色编码
     * @return true表示拥有角色
     */
    boolean hasRole(String roleCode);

    /**
     * 检查当前用户是否拥有任一权限
     *
     * @param permissions 权限标识列表
     * @return true表示拥有至少一个权限
     */
    boolean hasAnyPermission(String... permissions);

    /**
     * 检查当前用户是否拥有任一角色
     *
     * @param roleCodes 角色编码列表
     * @return true表示拥有至少一个角色
     */
    boolean hasAnyRole(String... roleCodes);

    // ==================== 用户查询 ====================

    /**
     * 根据ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserInfo getById(Long userId);

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    UserInfo getByUsername(String username);

    /**
     * 批量获取用户信息
     *
     * @param userIds 用户ID列表
     * @return 用户信息列表
     */
    List<UserInfo> listByIds(List<Long> userIds);

    /**
     * 根据部门ID获取用户列表
     *
     * @param deptId 部门ID
     * @return 用户信息列表
     */
    List<UserInfo> listByDeptId(Long deptId);

    /**
     * 根据角色编码获取用户列表
     *
     * @param roleCode 角色编码
     * @return 用户信息列表
     */
    List<UserInfo> listByRoleCode(String roleCode);
}
