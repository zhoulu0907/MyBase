package com.cmsr.onebase.module.app.core.dal.database.auth;

import com.cmsr.onebase.framework.common.enums.VersionTagEnum;
import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthPermissionDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppAuthPermissionMapper;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReq;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppAuthPermissionTableDef.APP_AUTH_PERMISSION;

/**
 * 应用权限功能数据访问层
 *
 * @author lingma
 * @date 2025-08-05
 */
@Repository
public class AppAuthPermissionRepository extends BaseBizRepository<AppAuthPermissionMapper, AppAuthPermissionDO> {

    public AppAuthPermissionDO findByQuery(AuthPermissionReq reqVO) {
        QueryWrapper queryWrapper = this.query()
                .and(APP_AUTH_PERMISSION.APPLICATION_ID.eq(reqVO.getApplicationId()))
                .and(APP_AUTH_PERMISSION.ROLE_UUID.eq(reqVO.getRoleUuid()))
                .and(APP_AUTH_PERMISSION.MENU_UUID.eq(reqVO.getMenuUuid()));
        return this.getOne(queryWrapper);
    }

    public List<AppAuthPermissionDO> findByAppIdAndRoleIdsAndMenuId(Long applicationId, Set<String> roleUuids, String menuUuid) {
        QueryWrapper queryWrapper = this.query()
                .and(APP_AUTH_PERMISSION.APPLICATION_ID.eq(applicationId))
                .and(APP_AUTH_PERMISSION.ROLE_UUID.in(roleUuids))
                .and(APP_AUTH_PERMISSION.MENU_UUID.eq(menuUuid));
        return list(queryWrapper);
    }

    public List<AppAuthPermissionDO> findByAppIdAndRoleIds(Long applicationId, Set<String> roleUuids) {
        QueryWrapper queryWrapper = this.query()
                .and(APP_AUTH_PERMISSION.APPLICATION_ID.eq(applicationId))
                .and(APP_AUTH_PERMISSION.ROLE_UUID.in(roleUuids));
        return list(queryWrapper);
    }

    public void deleteByMenuUuid(Long applicationId, String menuUuid) {
        this.updateChain()
                .where(APP_AUTH_PERMISSION.MENU_UUID.eq(menuUuid))
                .where(APP_AUTH_PERMISSION.APPLICATION_ID.eq(applicationId))
                .where(APP_AUTH_PERMISSION.VERSION_TAG.eq(VersionTagEnum.BUILD.getValue()))
                .remove();
    }
}