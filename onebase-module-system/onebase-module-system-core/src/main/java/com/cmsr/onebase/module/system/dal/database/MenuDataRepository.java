package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.system.vo.menu.SystemMenuListReqVO;
import com.cmsr.onebase.module.system.dal.dataobject.permission.MenuDO;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 菜单数据访问层
 *
 * @author matianyu
 * @date 2025-08-11
 */
@Repository
public class MenuDataRepository extends DataRepository<MenuDO> {

    public MenuDataRepository() {
        super(MenuDO.class);
    }

    /**
     * 根据父菜单ID统计子菜单数量
     *
     * @param parentId 父菜单ID
     * @return 子菜单数量
     */
    public int countByParentId(Long parentId) {
        List<MenuDO> menuDOS = findAllByConfig(new DefaultConfigStore().and(Compare.EQUAL, "parent_id", parentId));
        return menuDOS.size();
    }

    /**
     * 根据父菜单ID和菜单名称查找菜单
     *
     * @param parentId 父菜单ID
     * @param name 菜单名称
     * @return 菜单对象
     */
    public MenuDO findOneByParentIdAndName(Long parentId, String name) {
        return findOne(new DefaultConfigStore()
                .and(Compare.EQUAL, "parent_id", parentId)
                .and(Compare.EQUAL, "name", name));
    }

    /**
     * 根据组件名称查找菜单
     *
     * @param componentName 组件名称
     * @return 菜单对象
     */
    public MenuDO findOneByComponentName(String componentName) {
        return findOne(new DefaultConfigStore().and(Compare.EQUAL, "component_name", componentName));
    }

    /**
     * 根据权限标识查找菜单列表
     *
     * @param permission 权限标识
     * @return 菜单列表
     */
    public List<MenuDO> findListByPermission(String permission) {
        return findAllByConfig(new DefaultConfigStore().and(Compare.EQUAL, "permission", permission));
    }

    /**
     * 根据条件查询菜单列表
     *
     * @param reqVO 查询条件
     * @return 菜单列表
     */
    public List<MenuDO> findList(SystemMenuListReqVO reqVO) {
        DefaultConfigStore configStore = new DefaultConfigStore();

        if (StringUtils.isNotBlank(reqVO.getName())) {
            configStore.like(MenuDO.NAME, reqVO.getName());
        }
        if (StringUtils.isNotBlank(reqVO.getCode())) {
            configStore.startWith(MenuDO.PERMISSION, reqVO.getCode());
        }
        if (reqVO.getStatus() != null) {
            configStore.eq(MenuDO.STATUS, reqVO.getStatus());
        }

        return findAllByConfig(configStore);
    }

    /**
     * 根据code查询菜单列表
     *
     * @param codes 查询条件
     * @return 菜单列表
     */
    public List<MenuDO> findAllByCodes(Set<String> codes) {
        DefaultConfigStore configStore = new DefaultConfigStore();
        configStore.in(MenuDO.PERMISSION, codes);
        return findAllByConfig(configStore);
    }
}
