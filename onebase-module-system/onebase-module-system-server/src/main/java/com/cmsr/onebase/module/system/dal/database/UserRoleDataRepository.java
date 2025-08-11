package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.module.system.dal.dataobject.permission.UserRoleDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 用户角色 DataRepository
 *
 * @author matianyu
 * @date 2025-01-27
 */
@Repository
public class UserRoleDataRepository extends DataRepositoryNew<UserRoleDO> {

    public UserRoleDataRepository() {
        super(UserRoleDO.class);
    }

    public List<UserRoleDO> findListByUserId(Long userId) {
        return findAllByConfig(new DefaultConfigStore()
                .and(Compare.EQUAL, UserRoleDO.USER_ID, userId));
    }

    public List<UserRoleDO> findListByRoleIds(Collection<Long> roleIds) {
        return findAllByConfig(new DefaultConfigStore()
                .in(UserRoleDO.ROLE_ID, roleIds));
    }

    public long deleteByUserIdAndRoleIds(Long userId, Collection<Long> roleIds) {
        return deleteByConfig(new DefaultConfigStore()
                .eq(UserRoleDO.USER_ID, userId).in(UserRoleDO.ROLE_ID, roleIds));
    }

    public long deleteByUserId(Long userId) {
        return deleteByConfig(new DefaultConfigStore()
                .eq(UserRoleDO.USER_ID, userId));
    }

    public long deleteByRoleId(Long roleId) {
        return deleteByConfig(new DefaultConfigStore()
                .eq(UserRoleDO.ROLE_ID, roleId));
    }

    public long deleteByRoleIdAndUserIds(Long roleId, Set<Long> userIds) {
        return deleteByConfig(new DefaultConfigStore()
                .eq(UserRoleDO.ROLE_ID, roleId).in(UserRoleDO.USER_ID, userIds));
    }
}
