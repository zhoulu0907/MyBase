package com.cmsr.onebase.module.app.dal.database.auth;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthRoleUsersPageReqVO;
import com.cmsr.onebase.module.app.dal.dataobject.auth.AuthRoleUserDO;
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
public class AppAuthRoleUserRepository extends DataRepository<AuthRoleUserDO> {

    public AppAuthRoleUserRepository() {
        super(AuthRoleUserDO.class);
    }

    public void addRoleUser(Long roleId, List<Long> userIds) {
        for (Long userId : userIds) {
            ConfigStore configStore = new DefaultConfigStore();
            configStore.eq("role_id", roleId);
            configStore.eq("user_id", userId);
            if (this.countByConfig(configStore) == 0) {
                AuthRoleUserDO authRoleUserDO = new AuthRoleUserDO();
                authRoleUserDO.setRoleId(roleId);
                authRoleUserDO.setUserId(userId);
                this.insert(authRoleUserDO);
            }
        }

    }

    public PageResult<AuthRoleUserDO> pageByRoleId(AuthRoleUsersPageReqVO reqVO) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq("role_id", reqVO.getRoleId());
        return this.findPageWithConditions(configStore, reqVO.getPageNo(), reqVO.getPageSize());
    }

    public void deleteRoleUser(Long roleId, List<Long> userIds) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq("role_id", roleId);
        configStore.in("user_id", userIds);
        this.deleteByConfig(configStore);
    }

    public void deleteByRoleId(Long roleId) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq("role_id", roleId);
        this.deleteByConfig(configStore);
    }


}