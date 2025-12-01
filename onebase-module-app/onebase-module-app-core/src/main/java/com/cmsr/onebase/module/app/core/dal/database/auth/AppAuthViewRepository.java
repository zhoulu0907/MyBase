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
                .where(APP_AUTH_VIEW.MENU_UUID.eq(reqVO.getMenuId()));
        return this.list(queryWrapper);
    }



    public void deleteByQuery(AuthPermissionReq reqVO) {
        this.updateChain()
                .eq(AppAuthViewDO::getApplicationId, reqVO.getApplicationId())
                .eq(AppAuthViewDO::getRoleId, reqVO.getRoleId())
                .eq(AppAuthViewDO::getViewId, reqVO.getMenuId())
                .remove();
    }

    public List<AppAuthViewDO> findByAppIdAndRoleIdsAndMenuId(Long applicationId, Set<Long> roleIds, Long menuId) {
        QueryWrapper queryWrapper = this.query()
                .eq(AppAuthViewDO::getApplicationId, applicationId)
                .in(AppAuthViewDO::getRoleId, roleIds)
                .eq(AppAuthViewDO::getMenuId, menuId);
        return this.list(queryWrapper);
    }
}