package com.cmsr.onebase.module.system.service.dept;

import com.cmsr.onebase.framework.common.enums.XFromSceneTypeEnum;
import com.cmsr.onebase.module.system.dal.database.dept.CorpDeptDataRepository;
import com.cmsr.onebase.module.system.dal.database.dept.AbstractDeptDataRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service("corpDeptService")
@Validated
public class CorpDeptServiceImpl extends AbstractDeptServiceImpl {

    @Resource
    private CorpDeptDataRepository corpDeptDataRepository;

    @Override
    public AbstractDeptDataRepository getDeptDataRepository() {
        return corpDeptDataRepository;
    }

    @Override
    public String getXFromSceneType() {
        return XFromSceneTypeEnum.CORP.getCode();
    }
}
