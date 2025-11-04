package com.cmsr.onebase.module.app.core.dal.database.auth;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageParam;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.core.dal.dataobject.auth.AuthRoleDeptDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 应用权限用户角色数据访问层
 *
 * @author lingma
 * @date 2025-08-05
 */
@Repository
public class AppAuthRoleDeptRepository extends DataRepository<AuthRoleDeptDO> {

    public AppAuthRoleDeptRepository() {
        super(AuthRoleDeptDO.class);
    }

    public void addRoleDept(Long roleId, List<Long> deptIds, Integer isIncludeChild) {
        for (Long deptId : deptIds) {
            ConfigStore configStore = new DefaultConfigStore();
            configStore.eq("role_id", roleId);
            configStore.eq("dept_id", deptId);
            if (this.countByConfig(configStore) == 0) {
                AuthRoleDeptDO authRoleDeptDO = new AuthRoleDeptDO();
                authRoleDeptDO.setRoleId(roleId);
                authRoleDeptDO.setDeptId(deptId);
                authRoleDeptDO.setIsIncludeChild(isIncludeChild);
                this.insert(authRoleDeptDO);
            }
        }

    }

    public List<AuthRoleDeptDO> findByRoleId(Long roleId) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq("role_id", roleId);
        return findAllByConfig(configStore);
    }

    public PageResult<AuthRoleDeptDO> findByRoleId(Long roleId, PageParam pageParam) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq("role_id", roleId);
        return this.findPageWithConditions(configStore, pageParam.getPageNo(), pageParam.getPageSize());
    }


    public void deleteRoleDept(Long roleId, List<Long> deptIds) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq("role_id", roleId);
        configStore.in("dept_id", deptIds);
        this.deleteByConfig(configStore);
    }

    public void deleteByRoleId(Long roleId) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq("role_id", roleId);
        this.deleteByConfig(configStore);
    }


    public void deleteByDeptId(Long deptId) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq("dept_id", deptId);
        this.deleteByConfig(configStore);
    }

}