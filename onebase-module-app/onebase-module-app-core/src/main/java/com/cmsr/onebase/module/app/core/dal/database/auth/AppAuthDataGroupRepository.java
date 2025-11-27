package com.cmsr.onebase.module.app.core.dal.database.auth;

import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthDataGroupDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppAuthDataGroupMapper;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReq;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 应用权限数据组数据访问层
 *
 * @author lingma
 * @date 2025-08-05
 */
@Repository
public class AppAuthDataGroupRepository extends BaseAppRepository<AppAuthDataGroupMapper, AppAuthDataGroupDO> {

    public List<AppAuthDataGroupDO> findByQuery(AuthPermissionReq reqVO) {
        QueryWrapper queryWrapper = this.query()
                .eq(AppAuthDataGroupDO::getApplicationId, reqVO.getApplicationId())
                .eq(AppAuthDataGroupDO::getRoleId, reqVO.getRoleId())
                .eq(AppAuthDataGroupDO::getMenuId, reqVO.getMenuId())
                .orderBy(AppAuthDataGroupDO::getGroupOrder, true);
        return this.list(queryWrapper);
    }

    public List<AppAuthDataGroupDO> findByAppIdAndRoleIdsAndMenuId(Long applicationId, Set<Long> roleIds, Long menuId) {
        return mapper.findByAppIdAndRoleIdsAndMenuId(applicationId, roleIds, menuId);
    }

}