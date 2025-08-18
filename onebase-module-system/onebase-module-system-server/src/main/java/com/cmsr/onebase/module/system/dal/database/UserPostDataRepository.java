package com.cmsr.onebase.module.system.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepositoryNew;
import com.cmsr.onebase.module.system.dal.dataobject.dept.UserPostDO;
import org.springframework.stereotype.Repository;

@Repository
public class UserPostDataRepository extends DataRepositoryNew<UserPostDO> {
    public UserPostDataRepository() {
        super(UserPostDO.class);
    }
}
