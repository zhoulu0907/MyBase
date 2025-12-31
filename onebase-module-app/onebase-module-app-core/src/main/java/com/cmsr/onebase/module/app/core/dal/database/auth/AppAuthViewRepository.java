package com.cmsr.onebase.module.app.core.dal.database.auth;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthViewDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppAuthViewMapper;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReq;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppAuthViewTableDef.APP_AUTH_VIEW;

/**
 * 应用权限实体数据访问层
 *
 * @author lingma
 * @date 2025-08-05
 */
@Repository
public class AppAuthViewRepository extends BaseBizRepository<AppAuthViewMapper, AppAuthViewDO> {

    public List<AppAuthViewDO> findByQuery(AuthPermissionReq reqVO) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_AUTH_VIEW.APPLICATION_ID.eq(reqVO.getApplicationId()))
                .where(APP_AUTH_VIEW.ROLE_UUID.eq(reqVO.getRoleUuid()))
                .where(APP_AUTH_VIEW.MENU_UUID.eq(reqVO.getMenuUuid()));
        return this.list(queryWrapper);
    }


    public void deleteByQuery(AuthPermissionReq reqVO) {
        this.updateChain()
                .where(APP_AUTH_VIEW.APPLICATION_ID.eq(reqVO.getApplicationId()))
                .where(APP_AUTH_VIEW.ROLE_UUID.eq(reqVO.getRoleUuid()))
                .where(APP_AUTH_VIEW.MENU_UUID.eq(reqVO.getMenuUuid()))
                .remove();
    }

    public List<AppAuthViewDO> findByAppIdAndRoleUuidsAndMenuUuid(Long applicationId, Set<String> roleUuids, String menuUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_AUTH_VIEW.APPLICATION_ID.eq(applicationId))
                .where(APP_AUTH_VIEW.ROLE_UUID.in(roleUuids))
                .where(APP_AUTH_VIEW.MENU_UUID.eq(menuUuid));
        return this.list(queryWrapper);
    }

    public AppAuthViewDO findByAppIdAndUuid(Long applicationId, String viewUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_AUTH_VIEW.APPLICATION_ID.eq(applicationId))
                .where(APP_AUTH_VIEW.VIEW_UUID.eq(viewUuid));
        return this.getOne(queryWrapper);
    }

}