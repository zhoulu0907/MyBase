package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleMenuDO;
import com.cmsr.onebase.framework.orm.repo.BaseDataRepository;
import com.cmsr.onebase.module.system.dal.flex.mapper.SystemRoleMenuMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.cmsr.onebase.module.system.dal.dataobject.permission.RoleMenuDO.MENU_ID;
import static com.cmsr.onebase.module.system.dal.dataobject.permission.RoleMenuDO.ROLE_ID;

/**
 * 角色菜单 DataRepository
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class RoleMenuDataRepository extends BaseDataRepository<SystemRoleMenuMapper, RoleMenuDO> {

    public List<RoleMenuDO> findListByRoleId(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        return list(query().eq(ROLE_ID, roleId));
    }

    public List<RoleMenuDO> findListByRoleIds(Collection<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return list(query().in(ROLE_ID, roleIds));
    }

    public List<RoleMenuDO> findListByMenuId(Long menuId) {
        if (menuId == null) {
            return Collections.emptyList();
        }
        return list(query().eq(MENU_ID, menuId));
    }

    public long deleteByRoleIdAndMenuIds(Long roleId, Collection<Long> menuIds) {
        if (roleId == null || menuIds == null || menuIds.isEmpty()) {
            return 0L;
        }
        return mapper.deleteByQuery(query().eq(ROLE_ID, roleId).in(MENU_ID, menuIds));
    }

    public long deleteByRoleId(Long roleId) {
        if (roleId == null) {
            return 0L;
        }
        return mapper.deleteByQuery(query().eq(ROLE_ID, roleId));
    }

    public long deleteByMenuId(Long menuId) {
        if (menuId == null) {
            return 0L;
        }
        return mapper.deleteByQuery(query().eq(MENU_ID, menuId));
    }
}
