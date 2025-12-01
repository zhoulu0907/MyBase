package com.cmsr.onebase.module.app.core.dal.database.auth;

import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthDataGroupDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppAuthDataGroupMapper;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReq;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppAuthDataGroupTableDef.APP_AUTH_DATA_GROUP;

/**
 * 应用权限数据组数据访问层
 *
 * @author lingma
 * @date 2025-08-05
 */
@Repository
public class AppAuthDataGroupRepository extends BaseBizRepository<AppAuthDataGroupMapper, AppAuthDataGroupDO> {

    public List<AppAuthDataGroupDO> findByQuery(AuthPermissionReq reqVO) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_AUTH_DATA_GROUP.APPLICATION_ID.eq(reqVO.getApplicationId()))
                .where(APP_AUTH_DATA_GROUP.ROLE_UUID.eq(reqVO.getRoleId()))
                .where(APP_AUTH_DATA_GROUP.MENU_UUID.eq(reqVO.getMenuId()))
                .orderBy(APP_AUTH_DATA_GROUP.GROUP_ORDER, true);
        return this.list(queryWrapper);
    }

    public List<AppAuthDataGroupDO> findByAppIdAndRoleIdsAndMenuId(Long applicationId, Set<Long> roleIds, Long menuId) {
        return mapper.findByAppIdAndRoleIdsAndMenuId(applicationId, roleIds, menuId);
    }

}