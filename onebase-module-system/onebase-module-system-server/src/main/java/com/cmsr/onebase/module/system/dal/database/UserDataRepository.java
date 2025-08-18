package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.module.system.dal.dataobject.user.AdminUserDO;
import org.springframework.stereotype.Repository;

@Repository
public class UserDataRepository extends DataRepositoryNew<AdminUserDO> {
    public UserDataRepository() {
        super(AdminUserDO.class);
    }
}
