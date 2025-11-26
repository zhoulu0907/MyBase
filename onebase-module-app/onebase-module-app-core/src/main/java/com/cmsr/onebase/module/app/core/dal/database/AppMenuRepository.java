package com.cmsr.onebase.module.app.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.PageSetDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.PageSetPageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.MenuDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppMenuMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppMenuTableDef.APP_MENU;
import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppResourcePagesetPageTableDef.APP_RESOURCE_PAGESET_PAGE;
import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppResourcePagesetTableDef.APP_RESOURCE_PAGESET;

/**
 * @Author：huangjie
 * @Date：2025/8/6 9:31
 */
@Repository
// TODO: applicationId 需要托管至BaseAppRepository
public class AppMenuRepository extends BaseAppRepository<AppMenuMapper, MenuDO> {

    public List<MenuDO> findByApplicationIdAndType(Long applicationId, Set<Integer> menuTypes) {
        QueryWrapper queryWrapper = this.query()
                .eq(MenuDO::getApplicationId, applicationId)
                .in(MenuDO::getMenuType, menuTypes)
                .orderBy(MenuDO::getMenuSort, true);
        return list(queryWrapper);
    }

    public List<MenuDO> findByApplicationId(Long applicationId) {
        QueryWrapper queryWrapper = this.query()
                .eq(MenuDO::getApplicationId, applicationId)
                .orderBy(MenuDO::getMenuSort, true);
        return list(queryWrapper);
    }

    public long countByParentId(Long id) {
        QueryWrapper queryWrapper = this.query().eq(MenuDO::getParentId, id);
        return count(queryWrapper);
    }

    public void deleteByApplicationId(Long applicationId) {
        this.updateChain()
                .eq(MenuDO::getApplicationId, applicationId)
                .remove();
    }


    public int countByApplicationId(Long applicationId) {
        QueryWrapper queryWrapper = this.query().eq(MenuDO::getApplicationId, applicationId);
        return (int) count(queryWrapper);
    }

    public List<MenuDO> findVisibleByAppId(Long applicationId) {
        QueryWrapper queryWrapper = this.query().eq(MenuDO::getApplicationId, applicationId)
                .eq(MenuDO::getIsVisible, 1);
        return list(queryWrapper);
    }

    public List<MenuDO> findVisibleByAppIdAndMenuIds(Long applicationId, Set<Long> menuIds) {
        QueryWrapper queryWrapper = this.query()
                .eq(MenuDO::getApplicationId, applicationId)
                .in(MenuDO::getId, menuIds)
                .eq(MenuDO::getIsVisible, 1);
        return list(queryWrapper);
    }

    public Set<Long> findPageIdsByAppIdAndMenuId(Long applicationId, Long menuId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(
                        APP_RESOURCE_PAGESET_PAGE.PAGE_ID
                ).from(PageSetPageDO.class)
                .leftJoin(PageSetDO.class)
                .on(APP_RESOURCE_PAGESET_PAGE.PAGESET_ID.eq(APP_RESOURCE_PAGESET.ID))
                .leftJoin(MenuDO.class)
                .on(APP_RESOURCE_PAGESET.MENU_ID.eq(APP_MENU.ID))
                .where(APP_MENU.IS_VISIBLE.eq(1))
                .and(APP_MENU.APPLICATION_ID.eq(applicationId))
                .and(APP_MENU.ID.eq(menuId));
        return new HashSet<>(this.objListAs(queryWrapper, Long.class));
    }


}
