package com.cmsr.onebase.module.system.service.dept;

import com.cmsr.onebase.framework.common.enums.XFromSceneTypeEnum;
import com.cmsr.onebase.module.system.dal.database.dept.AbstractDeptDataRepository;
import com.cmsr.onebase.module.system.dal.database.dept.TenantDeptDataRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service("tenantDeptService")
@Validated
public class TenantDeptServiceImpl extends AbstractDeptServiceImpl{
    @Resource
    private TenantDeptDataRepository tenantDeptDataRepository;

    @Override
    public AbstractDeptDataRepository getDeptDataRepository() {
        return tenantDeptDataRepository;
    }

    @Override
    public String getXFromSceneType() {
        return XFromSceneTypeEnum.TENANT.getCode();
    }
}
