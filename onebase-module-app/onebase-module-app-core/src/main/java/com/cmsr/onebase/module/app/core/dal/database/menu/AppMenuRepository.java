package com.cmsr.onebase.module.app.core.dal.database.menu;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppMenuDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppMenuMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppMenuTableDef.APP_MENU;

/**
 * @Author：huangjie
 * @Date：2025/8/6 9:31
 */
@Repository
public class AppMenuRepository extends BaseBizRepository<AppMenuMapper, AppMenuDO> {


    public List<AppMenuDO> findByApplicationId(Long applicationId) {
        QueryWrapper queryWrapper = this.query()
                .eq(AppMenuDO::getApplicationId, applicationId)
                .orderBy(AppMenuDO::getMenuSort, true);
        return this.list(queryWrapper);
    }

    public List<AppMenuDO> listByIdsAndOrder(List<Long> menuIds) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_MENU.ID.in(menuIds))
                .orderBy(APP_MENU.MENU_SORT, true);
        return this.list(queryWrapper);
    }

    public List<String> findMenuUuidListByApplication(Long applicationId) {
        QueryWrapper queryWrapper = this.query()
                .select(APP_MENU.MENU_UUID)
                .where(APP_MENU.APPLICATION_ID.eq(applicationId))
                .orderBy(APP_MENU.MENU_SORT, true);
        return this.objListAs(queryWrapper, String.class);
    }

    public long countByParentId(Long applicationId, String menuUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_MENU.APPLICATION_ID.eq(applicationId))
                .where(APP_MENU.PARENT_UUID.eq(menuUuid));
        return count(queryWrapper);
    }

    public AppMenuDO findByAppIdAndMenuUuid(Long applicationId, String menuUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_MENU.APPLICATION_ID.eq(applicationId))
                .where(APP_MENU.MENU_UUID.eq(menuUuid));
        return this.getOne(queryWrapper);
    }

    public int countByApplicationId(Long applicationId) {
        QueryWrapper queryWrapper = this.query().eq(AppMenuDO::getApplicationId, applicationId);
        return (int) count(queryWrapper);
    }

    public List<AppMenuDO> findVisibleByAppIdAndType(Long applicationId, Set<Integer> menuTypes) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_MENU.APPLICATION_ID.eq(applicationId))
                .where(APP_MENU.MENU_TYPE.in(menuTypes))
                .where(APP_MENU.IS_VISIBLE.eq(1))
                .orderBy(AppMenuDO::getMenuSort, true);
        return list(queryWrapper);
    }

    public List<AppMenuDO> findByApplicationIdAndType(Long applicationId, Set<Integer> menuTypes) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_MENU.APPLICATION_ID.eq(applicationId))
                .where(APP_MENU.MENU_TYPE.in(menuTypes))
                .orderBy(AppMenuDO::getMenuSort, true);
        return list(queryWrapper);
    }

    public AppMenuDO findByUuidInApplication(Long applicationId, String menuUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_MENU.APPLICATION_ID.eq(applicationId))
                .where(APP_MENU.MENU_UUID.eq(menuUuid));
        return this.getOne(queryWrapper);
    }

    public boolean existsByEntityUuid(String entityUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_MENU.ENTITY_UUID.eq(entityUuid));
        return this.exists(queryWrapper);
    }


}
