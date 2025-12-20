package com.cmsr.onebase.module.app.core.dal.database.auth;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.core.dal.dataobject.AppAuthRoleDeptDO;
import com.cmsr.onebase.module.app.core.dal.mapper.AppAuthRoleDeptMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppAuthRoleDeptTableDef.APP_AUTH_ROLE_DEPT;
import static com.cmsr.onebase.module.app.core.dal.dataobject.table.AppAuthRoleTableDef.APP_AUTH_ROLE;

/**
 * 应用权限用户角色数据访问层
 *
 * @author lingma
 * @date 2025-08-05
 */
@Repository
public class AppAuthRoleDeptRepository extends ServiceImpl<AppAuthRoleDeptMapper, AppAuthRoleDeptDO> {

    public void addRoleDept(Long roleId, List<Long> deptIds, Integer isIncludeChild) {
        for (Long deptId : deptIds) {
            QueryWrapper queryWrapper = this.query()
                    .eq(AppAuthRoleDeptDO::getRoleId, roleId)
                    .eq(AppAuthRoleDeptDO::getDeptId, deptId);
            boolean exists = this.exists(queryWrapper);
            if (!exists) {
                AppAuthRoleDeptDO authRoleDeptDO = new AppAuthRoleDeptDO();
                authRoleDeptDO.setRoleId(roleId);
                authRoleDeptDO.setDeptId(deptId);
                authRoleDeptDO.setIsIncludeChild(isIncludeChild);
                this.save(authRoleDeptDO);
            }
        }
    }


    public void deleteByRoleId(Long roleId) {
        this.updateChain()
                .eq(AppAuthRoleDeptDO::getRoleId, roleId)
                .remove();
    }


    public List<AppAuthRoleDeptDO> findByApplicationId(Long applicationId) {
        QueryWrapper queryWrapper = this.query()
                .select(
                        APP_AUTH_ROLE_DEPT.ALL_COLUMNS
                )
                .from(APP_AUTH_ROLE_DEPT, APP_AUTH_ROLE)
                .where(APP_AUTH_ROLE_DEPT.ROLE_ID.eq(APP_AUTH_ROLE.ID))
                .and(APP_AUTH_ROLE.APPLICATION_ID.eq(applicationId));
        return this.list(queryWrapper);
    }
}