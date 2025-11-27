package com.cmsr.onebase.module.app.core.dal.database.auth;

import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthViewDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppAuthViewMapper;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReq;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 应用权限实体数据访问层
 *
 * @author lingma
 * @date 2025-08-05
 */
@Repository
public class AppAuthViewRepository extends BaseAppRepository<AppAuthViewMapper, AppAuthViewDO> {

    public List<AppAuthViewDO> findByQuery(AuthPermissionReq reqVO) {
        QueryWrapper queryWrapper = this.query()
                .eq(AppAuthViewDO::getApplicationId, reqVO.getApplicationId())
                .eq(AppAuthViewDO::getRoleId, reqVO.getRoleId())
                .eq(AppAuthViewDO::getMenuId, reqVO.getMenuId());
        return this.list(queryWrapper);
    }

    public AppAuthViewDO findByQuery(AuthPermissionReq reqVO, Long viewId) {
        QueryWrapper queryWrapper = this.query()
                .eq(AppAuthViewDO::getApplicationId, reqVO.getApplicationId())
                .eq(AppAuthViewDO::getRoleId, reqVO.getRoleId())
                .eq(AppAuthViewDO::getMenuId, reqVO.getMenuId())
                .eq(AppAuthViewDO::getViewId, viewId);
        return this.getOne(queryWrapper);
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