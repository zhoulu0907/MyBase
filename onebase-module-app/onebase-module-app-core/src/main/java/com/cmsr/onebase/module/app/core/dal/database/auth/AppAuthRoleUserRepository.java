package com.cmsr.onebase.module.app.core.dal.database.auth;

import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleUserDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppAuthRoleUserMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppAuthRoleUserTableDef.APP_AUTH_ROLE_USER;

/**
 * 应用权限用户角色数据访问层
 *
 * @author lingma
 * @date 2025-08-05
 */
@Repository
public class AppAuthRoleUserRepository extends ServiceImpl<AppAuthRoleUserMapper, AppAuthRoleUserDO> {

    public void addRoleUser(Long roleId, List<Long> userIds) {
        for (Long userId : userIds) {
            QueryWrapper queryWrapper = this.query()
                    .eq(AppAuthRoleUserDO::getRoleId, roleId)
                    .eq(AppAuthRoleUserDO::getUserId, userId);
            boolean exists = this.exists(queryWrapper);
            if (!exists) {
                AppAuthRoleUserDO authRoleUserDO = new AppAuthRoleUserDO();
                authRoleUserDO.setRoleId(roleId);
                authRoleUserDO.setUserId(userId);
                this.save(authRoleUserDO);
            }
        }

    }

    public List<AppAuthRoleUserDO> findByRoleId(Long roleId) {
        QueryWrapper queryWrapper = this.query()
                .eq(AppAuthRoleUserDO::getRoleId, roleId);
        return this.list(queryWrapper);
    }

    public long countByRoleId(Long roleId) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_AUTH_ROLE_USER.ROLE_ID.eq(roleId));
        return count(queryWrapper);
    }

    public List<AppAuthRoleUserDO> findByByRoleIds(List<Long> roleIds) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_AUTH_ROLE_USER.ROLE_ID.in(roleIds));
        return list(queryWrapper);
    }


    public void deleteByRoleId(Long roleId) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_AUTH_ROLE_USER.ROLE_ID.eq(roleId));
        this.remove(queryWrapper);
    }


    public void deleteByUserId(Long userId) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_AUTH_ROLE_USER.USER_ID.eq(userId));
        this.remove(queryWrapper);
    }

    public List<AppAuthRoleUserDO> findByUserId(Long userId) {
        QueryWrapper queryWrapper = this.query()
                .where(APP_AUTH_ROLE_USER.USER_ID.eq(userId));
        return list(queryWrapper);
    }

    public boolean existsByUserIdAndRoleId(Long roleId, Long userId) {
        QueryWrapper queryWrapper = this.query()
                .eq(AppAuthRoleUserDO::getUserId, userId)
                .eq(AppAuthRoleUserDO::getRoleId, roleId);
        return this.exists(queryWrapper);
    }
}