package com.cmsr.onebase.module.app.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppMenuDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetDO;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppResourcePagesetPageDO;
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
public class AppMenuRepository extends BaseAppRepository<AppMenuMapper, AppMenuDO> {

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

    public long countByParentId(Long id) {
        QueryWrapper queryWrapper = this.query().eq(AppMenuDO::getParentId, id);
        return count(queryWrapper);
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

    public List<AppMenuDO> findVisibleByAppIdAndMenuIds(Long applicationId, Set<Long> menuIds) {
        QueryWrapper queryWrapper = this.query()
                .eq(AppMenuDO::getApplicationId, applicationId)
                .in(AppMenuDO::getId, menuIds)
                .eq(AppMenuDO::getIsVisible, 1);
        return list(queryWrapper);
    }

    public Set<Long> findPageIdsByAppIdAndMenuId(Long applicationId, Long menuId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(
                        APP_RESOURCE_PAGESET_PAGE.PAGE_ID
                ).from(AppResourcePagesetPageDO.class)
                .leftJoin(AppResourcePagesetDO.class)
                .on(APP_RESOURCE_PAGESET_PAGE.PAGESET_ID.eq(APP_RESOURCE_PAGESET.ID))
                .leftJoin(AppMenuDO.class)
                .on(APP_RESOURCE_PAGESET.MENU_ID.eq(APP_MENU.ID))
                .where(APP_MENU.IS_VISIBLE.eq(1))
                .and(APP_MENU.APPLICATION_ID.eq(applicationId))
                .and(APP_MENU.ID.eq(menuId));
        return new HashSet<>(this.objListAs(queryWrapper, Long.class));
    }


}
