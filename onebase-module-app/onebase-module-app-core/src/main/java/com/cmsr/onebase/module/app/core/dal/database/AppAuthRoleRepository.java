package com.cmsr.onebase.module.app.core.dal.database;

import com.cmsr.onebase.framework.orm.repo.BaseAppRepository;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppAuthRoleMapper;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppAuthRoleTableDef.APP_AUTH_ROLE;
import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppAuthRoleUserTableDef.APP_AUTH_ROLE_USER;

/**
 * 应用角色数据访问类
 *
 * @author huangjie
 * @date 2025-08-05
 */
@Repository
public class AppAuthRoleRepository extends BaseAppRepository<AppAuthRoleMapper, AppAuthRoleDO> {

    public List<AppAuthRoleDO> findByApplicationId(Long applicationId) {
        QueryWrapper queryWrapper = this.query()
                .eq(AppAuthRoleDO::getApplicationId, applicationId)
                .orderBy(AppAuthRoleDO::getRoleType, true)
                .orderBy(AppAuthRoleDO::getRoleName, true);
        return this.list(queryWrapper);
    }

    public AppAuthRoleDO findByApplicationIdAndRoleName(Long applicationId, String roleName) {
        QueryWrapper queryWrapper = this.query()
                .eq(AppAuthRoleDO::getApplicationId, applicationId)
                .eq(AppAuthRoleDO::getRoleName, roleName);
        return getOne(queryWrapper);
    }

    public AppAuthRoleDO findByAppIdAndRoleCode(Long applicationId, String roleCode) {
        QueryWrapper queryWrapper = this.query()
                .eq(AppAuthRoleDO::getApplicationId, applicationId)
                .eq(AppAuthRoleDO::getRoleCode, roleCode);
        return getOne(queryWrapper);
    }

    public AppAuthRoleDO findByApplicationIdAndRoleNameAndIdNot(Long applicationId, String roleName, Long roleId) {
        QueryWrapper queryWrapper = this.query()
                .eq(AppAuthRoleDO::getApplicationId, applicationId)
                .eq(AppAuthRoleDO::getRoleName, roleName)
                .eq(AppAuthRoleDO::getId, roleId);
        return getOne(queryWrapper);
    }


    public List<AppAuthRoleDO> findByUserIdAndApplicationId(Long userId, Long applicationId) {
        QueryWrapper queryWrapper = this.query()
                .select(
                        APP_AUTH_ROLE.ID,
                        APP_AUTH_ROLE.ROLE_CODE,
                        APP_AUTH_ROLE.ROLE_TYPE,
                        APP_AUTH_ROLE.ROLE_NAME
                )
                .from(APP_AUTH_ROLE_USER, APP_AUTH_ROLE)
                .where(APP_AUTH_ROLE_USER.ROLE_ID.eq(APP_AUTH_ROLE.ID))
                .and(APP_AUTH_ROLE_USER.USER_ID.eq(userId));

        return this.listAs(queryWrapper, AppAuthRoleDO.class);
    }
}