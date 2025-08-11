package com.cmsr.onebase.module.app.dal.database.auth;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthRoleAddMemberReqVO;
import com.cmsr.onebase.module.app.controller.admin.auth.vo.AuthRoleDeleteMemberReqVO;
import com.cmsr.onebase.module.app.dal.dataobject.auth.AuthUserRoleDO;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.springframework.stereotype.Repository;

/**
 * 应用权限用户角色数据访问层
 *
 * @author lingma
 * @date 2025-08-05
 */
@Repository
public class AppAuthUserRoleRepository extends DataRepositoryNew<AuthUserRoleDO> {

    public AppAuthUserRoleRepository() {
        super(AuthUserRoleDO.class);
    }

    public void saveUserRole(AuthRoleAddMemberReqVO reqVO) {
        for (Long userId : reqVO.getUserIds()) {
            ConfigStore configStore = new DefaultConfigStore();
            configStore.eq("role_id", reqVO.getRoleId());
            configStore.eq("user_id", userId);
            if (this.countByConfig(configStore) == 0) {
                AuthUserRoleDO authUserRoleDO = new AuthUserRoleDO();
                authUserRoleDO.setRoleId(reqVO.getRoleId());
                authUserRoleDO.setUserId(userId);
                this.insert(authUserRoleDO);
            }
        }

    }

    public void deleteUserRole(AuthRoleDeleteMemberReqVO reqVO) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq("role_id", reqVO.getRoleId());
        configStore.in("user_id", reqVO.getUserIds());
        this.deleteByConfig(configStore);
    }

    public void deleteByRoleId(Long roleId) {
        ConfigStore configStore = new DefaultConfigStore();
        configStore.eq("role_id", roleId);
        this.deleteByConfig(configStore);
    }

}