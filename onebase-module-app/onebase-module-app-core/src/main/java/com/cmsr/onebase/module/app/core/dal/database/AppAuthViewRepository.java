package com.cmsr.onebase.module.app.core.dal.database;

import com.cmsr.onebase.framework.orm.mybatis.BaseAppRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AuthViewDO;
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
public class AppAuthViewRepository extends BaseAppRepository<AppAuthViewMapper, AuthViewDO> {

    public List<AuthViewDO> findByQuery(AuthPermissionReq reqVO) {
        QueryWrapper queryWrapper = this.query()
                .eq(AuthViewDO::getApplicationId, reqVO.getApplicationId())
                .eq(AuthViewDO::getRoleId, reqVO.getRoleId())
                .eq(AuthViewDO::getMenuId, reqVO.getMenuId());
        return this.list(queryWrapper);
    }

    public AuthViewDO findByQuery(AuthPermissionReq reqVO, Long viewId) {
        QueryWrapper queryWrapper = this.query()
                .eq(AuthViewDO::getApplicationId, reqVO.getApplicationId())
                .eq(AuthViewDO::getRoleId, reqVO.getRoleId())
                .eq(AuthViewDO::getMenuId, reqVO.getMenuId())
                .eq(AuthViewDO::getViewId, viewId);
        return this.getOne(queryWrapper);
    }

    public void deleteByQuery(AuthPermissionReq reqVO) {
        this.updateChain()
                .eq(AuthViewDO::getApplicationId, reqVO.getApplicationId())
                .eq(AuthViewDO::getRoleId, reqVO.getRoleId())
                .eq(AuthViewDO::getViewId, reqVO.getMenuId())
                .remove();
    }

    public List<AuthViewDO> findByAppIdAndRoleIdsAndMenuId(Long applicationId, Set<Long> roleIds, Long menuId) {
        QueryWrapper queryWrapper = this.query()
                .eq(AuthViewDO::getApplicationId, applicationId)
                .in(AuthViewDO::getRoleId, roleIds)
                .eq(AuthViewDO::getMenuId, menuId);
        return this.list(queryWrapper);
    }
}