package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleMenuDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * 角色菜单 DataRepository
 *
 * @author matianyu
 * @date 2025-01-27
 */
@Repository
public class RoleMenuDataRepository extends DataRepository<RoleMenuDO> {

    public RoleMenuDataRepository() {
        super(RoleMenuDO.class);
    }

    public List<RoleMenuDO> findListByRoleId(Long roleId) {
        return findAllByConfig(new DefaultConfigStore()
                .and(Compare.EQUAL, RoleMenuDO.ROLE_ID, roleId));
    }

    public List<RoleMenuDO> findListByRoleIds(Collection<Long> roleIds) {
        return findAllByConfig(new DefaultConfigStore()
                .in(RoleMenuDO.ROLE_ID, roleIds));
    }

    public List<RoleMenuDO> findListByMenuId(Long menuId) {
        return findAllByConfig(new DefaultConfigStore()
                .and(Compare.EQUAL, RoleMenuDO.MENU_ID, menuId));
    }

    public long deleteByRoleIdAndMenuIds(Long roleId, Collection<Long> menuIds) {
        return deleteByConfig(new DefaultConfigStore()
                .eq(RoleMenuDO.ROLE_ID, roleId).in(RoleMenuDO.MENU_ID, menuIds));
    }

    public long deleteByRoleId(Long roleId) {
        return deleteByConfig(new DefaultConfigStore()
                .eq(RoleMenuDO.ROLE_ID, roleId));
    }

    public long deleteByMenuId(Long menuId) {
        return deleteByConfig(new DefaultConfigStore()
                .eq(RoleMenuDO.MENU_ID, menuId));
    }
}

