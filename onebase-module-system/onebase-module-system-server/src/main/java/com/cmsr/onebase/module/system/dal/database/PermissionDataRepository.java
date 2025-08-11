package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.module.system.dal.dataobject.permission.RoleMenuDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * 角色菜单权限数据访问层
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
public class PermissionDataRepository extends DataRepositoryNew<RoleMenuDO> {

    public PermissionDataRepository() {
        super(RoleMenuDO.class);
    }

    /**
     * 根据角色ID查询角色菜单列表
     *
     * @param roleId 角色ID
     * @return 角色菜单列表
     */
    public List<RoleMenuDO> findRoleMenuListByRoleId(Long roleId) {
        return findAllByConfig(new DefaultConfigStore().and(Compare.EQUAL, RoleMenuDO.ROLE_ID, roleId));
    }

    /**
     * 根据角色ID集合查询角色菜单列表
     *
     * @param roleIds 角色ID集合
     * @return 角色菜单列表
     */
    public List<RoleMenuDO> findRoleMenuListByRoleIds(Collection<Long> roleIds) {
        return findAllByConfig(new DefaultConfigStore().and(Compare.IN, RoleMenuDO.ROLE_ID, roleIds));
    }

    /**
     * 根据菜单ID查询角色菜单列表
     *
     * @param menuId 菜单ID
     * @return 角色菜单列表
     */
    public List<RoleMenuDO> findRoleMenuListByMenuId(Long menuId) {
        return findAllByConfig(new DefaultConfigStore().and(Compare.EQUAL, RoleMenuDO.MENU_ID, menuId));
    }

    /**
     * 根据角色ID删除角色菜单关系
     *
     * @param roleId 角色ID
     * @return 删除数量
     */
    public long deleteRoleMenuByRoleId(Long roleId) {
        return deleteByConfig(new DefaultConfigStore().and(Compare.EQUAL, RoleMenuDO.ROLE_ID, roleId));
    }

    /**
     * 根据菜单ID删除角色菜单关系
     *
     * @param menuId 菜单ID
     * @return 删除数量
     */
    public long deleteRoleMenuByMenuId(Long menuId) {
        return deleteByConfig(new DefaultConfigStore().and(Compare.EQUAL, RoleMenuDO.MENU_ID, menuId));
    }
}
