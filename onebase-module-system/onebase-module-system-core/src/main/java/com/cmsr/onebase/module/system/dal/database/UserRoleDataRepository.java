package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.permission.UserRoleDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 用户角色关联数据访问层
 *
 * 负责用户角色关联相关的数据操作，继承DataRepositoryNew，提供标准CRUD能力。
 *
 * @author matianyu
 * @date 2025-08-18
 */
@Repository
public class UserRoleDataRepository extends DataRepository<UserRoleDO> {

    /**
     * 构造方法，指定默认实体类
     */
    public UserRoleDataRepository() {
        super(UserRoleDO.class);
    }

    /**
     * 根据用户ID查询用户角色关联
     *
     * @param userId 用户ID
     * @return 用户角色关联列表
     */
    public List<UserRoleDO> findListByUserId(Long userId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(UserRoleDO.USER_ID, userId);
        return findAllByConfig(configStore);
    }

    /**
     * 根据角色ID查询用户角色关联
     *
     * @param roleId 角色ID
     * @return 用户角色关联列表
     */
    public List<UserRoleDO> findListByRoleIds(Long roleId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(UserRoleDO.ROLE_ID, roleId);
        return findAllByConfig(configStore);
    }

    /**
     * 根据角色ID列表查询用户角色关联
     *
     * @param roleIds 角色ID列表
     * @return 用户角色关联列表
     */
    public List<UserRoleDO> findListByRoleIds(Collection<Long> roleIds) {
        return findAllByConfig(new DefaultConfigStore()
                .in(UserRoleDO.ROLE_ID, roleIds));
    }

    /**
     * 根据用户ID和角色ID查询用户角色关联
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 用户角色关联对象
     */
    public UserRoleDO findByUserIdAndRoleId(Long userId, Long roleId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(UserRoleDO.USER_ID, userId).eq(UserRoleDO.ROLE_ID, roleId);
        return findOne(configStore);
    }

    /**
     * 根据用户ID删除用户角色关联
     *
     * @param userId 用户ID
     */
    public void deleteByUserId(Long userId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(UserRoleDO.USER_ID, userId);
        deleteByConfig(configStore);
    }

    /**
     * 根据角色ID和用户ID列表删除用户角色关联
     *
     * @param roleId 角色ID
     * @param userIds 用户ID列表
     */
    public void deleteByRoleIdAndUserIds(Long roleId, Collection<Long> userIds) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(UserRoleDO.ROLE_ID, roleId).in(UserRoleDO.USER_ID, userIds);
        deleteByConfig(configStore);
    }

    public long deleteByUserIdAndRoleIds(Long userId, Collection<Long> roleIds) {
        return deleteByConfig(new DefaultConfigStore()
                .eq(UserRoleDO.USER_ID, userId).in(UserRoleDO.ROLE_ID, roleIds));
    }


    public long deleteByRoleId(Long roleId) {
        return deleteByConfig(new DefaultConfigStore()
                .eq(UserRoleDO.ROLE_ID, roleId));
    }

    public long deleteByRoleIdAndUserIds(Long roleId, Set<Long> userIds) {
        return deleteByConfig(new DefaultConfigStore()
                .eq(UserRoleDO.ROLE_ID, roleId).in(UserRoleDO.USER_ID, userIds));
    }

    /**
     *  configStore.eq(UserRoleDO.TENANT_ID,tenantId); 
     *  通过id，查询租户信息，前面忽略掉了切面的租户
     * @param roleId
     * @param tenantId
     * @return
     */
    public List<UserRoleDO> getUserRoleByRoleIdAndTenantId(Long roleId,Long tenantId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(UserRoleDO.ROLE_ID, roleId);
        configStore.eq(UserRoleDO.TENANT_ID,tenantId);
        return findAllByConfig(configStore);
    }

    public List<UserRoleDO> findAdminByRoleIdAndUserId(Long roleId, Long userId) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(UserRoleDO.ROLE_ID, roleId);
        configStore.eq(UserRoleDO.USER_ID, userId);
        return findAllByConfig(configStore);
    }

    public List<UserRoleDO> getRoleByUserIds(List<Long> userIds) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.eq(UserRoleDO.USER_ID, userIds);
        return findAllByConfig(configStore);
    }
}
