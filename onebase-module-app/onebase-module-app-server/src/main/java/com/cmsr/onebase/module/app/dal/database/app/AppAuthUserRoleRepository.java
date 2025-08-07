package com.cmsr.onebase.module.app.dal.database.app;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.app.dal.dataobject.app.AuthUserRoleDO;
import org.springframework.stereotype.Repository;

/**
 * 应用权限用户角色数据访问层
 *
 * @author lingma
 * @date 2025-08-05
 */
@Repository
public class AppAuthUserRoleRepository extends DataRepository {

    public AppAuthUserRoleRepository() {
        super(AuthUserRoleDO.class);
    }
}