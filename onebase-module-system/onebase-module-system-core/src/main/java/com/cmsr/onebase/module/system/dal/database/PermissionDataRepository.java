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
 * 角色菜单权限数据访问层
 *
 * @author matianyu
 * @date 2025-12-22
 */
@Repository
public class PermissionDataRepository extends BaseDataRepository<SystemRoleMenuMapper, RoleMenuDO> {

    /**
     * 根据角色ID查询角色菜单列表
     *
     * @param roleId 角色ID
     * @return 角色菜单列表
     */
    public List<RoleMenuDO> findRoleMenuListByRoleId(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        return list(query().eq(ROLE_ID, roleId));
    }

    /**
     * 根据角色ID集合查询角色菜单列表
     *
     * @param roleIds 角色ID集合
     * @return 角色菜单列表
     */
    public List<RoleMenuDO> findRoleMenuListByRoleIds(Collection<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return list(query().in(ROLE_ID, roleIds));
    }

    /**
     * 根据菜单ID查询角色菜单列表
     *
     * @param menuId 菜单ID
     * @return 角色菜单列表
     */
    public List<RoleMenuDO> findRoleMenuListByMenuId(Long menuId) {
        if (menuId == null) {
            return Collections.emptyList();
        }
        return list(query().eq(MENU_ID, menuId));
    }

    /**
     * 根据角色ID删除角色菜单关系
     *
     * @param roleId 角色ID
     * @return 删除数量
     */
    public long deleteRoleMenuByRoleId(Long roleId) {
        if (roleId == null) {
            return 0L;
        }
        return mapper.deleteByQuery(query().eq(ROLE_ID, roleId));
    }

    /**
     * 根据菜单ID删除角色菜单关系
     *
     * @param menuId 菜单ID
     * @return 删除数量
     */
    public long deleteRoleMenuByMenuId(Long menuId) {
        if (menuId == null) {
            return 0L;
        }
        return mapper.deleteByQuery(query().eq(MENU_ID, menuId));
    }
}
