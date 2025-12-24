package com.cmsr.onebase.module.app.core.dal.database.auth;

import com.cmsr.onebase.framework.common.enums.VersionTagEnum;
import com.cmsr.onebase.framework.orm.repo.BaseBizRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthFieldDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppAuthFieldMapper;
import com.cmsr.onebase.module.app.core.vo.auth.AuthPermissionReq;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppAuthFieldTableDef.APP_AUTH_FIELD;

/**
 * 应用权限字段数据访问层
 *
 * @author lingma
 * @date 2025-08-05
 */
@Repository
public class AppAuthFieldRepository extends BaseBizRepository<AppAuthFieldMapper, AppAuthFieldDO> {

    public List<AppAuthFieldDO> findByQueryRequest(AuthPermissionReq reqVO) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_AUTH_FIELD.APPLICATION_ID.eq(reqVO.getApplicationId()))
                .where(APP_AUTH_FIELD.ROLE_UUID.eq(reqVO.getRoleUuid()))
                .where(APP_AUTH_FIELD.MENU_UUID.eq(reqVO.getMenuUuid()));
        return list(queryWrapper);
    }

    public void deleteByQueryRequest(AuthPermissionReq reqVO) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_AUTH_FIELD.APPLICATION_ID.eq(reqVO.getApplicationId()))
                .where(APP_AUTH_FIELD.ROLE_UUID.eq(reqVO.getRoleUuid()))
                .where(APP_AUTH_FIELD.MENU_UUID.eq(reqVO.getMenuUuid()));
        super.remove(queryWrapper);
    }

    public List<AppAuthFieldDO> findByAppIdAndRoleIdsAndMenuId(Long applicationId, Set<String> roleUuids, String menuUuid) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_AUTH_FIELD.APPLICATION_ID.eq(applicationId))
                .where(APP_AUTH_FIELD.ROLE_UUID.in(roleUuids))
                .where(APP_AUTH_FIELD.MENU_UUID.eq(menuUuid));
        return list(queryWrapper);
    }

    public void deleteByMenuUuid(Long applicationId, String menuUuid) {
        this.updateChain()
                .where(APP_AUTH_FIELD.MENU_UUID.eq(menuUuid))
                .where(APP_AUTH_FIELD.APPLICATION_ID.eq(applicationId))
                .where(APP_AUTH_FIELD.VERSION_TAG.eq(VersionTagEnum.BUILD.getValue()))
                .remove();
    }
}