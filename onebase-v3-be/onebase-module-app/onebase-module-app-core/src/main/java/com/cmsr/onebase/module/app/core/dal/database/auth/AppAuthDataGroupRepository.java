package com.cmsr.onebase.module.app.core.dal.database.auth;

import com.cmsr.onebase.framework.common.enums.VersionTagEnum;
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
                .where(APP_AUTH_DATA_GROUP.ROLE_UUID.eq(reqVO.getRoleUuid()))
                .where(APP_AUTH_DATA_GROUP.MENU_UUID.eq(reqVO.getMenuUuid()))
                .orderBy(APP_AUTH_DATA_GROUP.GROUP_ORDER, true);
        return this.list(queryWrapper);
    }

    public List<AppAuthDataGroupDO> findByAppIdAndRoleIdsAndMenuId(Long applicationId, Set<String> roleUuids, String menuUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_AUTH_DATA_GROUP.APPLICATION_ID.eq(applicationId))
                .where(APP_AUTH_DATA_GROUP.ROLE_UUID.in(roleUuids))
                .where(APP_AUTH_DATA_GROUP.MENU_UUID.eq(menuUuid));
        return this.list(queryWrapper);
    }

    public void deleteByMenuUuid(Long applicationId, String menuUuid) {
        this.updateChain()
                .where(APP_AUTH_DATA_GROUP.MENU_UUID.eq(menuUuid))
                .where(APP_AUTH_DATA_GROUP.APPLICATION_ID.eq(applicationId))
                .where(APP_AUTH_DATA_GROUP.VERSION_TAG.eq(VersionTagEnum.BUILD.getValue()))
                .remove();
    }
}