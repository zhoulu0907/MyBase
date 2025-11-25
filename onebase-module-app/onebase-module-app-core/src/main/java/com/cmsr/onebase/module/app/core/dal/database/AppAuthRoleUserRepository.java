package com.cmsr.onebase.module.app.core.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.core.dal.dataobject.AuthRoleUserDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppAuthRoleUserMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 应用权限用户角色数据访问层
 *
 * @author lingma
 * @date 2025-08-05
 */
@Repository
public class AppAuthRoleUserRepository extends ServiceImpl<AppAuthRoleUserMapper, AuthRoleUserDO> {

    public void addRoleUser(Long roleId, List<Long> userIds) {
        for (Long userId : userIds) {
            QueryWrapper queryWrapper = this.query()
                    .eq(AuthRoleUserDO::getRoleId, roleId)
                    .eq(AuthRoleUserDO::getUserId, userId);
            boolean exists = this.exists(queryWrapper);
            if (!exists) {
                AuthRoleUserDO authRoleUserDO = new AuthRoleUserDO();
                authRoleUserDO.setRoleId(roleId);
                authRoleUserDO.setUserId(userId);
                this.save(authRoleUserDO);
            }
        }

    }

    public List<AuthRoleUserDO> findByRoleId(Long roleId) {
        QueryWrapper queryWrapper = this.query()
                .eq(AuthRoleUserDO::getRoleId, roleId);
        return this.list(queryWrapper);
    }

    public long countByRoleId(Long roleId) {
        QueryWrapper queryWrapper = this.query()
                .eq(AuthRoleUserDO::getRoleId, roleId);
        return count(queryWrapper);
    }

    public List<AuthRoleUserDO> findByByRoleIds(List<Long> roleIds) {
        QueryWrapper queryWrapper = this.query()
                .in(AuthRoleUserDO::getRoleId, roleIds);
        return list(queryWrapper);
    }

    public PageResult<AuthRoleUserDO> findByRoleId(Long roleId, PageParam pageParam) {
        QueryWrapper queryWrapper = this.query()
                .eq(AuthRoleUserDO::getRoleId, roleId);
        Page<AuthRoleUserDO> pageQuery = Page.of(pageParam.getPageNo(), pageParam.getPageSize());
        Page<AuthRoleUserDO> pageResult = this.page(pageQuery, queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }


    public void deleteRoleUser(Long roleId, List<Long> userIds) {
        this.updateChain()
                .eq(AuthRoleUserDO::getRoleId, roleId)
                .in(AuthRoleUserDO::getUserId, userIds)
                .remove();
    }

    public void deleteRoleUser(Long roleId, Long userId) {
        this.updateChain()
                .eq(AuthRoleUserDO::getRoleId, roleId)
                .eq(AuthRoleUserDO::getUserId, userId)
                .remove();
    }

    public void deleteByRoleId(Long roleId) {
        this.updateChain()
                .eq(AuthRoleUserDO::getRoleId, roleId)
                .remove();
    }


    public void deleteByUserId(Long userId) {
        this.updateChain()
                .eq(AuthRoleUserDO::getUserId, userId)
                .remove();
    }

    public List<AuthRoleUserDO> findByUserId(Long userId) {
        QueryWrapper queryWrapper = this.query()
                .eq(AuthRoleUserDO::getUserId, userId);
        return list(queryWrapper);
    }

    public List<AuthRoleUserDO> findAdminByRoleIdAndUserId(Long roleId, Long userId) {
        QueryWrapper queryWrapper = this.query()
                .eq(AuthRoleUserDO::getUserId, userId)
                .eq(AuthRoleUserDO::getRoleId, roleId);
        return list(queryWrapper);
    }
}