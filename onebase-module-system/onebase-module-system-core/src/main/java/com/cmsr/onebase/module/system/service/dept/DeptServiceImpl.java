package com.cmsr.onebase.module.system.service.dept;

import com.cmsr.onebase.framework.common.enums.XFromSceneTypeEnum;
import com.cmsr.onebase.module.system.dal.database.dept.DeptDataRepository;
import com.cmsr.onebase.module.system.dal.database.dept.AbstractDeptDataRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service("deptService")
@Validated
public class DeptServiceImpl extends AbstractDeptServiceImpl {

    @Resource
    private DeptDataRepository deptDataRepository;

    @Override
    public AbstractDeptDataRepository getDeptDataRepository() {
        return deptDataRepository;
    }

    @Override
    public String getXFromSceneType() {
        return XFromSceneTypeEnum.ALL.getCode();
    }
}
