package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.module.system.dal.dataobject.permission.UserRoleDO;
import com.cmsr.onebase.module.system.dal.flex.base.BaseDataServiceImpl;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemUserRoleMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 用户角色关联数据访问层
 * <p>
 * 基于 MyBatis-Flex 实现用户角色关联相关的 CRUD 及常用查询能力。
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class UserRoleDataRepository extends BaseDataServiceImpl<SystemUserRoleMapper, UserRoleDO> {

    /**
     * 根据用户ID查询用户角色关联
     *
     * @param userId 用户ID
     * @return 用户角色关联列表
     */
    public List<UserRoleDO> findListByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return list(query().eq(UserRoleDO.USER_ID, userId));
    }

    /**
     * 根据角色ID查询用户角色关联
     *
     * @param roleId 角色ID
     * @return 用户角色关联列表
     */
    public List<UserRoleDO> findListByRoleIds(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        return list(query().eq(UserRoleDO.ROLE_ID, roleId));
    }

    /**
     * 根据角色ID列表查询用户角色关联
     *
     * @param roleIds 角色ID列表
     * @return 用户角色关联列表
     */
    public List<UserRoleDO> findListByRoleIds(Collection<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return list(query().in(UserRoleDO.ROLE_ID, roleIds));
    }

    /**
     * 根据用户ID和角色ID查询用户角色关联
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 用户角色关联对象
     */
    public UserRoleDO findByUserIdAndRoleId(Long userId, Long roleId) {
        if (userId == null || roleId == null) {
            return null;
        }
        return getOne(query().eq(UserRoleDO.USER_ID, userId).eq(UserRoleDO.ROLE_ID, roleId));
    }

    /**
     * 根据用户ID删除用户角色关联
     *
     * @param userId 用户ID
     */
    public void deleteByUserId(Long userId) {
        if (userId == null) {
            return;
        }
        remove(query().eq(UserRoleDO.USER_ID, userId));
    }

    /**
     * 根据角色ID和用户ID列表删除用户角色关联
     *
     * @param roleId 角色ID
     * @param userIds 用户ID列表
     */
    public void deleteByRoleIdAndUserIds(Long roleId, Collection<Long> userIds) {
        if (roleId == null || userIds == null || userIds.isEmpty()) {
            return;
        }
        remove(query().eq(UserRoleDO.ROLE_ID, roleId).in(UserRoleDO.USER_ID, userIds));
    }

    /**
     * 根据用户ID和角色ID列表删除用户角色关联
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 删除的行数
     */
    public boolean deleteByUserIdAndRoleIds(Long userId, Collection<Long> roleIds) {
        if (userId == null || roleIds == null || roleIds.isEmpty()) {
            return false;
        }
        return remove(query().eq(UserRoleDO.USER_ID, userId).in(UserRoleDO.ROLE_ID, roleIds));
    }

    /**
     * 根据角色ID删除用户角色关联
     *
     * @param roleId 角色ID
     * @return 删除的行数
     */
    public boolean deleteByRoleId(Long roleId) {
        if (roleId == null) {
            return false;
        }
        return remove(query().eq(UserRoleDO.ROLE_ID, roleId));
    }

    /**
     * 根据角色ID和用户ID集合删除用户角色关联
     *
     * @param roleId 角色ID
     * @param userIds 用户ID集合
     * @return 删除的行数
     */
    public boolean deleteByRoleIdAndUserIds(Long roleId, Set<Long> userIds) {
        if (roleId == null || userIds == null || userIds.isEmpty()) {
            return false;
        }
        return remove(query().eq(UserRoleDO.ROLE_ID, roleId).in(UserRoleDO.USER_ID, userIds));
    }

    /**
     * 通过角色ID和租户ID查询用户角色关联
     * <p>
     * 用于在忽略租户切面的场景下，通过 tenantId 精确查询。
     *
     * @param roleId 角色ID
     * @param tenantId 租户ID
     * @return 用户角色关联列表
     */
    public List<UserRoleDO> getUserRoleByRoleIdAndTenantId(Long roleId, Long tenantId) {
        if (roleId == null || tenantId == null) {
            return Collections.emptyList();
        }
        return list(query().eq(UserRoleDO.ROLE_ID, roleId).eq(UserRoleDO.TENANT_ID, tenantId));
    }

    /**
     * 根据角色ID和用户ID查询是否存在对应关联
     *
     * @param roleId 角色ID
     * @param userId 用户ID
     * @return 用户角色关联列表
     */
    public List<UserRoleDO> findAdminByRoleIdAndUserId(Long roleId, Long userId) {
        if (roleId == null || userId == null) {
            return Collections.emptyList();
        }
        return list(query().eq(UserRoleDO.ROLE_ID, roleId).eq(UserRoleDO.USER_ID, userId));
    }

    /**
     * 根据用户ID列表查询用户角色关联
     *
     * @param userIds 用户ID列表
     * @return 用户角色关联列表
     */
    public List<UserRoleDO> getRoleByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }
        return list(query().in(UserRoleDO.USER_ID, userIds));
    }
}

