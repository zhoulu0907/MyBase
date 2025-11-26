package com.cmsr.onebase.module.app.core.dal.database;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleUserDO;
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
                .eq(AppAuthRoleUserDO::getRoleId, roleId);
        return count(queryWrapper);
    }

    public List<AppAuthRoleUserDO> findByByRoleIds(List<Long> roleIds) {
        QueryWrapper queryWrapper = this.query()
                .in(AppAuthRoleUserDO::getRoleId, roleIds);
        return list(queryWrapper);
    }

    public PageResult<AppAuthRoleUserDO> findByRoleId(Long roleId, PageParam pageParam) {
        QueryWrapper queryWrapper = this.query()
                .eq(AppAuthRoleUserDO::getRoleId, roleId);
        Page<AppAuthRoleUserDO> pageQuery = Page.of(pageParam.getPageNo(), pageParam.getPageSize());
        Page<AppAuthRoleUserDO> pageResult = this.page(pageQuery, queryWrapper);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotalRow());
    }


    public void deleteRoleUser(Long roleId, List<Long> userIds) {
        this.updateChain()
                .eq(AppAuthRoleUserDO::getRoleId, roleId)
                .in(AppAuthRoleUserDO::getUserId, userIds)
                .remove();
    }

    public void deleteRoleUser(Long roleId, Long userId) {
        this.updateChain()
                .eq(AppAuthRoleUserDO::getRoleId, roleId)
                .eq(AppAuthRoleUserDO::getUserId, userId)
                .remove();
    }

    public void deleteByRoleId(Long roleId) {
        this.updateChain()
                .eq(AppAuthRoleUserDO::getRoleId, roleId)
                .remove();
    }


    public void deleteByUserId(Long userId) {
        this.updateChain()
                .eq(AppAuthRoleUserDO::getUserId, userId)
                .remove();
    }

    public List<AppAuthRoleUserDO> findByUserId(Long userId) {
        QueryWrapper queryWrapper = this.query()
                .eq(AppAuthRoleUserDO::getUserId, userId);
        return list(queryWrapper);
    }

    public List<AppAuthRoleUserDO> findAdminByRoleIdAndUserId(Long roleId, Long userId) {
        QueryWrapper queryWrapper = this.query()
                .eq(AppAuthRoleUserDO::getUserId, userId)
                .eq(AppAuthRoleUserDO::getRoleId, roleId);
        return list(queryWrapper);
    }
}