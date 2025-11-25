package com.cmsr.onebase.module.app.core.dal.database;

import com.cmsr.onebase.framework.orm.mybatis.BaseAppRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AuthDataGroupDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppAuthDataGroupMapper;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReq;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppAuthDataGroupTableDef.APP_AUTH_DATA_GROUP;
import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppAuthRoleTableDef.APP_AUTH_ROLE;

/**
 * 应用权限数据组数据访问层
 *
 * @author lingma
 * @date 2025-08-05
 */
@Repository
public class AppAuthDataGroupRepository extends BaseAppRepository<AppAuthDataGroupMapper, AuthDataGroupDO> {

    public List<AuthDataGroupDO> findByQuery(AuthPermissionReq reqVO) {
        QueryWrapper queryWrapper = this.query()
                .eq(AuthDataGroupDO::getApplicationId, reqVO.getApplicationId())
                .eq(AuthDataGroupDO::getRoleId, reqVO.getRoleId())
                .eq(AuthDataGroupDO::getMenuId, reqVO.getMenuId())
                .orderBy(AuthDataGroupDO::getGroupOrder, true);
        return this.list(queryWrapper);
    }

    public List<AuthDataGroupDO> findByAppIdAndRoleIdsAndMenuId(Long applicationId, Set<Long> roleIds, Long menuId) {
        QueryWrapper queryWrapper = this.query()
                .select(
                        APP_AUTH_ROLE.ROLE_CODE,
                        APP_AUTH_ROLE.ROLE_TYPE,
                        APP_AUTH_DATA_GROUP.ALL_COLUMNS
                )
                .from(APP_AUTH_ROLE)
                .leftJoin(APP_AUTH_DATA_GROUP)
                .on(APP_AUTH_ROLE.APPLICATION_ID.eq(APP_AUTH_DATA_GROUP.APPLICATION_ID)
                        .and(APP_AUTH_ROLE.ID.eq(APP_AUTH_DATA_GROUP.ROLE_ID)))
                .where(APP_AUTH_ROLE.ID.in(roleIds))
                .where(APP_AUTH_DATA_GROUP.MENU_ID.eq(menuId).or(APP_AUTH_DATA_GROUP.MENU_ID.isNull()));
        return this.listAs(queryWrapper, AuthDataGroupDO.class);
    }


}