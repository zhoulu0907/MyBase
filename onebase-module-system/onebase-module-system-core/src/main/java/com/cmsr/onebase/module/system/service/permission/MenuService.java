package com.cmsr.onebase.module.system.service.permission;

import com.cmsr.onebase.module.system.vo.menu.SystemMenuListReqVO;
import com.cmsr.onebase.module.system.vo.menu.SystemMenuSaveVO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.MenuDO;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 菜单 Service 接口
 *
 */
public interface MenuService {

    /**
     * 创建菜单
     *
     * @param createReqVO 菜单信息
     * @return 创建出来的菜单编号
     */
    Long createMenu(SystemMenuSaveVO createReqVO);

    /**
     * 更新菜单
     *
     * @param updateReqVO 菜单信息
     */
    void updateMenu(SystemMenuSaveVO updateReqVO);

    /**
     * 删除菜单
     *
     * @param id 菜单编号
     */
    void deleteMenu(Long id);

    /**
     * 获得所有菜单列表
     *
     * @return 菜单列表
     */
    List<MenuDO> getAllEnableMenuList();

    /**
     * 基于租户，筛选菜单列表
     * 注意，如果是系统租户，返回的还是全菜单
     *
     * @param reqVO 筛选条件请求 VO
     * @return 菜单列表
     */
    List<MenuDO> getMenuListByTenant(SystemMenuListReqVO reqVO);

    /**
     * 过滤掉关闭的菜单及其子菜单
     *
     * @param list 菜单列表
     * @return 过滤后的菜单列表
     */
    List<MenuDO> filterDisableMenus(List<MenuDO> list);

    /**
     * 筛选菜单列表
     *
     * @param reqVO 筛选条件请求 VO
     * @return 菜单列表
     */
    List<MenuDO> getAllActiveMenuList(SystemMenuListReqVO reqVO);

    /**
     * 获得权限对应的菜单编号数组
     *
     * @param permission 权限标识
     * @return 数组
     */
    List<Long> getMenuIdListByPermissionFromCache(String permission);

    /**
     * 获得菜单
     *
     * @param id 菜单编号
     * @return 菜单
     */
    MenuDO getMenu(Long id);

    /**
     * 获得菜单数组
     *
     * @param ids 菜单编号数组
     * @return 菜单数组
     */
    List<MenuDO> getAllActiveMenuList(Collection<Long> ids);
    /**
     * 获得菜单数组
     *
     * @param menuCodes 菜单编号数组
     * @return 菜单数组
     */
    List<MenuDO> getAllActiveMenuListByCodes(Set<String> menuCodes);
}
