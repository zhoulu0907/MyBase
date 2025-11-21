package com.cmsr.onebase.module.system.dal.database.user;

import com.cmsr.onebase.framework.common.enums.XFromSceneTypeEnum;
import org.springframework.stereotype.Repository;

@Repository
public class AdminUserDataRepository extends AbstractUserDataRepository {
    @Override
    public String getXFromSceneType() {
        return XFromSceneTypeEnum.ALL.getCode();
    }
}
