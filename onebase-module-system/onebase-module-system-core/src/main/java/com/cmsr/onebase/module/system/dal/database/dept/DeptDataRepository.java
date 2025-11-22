package com.cmsr.onebase.module.system.dal.database.dept;

import com.cmsr.onebase.framework.common.enums.XFromSceneTypeEnum;
import org.springframework.stereotype.Repository;

@Repository
public class DeptDataRepository extends AbstractDeptDataRepository {
    @Override
    public String getXFromSceneType() {
        return XFromSceneTypeEnum.CORP.getCode();
    }
}
