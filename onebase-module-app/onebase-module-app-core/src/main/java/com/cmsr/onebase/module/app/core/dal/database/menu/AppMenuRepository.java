package com.cmsr.onebase.module.app.core.dal.database.menu;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppMenuDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePageDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetPageDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppMenuMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppMenuTableDef.APP_MENU;
import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppResourcePageTableDef.APP_RESOURCE_PAGE;
import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppResourcePagesetPageTableDef.APP_RESOURCE_PAGESET_PAGE;
import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppResourcePagesetTableDef.APP_RESOURCE_PAGESET;

/**
 * @Author：huangjie
 * @Date：2025/8/6 9:31
 */
@Repository
public class AppMenuRepository extends BaseBizRepository<AppMenuMapper, AppMenuDO> {

    public List<AppMenuDO> findByApplicationIdAndType(Long applicationId, Set<Integer> menuTypes) {
        QueryWrapper queryWrapper = this.query()
                .eq(AppMenuDO::getApplicationId, applicationId)
                .in(AppMenuDO::getMenuType, menuTypes)
                .orderBy(AppMenuDO::getMenuSort, true);
        return list(queryWrapper);
    }

    public List<AppMenuDO> findByApplicationId(Long applicationId) {
        QueryWrapper queryWrapper = this.query()
                .eq(AppMenuDO::getApplicationId, applicationId)
                .orderBy(AppMenuDO::getMenuSort, true);
        return list(queryWrapper);
    }

    public long countByParentId(Long applicationId, String menuUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_MENU.APPLICATION_ID.eq(applicationId))
                .where(APP_MENU.MENU_UUID.eq(menuUuid));
        return count(queryWrapper);
    }

    public AppMenuDO findByAppIdAndMenuUuid(Long applicationId, String menuUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_MENU.APPLICATION_ID.eq(applicationId))
                .where(APP_MENU.MENU_UUID.eq(menuUuid));
        return this.getOne(queryWrapper);
    }

    public void deleteByApplicationId(Long applicationId) {
        this.updateChain()
                .eq(AppMenuDO::getApplicationId, applicationId)
                .remove();
    }


    public int countByApplicationId(Long applicationId) {
        QueryWrapper queryWrapper = this.query().eq(AppMenuDO::getApplicationId, applicationId);
        return (int) count(queryWrapper);
    }

    public List<AppMenuDO> findVisibleByAppId(Long applicationId) {
        QueryWrapper queryWrapper = this.query().eq(AppMenuDO::getApplicationId, applicationId)
                .eq(AppMenuDO::getIsVisible, 1);
        return list(queryWrapper);
    }

    public List<AppMenuDO> findVisibleByAppIdAndMenuIds(Long applicationId, Set<String> menuUuids) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_MENU.APPLICATION_ID.eq(applicationId))
                .where(APP_MENU.MENU_UUID.in(menuUuids))
                .where(APP_MENU.IS_VISIBLE.eq(1));
        return list(queryWrapper);
    }

    public List<AppResourcePagesetPageDO> findPagesetPageByMenuId(Long menuId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(
                        APP_RESOURCE_PAGESET_PAGE.ALL_COLUMNS
                ).from(APP_RESOURCE_PAGESET_PAGE)
                .leftJoin(APP_RESOURCE_PAGESET)
                .on(APP_RESOURCE_PAGESET_PAGE.PAGESET_UUID.eq(APP_RESOURCE_PAGESET.PAGESET_UUID)
                        .and(APP_RESOURCE_PAGESET_PAGE.APPLICATION_ID.eq(APP_RESOURCE_PAGESET.APPLICATION_ID))
                        .and(APP_RESOURCE_PAGESET_PAGE.VERSION_TAG.eq(APP_RESOURCE_PAGESET.VERSION_TAG))
                )
                .leftJoin(APP_MENU)
                .on(APP_RESOURCE_PAGESET.MENU_UUID.eq(APP_MENU.MENU_UUID)
                        .and(APP_RESOURCE_PAGESET.APPLICATION_ID.eq(APP_MENU.APPLICATION_ID))
                        .and(APP_RESOURCE_PAGESET.VERSION_TAG.eq(APP_MENU.VERSION_TAG))
                )
                .where(APP_MENU.ID.eq(menuId));
        //.where(APP_MENU.IS_VISIBLE.eq(1));
        return this.listAs(queryWrapper, AppResourcePagesetPageDO.class);
    }

    public List<AppResourcePageDO> findPagesByMenuId(Long menuId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(
                        APP_RESOURCE_PAGE.ALL_COLUMNS
                ).from(APP_RESOURCE_PAGE)
                .leftJoin(APP_MENU)
                .on(APP_RESOURCE_PAGE.MENU_UUID.eq(APP_MENU.MENU_UUID)
                        .and(APP_RESOURCE_PAGE.APPLICATION_ID.eq(APP_MENU.APPLICATION_ID))
                        .and(APP_RESOURCE_PAGE.VERSION_TAG.eq(APP_MENU.VERSION_TAG))
                )
                .where(APP_MENU.ID.eq(menuId));
    }


}
