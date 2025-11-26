package com.cmsr.onebase.module.app.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AuthPermissionDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppAuthPermissionMapper;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReq;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppAuthPermissionTableDef.APP_AUTH_PERMISSION;
import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppAuthRoleTableDef.APP_AUTH_ROLE;

/**
 * 应用权限功能数据访问层
 *
 * @author lingma
 * @date 2025-08-05
 */
@Repository
public class AppAuthPermissionRepository extends BaseAppRepository<AppAuthPermissionMapper, AuthPermissionDO> {

    public AuthPermissionDO findByQuery(AuthPermissionReq reqVO) {
        QueryWrapper queryWrapper = this.query()
                .eq(AuthPermissionReq::getApplicationId, reqVO.getApplicationId())
                .eq(AuthPermissionReq::getRoleId, reqVO.getRoleId())
                .eq(AuthPermissionReq::getMenuId, reqVO.getMenuId());
        return this.getOne(queryWrapper);
    }

    public List<AuthPermissionDO> findByAppIdAndRoleIdsAndMenuId(Long applicationId, Set<Long> roleIds, Long menuId) {
        QueryWrapper queryWrapper = this.query()
                .select(
                        APP_AUTH_ROLE.ROLE_CODE,
                        APP_AUTH_ROLE.ROLE_TYPE,
                        APP_AUTH_PERMISSION.ALL_COLUMNS
                )
                .from(APP_AUTH_ROLE)
                .leftJoin(APP_AUTH_PERMISSION)
                .on(APP_AUTH_ROLE.APPLICATION_ID.eq(APP_AUTH_PERMISSION.APPLICATION_ID)
                        .and(APP_AUTH_ROLE.ID.eq(APP_AUTH_PERMISSION.ROLE_ID)))
                .where(APP_AUTH_ROLE.APPLICATION_ID.eq(applicationId))
                .and(APP_AUTH_ROLE.ID.in(roleIds))
                .and(APP_AUTH_PERMISSION.MENU_ID.eq(menuId).or(APP_AUTH_PERMISSION.MENU_ID.isNull()));
        return this.listAs(queryWrapper, AuthPermissionDO.class);
    }


    public List<AuthPermissionDO> findByAppIdAndRoleIds(Long applicationId, Set<Long> roleIds) {
        QueryWrapper queryWrapper = this.query()
                .select(
                        APP_AUTH_ROLE.ROLE_CODE,
                        APP_AUTH_ROLE.ROLE_TYPE,
                        APP_AUTH_PERMISSION.ALL_COLUMNS
                )
                .from(APP_AUTH_ROLE)
                .leftJoin(APP_AUTH_PERMISSION)
                .on(APP_AUTH_ROLE.APPLICATION_ID.eq(APP_AUTH_PERMISSION.APPLICATION_ID)
                        .and(APP_AUTH_ROLE.ID.eq(APP_AUTH_PERMISSION.ROLE_ID)))
                .where(APP_AUTH_ROLE.APPLICATION_ID.eq(applicationId))
                .and(APP_AUTH_ROLE.ID.in(roleIds));
        return this.listAs(queryWrapper, AuthPermissionDO.class);
    }
}