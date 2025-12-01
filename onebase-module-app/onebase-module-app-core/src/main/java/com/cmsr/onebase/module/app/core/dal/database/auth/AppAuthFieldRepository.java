package com.cmsr.onebase.module.app.core.dal.database.auth;

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

    public List<AppAuthFieldDO> findByQuery(AuthPermissionReq reqVO) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_AUTH_FIELD.APPLICATION_ID.eq(reqVO.getApplicationId()))
                .where(APP_AUTH_FIELD.ROLE_UUID.eq(reqVO.getRoleUuid()))
                .where(APP_AUTH_FIELD.MENU_UUID.eq(reqVO.getMenuUuid()));
        return list(queryWrapper);
    }

    public AppAuthFieldDO findByQuery(AuthPermissionReq reqVO, Long fieldId) {
        QueryWrapper queryWrapper = this.query()
                .eq(AppAuthFieldDO::getApplicationId, reqVO.getApplicationId())
                .eq(AppAuthFieldDO::getRoleId, reqVO.getRoleId())
                .eq(AppAuthFieldDO::getMenuId, reqVO.getMenuId())
                .eq(AppAuthFieldDO::getFieldId, fieldId);
        return this.getOne(queryWrapper);
    }

    public void deleteByQuery(AuthPermissionReq reqVO) {
        this.updateChain()
                .eq(AppAuthFieldDO::getApplicationId, reqVO.getApplicationId())
                .eq(AppAuthFieldDO::getRoleId, reqVO.getRoleId())
                .eq(AppAuthFieldDO::getMenuId, reqVO.getMenuId())
                .remove();
    }

    public List<AppAuthFieldDO> findByAppIdAndRoleIdsAndMenuId(Long applicationId, Set<Long> roleIds, Long menuId) {
        QueryWrapper queryWrapper = this.query()
                .eq(AppAuthFieldDO::getApplicationId, applicationId)
                .in(AppAuthFieldDO::getRoleId, roleIds)
                .eq(AppAuthFieldDO::getMenuId, menuId);
        return list(queryWrapper);
    }
}